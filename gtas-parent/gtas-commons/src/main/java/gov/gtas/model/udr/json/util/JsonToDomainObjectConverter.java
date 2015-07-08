package gov.gtas.model.udr.json.util;

import gov.gtas.error.CommonErrorConstants;
import gov.gtas.error.CommonServiceException;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.User;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.RuleCond;
import gov.gtas.model.udr.RuleCondPk;
import gov.gtas.model.udr.RuleMeta;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.udr.enumtype.EntityLookupEnum;
import gov.gtas.model.udr.enumtype.OperatorCodeEnum;
import gov.gtas.model.udr.enumtype.ValueTypesEnum;
import gov.gtas.model.udr.enumtype.YesNoEnum;
import gov.gtas.model.udr.json.MetaData;
import gov.gtas.model.udr.json.QueryObject;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.model.udr.json.UdrSpecification;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Utility functions to convert JSON objects to JPA domain objects.
 * 
 * @author GTAS3 (AB)
 *
 */
public class JsonToDomainObjectConverter {
	/**
	 * Creates a meta data domain object from the JSON UDR specification object.
	 * 
	 * @param inputJson
	 *            the UDR JSON object.
	 * @return RuleMeta domain object
	 */

	public static RuleMeta extractRuleMeta(UdrSpecification inputJson) {
		final MetaData metaData = inputJson.getSummary();
		JsonValidationUtils.validateMetaData(metaData);

		final Long id = inputJson.getId();
		final String title = metaData.getTitle();
		final Date startDate = metaData.getStartDate();
		final String descr = metaData.getDescription();
		final YesNoEnum enabled = metaData.isEnabled() ? YesNoEnum.Y
				: YesNoEnum.N;
		final Date endDate = metaData.getEndDate();

		RuleMeta ret = createRuleMeta(id, title, descr, startDate, endDate,
				enabled);
		return ret;
	}

	/**
	 * Converts the rule details portion of the UDR JSON specifications object
	 * (i.e., QueryObject) object to a blob.
	 * 
	 * @param qObj
	 *            the QueryObject part of JSON UDR object.
	 * @return the binary BLOB data corresponding to the QueryObject.
	 * @throws IOException
	 *             on error
	 * @throws ClassNotFoundException
	 *             on error
	 */
	public static byte[] convertQueryObjectToBlob(QueryObject qObj)
			throws IOException, ClassNotFoundException {
		if (qObj != null) {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final GZIPOutputStream gzipOutStream = new GZIPOutputStream(bos);
			final ObjectOutputStream out = new ObjectOutputStream(gzipOutStream);
			out.writeObject(qObj);
			out.close();
			byte[] bytes = bos.toByteArray();
			return bytes;
		} else {
			return null;
		}
	}

	/**
	 * Extracts the JSON UdrSpecification object from the UdrRule domain object
	 * fetched from the DB.
	 * 
	 * @param rule
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static UdrSpecification getJsonFromUdrRule(UdrRule rule)
			throws IOException, ClassNotFoundException {
		QueryObject qObj = null;
		MetaData meta = createMetadataFromUdrRule(rule);
		if (rule.getUdrConditionObject() != null) {
			final ByteArrayInputStream bis = new ByteArrayInputStream(
					rule.getUdrConditionObject());
			final GZIPInputStream gzipInStream = new GZIPInputStream(bis);
			final ObjectInputStream in = new ObjectInputStream(gzipInStream);
			qObj = (QueryObject) in.readObject();
			in.close();
		}
		// create the UDR spec object
		UdrSpecification ret = new UdrSpecification(rule.getId(), qObj, meta);

		return ret;
	}

	/**
	 * Creates a JSON meta data object from the meta data information in a
	 * domain UDR rule object.
	 * 
	 * @param uRule
	 *            the domain UDR rule object.
	 * @return JSON meta data object (i.e., the summary item)
	 */
	private static MetaData createMetadataFromUdrRule(UdrRule uRule) {
		RuleMeta ruleMeta = uRule.getMetaData();
		String authorUserId = uRule.getAuthor().getUserId();
		final MetaData ret = new MetaData(ruleMeta.getTitle(),
				ruleMeta.getDescription(), ruleMeta.getStartDt(), authorUserId);

		ret.setEnabled(ruleMeta.getEnabled() == YesNoEnum.Y ? true : false);
		ret.setEndDate(ruleMeta.getEndDt());

		return ret;
	}

