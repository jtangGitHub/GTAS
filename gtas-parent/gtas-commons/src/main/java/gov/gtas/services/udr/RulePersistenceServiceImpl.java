package gov.gtas.services.udr;

import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.constant.RuleConstants;
import gov.gtas.enumtype.YesNoEnum;
import gov.gtas.error.ErrorHandler;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.BaseEntity;
import gov.gtas.model.User;
import gov.gtas.model.udr.KnowledgeBase;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.RuleMeta;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.repository.udr.UdrRuleRepository;
import gov.gtas.services.security.UserData;
import gov.gtas.services.security.UserService;
import gov.gtas.services.security.UserServiceUtil;
import gov.gtas.util.DateCalendarUtils;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * The back-end service for persisting rules.
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

	private static final int UPDATE_BATCH_SIZE = 100;

	@PersistenceContext
	private EntityManager entityManager;

	@Resource
	private UdrRuleRepository udrRuleRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private UserServiceUtil userServiceUtil;

	@Override
	@Transactional
	public UdrRule create(UdrRule r, String userId) {
		final User user = fetchUser(userId);
		// remove meta for now, since its ID is the same as the parent UdrRule
		// ID.
		// we will add it after saving the UDR rule and the ID has been
		// generated.
		RuleMeta savedMeta = r.getMetaData();
		r.setMetaData(null);

		if (savedMeta == null) {
			ErrorHandler errorHandler = ErrorHandlerFactory.getErrorHandler();
			throw errorHandler.createException(
					CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
					"UDR metatdata", "RulePersistenceServiceImpl.create()");
		}

		// set the audit fields
		r.setEditDt(new Date());
		r.setAuthor(user);
		r.setEditedBy(user);

		// save the rule with the meta data stripped.
		// Once the rule id is generated we will add back the meta data
		// and set its key to the rule ID.
		UdrRule rule = udrRuleRepository.save(r);

		// now add back the meta and conditions and update the rule.
		long ruleid = rule.getId();
		savedMeta.setId(ruleid);
		rule.setMetaData(savedMeta);
		savedMeta.setParent(rule);
		rule = udrRuleRepository.save(rule);

		return rule;
	}

	@Override
	@Transactional
	public UdrRule delete(Long id, String userId) {
		final User user = fetchUser(userId);

		UdrRule ruleToDelete = udrRuleRepository.findOne(id);
		if (ruleToDelete != null && ruleToDelete.getDeleted() == YesNoEnum.N) {
			ruleToDelete.setDeleted(YesNoEnum.Y);
			ruleToDelete.setDeleteId(ruleToDelete.getId());
			RuleMeta meta = ruleToDelete.getMetaData();
			meta.setEnabled(YesNoEnum.N);
			ruleToDelete.setEditedBy(user);
			ruleToDelete.setEditDt(new Date());

			// remove references to the Knowledge Base
			if (ruleToDelete.getEngineRules() != null) {
				for (Rule rl : ruleToDelete.getEngineRules()) {
					rl.setKnowledgeBase(null);
				}
			}
			udrRuleRepository.save(ruleToDelete);
		} else {
			ruleToDelete = null; // in case delete flag was Y
			logger.warn("RulePersistenceServiceImpl.delete() - object does not exist or has already been deleted:"
					+ id);
		}
		return ruleToDelete;
	}

	@Override
	@Transactional(value = TxType.SUPPORTS)
	public List<UdrRule> findAll() {
		return (List<UdrRule>) udrRuleRepository.findByDeletedAndEnabled(YesNoEnum.N, YesNoEnum.Y);
	}

	/* (non-Javadoc)
	 * @see gov.gtas.services.udr.RulePersistenceService#findAllSummary()
	 */
	@Override
	public List<Object[]> findAllUdrSummary(String userId) {
		if(StringUtils.isEmpty(userId)){
		    return udrRuleRepository.findAllUdrRuleSummary();
		} else {
			return udrRuleRepository.findAllUdrRuleSummaryByAuthor(userId);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.services.udr.RulePersistenceService#batchUpdate(java.util.List)
	 */
	@Override
	public Collection<? extends BaseEntity> batchUpdate(
			Collection<? extends BaseEntity> entities) {
		/*
		 * Note: this method is only used for Knowledge base maintenance.
		 * Hence there is no need for logging the updates in this method.
		 */
		List<BaseEntity> ret = new LinkedList<BaseEntity>();
		int count = 0;
		for (BaseEntity ent : entities) {
			BaseEntity upd = entityManager.merge(ent);
			ret.add(upd);
			++count;
			if (count > UPDATE_BATCH_SIZE) {
				entityManager.flush();
				entityManager.clear();
			}
		}
		return ret;
	}

	@Override
	@Transactional
	public UdrRule update(UdrRule rule, String userId) {
		final User user = fetchUser(userId);

		if (rule.getId() == null) {
			ErrorHandler errorHandler = ErrorHandlerFactory.getErrorHandler();
			throw errorHandler.createException(
					CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE, "id",
					"Update UDR");
		}

		rule.setEditDt(new Date());
		rule.setEditedBy(user);
		UdrRule updatedRule = udrRuleRepository.save(rule);
		return updatedRule;
	}

	@Override
	@Transactional(TxType.SUPPORTS)
	public UdrRule findById(Long id) {
		return udrRuleRepository.findOne(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.services.udr.RulePersistenceService#findByTitleAndAuthor(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	@Transactional(TxType.SUPPORTS)
	public UdrRule findByTitleAndAuthor(String title, String authorUserId) {
		return udrRuleRepository
				.getUdrRuleByTitleAndAuthor(title, authorUserId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.services.udr.RulePersistenceService#findByAuthor(java.lang.
	 * String )
	 */
	@Override
	public List<UdrRule> findByAuthor(String authorUserId) {
		return udrRuleRepository.findUdrRuleByAuthor(authorUserId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.services.udr.RulePersistenceService#findValidUdrOnDate(java.
	 * util.Date)
	 */
	@Override
	public List<UdrRule> findValidUdrOnDate(Date targetDate) {
		List<UdrRule> ret = null;
		try {
			// remove the time portion of the date
			Date tDate = DateCalendarUtils.parseJsonDate(DateCalendarUtils
					.formatJsonDate(targetDate));
			ret = udrRuleRepository.findValidUdrRuleByDate(tDate);
		} catch (ParseException ex) {
			throw ErrorHandlerFactory.getErrorHandler().createException(
					CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE, ex,
					"targetDate",
					"RulePersistenceServiceImpl.findValidUdrOnDate");
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.services.udr.RulePersistenceService#findDefaultKnowledgeBase()
	 */
	@Override
	@Cacheable(value = "knowledgebase")
	public KnowledgeBase findUdrKnowledgeBase() {
		return this.findUdrKnowledgeBase(RuleConstants.UDR_KNOWLEDGE_BASE_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.services.udr.RulePersistenceService#findUdrKnowledgeBase(java
	 * .lang.String)
	 */
	@Override
	@Cacheable(value = "knowledgebase")
	public KnowledgeBase findUdrKnowledgeBase(String kbName) {
		return udrRuleRepository.getKnowledgeBaseByName(kbName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.services.udr.RulePersistenceService#saveKnowledgeBase(gov.gtas
	 * .model.udr.KnowledgeBase)
	 */
	@Override
	@CacheEvict(value = "knowledgebase", allEntries = true)
	public KnowledgeBase saveKnowledgeBase(KnowledgeBase kb) {
		kb.setCreationDt(new Date());
		if (kb.getId() == null) {
			entityManager.persist(kb);
		} else {
			entityManager.merge(kb);
		}
		return kb;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.services.udr.RulePersistenceService#deleteKnowledgeBase(java
	 * .lang.String)
	 */
	@Override
	@CacheEvict(value = "knowledgebase", allEntries = true)
	public KnowledgeBase deleteKnowledgeBase(String kbName) {
		KnowledgeBase kb = findUdrKnowledgeBase(kbName);
		if (kb != null) {
			entityManager.remove(kb);
		}
		return kb;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.services.udr.RulePersistenceService#findRulesByKnowledgeBaseId(
	 * java.lang.Long)
	 */
	@Override
	public List<Rule> findRulesByKnowledgeBaseId(Long id) {
		return udrRuleRepository.getRuleByKbId(id);
	}

	/**
	 * Fetches the user object and throws an unchecked exception if the user
	 * cannot be found.
	 * 
	 * @param userId
	 *            the ID of the user to fetch.
	 * @return the user fetched from the DB.
	 */
	private User fetchUser(final String userId) {
		UserData userData = userService.findById(userId);
		final User user = userServiceUtil.mapUserEntityFromUserData(userData);
		if (user.getUserId() == null) {
			ErrorHandler errorHandler = ErrorHandlerFactory.getErrorHandler();
			throw errorHandler.createException(
					CommonErrorConstants.INVALID_USER_ID_ERROR_CODE, userId);
		}
		return user;
	}

	/**
	 * @return the entityManager
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

}
