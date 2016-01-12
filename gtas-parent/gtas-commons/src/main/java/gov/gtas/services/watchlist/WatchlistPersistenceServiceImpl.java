package gov.gtas.services.watchlist;

import static gov.gtas.constant.AuditLogConstants.WATCHLIST_LOG_CREATE_MESSAGE;
import static gov.gtas.constant.AuditLogConstants.WATCHLIST_LOG_DELETE_ALL_MESSAGE;
import static gov.gtas.constant.AuditLogConstants.WATCHLIST_LOG_DELETE_MESSAGE;
//import static gov.gtas.constant.AuditLogConstants.WATCHLIST_LOG_TARGET_PREFIX;
//import static gov.gtas.constant.AuditLogConstants.WATCHLIST_LOG_TARGET_SUFFIX;
import static gov.gtas.constant.AuditLogConstants.WATCHLIST_LOG_UPDATE_MESSAGE;
import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.constant.WatchlistConstants;
import gov.gtas.enumtype.AuditActionType;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.Status;
import gov.gtas.error.ErrorHandler;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.json.AuditActionData;
import gov.gtas.json.AuditActionTarget;
import gov.gtas.model.AuditRecord;
import gov.gtas.model.User;
import gov.gtas.model.watchlist.Watchlist;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.repository.AuditRecordRepository;
import gov.gtas.repository.watchlist.WatchlistItemRepository;
import gov.gtas.repository.watchlist.WatchlistRepository;
import gov.gtas.services.security.UserData;
import gov.gtas.services.security.UserService;
import gov.gtas.services.security.UserServiceUtil;
import gov.gtas.util.DateCalendarUtils;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * The back-end service for persisting watch lists.
 * 
 * @author GTAS3 (AB)
 *
 */