	/**
	 * Creates a domain UdrRule object from the JSON UdrSpecification object.
	 * (Note: this object can be inserted/updated into the DB.)
	 * 
	 * @param inputJson
	 * @return
	 * @throws IOException
	 */
	public static UdrRule createUdrRuleFromJson(UdrSpecification inputJson,
			User author) throws IOException {
		if (inputJson == null) {
			throw new CommonServiceException(
					CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
					String.format(
							CommonErrorConstants.NULL_ARGUMENT_ERROR_MESSAGE,
							"inputJson",
							"JsonToDomainObjectConverter.createUdrRuleFromJson()"));
		}
		
		final RuleMeta metaData = extractRuleMeta(inputJson);
		
        final UdrRule rule = createUdrRule(inputJson.getId(), metaData, 
        		createQueryObjectBlob(inputJson),
        		author);
		createEngineRules(rule, inputJson);

		return rule;

	}
    /**
     * Creates engine rules from "minterms" (i.e., sets of AND conditions)
     * @param parent the parent UDR
     * @param inputJson the JSON UDR object
     * @throws ParseException on error
     */
	public static void createEngineRules(UdrRule parent,
			UdrSpecification inputJson) {
		QueryObject qobj = inputJson.getDetails();
		List<List<QueryTerm>> ruleDataList = qobj.createFlattenedList();
		int indx = 0;
		for (List<QueryTerm> ruleData : ruleDataList) {
			parent.addEngineRule(createEngineRule(ruleData, parent, indx));
			++indx;
		}
	}
	public static List<Rule> listEngineRules(UdrRule parent,
			UdrSpecification inputJson) {
		List<Rule> ret = new LinkedList<Rule>();
		QueryObject qobj = inputJson.getDetails();
		List<List<QueryTerm>> ruleDataList = qobj.createFlattenedList();
		int indx = 0;
		for (List<QueryTerm> ruleData : ruleDataList) {
			Rule r = createEngineRule(ruleData, parent, indx);
			r.setParent(parent);
			ret.add(r);
			++indx;
		}
		return ret;
	}
   /**
    * Creates a single engine rule from a minterm.
    * @param ruleData the minterm.
    * @param parent the parent UDR rule.
    * @param indx the ordering index of the rule with respect to the parent.
    * @return the engine rule created.
    * @throws ParseException parse exception.
    */
	private static Rule createEngineRule(List<QueryTerm> ruleData, UdrRule parent,
			int indx) {
		Rule ret = new Rule(parent, indx, null);
		int seq = 0;
		for (QueryTerm trm : ruleData) {
			RuleCondPk pk = new RuleCondPk(indx, seq++);
			RuleCond cond = new RuleCond(pk, EntityLookupEnum.valueOf(trm
					.getEntity()), trm.getField(), OperatorCodeEnum.valueOf(trm
					.getOperator()));
			try{
			     cond.addValueToCondition(trm.getValues(), ValueTypesEnum.valueOf(trm.getType()));
	        } catch(ParseException pe){
	        	StringBuilder bldr = new StringBuilder("[");
	        	for(String val:trm.getValues()){
	        		bldr.append(val).append(",");
	        	}
	        	bldr.append("]");
	        	throw ErrorHandlerFactory.getErrorHandler().createException(CommonErrorConstants.INPUT_JSON_FORMAT_ERROR_CODE, 
	        			bldr.toString(), trm.getType(), "'Engine Rule Creation'");
	        }
			ret.addConditionToRule(cond);
		}
		return ret;
	}

	/**
	 * Converts a "detail" portion of the UDR JSON object to compressed binary
	 * data for saving in the database as a BLOB.
	 * 
	 * @param json
	 *            the json object to serialize.
	 * @return the binary blob object
	 */
	private static byte[] createQueryObjectBlob(UdrSpecification json) throws IOException {
		QueryObject qObj = json.getDetails();
		byte[] retBlob = null;
		if(qObj != null){
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final GZIPOutputStream gzipOutStream = new GZIPOutputStream(bos);
			final ObjectOutputStream out = new ObjectOutputStream(gzipOutStream);
			out.writeObject(qObj);
			out.close();
			retBlob = bos.toByteArray();
		}
		return retBlob;
	}

	/**
	 * Creates a UdrRule domain object.
	 * 
	 * @param id
	 *            the Id of the domain UDR Rule object.
	 * @param title
	 *            the title of the rule.
	 * @param descr
	 *            the rule description.
	 * @param enabled
	 *            enabled state of the rule.
	 * @return the UDR rule domain object.
	 */
	private static UdrRule createUdrRule(Long id, RuleMeta meta, byte[] queryObjectBlob, User author) {
		UdrRule rule = new UdrRule();
		rule.setId(id);
		rule.setDeleted(YesNoEnum.N);
		rule.setEditDt(new Date());
		rule.setAuthor(author);
		rule.setTitle(meta.getTitle());
//		RuleMeta meta = createRuleMeta(id, title, descr, startDate, endDate,
//				enabled);
		rule.setMetaData(meta);
		rule.setUdrConditionObject(queryObjectBlob);
		return rule;
	}

	/**
	 * Creates the meta data portion of the UdrRule domain object.
	 * 
	 * @param id
	 *            the Id of the domain UDR Rule object.
	 * @param title
	 *            the title of the rule.
	 * @param descr
	 *            the rule description.
	 * @param startDate
	 *            the day the rule should become active (if enabled).
	 * @param endDate
	 *            the day the rule should cease to be active (optional).
	 * @param enabled
	 *            enabled state of the rule.
	 * @return
	 */
	private static RuleMeta createRuleMeta(Long id, String title, String descr,
			Date startDate, Date endDate, YesNoEnum enabled) {
		RuleMeta meta = new RuleMeta();
		if (id != null) {
			meta.setId(id);
		}
		meta.setDescription(descr);
		meta.setEnabled(enabled);
		meta.setHitSharing(YesNoEnum.N);
		meta.setPriorityHigh(YesNoEnum.N);
		meta.setStartDt(startDate);
		meta.setEndDt(endDate);
		meta.setTitle(title);
		return meta;
	}

}
