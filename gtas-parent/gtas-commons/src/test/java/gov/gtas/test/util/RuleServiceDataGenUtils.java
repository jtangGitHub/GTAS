package gov.gtas.test.util;

import gov.gtas.model.Role;
import gov.gtas.model.User;
import gov.gtas.model.udr.EntityAttributeConstants;
import gov.gtas.model.udr.EntityLookupEnum;
import gov.gtas.model.udr.OperatorCodeEnum;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.RuleCond;
import gov.gtas.model.udr.RuleCondPk;
import gov.gtas.model.udr.RuleMeta;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.udr.YesNoEnum;
import gov.gtas.services.UserService;
import gov.gtas.services.udr.RulePersistenceService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class RuleServiceDataGenUtils {
	public static final String TEST_RULE_TITLE_PREFIX = "TestRule";

	public static final int TEST_ROLE1_ID = 1;
	public static final String TEST_ROLE1_DESCRIPTION = "admin";
	public static final String TEST_USER1_ID = "jpjones";

	public static final int TEST_ROLE2_ID = 99;
	public static final String TEST_ROLE2_DESCRIPTION = "readonly";
	public static final String TEST_USER2_ID = "pawnX";

	private UserService userService;
	private RulePersistenceService rulePersistenceService;

	private final Random randomGenerator;
	
	public RuleServiceDataGenUtils(UserService usrSvc,
			RulePersistenceService rpSvc) {
		this.userService = usrSvc;
		this.rulePersistenceService = rpSvc;
		randomGenerator = new Random(System.currentTimeMillis());
	}

	public void initUserData() {
		try {
			Role role = new Role();
			User user = new User();

			role.setRoleDescription(TEST_ROLE1_DESCRIPTION);
			role.setRoleId(TEST_ROLE1_ID);
			user.setFirstName("JP");
			user.setLastName("Jones");
			user.setUserId(TEST_USER1_ID);
			user.setPassword("passsword");
			user.setUserRole(role);
			List<User> roleUsers = new ArrayList<User>();
			roleUsers.add(user);
			role.setUserList(roleUsers);
			userService.create(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public UdrRule createUdrRule(String title, String descr, YesNoEnum enabled) {
		UdrRule rule = new UdrRule();
		rule.setDeleted(YesNoEnum.N);
		rule.setEditDt(new Date());
		RuleMeta meta = createRuleMeta(title, descr, enabled);
		rule.setMetaData(meta);
		return rule;
	}

	public Rule createRuleWithOneCondition(UdrRule parent, int index) {
		Rule rule = new Rule(parent, index, null);
		rule.addConditionToRule(createCondition(1, EntityLookupEnum.Pax,
				EntityAttributeConstants.PAX_ATTTR_EMBARKATION_AIRPORT_NAME,
				OperatorCodeEnum.EQUAL, "IAD"));
		return rule;
	}

	public RuleCond createCondition(int seq, EntityLookupEnum entity,
			String attr, OperatorCodeEnum opCode, Object value) {
		RuleCondPk key = new RuleCondPk(0L, seq);
		RuleCond cond = new RuleCond(key, entity, attr, opCode);
		cond.addValueToCondition("test", value);
		return cond;
	}
	public String generateTestRuleTitle(int ruleIndx){
		StringBuilder bldr = new StringBuilder(TEST_RULE_TITLE_PREFIX);
		bldr.append(ruleIndx).append('.');
		bldr.append(this.randomGenerator.nextInt());
		
		return bldr.toString();		
	}
	private RuleMeta createRuleMeta(String title, String descr,
			YesNoEnum enabled) {
		RuleMeta meta = new RuleMeta();
		meta.setDescription(descr);
		meta.setEnabled(enabled);
		meta.setHitSharing(YesNoEnum.N);
		meta.setPriorityHigh(YesNoEnum.N);
		meta.setStartDt(new Date());
		meta.setTitle(title);
		return meta;
	}
}