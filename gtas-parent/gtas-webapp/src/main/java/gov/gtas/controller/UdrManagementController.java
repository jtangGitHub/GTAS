package gov.gtas.controller;

import static gov.gtas.constant.GtasSecurityConstants.UNAUTHORIZED_ERROR_CODE;
import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.constants.Constants;
import gov.gtas.enumtype.Status;
import gov.gtas.error.CommonServiceException;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.udr.json.JsonUdrListElement;
import gov.gtas.model.udr.json.MetaData;
import gov.gtas.model.udr.json.UdrSpecification;
import gov.gtas.model.udr.json.util.UdrSpecificationBuilder;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.svc.RuleManagementService;
import gov.gtas.svc.UdrService;
import gov.gtas.util.DateCalendarUtils;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * The REST service end-point controller for creating and managing user Defined
 * Rules (UDR) for targeting.
 * 
 * @author GTAS3 (AB)
 *
 */
@RestController
public class UdrManagementController {
	/*
	 * The logger for the UdrManagementController
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(UdrManagementController.class);

	@Autowired
	private UdrService udrService;

	@Autowired
	private RuleManagementService ruleManagementService;

	@RequestMapping(value = Constants.UDR_GET_BY_AUTHOR_TITLE, method = RequestMethod.GET)
	public JsonServiceResponse getUDR(@PathVariable String authorId,
			@PathVariable String title) {
		logger.debug("******** user =" + authorId + ", title=" + title);
		UdrSpecification resp = udrService.fetchUdr(authorId, title);
		return new JsonServiceResponse(Status.SUCCESS,
				"GET UDR was successful", resp);
	}

	@RequestMapping(value = Constants.UDR_GET_BY_ID, method = RequestMethod.GET)
	public JsonServiceResponse getUDRById(@PathVariable Long id) {
		logger.debug("******** Received GET UDR request for id=" + id);
		UdrSpecification resp = udrService.fetchUdr(id);
		return new JsonServiceResponse(Status.SUCCESS,
				"GET UDR by ID was successful", resp);
	}

	@RequestMapping(value = Constants.UDR_GETALL_BY_USER, method = RequestMethod.GET)
	public JsonServiceResponse getUDRList() {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		logger.debug("******** user =" + userId);
		List<JsonUdrListElement> resp = udrService.fetchUdrSummaryList(userId);
		return new JsonServiceResponse(Status.SUCCESS,
				"GET all UDR By author was successful", resp);
	}

	@RequestMapping(value = Constants.UDR_GETALL, method = RequestMethod.GET)
	public JsonServiceResponse getAllUDRList() {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		logger.debug("******** user =" + userId);
		List<JsonUdrListElement> resp = udrService.fetchUdrSummaryList();
		return new JsonServiceResponse(Status.SUCCESS,
				"GET all UDR was successful", resp);
	}

	@RequestMapping(value = Constants.UDR_GETDRL, method = RequestMethod.GET)
	public JsonServiceResponse getDrl() {
		String rules = ruleManagementService
				.fetchDefaultDrlRulesFromKnowledgeBase();
		return createDrlRulesResponse(rules);
	}

	@RequestMapping(value = Constants.UDR_GETDRL_BY_NAME, method = RequestMethod.GET)
	public JsonServiceResponse getDrlByName(@PathVariable String kbName) {
		String rules = ruleManagementService
				.fetchDrlRulesFromKnowledgeBase(kbName);
		return createDrlRulesResponse(rules);
	}

	/**
	 * Creates the DRL rule response JSON object.
	 * 
	 * @param rules
	 *            the DRL rules.
	 * @return the JSON response object containing the rules.
	 */
	private JsonServiceResponse createDrlRulesResponse(String rules) {
		logger.debug("******* The rules:\n" + rules + "\n***************\n");
		JsonServiceResponse resp = new JsonServiceResponse(Status.SUCCESS,
				"Drools rules fetched successfully");
		String[] lines = rules.split("\n");
		resp.addResponseDetails(new JsonServiceResponse.ServiceResponseDetailAttribute(
				"DRL Rules", lines));
		return resp;
	}

