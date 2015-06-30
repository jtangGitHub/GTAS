package gov.gtas.model.udr.json.util;

import gov.gtas.error.CommonErrorConstants;
import gov.gtas.error.CommonServiceException;
import gov.gtas.model.User;
import gov.gtas.model.udr.RuleMeta;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.udr.YesNoEnum;
import gov.gtas.model.udr.json.MetaData;
import gov.gtas.model.udr.json.UdrSpecification;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Utility functions to convert JSON objects to domain objects.
 * 
 * @author GTAS3 (AB)
 *
 */
public class JsonToDomainObjectConverter {
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
		UdrSpecification ret = null;
		if (rule.getUdrConditionObject() != null) {
			final ByteArrayInputStream bis = new ByteArrayInputStream(
					rule.getUdrConditionObject());
			final GZIPInputStream gzipInStream = new GZIPInputStream(bis);
			final ObjectInputStream in = new ObjectInputStream(gzipInStream);
			ret = (UdrSpecification) in.readObject();
			in.close();
		}
		//set the id, since id cannot be set on initial create
		ret.setId(rule.getId());
		
		return ret;
	}

	/**
	 * Creates a domain UdrRule object from the JSON UdrSpecification object.
	 * (Note: this object can be inserted/updated into the DB.)
	 * @param inputJson
	 * @return
	 * @throws IOException
	 */
	public static UdrRule createUdrRuleFromJson(UdrSpecification inputJson, User author)
			throws IOException {
		if (inputJson == null) {
			throw new CommonServiceException(
					CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
					String.format(
							CommonErrorConstants.NULL_ARGUMENT_ERROR_MESSAGE,
							"inputJson",
							"JsonToDomainObjectConverter.createUdrRuleFromJson()"));
		}
		
		final MetaData metaData = inputJson.getSummary();
		JsonValidationUtils.validateMetaData(metaData);
		
		final String title = metaData.getTitle();
		final Date startDate = metaData.getStartDate();
		final String descr = metaData.getDescription();
		final boolean enabled = metaData.isEnabled();
		final Date endDate = metaData.getEndDate();
		final UdrRule rule = createUdrRule(inputJson.getId(), title, descr, startDate, endDate,
				enabled ? YesNoEnum.Y : YesNoEnum.N, author);
		
		setJsonObjectInUdrRule(rule, inputJson);

		return rule;

	}
	/**
	 * Converts a UdrSpecification JSON object to compressed binary data for
	 * saving in the database as a BLOB.
	 * 
	 * @param rule
	 *            the rule domain object.
	 * @param json
	 *            the json object to serialize.
	 */
	private static void setJsonObjectInUdrRule(UdrRule rule,
			UdrSpecification json) throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final GZIPOutputStream gzipOutStream = new GZIPOutputStream(bos);
		final ObjectOutputStream out = new ObjectOutputStream(gzipOutStream);
		out.writeObject(json);
		out.close();

		byte[] bytes = bos.toByteArray();
		rule.setUdrConditionObject(bytes);
	}

	/**
	 * Creates a UdrRule domain object.
	 * @param title
	 * @param descr
	 * @param enabled
	 * @return
	 */
	private static UdrRule createUdrRule(Long id, String title, String descr,
			Date startDate, Date endDate, YesNoEnum enabled, User author) {
		UdrRule rule = new UdrRule();
		rule.setId(id);
		rule.setDeleted(YesNoEnum.N);
		rule.setEditDt(new Date());
		rule.setAuthor(author);
		RuleMeta meta = createRuleMeta(id, title, descr, startDate, endDate,
				enabled);
		rule.setMetaData(meta);
		return rule;
	}

	/**
	 * Creates the meta data portion of the UdrRule domain object.
	 * @param title
	 * @param descr
	 * @param enabled
	 * @return
	 */
	private static RuleMeta createRuleMeta(Long id, String title, String descr,
			Date startDate, Date endDate, YesNoEnum enabled) {
		RuleMeta meta = new RuleMeta();
		meta.setId(id);
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