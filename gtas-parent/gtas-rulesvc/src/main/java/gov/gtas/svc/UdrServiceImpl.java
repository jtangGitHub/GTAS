package gov.gtas.svc;

import gov.gtas.error.BasicErrorHandler;
import gov.gtas.error.CommonErrorConstants;
import gov.gtas.model.User;
import gov.gtas.model.udr.RuleMeta;
import gov.gtas.model.udr.UdrConstants;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.udr.json.JsonServiceResponse;
import gov.gtas.model.udr.json.JsonUdrListElement;
import gov.gtas.model.udr.json.MetaData;
import gov.gtas.model.udr.json.QueryObject;
import gov.gtas.model.udr.json.UdrSpecification;
import gov.gtas.model.udr.json.util.JsonToDomainObjectConverter;
import gov.gtas.services.UserService;
import gov.gtas.services.udr.RulePersistenceService;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the UDR Service API.
 * 
 * @author GTAS3 (AB)
 *
 */
@Service
public class UdrServiceImpl implements UdrService {
	private static Logger logger = LoggerFactory
			.getLogger(UdrServiceImpl.class);

	/* The spring context supplied error handler component. */
	@Autowired
	private BasicErrorHandler errorHandler;

	@Autowired
	private RulePersistenceService rulePersistenceService;

