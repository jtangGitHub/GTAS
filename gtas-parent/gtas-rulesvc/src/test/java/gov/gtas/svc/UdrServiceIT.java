package gov.gtas.svc;

import static gov.gtas.constant.AuditLogConstants.UDR_LOG_TARGET_PREFIX;
import static gov.gtas.constant.AuditLogConstants.UDR_LOG_TARGET_SUFFIX;
import static gov.gtas.constant.DomainModelConstants.UDR_UNIQUE_CONSTRAINT_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.gtas.config.RuleServiceConfig;
import gov.gtas.constant.RuleConstants;
import gov.gtas.enumtype.AuditActionType;
import gov.gtas.enumtype.ConditionEnum;
import gov.gtas.enumtype.Status;
import gov.gtas.error.ErrorUtils;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.AuditRecord;
import gov.gtas.model.Role;
import gov.gtas.model.User;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.udr.json.JsonUdrListElement;
import gov.gtas.model.udr.json.MetaData;
import gov.gtas.model.udr.json.QueryEntity;
import gov.gtas.model.udr.json.QueryObject;
import gov.gtas.model.udr.json.UdrSpecification;
import gov.gtas.model.udr.json.util.JsonToDomainObjectConverter;
import gov.gtas.model.udr.json.util.UdrSpecificationBuilder;
import gov.gtas.services.AuditLogPersistenceService;
import gov.gtas.services.security.RoleData;
import gov.gtas.services.security.UserData;
import gov.gtas.services.security.UserService;
import gov.gtas.services.security.UserServiceUtil;
import gov.gtas.services.udr.RulePersistenceService;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration tests for the UDR management service.
 * 
 * @author GTAS3 (AB)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RuleServiceConfig.class)
@TransactionConfiguration(defaultRollback = true)
public class UdrServiceIT {
	private static final String RULE_TITLE1 = "Hello Rule 1";
	private static final String RULE_DESCRIPTION1 = "This is a test";
	private static final String RULE_TITLE2 = "Hello Rule 2";
	private static final String RULE_DESCRIPTION2 = "This is a test2";
	
	private static final String USERID_1 = "phenry";
	private static final String USER1_FIRST_NAME = "Patrick";
	private static final String USER1_LAST_NAME = "Henry";

	private static final String USERID_2 = "jpjones";
	private static final String USER2_FIRST_NAME = "John";
	private static final String USER2_LAST_NAME = "Jones";

	@Autowired
	UdrService udrService;

	@Autowired
	RulePersistenceService ruleService;

	@Autowired
	UserService userService;

	@Autowired
	UserServiceUtil userServiceUtil;
	