@Service
public class WatchlistPersistenceServiceImpl implements
		WatchlistPersistenceService {
	/*
	 * The logger for the WatchlistPersistenceServiceImpl.
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(WatchlistPersistenceServiceImpl.class);

	// private static final int UPDATE_BATCH_SIZE = 100;

	@PersistenceContext
	private EntityManager entityManager;

	@Resource
	private WatchlistRepository watchlistRepository;

	@Resource
	private WatchlistItemRepository watchlistItemRepository;

	@Resource
	private AuditRecordRepository auditRecordRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private UserServiceUtil userServiceUtil;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.services.watchlist.WatchlistPersistenceService#createOrUpdate
	 * (java.lang.String, java.lang.String, java.util.List, java.util.List,
	 * java.util.List, java.lang.String)
	 */
	@Override
	@Transactional
	public List<Long> createUpdateDelete(String wlName, EntityEnum entity,
			List<WatchlistItem> createUpdateList,
			List<WatchlistItem> deleteList, String userId) {
		final User user = fetchUser(userId);
		Watchlist watchlist = watchlistRepository.getWatchlistByName(wlName);
		if (watchlist == null) {
			watchlist = new Watchlist(wlName, entity);
		} else if (entity != watchlist.getWatchlistEntity()) {
			// existing watch list has a different entity than that specified in
			// the create/update operation
			ErrorHandlerFactory.createAndThrowException(CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE, "entity", "Update Watchlist");
		}
		// set the audit fields
		watchlist.setEditTimestamp(new Date());
		watchlist.setWatchListEditor(user);
		watchlist = watchlistRepository.save(watchlist);
		List<Long> ret = new LinkedList<Long>();
		ret.add(watchlist.getId());
		if(CollectionUtils.isEmpty(createUpdateList)){
			doDeleteWithLogging(watchlist, user, deleteList);
		} else if(CollectionUtils.isEmpty(deleteList)) {
			Collection<Long> createUpdateIds = doCreateUpdateWithLogging(watchlist, user, createUpdateList);
			ret.addAll(createUpdateIds);
		} else {
			Collection<Long> createUpdateIds = doCreateUpdateWithLogging(watchlist, user, createUpdateList);
			doDeleteWithLogging(watchlist, user, deleteList);
			ret.addAll(createUpdateIds);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.services.watchlist.WatchlistPersistenceService#
	 * findWatchlistItems (java.lang.String)
	 */
	@Override
	public List<WatchlistItem> findWatchlistItems(String watchlistName) {
		return watchlistItemRepository.getItemsByWatchlistName(watchlistName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.services.watchlist.WatchlistPersistenceService#
	 * findAllWatchlistItems ()
	 */
	@Override
	public Iterable<WatchlistItem> findAllWatchlistItems() {
		return watchlistItemRepository.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.services.watchlist.WatchlistPersistenceService#findAll()
	 */
	@Override
	public List<Watchlist> findAllSummary() {
		List<Object[]> summaryList = watchlistRepository
				.fetchWatchlistSummary();
		List<Watchlist> ret = new LinkedList<Watchlist>();
		for (Object[] line : summaryList) {
			ret.add(new Watchlist(line[0].toString(), (EntityEnum) line[1]));
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.services.watchlist.WatchlistPersistenceService#findByName(java
	 * .lang.String)
	 */
	@Override
	public Watchlist findByName(String name) {
		Watchlist wl = watchlistRepository.getWatchlistByName(name);
		return wl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.services.watchlist.WatchlistPersistenceService#deleteWatchlist
	 * (java.lang.String)
	 */
	@Override
	@Transactional
	public Watchlist deleteWatchlist(String name, boolean forceFlag, String userId) {
		final User user = fetchUser(userId);
		Watchlist wl = watchlistRepository.getWatchlistByName(name);
		if(wl != null) {
			List<WatchlistItem> childItems = watchlistItemRepository
					.getItemsByWatchlistName(name);
		    if (!CollectionUtils.isEmpty(childItems) && forceFlag) {
		    	watchlistItemRepository.delete(childItems);
		    	watchlistRepository.delete(wl);
		    } else if(CollectionUtils.isEmpty(childItems)){
			    watchlistRepository.delete(wl);
		    } else {
				throw ErrorHandlerFactory
						.getErrorHandler()
						.createException(
								WatchlistConstants.CANNOT_DELETE_NONEMPTY_WATCHLIST_ERROR_CODE,
								name);
		    }
		    //write the audit record
		    auditRecordRepository.save(createAuditLogRecord(AuditActionType.DELETE_ALL_WL, wl, null, WATCHLIST_LOG_DELETE_ALL_MESSAGE, user));
		} else {
			logger.warn("WatchlistPersistenceServiceImpl.deleteWatchlist - cannot delete watchlist since it does not exist:"
					+ name);						
		}
		return wl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.services.watchlist.WatchlistPersistenceService#
	 * findLogEntriesForWatchlist(java.lang.String)
	 */
	@Override
	public List<AuditRecord> findLogEntriesForWatchlist(String watchlistName) {
    	AuditActionTarget target = new AuditActionTarget(AuditActionType.CREATE_WL, watchlistName, null);
		return auditRecordRepository.findByTarget(target.toString());
	}

	private void doDeleteWithLogging(Watchlist watchlist,
			User editUser, Collection<WatchlistItem> deleteItems) {
        if(!CollectionUtils.isEmpty(deleteItems)){
    		List<AuditRecord> logRecords = new LinkedList<AuditRecord>();
    		Map<Long, WatchlistItem> updateDeleteItemMap = validateItemsPresentInDb(deleteItems);
			for (WatchlistItem item : deleteItems) {
				WatchlistItem itemToDelete = updateDeleteItemMap.get(item
						.getId());
				logRecords.add(createAuditLogRecord(AuditActionType.DELETE_WL,
								watchlist, itemToDelete,
						WATCHLIST_LOG_DELETE_MESSAGE, editUser));
			}
			watchlistItemRepository.delete(deleteItems);
			auditRecordRepository.save(logRecords);
		}
	}
	private Collection<Long> doCreateUpdateWithLogging(Watchlist watchlist,
			User editUser, Collection<WatchlistItem> createUpdateItems) {
		final List<Long> ret = new LinkedList<Long>();
		List<AuditRecord> logRecords = new LinkedList<AuditRecord>();
		if (createUpdateItems != null && createUpdateItems.size() > 0) {
			List<WatchlistItem> updList = new LinkedList<WatchlistItem>();
			for (WatchlistItem item : createUpdateItems) {
				if (item.getId() != null) {
					logRecords.add(createAuditLogRecord(AuditActionType.UPDATE_WL,
									watchlist, item,
							WATCHLIST_LOG_UPDATE_MESSAGE, editUser));
					updList.add(item);
				} else {
					logRecords.add(createAuditLogRecord(AuditActionType.CREATE_WL,
									watchlist, item,
							        WATCHLIST_LOG_CREATE_MESSAGE, editUser));
				}
				item.setWatchlist(watchlist);
			}
			validateItemsPresentInDb(updList);
			Iterable<WatchlistItem> savedItems = watchlistItemRepository.save(createUpdateItems);
			auditRecordRepository.save(logRecords);
			savedItems.forEach(item->ret.add(item.getId()));
		} 
		return ret;
	}
    private AuditRecord createAuditLogRecord(AuditActionType type, Watchlist watchlist, WatchlistItem item, String message, User editUser){
    	AuditActionTarget target = new AuditActionTarget(type, watchlist.getWatchlistName(), null);
    	AuditActionData actionData = new AuditActionData();
    	if(item != null) {
    	    actionData.addProperty("itemId", item.getId()!=null?String.valueOf(item.getId()):StringUtils.EMPTY);
    	}
    	actionData.addProperty("user", editUser.getUserId());
    	actionData.addProperty("editDate", watchlist.getEditTimestamp()!=null?DateCalendarUtils.formatJsonDate(watchlist.getEditTimestamp()):StringUtils.EMPTY);
    	return new AuditRecord(type, target.toString(), Status.SUCCESS, message, actionData.toString(), editUser);
    }
	private Map<Long, WatchlistItem> validateItemsPresentInDb(
			Collection<WatchlistItem> targetItems) {
		Map<Long, WatchlistItem> ret = new HashMap<Long, WatchlistItem>();
		if (targetItems != null && targetItems.size() > 0) {
			List<Long> lst = targetItems.stream().map(itm -> itm.getId())
					.collect(Collectors.toList());
			Iterable<WatchlistItem> items = watchlistItemRepository
					.findAll(lst);
			int itemCount = 0;
			for (WatchlistItem itm : items) {
				ret.put(itm.getId(), itm);
				++itemCount;
			}
			if (targetItems.size() != itemCount) {
				handleMissingWlItemError(targetItems, ret.keySet());
			}
		}
		return ret;
	}
    private void handleMissingWlItemError(Collection<WatchlistItem> targetItems, Set<Long> foundKeys){
    	final StringBuilder bldr = new StringBuilder();
    	targetItems.forEach(itm->bldr.append(itm.getId()).append(','));
    	String targets = bldr.substring(0, bldr.length()-1).toString();
    	String found =  StringUtils.EMPTY;
    	if(!CollectionUtils.isEmpty(foundKeys)){
        	final StringBuilder bldr2 = new StringBuilder();
        	foundKeys.forEach(itm->bldr2.append(itm).append(','));  
        	found = bldr2.substring(0, bldr2.length()-1).toString();
    	}
    	
		ErrorHandlerFactory.createAndThrowException(
				WatchlistConstants.MISSING_DELETE_OR_UPDATE_ITEM_ERROR_CODE, targets, found);
    	
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
}