	@Autowired
	UserService userService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.svc.UdrService#fetchUdr(java.lang.String, java.lang.String)
	 */
	@Override
	public UdrSpecification fetchUdr(String userId, String title) {
		UdrRule fetchedRule = rulePersistenceService.findByTitleAndAuthor(
				title, userId);
		if (fetchedRule == null) {
			throw errorHandler.createException(
					CommonErrorConstants.QUERY_RESULT_EMPTY_ERROR_CODE, "UDR",
					"title=" + title);
		}
		UdrSpecification jsonObject = null;
		try {
			jsonObject = JsonToDomainObjectConverter
					.getJsonFromUdrRule(fetchedRule);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage());
		}
		return jsonObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.svc.UdrService#fetchUdr(java.lang.Long)
	 */
	@Override
	public UdrSpecification fetchUdr(Long id) {
		UdrRule fetchedRule = rulePersistenceService.findById(id);
		if (fetchedRule == null) {
			throw errorHandler.createException(
					CommonErrorConstants.QUERY_RESULT_EMPTY_ERROR_CODE, "UDR",
					"id=" + id);
		}
		UdrSpecification jsonObject = null;
		try {
			jsonObject = JsonToDomainObjectConverter
					.getJsonFromUdrRule(fetchedRule);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage());
		}
		return jsonObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.svc.UdrService#fetchUdrSummaryList(java.lang.String)
	 */
	@Override
	public List<JsonUdrListElement> fetchUdrSummaryList(String userId) {
		List<UdrRule> fetchedRuleList = rulePersistenceService
				.findByAuthor(userId);
		if (fetchedRuleList == null || fetchedRuleList.isEmpty()) {
			throw errorHandler.createException(
					CommonErrorConstants.QUERY_RESULT_EMPTY_ERROR_CODE,
					"UDR List", "userId=" + userId);
		}
		List<JsonUdrListElement> ret = new LinkedList<JsonUdrListElement>();
		try {
			for (UdrRule rule : fetchedRuleList) {
				if (rule.getUdrConditionObject() != null) {
					ret.add(new JsonUdrListElement(rule.getId(),
							JsonToDomainObjectConverter
									.getJsonFromUdrRule(rule).getSummary()));
				}
			}
		} catch (ClassNotFoundException | IOException ex) {
			throw new RuntimeException("Error in getUdrList", ex);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.svc.UdrService#createUdr(gov.gtas.model.udr.json.UdrSpecification
	 * )
	 */
	@Override
	public JsonServiceResponse createUdr(String userId,
			UdrSpecification udrToCreate) {
		if (udrToCreate == null) {
			throw errorHandler.createException(
					CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
					"Create UDR", "udrToCreate");
		}
		MetaData meta = udrToCreate.getSummary();
		if (meta == null) {
			throw errorHandler.createException(
					CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
					"Create UDR", "udrToCreate.summary");
		}
		// get the author object
		String authorUserId = meta.getAuthor();
		User author = fetchRuleAuthor(userId, authorUserId);

		UdrRule ruleToSave = null;
		try {
			ruleToSave = JsonToDomainObjectConverter.createUdrRuleFromJson(
					udrToCreate, author);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new RuntimeException(ioe.getMessage());
		}

		UdrRule savedRule = rulePersistenceService.create(ruleToSave, userId);

		processRuleGeneration();// TODO placeholder

		return createResponse(true, UdrConstants.UDR_CREATE_OP_NAME, savedRule);
	}

	private User fetchRuleAuthor(final String userId, final String authorUserId) {
		String authorId = authorUserId;
		if (StringUtils.isEmpty(authorId)) {
			authorId = userId;
		}
		User user = userService.findById(authorId);
		if (user == null) {
			throw errorHandler.createException(
					CommonErrorConstants.INVALID_USER_ID_ERROR_CODE, authorId);
		}
		return user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.svc.UdrService#updateUdr(gov.gtas.model.udr.json.UdrSpecification
	 * )
	 */
	@Override
	public JsonServiceResponse updateUdr(String userId,
			UdrSpecification udrToUpdate) {
		if (udrToUpdate == null) {
			throw errorHandler.createException(
					CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE, "udrToUpdate",
					"Update UDR");
		}
		Long id = udrToUpdate.getId();
		if (id == null) {
			throw errorHandler.createException(
					CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE, "id",
					"Update UDR");
		}
		MetaData meta = udrToUpdate.getSummary();
		if (meta == null) {
			throw errorHandler.createException(
					CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE, "udrToUpdate.summary",
					"Update UDR");
		}
		// get the author object
		String authorUserId = meta.getAuthor();
		// fetch the UdrRule
		UdrRule ruleToUpdate = rulePersistenceService.findById(id);
		if (!ruleToUpdate.getAuthor().getUserId().equals(authorUserId)) {
			// TODO throw exception here
			logger.error(String
					.format("UdrServiceImpl.updateUdr() - %s trying to update rule by different author %s!",
							authorUserId, ruleToUpdate.getAuthor().getUserId()));
		}
		// update the meta data
		RuleMeta ruleMeta = JsonToDomainObjectConverter
				.extractRuleMeta(udrToUpdate);
		ruleToUpdate.setMetaData(ruleMeta);

		QueryObject queryObject = udrToUpdate.getDetails();
		if (queryObject != null) {
			try {
				final byte[] ruleBlob = JsonToDomainObjectConverter
						.convertQueryObjectToBlob(queryObject);
				ruleToUpdate.setUdrConditionObject(ruleBlob);
			} catch (IOException | ClassNotFoundException ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex.getMessage());
			}
			
			processRuleGeneration();// TODO placeholder

		}
		UdrRule updatedRule = rulePersistenceService.update(ruleToUpdate,
				userId);


		return createResponse(true, UdrConstants.UDR_UPDATE_OP_NAME, updatedRule);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.svc.UdrService#deleteUdr(java.lang.Long)
	 */
	@Override
	public JsonServiceResponse deleteUdr(String userId, Long id) {
		UdrRule deletedRule = rulePersistenceService.delete(id, userId);
		if(deletedRule != null){
			return createResponse(true, UdrConstants.UDR_DELETE_OP_NAME, deletedRule);
		}else {
			return createResponse(false, UdrConstants.UDR_DELETE_OP_NAME, deletedRule);
		}
	}

	private JsonServiceResponse createResponse(boolean success, String op, UdrRule rule){
		JsonServiceResponse resp  = null;
		if(success){
		resp = new JsonServiceResponse(JsonServiceResponse.SUCCESS_RESPONSE,
				"UDR Service", op, String.format(
						op + " on UDR Rule with title='%s' and ID='%s' was successful.",
						rule.getTitle(),
						rule.getId()));
		resp.addResponseDetails(new JsonServiceResponse.ServiceResponseDetailAttribute(UdrConstants.UDR_ID_ATTRIBUTE_NAME, String.valueOf(rule.getId())));
		resp.addResponseDetails(new JsonServiceResponse.ServiceResponseDetailAttribute(UdrConstants.UDR_TITLE_ATTRIBUTE_NAME, String.valueOf(rule.getTitle())));
		} else {
			if(rule != null){
			resp = new JsonServiceResponse(JsonServiceResponse.FAILURE_RESPONSE,
					"UDR Service", op, String.format(
							op + " on UDR Rule with title='%s' and ID='%s' failed.",
							rule.getTitle(),
							rule.getId()));
			} else {
				resp = new JsonServiceResponse(JsonServiceResponse.FAILURE_RESPONSE,
				   "UDR Service", op, op + " failed.");
				
			}
			
		}
		return resp;
		
	}
	// placeholder
	private void processRuleGeneration() {

	}
}