	@Autowired
	private AuditLogPersistenceService auditLogPersistenceService;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Transactional
	public void testCreateUdr() {
		User user = createUser();
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(user.getUserId(), RULE_TITLE1,
				RULE_DESCRIPTION1);
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec);
		assertEquals(Status.SUCCESS, resp.getStatus());
	}

	@Test
	@Transactional
	public void testCreateDuplicateUdr() {
		User user = createUser();
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(user.getUserId(), RULE_TITLE1,
				RULE_DESCRIPTION1);
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec);
		assertEquals(Status.SUCCESS, resp.getStatus());
		try {
			udrService.createUdr(user.getUserId(), spec);
			fail("Expecting Exception");
		} catch (JpaSystemException jse) {
			assertTrue(ErrorUtils.isConstraintViolationException(jse, UDR_UNIQUE_CONSTRAINT_NAME));
		}
	}

	@Test
	@Transactional
	public void testCreateUdrWithSingleConditionAND() {
		User user = createUser();
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(user.getUserId(), RULE_TITLE1,
				RULE_DESCRIPTION1);
		QueryObject details = spec.getDetails();
		details.setCondition(ConditionEnum.AND.toString());
		List<QueryEntity> terms = details.getRules();
		List<QueryEntity> newterms = new LinkedList<QueryEntity>();
		newterms.add(terms.get(0));
		details.setRules(newterms);
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec);
		assertEquals(Status.SUCCESS, resp.getStatus());
		Long id = Long.valueOf(resp.findResponseDetailValue(RuleConstants.UDR_ID_ATTRIBUTE_NAME));
		assertNotNull("The saved ID is null", id);
		UdrRule rule = ruleService.findById(id);
		assertNotNull(rule);
		List<Rule> engineRules = rule.getEngineRules();
		assertNotNull(engineRules);
		assertEquals(1, engineRules.size());
		String drl = engineRules.get(0).getRuleDrl();
		assertFalse(StringUtils.isEmpty(drl));
		String[] criteria = engineRules.get(0).getRuleCriteria();
		assertNotNull(criteria);
		assertEquals(1, criteria.length);
	}

	@Test
	@Transactional
	public void testCreateUdrWithSingleConditionOR() {
		User user = createUser();
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(user.getUserId(), RULE_TITLE1,
				RULE_DESCRIPTION1);
		QueryObject details = spec.getDetails();
		details.setCondition(ConditionEnum.OR.toString());
		List<QueryEntity> terms = details.getRules();
		List<QueryEntity> newterms = new LinkedList<QueryEntity>();
		newterms.add(terms.get(0));
		details.setRules(newterms);
		
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec);
		
		assertEquals(Status.SUCCESS, resp.getStatus());
		Long id = Long.valueOf(resp.findResponseDetailValue(RuleConstants.UDR_ID_ATTRIBUTE_NAME));
		assertNotNull("The saved ID is null", id);
		UdrRule rule = ruleService.findById(id);
		assertNotNull(rule);
		List<Rule> engineRules = rule.getEngineRules();
		assertNotNull(engineRules);
		assertEquals(1, engineRules.size());
		String drl = engineRules.get(0).getRuleDrl();
		assertFalse(StringUtils.isEmpty(drl));
		String[] criteria = engineRules.get(0).getRuleCriteria();
		assertNotNull(criteria);
		assertEquals(1, criteria.length);
		assertNotNull("Engine Rule has a null Knowledge Base reference", engineRules.get(0).getKnowledgeBase());
		
		//verify the audit log
		verifyAuditLog(AuditActionType.CREATE_UDR, RULE_TITLE1, spec, false);

	}

	@Test
	@Transactional
	public void testFetchSummaryList() {
		User user = createUser();
		UdrSpecification spec1 = UdrSpecificationBuilder.createSampleSpec(user.getUserId(), RULE_TITLE1,
				RULE_DESCRIPTION1);
		UdrSpecification spec2 = UdrSpecificationBuilder.createSampleSpec(user.getUserId(), RULE_TITLE2,
				RULE_DESCRIPTION1);
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec1);
		assertEquals(Status.SUCCESS, resp.getStatus());
		resp = udrService.createUdr(user.getUserId(), spec2);
		assertEquals(Status.SUCCESS, resp.getStatus());
		List<JsonUdrListElement> listResp = udrService.fetchUdrSummaryList(user.getUserId());
		assertNotNull(listResp);
		assertEquals(2, listResp.size());
	}

	@Test
	@Transactional
	public void testFetchUdrById() {
		try {
			User user = createUser();
			UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(user.getUserId(), RULE_TITLE1,
					RULE_DESCRIPTION1);
			JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec);
			assertEquals(Status.SUCCESS, resp.getStatus());
			assertNotNull(resp.getResponseDetails());
			Long id = Long.valueOf((String) (resp.getResponseDetails().get(0).getAttributeValue()));
			assertNotNull(id);
			UdrSpecification specFetched = udrService.fetchUdr(id);
			assertNotNull(specFetched);
			assertEquals(spec.getSummary().getTitle(), specFetched.getSummary().getTitle());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFetchUdrByAuthorTitle() {
		User user = createUser();
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(user.getUserId(), RULE_TITLE1,
				RULE_DESCRIPTION1);
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec);
		assertEquals(Status.SUCCESS, resp.getStatus());
		assertNotNull(resp.getResponseDetails());
		String title = (String) resp.getResponseDetails().get(1).getAttributeValue();
		assertEquals(RULE_TITLE1, title);
		UdrSpecification specFetched = udrService.fetchUdr(user.getUserId(), title);
		assertNotNull(specFetched);
		assertEquals(spec.getSummary().getTitle(), specFetched.getSummary().getTitle());
	}

	@Test
	@Transactional
	public void testCreateAndUpdateMetaOnly() throws Exception {
		User user = createUser();
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(user.getUserId(), RULE_TITLE1,
				RULE_DESCRIPTION1);

		// create Udr Rule
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec);
		assertEquals(Status.SUCCESS, resp.getStatus());
		assertNotNull(resp.getResponseDetails());
		String title = (String) resp.getResponseDetails().get(1).getAttributeValue();
		assertEquals(RULE_TITLE1, title);
		Long id = Long.valueOf(resp.findResponseDetailValue(RuleConstants.UDR_ID_ATTRIBUTE_NAME));
		assertNotNull("The saved ID is null", id);
		UdrRule rule = ruleService.findById(id);
		List<Rule> engineRules = rule.getEngineRules();
		assertNotNull(engineRules);
		assertEquals(3, engineRules.size());
		String drl = engineRules.get(0).getRuleDrl();
		assertFalse(StringUtils.isEmpty(drl));
		String[] criteria = engineRules.get(0).getRuleCriteria();
		assertNotNull(criteria);
		assertEquals(1, criteria.length);
		assertNotNull("Engine Rule has a null Knowledge Base reference", engineRules.get(0).getKnowledgeBase());

		// Extract the UDR
		UdrSpecification specFetched = JsonToDomainObjectConverter.getJsonFromUdrRule(rule);
		assertNotNull(specFetched);
		specFetched.getSummary().setDescription(RULE_DESCRIPTION2);
		specFetched.setDetails(null);
		
		udrService.updateUdr(user.getUserId(), specFetched);
		
		specFetched = udrService.fetchUdr(user.getUserId(), title);
		assertNotNull(specFetched);
		assertEquals(RULE_DESCRIPTION2, specFetched.getSummary().getDescription());
		
		//verify the audit log
		verifyAuditLog(AuditActionType.UPDATE_UDR_META, RULE_TITLE1, specFetched, true);		
	}
	@Test
	@Transactional
	public void testUpdateAll() {
		User user = createUser();
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(user.getUserId(), RULE_TITLE1,
				RULE_DESCRIPTION1);
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec);
		assertEquals(Status.SUCCESS, resp.getStatus());
		assertNotNull(resp.getResponseDetails());
		String title = (String) resp.getResponseDetails().get(1).getAttributeValue();
		assertEquals(RULE_TITLE1, title);
		UdrSpecification specFetched = udrService.fetchUdr(user.getUserId(), title);
		UdrSpecification updatedSpec = UdrSpecificationBuilder.createSampleSpec2(user.getUserId(), RULE_TITLE1,
				RULE_DESCRIPTION2);
		updatedSpec.setId(specFetched.getId());
		
		udrService.updateUdr(user.getUserId(), updatedSpec);
		
		specFetched = udrService.fetchUdr(user.getUserId(), title);
		assertNotNull(specFetched);
		assertEquals(RULE_DESCRIPTION2, specFetched.getSummary().getDescription());
		
		//verify the audit log
		verifyAuditLog(AuditActionType.UPDATE_UDR, RULE_TITLE1, updatedSpec, false);

	}

	@Test
	@Transactional
	public void testUpdateByNonAuthor() {
		User user = createUser();
		User user2 = createUser(USERID_2, USER2_FIRST_NAME, USER2_LAST_NAME);
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(user.getUserId(), RULE_TITLE1,
				RULE_DESCRIPTION1);
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec);
		assertEquals(Status.SUCCESS, resp.getStatus());
		assertNotNull(resp.getResponseDetails());
		String title = (String) resp.getResponseDetails().get(1).getAttributeValue();
		assertEquals(RULE_TITLE1, title);
		UdrSpecification specFetched = udrService.fetchUdr(user.getUserId(), title);
		UdrSpecification updatedSpec = UdrSpecificationBuilder.createSampleSpec2(user.getUserId(), RULE_TITLE1,
				RULE_DESCRIPTION2);
		updatedSpec.setId(specFetched.getId());
		udrService.updateUdr(user2.getUserId(), updatedSpec);
		specFetched = udrService.fetchUdr(user.getUserId(), title);
		assertNotNull(specFetched);
		assertEquals(RULE_DESCRIPTION2, specFetched.getSummary().getDescription());
		assertEquals(user.getUserId(), specFetched.getSummary().getAuthor());
	}

	@Test
	@Transactional
	public void testDelete() {
		User user = createUser();
		UdrSpecification spec1 = UdrSpecificationBuilder.createSampleSpec(user.getUserId(), RULE_TITLE1,
				RULE_DESCRIPTION1);
		UdrSpecification spec2 = UdrSpecificationBuilder.createSampleSpec(user.getUserId(), RULE_TITLE2,
				RULE_DESCRIPTION1);
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec1);
		assertEquals(Status.SUCCESS, resp.getStatus());
		Long id = Long.valueOf((String) (resp.getResponseDetails().get(0).getAttributeValue()));

		resp = udrService.createUdr(user.getUserId(), spec2);
		assertEquals(Status.SUCCESS, resp.getStatus());
		List<JsonUdrListElement> listResp = udrService.fetchUdrSummaryList(user.getUserId());
		assertNotNull(listResp);
		assertEquals(2, listResp.size());

		udrService.deleteUdr(user.getUserId(), id);

		listResp = udrService.fetchUdrSummaryList(user.getUserId());
		assertNotNull(listResp);
		assertEquals(1, listResp.size());
	}

	@Test
	@Transactional
	public void testDeleteAndRecreate() {
		User user = createUser();
		UdrSpecification spec1 = UdrSpecificationBuilder.createSampleSpec(user.getUserId(), RULE_TITLE1,
				RULE_DESCRIPTION1);
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec1);
		assertEquals(Status.SUCCESS, resp.getStatus());
		Long id = Long.valueOf((String) (resp.getResponseDetails().get(0).getAttributeValue()));

		List<JsonUdrListElement> listResp = udrService.fetchUdrSummaryList(user.getUserId());
		assertNotNull(listResp);
		assertEquals(1, listResp.size());

		udrService.deleteUdr(user.getUserId(), id);

		listResp = udrService.fetchUdrSummaryList(user.getUserId());
		assertNotNull(listResp);
		assertEquals(0, listResp.size());

		resp = udrService.createUdr(user.getUserId(), spec1);
		assertEquals(Status.SUCCESS, resp.getStatus());
		Long id2 = Long.valueOf((String) (resp.getResponseDetails().get(0).getAttributeValue()));
		assertTrue(id.longValue() != id2.longValue());

		listResp = udrService.fetchUdrSummaryList(user.getUserId());
		assertNotNull(listResp);
		assertEquals(1, listResp.size());
	}

	@Test
	@Transactional
	public void testKnowledgeBaseReferences() {
		User user = createUser(USERID_1, USER1_FIRST_NAME, USER1_LAST_NAME);
		UdrSpecification spec1 = UdrSpecificationBuilder.createSampleSpec(user.getUserId(), RULE_TITLE1,
				RULE_DESCRIPTION1);
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec1);
		assertEquals(Status.SUCCESS, resp.getStatus());
		Long id = Long.valueOf((String) (resp.getResponseDetails().get(0).getAttributeValue()));
		UdrRule rule = ruleService.findById(id);
		assertNotNull("Could not fetch saved UDR", rule);
		assertEquals(3, rule.getEngineRules().size());
		assertNotNull(rule.getEngineRules().get(0).getKnowledgeBase());
		assertNotNull(rule.getEngineRules().get(1).getKnowledgeBase());
		assertNotNull(rule.getEngineRules().get(2).getKnowledgeBase());

		// now delete the UDR
		ruleService.delete(id, user.getUserId());
		rule = ruleService.findById(id);
		assertEquals(3, rule.getEngineRules().size());
		assertNull(rule.getEngineRules().get(0).getKnowledgeBase());
		assertNull(rule.getEngineRules().get(1).getKnowledgeBase());
		assertNull(rule.getEngineRules().get(2).getKnowledgeBase());
	}
	private User createUser() {
		return createUser(USERID_1, USER1_FIRST_NAME, USER1_LAST_NAME);
	}
	private User createUser(String userId, String firstName, String lastName) {
		String ROLE_NAME = "user";
		String USER_FNAME = firstName;
		String USER_LASTNAME = lastName;
		String USER_ID = userId;
		Set<RoleData> roles = new HashSet<RoleData>();
		roles.add(new RoleData(1, "ADMIN"));

		UserData usr = new UserData(USER_ID, "password", USER_FNAME, USER_LASTNAME, 1, roles);
		Role role = new Role();
		role.setRoleDescription(ROLE_NAME);

		User user = userServiceUtil.mapUserEntityFromUserData(userService.create(usr));

		return user;
	}
	
    private void verifyAuditLog(AuditActionType type, String title, UdrSpecification spec, boolean metaOnly){
    	List<AuditRecord> logList = auditLogPersistenceService.findByTarget(UDR_LOG_TARGET_PREFIX + title + UDR_LOG_TARGET_SUFFIX);
    	AuditRecord log = null;
    	assertTrue(logList.size() >= 1);
    	for(AuditRecord r:logList){
    		if(r.getActionType() == type){
    			log = r;
    			break;
    		}
    	}
    	assertNotNull(log);
    	ObjectMapper mapper = new ObjectMapper();
    	try{
    	   if(metaOnly){
    		   MetaData meta = mapper.readValue(log.getActionData(), MetaData.class);
    		   assertEquals(spec.getSummary().getDescription(), meta.getDescription());
    	   } else {
    		   UdrSpecification dataSpec=  mapper.readValue(log.getActionData(), UdrSpecification.class);
    		   assertEquals(spec.getSummary().getDescription(), dataSpec.getSummary().getDescription());    		   
    	   }
    	} catch(Exception ex){
    		ex.printStackTrace();
    		fail("Not Expecting Exception");
    	}
    	
    }

}
