package gov.gtas.services.udr;

import gov.gtas.error.BasicErrorHandler;
import gov.gtas.error.CommonErrorConstants;
import gov.gtas.model.User;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.RuleCond;
import gov.gtas.model.udr.RuleMeta;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.udr.YesNoEnum;
import gov.gtas.repository.udr.UdrRuleRepository;
import gov.gtas.services.UserService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 
 * @author GTAS3 (AB)
 *
 */
@Service
public class RulePersistenceServiceImpl implements RulePersistenceService {
	/*
	 * The logger for the RulePersistenceService.
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(RulePersistenceServiceImpl.class);
	
    @Resource
    private UdrRuleRepository udrRuleRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BasicErrorHandler errorHandler;
    
	@Override
	@Transactional
	public UdrRule create(UdrRule r, String userId) {
		final User user = userService.findById(userId);
		if(user == null){
			throw errorHandler.createException(CommonErrorConstants.INVALID_USER_ID_ERROR_CODE, userId);
		}
		// save meta and rule conditions for now
		//we will add them after saving the UDR rule and its child Drools rules first.
		RuleMeta savedMeta = r.getMetaData();
		Map<Integer, List<RuleCond>> ruleConditionMap = null;
		if(r.getEngineRules() != null){
			ruleConditionMap = saveEngineRuleConditions(r);
		}
		
		r.setEditDt(new Date());
		r.setAuthor(user);
		r.setEditedBy(user);
		r.setMetaData(null);
		
		//save the rule with the meta data and conditions stripped.
		//Once the rule id is generated we will add back the meta and conditions
		//and set their composite keys with the rule ID.
		UdrRule rule = udrRuleRepository.save(r);
		
		//now add back the meta and conditions and update the rule.
		if(savedMeta != null || ruleConditionMap != null){
			long ruleid = rule.getId();
			if(savedMeta != null){
				savedMeta.setId(ruleid);
				rule.setMetaData(savedMeta);
				savedMeta.setParent(rule);
			}
			if(ruleConditionMap != null){
				for(Rule engineRule: rule.getEngineRules()){
					for(RuleCond rc : ruleConditionMap.get(engineRule.getRuleIndex())) {
						rc.refreshParentRuleId(engineRule.getId());
						engineRule.addConditionToRule(rc);
					}	
				}
			}
			rule = udrRuleRepository.save(rule);
		}
		return rule;
	}
    private Map<Integer, List<RuleCond>> saveEngineRuleConditions(UdrRule udrRule){
    	Map<Integer, List<RuleCond>> ruleConditionMap = new HashMap<Integer, List<RuleCond>>();
    	for(Rule r: udrRule.getEngineRules()){
    		ruleConditionMap.put(r.getRuleIndex() ,r.getRuleConds());
		    r.removeAllConditions();
    	}
    	return ruleConditionMap;
    }
	@Override
	@Transactional
	public UdrRule delete(Long id, String userId) {
		final User user = userService.findById(userId);
		if(user == null){
			throw errorHandler.createException(CommonErrorConstants.INVALID_USER_ID_ERROR_CODE, userId);
		}
		UdrRule ruleToDelete = udrRuleRepository.findOne(id);
		if(ruleToDelete != null){
			ruleToDelete.setDeleted(YesNoEnum.Y);
			RuleMeta meta = ruleToDelete.getMetaData();
			meta.setEnabled(YesNoEnum.N);
			ruleToDelete.setEditedBy(user);
			ruleToDelete.setEditDt(new Date());
			udrRuleRepository.save(ruleToDelete);
		}else{
			logger.warn("RulePersistenceServiceImpl.delete() - object does not exist:"+id);
		}
		return ruleToDelete;
	}
	
	@Override
	@Transactional(value=TxType.SUPPORTS)
	public List<UdrRule> findAll() {
		return (List<UdrRule>)udrRuleRepository.findByDeleted(YesNoEnum.N);				
	}

	@Override
	@Transactional
	public UdrRule update(UdrRule rule, String userId) {
		final User user = userService.findById(userId);
		if(user == null){
			throw errorHandler.createException(CommonErrorConstants.INVALID_USER_ID_ERROR_CODE, userId);
		}
		Long id = verifyUdrRuleExists(rule, userId);
		if(id != null){
			rule.setId(id);
		} else {
			RuleMeta meta = rule.getMetaData();
			String title = meta!=null?meta.getTitle():"UNKNOWN";
			throw errorHandler.createException(CommonErrorConstants.UPDATE_RECORD_MISSING_ERROR_CODE, title, userId);
		}
		rule.setEditDt(new Date()); 
		rule.setEditedBy(user);
		rule.setAuthor(user);//TODO use actual author
		udrRuleRepository.save(rule);
		return rule;
	}
    private Long verifyUdrRuleExists(UdrRule rule, String userId){
    	Long id = rule.getId();
    	UdrRule fetchedRule = null;
    	if(id != null && id.longValue() > 0){
    		fetchedRule = this.findById(id);
    	}else {
    		//this rule was constructed from the UI request and id is not provided
    		RuleMeta meta = rule.getMetaData();
    		//User author = rule.getAuthor();////for now we will use the user doing the update
    		if(meta != null){
    		   fetchedRule = this.findByTitleAndAuthor(meta.getTitle(), userId);
    		}
    	}
    	if(fetchedRule != null){
    		return fetchedRule.getId();
    	} else {
    		return null;
    	}
    }
	@Override
	public UdrRule findById(Long id) {
		return udrRuleRepository.findOne(id);
	}
	/* (non-Javadoc)
	 * @see gov.gtas.services.udr.RulePersistenceService#findByTitleAndAuthor(java.lang.String, java.lang.String)
	 */
	@Override
	public UdrRule findByTitleAndAuthor(String title, String authorUserId) {
		return udrRuleRepository.getUdrRuleByTitleAndAuthor(title, authorUserId);
	}

}
