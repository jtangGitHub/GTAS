package gov.gtas.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.constant.UdrConstants;
import gov.gtas.enumtype.YesNoEnum;
import gov.gtas.model.udr.KnowledgeBase;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.RuleMeta;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.services.udr.RulePersistenceService;
import gov.gtas.test.util.RuleServiceDataGenUtils;
import gov.gtas.util.DateCalendarUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
/**
 * Persistence layer tests for UDR and the Rule Engine.
 * The parent domain object for UDR is UdrRule.
 * The parent domain object for the Rule Engine is KnowledgeBase.
 * @author GTAS3 (AB)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CommonServicesConfig.class)
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback = true)
public class RulePersistenceServiceIT {
    private static final String TEST_KB_NAME = "Foo Knowledge Base";
 
	@Autowired
	private RulePersistenceService testTarget;
	@Autowired
	private UserService userService;

	private RuleServiceDataGenUtils testGenUtils;
	
	@Before
	public void setUp() throws Exception {
		testGenUtils = new RuleServiceDataGenUtils(userService);
		testGenUtils.initUserData();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Transactional
	@Test()
	public void testCreateUdrRuleNoChild() {
		final String RULE_DESCRIPTION = "This is a Simple Rule";
		String testRuleTitle = testGenUtils.generateTestRuleTitle(1);
		UdrRule r = testGenUtils.createUdrRule(testRuleTitle,RULE_DESCRIPTION,
				YesNoEnum.Y);
		UdrRule rsav = testTarget.create(r, RuleServiceDataGenUtils.TEST_USER1_ID);
		assertNotNull(rsav);
		long id = rsav.getId();
		assertTrue(id > 0);
		RuleMeta meta = rsav.getMetaData();
		assertNotNull(meta);

		// read the rule back
		UdrRule readRule = testTarget.findById(rsav.getId());
		assertNotNull(readRule);
		assertNotNull(readRule.getMetaData());
		assertEquals(meta, readRule.getMetaData());
	}
	
	@Transactional
	@Test()
	public void testFetchUdrRuleByTitleAndAuthor() {
		final String RULE_DESCRIPTION = "This is a Simple Rule";
		String testRuleTitle = testGenUtils.generateTestRuleTitle(2);
		UdrRule r = testGenUtils.createUdrRule(testRuleTitle, RULE_DESCRIPTION,
				YesNoEnum.Y);
		UdrRule rsav = testTarget.create(r, RuleServiceDataGenUtils.TEST_USER1_ID);
		assertNotNull(rsav);
		long id = rsav.getId();
		assertTrue(id > 0);
		RuleMeta meta = rsav.getMetaData();
		assertNotNull(meta);

		// read the rule back
		UdrRule readRule = testTarget.findByTitleAndAuthor(testRuleTitle, RuleServiceDataGenUtils.TEST_USER1_ID);
		assertNotNull(String.format("Could not get Rule with title='%s', author ='%s'", testRuleTitle, RuleServiceDataGenUtils.TEST_USER1_ID), readRule);
		assertNotNull(readRule.getMetaData());
		assertEquals(meta, readRule.getMetaData());
	}
	/**
	 * The update pattern tested here is:
	 * 1. Fetch a UdrRule from the persistence service.
	 * 2. Modify some of UdrRule attributes.
	 * 3. save the modified UdrRule back.
	 * 4. verify that the version has been incremented.
	 */
	@Transactional
	@Test()
	public void testUpdateUdrRuleMetaData() throws Exception{
		final String RULE_DESCRIPTION = "This is a Simple Rule";
		String testRuleTitle = testGenUtils.generateTestRuleTitle(3);
		UdrRule r = testGenUtils.createUdrRule(testRuleTitle, RULE_DESCRIPTION,
				YesNoEnum.Y);
		r = testTarget.create(r, RuleServiceDataGenUtils.TEST_USER1_ID);
		UdrRule rsav = testTarget.findById(r.getId());
		assertNotNull(rsav);
		long id = rsav.getId();
		assertTrue(id > 0);
		RuleMeta meta = rsav.getMetaData();
		assertNotNull(meta);

		// modify meta and update
		meta.setDescription("This is a Simple Rule - Updated");
		meta.setEndDt(DateCalendarUtils.parseJsonDate("2015-12-31"));
		testTarget.update(rsav, RuleServiceDataGenUtils.TEST_USER1_ID);

		// read the rule back
		UdrRule readRule = testTarget.findById(rsav.getId());
		assertNotNull(readRule);

		assertNotNull(readRule.getMetaData());
		assertEquals(meta, readRule.getMetaData());
	}


	@Transactional
	@Test()
	public void testCreateRuleWithOneEngineRule() {
		final String CRITERION1 = "1 criterion";
		final String CRITERION2 = "2 criterion";
		final String TEST_DRL = "JUST TEST DRL";
		
		final String RULE_DESCRIPTION = "This is a Rule with conditions";
		String testRuleTitle = testGenUtils.generateTestRuleTitle(5);
		UdrRule r = testGenUtils.createUdrRule(testRuleTitle, RULE_DESCRIPTION,
				YesNoEnum.Y);
		Rule engineRule = new Rule(r, 1, null);
        engineRule.setRuleDrl(TEST_DRL);
        engineRule.addRuleCriteria(Arrays.asList(new String[]{CRITERION1, CRITERION2}));
		r.addEngineRule(engineRule);
		UdrRule rsav = testTarget.create(r, RuleServiceDataGenUtils.TEST_USER1_ID);
		assertNotNull(rsav);
		long id = rsav.getId();
		assertTrue(id > 0);
		RuleMeta meta = rsav.getMetaData();
		assertNotNull(meta);
		List<Rule> rules = rsav.getEngineRules();
		assertNotNull(rules);
		assertEquals(1, rules.size());
		Rule rule = rules.get(0);
		assertTrue(rule.getId() > 0);
		String[] criteriaDescriptions = rule.getRuleCriteria();
		assertNotNull(criteriaDescriptions);
		assertEquals("Expected two criteria", 2, criteriaDescriptions.length);
		assertEquals("Expected criteria match", CRITERION1, criteriaDescriptions[0]);
		assertEquals("Expected criteria match", CRITERION2, criteriaDescriptions[1]);
		assertEquals("Expected DRL Match", TEST_DRL, rule.getRuleDrl());
		

		// read the rule back
		UdrRule readRule = testTarget.findById(rsav.getId());
		assertNotNull(readRule);
		assertNotNull(readRule.getMetaData());
		assertEquals(meta, readRule.getMetaData());
		rules = readRule.getEngineRules();
		rule = rules.get(0);
		criteriaDescriptions = rule.getRuleCriteria();
		assertNotNull(criteriaDescriptions);
		assertEquals("Expected two criteria", 2, criteriaDescriptions.length);
		assertEquals("Expected criteria match", CRITERION1, criteriaDescriptions[0]);
		assertEquals("Expected criteria match", CRITERION2, criteriaDescriptions[1]);
		assertEquals("Expected DRL Match", TEST_DRL, rule.getRuleDrl());
	}

	@Transactional
	@Test()
	public void testDeleteRule() {
		final String RULE_DESCRIPTION = "This is a Simple Rule";
		String testRuleTitle = testGenUtils.generateTestRuleTitle(6);
		UdrRule r = testGenUtils.createUdrRule(testRuleTitle, RULE_DESCRIPTION,
				YesNoEnum.Y);
		UdrRule rsav = testTarget.create(r, RuleServiceDataGenUtils.TEST_USER1_ID);

		assertNotNull(rsav);
		long id = rsav.getId();
		assertTrue(id > 0);
		UdrRule deletedRule = testTarget.delete(id,
				RuleServiceDataGenUtils.TEST_USER1_ID);
		assertEquals(YesNoEnum.Y, deletedRule.getDeleted());

		//read the rule back and make sure it is disabled
		UdrRule readRule = testTarget.findById(rsav.getId());
		assertEquals(YesNoEnum.Y, readRule.getDeleted());
		assertEquals(YesNoEnum.N, readRule.getMetaData().getEnabled());
		
		// read back all the rules and make sure that this rule is not present
		List<UdrRule> allRules = testTarget.findAll();
		for (UdrRule rl : allRules) {
			if (rl.getId() == id) {
				fail("deleted rule is fetched by RulePersistenceService.findAll()");
			}
		}
	}
	@Transactional
	@Test()
	public void testCreateRetrieveKnowledgeBase() throws Exception{
		final String RULE_TEXT = "rule \"foo\"\nwhen then end";
		final String KB_TEXT = "jkhlkj$$ && *(&)(*&)";
		KnowledgeBase kb = new KnowledgeBase(TEST_KB_NAME);
		kb.setCreationDt(new Date());
		kb.setKbBlob(KB_TEXT.getBytes(UdrConstants.UDR_EXTERNAL_CHARACTER_ENCODING));
		kb.setRulesBlob(RULE_TEXT.getBytes(UdrConstants.UDR_EXTERNAL_CHARACTER_ENCODING));
		testTarget.saveKnowledgeBase(kb);
		KnowledgeBase readKb = testTarget.findUdrKnowledgeBase(TEST_KB_NAME);

		assertNotNull(readKb);
		long id = readKb.getId();
		assertTrue(id > 0);
		String kbString = new String(readKb.getKbBlob(), UdrConstants.UDR_EXTERNAL_CHARACTER_ENCODING);
		assertEquals(KB_TEXT, kbString);
		String rlString = new String(readKb.getRulesBlob(), UdrConstants.UDR_EXTERNAL_CHARACTER_ENCODING);
        assertEquals(RULE_TEXT, rlString);
        
        assertNotNull(readKb.getVersion());       
	}
	@Transactional
	@Test()
	public void testUpdateKnowledgeBase() throws Exception{
		final String RULE_TEXT = "rule \"foo\"\nwhen then end";
		final String KB_TEXT = "jkhlkj$$ && *(&)(*&)";
		final String UPDATED_KB_TEXT = "jkh666633339999lkj$$ && *(&)(*&)";
		KnowledgeBase kb = new KnowledgeBase(TEST_KB_NAME);
		kb.setCreationDt(new Date());
		kb.setKbBlob(KB_TEXT.getBytes(UdrConstants.UDR_EXTERNAL_CHARACTER_ENCODING));
		kb.setRulesBlob(RULE_TEXT.getBytes(UdrConstants.UDR_EXTERNAL_CHARACTER_ENCODING));
		testTarget.saveKnowledgeBase(kb);
		KnowledgeBase readKb = testTarget.findUdrKnowledgeBase(TEST_KB_NAME);
		long preVersion = readKb.getVersion();
		//update
        readKb.setKbBlob(UPDATED_KB_TEXT.getBytes(UdrConstants.UDR_EXTERNAL_CHARACTER_ENCODING));
        testTarget.saveKnowledgeBase(readKb);
        
        readKb = testTarget.findUdrKnowledgeBase(TEST_KB_NAME);
		assertNotNull(readKb);
		long id = readKb.getId();
		assertTrue(id > 0);
		String kbString = new String(readKb.getKbBlob(), UdrConstants.UDR_EXTERNAL_CHARACTER_ENCODING);
		System.out.println(kbString);
		assertEquals(UPDATED_KB_TEXT, kbString);
		String rlString = new String(readKb.getRulesBlob(), UdrConstants.UDR_EXTERNAL_CHARACTER_ENCODING);
        assertEquals(RULE_TEXT, rlString);
        
        assertNotNull(readKb.getVersion());       
		assertEquals(preVersion+1L, readKb.getVersion());
	}
}