	@RequestMapping(value = Constants.UDR_POST, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse createUDR(@RequestBody UdrSpecification inputSpec) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		logger.debug("******** Received UDR Create request by user =" + userId);
		if (inputSpec == null) {
			throw new CommonServiceException(
					CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
					String.format(
							CommonErrorConstants.NULL_ARGUMENT_ERROR_MESSAGE,
							"Create Query For Rule", "inputSpec"));
		}
		/*
		 * The Jackson JSON parser assumes that the time zone is GMT if no
		 * offset is explicitly indicated. Thus "2015-07-10" is interpreted as
		 * "2015-07-10T00:00:00" GMT or "2015-07-09T20:00:00" EDT, i.e., the
		 * previous day. The following 3 lines of code reverses this
		 * interpretation.
		 */
		MetaData meta = inputSpec.getSummary();
		if (meta != null) {
			meta.setStartDate(fixMetaDataDates(meta.getStartDate()));
			meta.setEndDate(fixMetaDataDates(meta.getEndDate()));
		}

		JsonServiceResponse resp = udrService.createUdr(userId, inputSpec);

		return resp;
	}

	@RequestMapping(value = Constants.UDR_COPY, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse copyUDR(@PathVariable Long id) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		logger.debug("******** Received UDR Create request by user =" + userId);
		if (id == null) {
			throw new CommonServiceException(
					CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
					String.format(
							CommonErrorConstants.NULL_ARGUMENT_ERROR_MESSAGE,
							"Copy UDR", "id"));
		}
		JsonServiceResponse resp = udrService.copyUdr(userId, id);

		return resp;
	}
	/**
	 * Subtracts the offset to reverse the interpretation at GMT time.
	 * 
	 * @param inputUdrdate
	 *            the date to "fix"
	 * @return the fixed date.
	 */
	private Date fixMetaDataDates(Date inputUdrdate) {
		if (inputUdrdate != null) {
			long offset = DateCalendarUtils
					.calculateOffsetFromGMT(inputUdrdate);
			return new Date(inputUdrdate.getTime() - offset);
		} else {
			return null;
		}

	}

	@RequestMapping(value = Constants.UDR_PUT, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse updateUDR(@PathVariable Long id,
			@RequestBody UdrSpecification inputSpec) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		logger.debug("******** Received UDR Update request by user =" + userId);

		/*
		 * The Jackson JSON parser assumes that the time zone is GMT if no
		 * offset is explicitly indicated. Thus "2015-07-10" is interpreted as
		 * "2015-07-10T00:00:00" GMT or "2015-07-09T20:00:00" EDT, i.e., the
		 * previous day. The following 3 lines of code reverses this
		 * interpretation.
		 */
		MetaData meta = inputSpec.getSummary();
		if (meta != null) {
			meta.setStartDate(fixMetaDataDates(meta.getStartDate()));
			meta.setEndDate(fixMetaDataDates(meta.getEndDate()));
		}

		JsonServiceResponse resp = udrService.updateUdr(userId, inputSpec);

		return resp;
	}

	@RequestMapping(value = Constants.UDR_DELETE, method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse deleteUDR(@PathVariable Long id) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		logger.debug("******** Received UDR Delete request by user =" + userId
				+ " for " + id);
		JsonServiceResponse resp = udrService.deleteUdr(userId, id);

		return resp;
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = Constants.UDR_TEST, method = RequestMethod.GET)
	public UdrSpecification getUDR() {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		if (StringUtils.isEmpty(userId) || !userId.equals("gtas")) {
			CommonServiceException ex = ErrorHandlerFactory.getErrorHandler()
					.createException(UNAUTHORIZED_ERROR_CODE, userId,
							"Generate Test Operation");
			ex.setLogable(true);
			throw ex;
		}
		UdrSpecification resp = UdrSpecificationBuilder.createSampleSpec();
		return resp;
	}

}
