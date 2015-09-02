package gov.gtas.svc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.gtas.config.RuleServiceConfig;
import gov.gtas.constant.WatchlistConstants;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.WatchlistEditEnum;
import gov.gtas.error.CommonServiceException;
import gov.gtas.model.Role;
import gov.gtas.model.User;
import gov.gtas.model.udr.json.JsonServiceResponse;
import gov.gtas.model.udr.json.JsonServiceResponse.ServiceResponseDetailAttribute;
import gov.gtas.model.watchlist.Watchlist;
import gov.gtas.model.watchlist.WatchlistEditLog;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.model.watchlist.json.WatchlistSpec;
import gov.gtas.services.UserService;
import gov.gtas.services.watchlist.WatchlistPersistenceService;
import gov.gtas.util.DateCalendarUtils;
import gov.gtas.util.SampleDataGenerator;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
/**
 * Integration tests for the UDR management service.
 * @author GTAS3 (AB)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=RuleServiceConfig.class)
@TransactionConfiguration(defaultRollback = true)
public class WatchlistServiceIT {
	private static final String WL_NAME1 = "Hello WL 1";
	private static final String WL_KB_NAME = "Test WL KB";
	
	@Autowired
    WatchlistService wlService;
    
	@Autowired
    WatchlistPersistenceService wlPersistenceService;

	@Autowired
    RuleManagementService ruleManagementService;

	@Autowired
    UserService userService;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Transactional
	public void testCreateWatchlist() {
		User user = createUser();
		WatchlistSpec spec = SampleDataGenerator.newWlWith2Items(WL_NAME1);
		JsonServiceResponse resp = wlService.createOrUpdateWatchlist(user.getUserId(), spec);
		assertEquals(JsonServiceResponse.SUCCESS_RESPONSE, resp.getStatus());
		List<ServiceResponseDetailAttribute> respDetails = resp.getResponseDetails();
		assertEquals(2, respDetails.size());
		Watchlist wl = wlPersistenceService.findByName(WL_NAME1);
		assertNotNull(wl);
		assertNotNull(wl.getId());
		assertEquals(WL_NAME1, wl.getWatchlistName());
		assertEquals(EntityEnum.PASSENGER, wl.getWatchlistEntity());
		assertEquals(user, wl.getWatchListEditor());
		assertTrue(DateCalendarUtils.dateRoundedEquals(new Date(), wl.getEditTimestamp(), Calendar.HOUR));
		List<WatchlistItem> items = wlPersistenceService.findWatchlistItems(WL_NAME1);
		assertEquals(2, items.size());
		for(WatchlistItem itm:items){
			assertNotNull(itm.getItemRuleData());
			assertEquals(WL_NAME1, itm.getWatchlist().getWatchlistName());
			String itmData = itm.getItemData();
			assertTrue(!StringUtils.isEmpty(itmData));
			assertTrue(itmData.matches("\\{.*\\}"));
		}
		
		List<WatchlistEditLog> logs = wlPersistenceService.findLogEntriesForWatchlist(WL_NAME1);
		assertNotNull(logs);
		assertEquals(2, logs.size());		
		for(WatchlistEditLog lg:logs){
			assertNotNull(lg.getId());
			assertTrue(DateCalendarUtils.dateRoundedEquals(new Date(), lg.getEditTimestamp(), Calendar.HOUR));
			assertEquals(WL_NAME1, lg.getEditedWatchlist());
			assertEquals(WatchlistEditEnum.C, lg.getEditType());
			String itmData = lg.getEditData();
			assertTrue(!StringUtils.isEmpty(itmData));
			assertTrue(itmData.matches("\\{.*\\}"));
		}
	}
	@Test
	@Transactional
	public void testUpdateDeleteWatchlistItem() {
		User user = createUser();
		WatchlistSpec spec = SampleDataGenerator.newWlWith2Items(WL_NAME1);
		JsonServiceResponse resp = wlService.createOrUpdateWatchlist(user.getUserId(), spec);
		assertEquals(JsonServiceResponse.SUCCESS_RESPONSE, resp.getStatus());
		spec = wlService.fetchWatchlist(WL_NAME1);
		assertNotNull(spec);
		List<WatchlistItemSpec> items = spec.getWatchlistItems();
		assertNotNull(items);
		assertEquals(2,items.size());
		items.get(0).setAction(WatchlistEditEnum.U.getOperationName());
		items.get(1).setAction(WatchlistEditEnum.D.getOperationName());

		resp = wlService.createOrUpdateWatchlist(user.getUserId(), spec);
		assertEquals(JsonServiceResponse.SUCCESS_RESPONSE, resp.getStatus());

		List<WatchlistItem> updItems = wlPersistenceService.findWatchlistItems(WL_NAME1);
		assertEquals(1, updItems.size());
		WatchlistItem itm = updItems.get(0);
		assertNotNull(itm.getItemRuleData());
		assertEquals(WL_NAME1, itm.getWatchlist().getWatchlistName());
		String itmData = itm.getItemData();
		assertTrue(!StringUtils.isEmpty(itmData));
		assertTrue(itmData.matches("\\{.*\\}"));
			
		List<WatchlistEditLog> logs = wlPersistenceService.findLogEntriesForWatchlist(WL_NAME1);
		assertNotNull(logs);
		assertEquals(4, logs.size());
		int createCount = 0;
		int updateCount = 0;
		int deleteCount = 0;
		for(WatchlistEditLog lg:logs){
			assertNotNull(lg.getId());
			assertTrue(DateCalendarUtils.dateRoundedEquals(new Date(), lg.getEditTimestamp(), Calendar.HOUR));
			assertEquals(WL_NAME1, lg.getEditedWatchlist());
			switch(lg.getEditType()){
			case C:
				createCount++;
				break;
			case U:
				updateCount++;
				break;
			case D:
				deleteCount++;
				break;
			}
			String itmLogData = lg.getEditData();
			assertTrue(!StringUtils.isEmpty(itmLogData));
			assertTrue(itmLogData.matches("\\{.*\\}"));
		}
		assertEquals(2, createCount);
		assertEquals(1, updateCount);
		assertEquals(1, deleteCount);
	}

	@Test
	@Transactional
	public void testUpdateWatchlistItemError() {
		User user = createUser();
		WatchlistSpec spec = SampleDataGenerator.newWlWith2Items(WL_NAME1);
		JsonServiceResponse resp = wlService.createOrUpdateWatchlist(user.getUserId(), spec);
		assertEquals(JsonServiceResponse.SUCCESS_RESPONSE, resp.getStatus());
		spec = wlService.fetchWatchlist(WL_NAME1);
		assertNotNull(spec);
		List<WatchlistItemSpec> items = spec.getWatchlistItems();
		assertNotNull(items);
		assertEquals(2,items.size());
		items.get(0).setAction(WatchlistEditEnum.U.getOperationName());
		items.get(0).setId(2341L);
		items.get(1).setAction(WatchlistEditEnum.D.getOperationName());
        try{
		    wlService.createOrUpdateWatchlist(user.getUserId(), spec);
		    fail("Expecting exception");
        } catch(CommonServiceException cse){
		     assertEquals(WatchlistConstants.MISSING_DELETE_OR_UPDATE_ITEM_ERROR_CODE, cse.getErrorCode());
        }
	}
	
	@Test
	@Transactional
	public void testDeleteWatchlistItemError() {
		User user = createUser();
		WatchlistSpec spec = SampleDataGenerator.newWlWith2Items(WL_NAME1);
		JsonServiceResponse resp = wlService.createOrUpdateWatchlist(user.getUserId(), spec);
		assertEquals(JsonServiceResponse.SUCCESS_RESPONSE, resp.getStatus());
		spec = wlService.fetchWatchlist(WL_NAME1);
		assertNotNull(spec);
		List<WatchlistItemSpec> items = spec.getWatchlistItems();
		assertNotNull(items);
		assertEquals(2,items.size());
		items.get(0).setAction(WatchlistEditEnum.U.getOperationName());
		items.get(1).setAction(WatchlistEditEnum.D.getOperationName());
		items.get(1).setId(2341L);
        try{
		    wlService.createOrUpdateWatchlist(user.getUserId(), spec);
		    fail("Expecting exception");
        } catch(CommonServiceException cse){
		     assertEquals(WatchlistConstants.MISSING_DELETE_OR_UPDATE_ITEM_ERROR_CODE, cse.getErrorCode());
        }
	}
	@Test
	@Transactional
	public void testKnowledgeBaseForWl() {
		User user = createUser();
		WatchlistSpec spec = SampleDataGenerator.newWlWith2Items(WL_NAME1);
		JsonServiceResponse resp = wlService.createOrUpdateWatchlist(user.getUserId(), spec);
		assertEquals(JsonServiceResponse.SUCCESS_RESPONSE, resp.getStatus());
		resp = wlService.activateAllWatchlists(WL_KB_NAME);
		assertEquals(JsonServiceResponse.SUCCESS_RESPONSE, resp.getStatus());
		String drl = ruleManagementService.fetchDrlRulesFromKnowledgeBase(WL_KB_NAME);
		assertNotNull(drl);
	}
   private User createUser(){
		String ROLE_NAME = "user";
		String USER_FNAME = "Patrick";
		String USER_LASTNAME = "Henry";
		String USER_ID = "phenry";

	   User usr = new User();
	   Role role = new Role();
	   role.setRoleDescription(ROLE_NAME);
	   //role.setRoleId(ROLE_ID);
	   usr.setUserRole(role);
	   usr.setFirstName(USER_FNAME);
	   usr.setLastName(USER_LASTNAME);
	   usr.setPassword("password");
	   usr.setUserId(USER_ID);
	   
		usr = userService.create(usr);

	   return usr;
   }
}
