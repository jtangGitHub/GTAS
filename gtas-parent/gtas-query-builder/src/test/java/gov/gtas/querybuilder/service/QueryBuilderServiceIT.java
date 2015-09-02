package gov.gtas.querybuilder.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.OperatorEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.udr.json.QueryEntity;
import gov.gtas.model.udr.json.QueryObject;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.querybuilder.config.QueryBuilderAppConfig;
import gov.gtas.querybuilder.constants.Constants;
import gov.gtas.querybuilder.exceptions.InvalidQueryException;
import gov.gtas.querybuilder.exceptions.QueryAlreadyExistsException;
import gov.gtas.querybuilder.exceptions.QueryDoesNotExistException;
import gov.gtas.querybuilder.model.IQueryResult;
import gov.gtas.querybuilder.model.IUserQueryResult;
import gov.gtas.querybuilder.model.QueryRequest;
import gov.gtas.services.FlightService;
import gov.gtas.services.PassengerService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *	Query Builder Service Integration Test 
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CommonServicesConfig.class, QueryBuilderAppConfig.class})
public class QueryBuilderServiceIT {

	@PersistenceContext 
 	private EntityManager entityManager;
	@Autowired
	private QueryBuilderService queryService;
	@Autowired
	private PassengerService passengerService;
	@Autowired
	private FlightService flightService;
	
	private static final String TITLE = "Integration Test";
	private static final String UPDATED_TITLE = "Updated Int. Test";
	private static final String DESCRIPTION = "A simple query created during integration test";
	private static final String USER_ID = "ladebiyi";
	
	private static QueryObject query;
	
	@BeforeClass
	public static void setUp() throws Exception {
		query = buildSimpleBetweenQuery();
	}
		
	@Test
	@Transactional
	public void testSaveQuery() throws QueryAlreadyExistsException, InvalidQueryException, QueryDoesNotExistException {
		QueryRequest request = new QueryRequest();
		
		request.setTitle(TITLE);
		request.setDescription(DESCRIPTION);
		request.setQuery(query);
		request.setUserId(USER_ID);
		
		IUserQueryResult result = queryService.saveQuery(request);
		assertNotNull(result.getId());
		
		deleteUserQuery(result.getId());
	}
	
	@Test(expected = QueryAlreadyExistsException.class)
	@Transactional
	public void testSaveDuplicateQuery() throws QueryAlreadyExistsException, InvalidQueryException {
		QueryRequest request = new QueryRequest();
		
		request.setTitle(TITLE);
		request.setDescription(DESCRIPTION);
		request.setQuery(query);
		request.setUserId(USER_ID);
		
		// create a user query
		IUserQueryResult result = queryService.saveQuery(request);
		assertNotNull(result.getId());
		
		// try to create a duplicate query
		// this call should fail and throw exception
		queryService.saveQuery(request);
		
		deleteUserQuery(result.getId());
	}
	
	@Test(expected = InvalidQueryException.class)
	@Transactional
	public void testSaveInvalidQuery() throws QueryAlreadyExistsException, InvalidQueryException {
		QueryRequest request = new QueryRequest();
		
		request.setTitle(TITLE);
		request.setDescription(DESCRIPTION);
		request.setQuery(null);
		request.setUserId(USER_ID);
		
		// create a user query
		queryService.saveQuery(request);
	}
	
	@Test
	@Transactional
	public void testEditQuery() throws QueryAlreadyExistsException, InvalidQueryException, QueryDoesNotExistException {
		QueryRequest request = new QueryRequest();
		
		request.setTitle(TITLE);
		request.setDescription(DESCRIPTION);
		request.setQuery(query);
		request.setUserId(USER_ID);
		
		// create a new query
		IUserQueryResult result = queryService.saveQuery(request);
		
		// update the query
		request.setId(result.getId());
		request.setTitle(UPDATED_TITLE);
		IUserQueryResult updatedResult = queryService.editQuery(request);
		
		assertEquals(result.getId(), updatedResult.getId());
		assertEquals(UPDATED_TITLE, updatedResult.getTitle());
		
		deleteUserQuery(result.getId());
	}
	
	@Test(expected = InvalidQueryException.class)
	@Transactional
	public void testEditInvalidQuery() throws QueryAlreadyExistsException, InvalidQueryException, QueryDoesNotExistException {
		QueryRequest request = new QueryRequest();
		
		request.setTitle(TITLE);
		request.setDescription(DESCRIPTION);
		request.setQuery(query);
		request.setUserId(USER_ID);
		
		// create a new query
		IUserQueryResult result = queryService.saveQuery(request);
		
		// update the query
		request.setId(result.getId());
		request.setTitle(UPDATED_TITLE);
		request.setQuery(null);
		
		// try updating the query with an invalid user query
		queryService.editQuery(request);
		
		deleteUserQuery(result.getId());
	}
	
	@Test(expected = QueryDoesNotExistException.class)
	@Transactional
	public void testEditDoesNotExistQuery() throws QueryAlreadyExistsException, QueryDoesNotExistException, InvalidQueryException {
		QueryRequest request = new QueryRequest();
		
		request.setId(1);
		request.setTitle(TITLE);
		request.setDescription(DESCRIPTION);
		request.setQuery(query);
		request.setUserId(USER_ID);
		
		queryService.editQuery(request);
	}
	
	@Test(expected = QueryAlreadyExistsException.class)
	@Transactional
	public void testEditQueryAlreadyExists() throws QueryAlreadyExistsException, InvalidQueryException, QueryDoesNotExistException {
		QueryRequest request = new QueryRequest();
		
		request.setTitle(TITLE);
		request.setDescription(DESCRIPTION);
		request.setQuery(query);
		request.setUserId(USER_ID);
		
		// create a new query
		IUserQueryResult result = queryService.saveQuery(request);
		
		// create another query
		request.setTitle(TITLE + "2");
		request.setDescription(DESCRIPTION);
		request.setQuery(query);
		request.setUserId(USER_ID);
		
		IUserQueryResult secondResult = queryService.saveQuery(request);
		
		// try to update the second query using
		// the same title in the first query
		// thereby trying to create a duplicate query
		// for the same user, which is not allowed
		request.setTitle(TITLE);
		request.setId(secondResult.getId());
		queryService.editQuery(request);
		
		deleteUserQuery(result.getId());
		deleteUserQuery(secondResult.getId());
	}
	
	@Test
	@Transactional
	public void testListQueryByUser() throws QueryAlreadyExistsException, InvalidQueryException {
		QueryRequest request = new QueryRequest();
		
		request.setTitle(TITLE);
		request.setDescription(DESCRIPTION);
		request.setQuery(query);
		request.setUserId(USER_ID);
		
		// create a new query
		IUserQueryResult result = queryService.saveQuery(request);
		
		List<IUserQueryResult> resultList = queryService.listQueryByUser(USER_ID);
		
		assertNotNull(resultList);
		assertEquals(1, resultList.size());
		
		deleteUserQuery(result.getId());
	}

	@Test(expected = QueryDoesNotExistException.class)
	@Transactional
	public void testDeleteQuery() throws QueryAlreadyExistsException, InvalidQueryException, QueryDoesNotExistException {
		QueryRequest request = new QueryRequest();
		
		request.setTitle(TITLE);
		request.setDescription(DESCRIPTION);
		request.setQuery(query);
		request.setUserId(USER_ID);
		
		// create a new query
		IUserQueryResult result = queryService.saveQuery(request);
		
		// delete - soft delete
		queryService.deleteQuery(USER_ID, result.getId());
	
		// try updating the query
		// you should get an exception if the query was
		// successfully deleted
		request.setId(result.getId());
		queryService.editQuery(request);
	}
	
	@Test
	@Transactional
	public void testRunFlightQuery() throws InvalidQueryException  {
		QueryObject queryObject = new QueryObject();
		
		List<QueryEntity> rules = new ArrayList<>();
		QueryTerm term = new QueryTerm();
		term.setEntity(EntityEnum.FLIGHT.getEntityName());
		term.setField("carrier");
		term.setOperator(OperatorEnum.EQUAL.toString());
		term.setType(TypeEnum.STRING.toString());
		term.setValue(new String[]{"AB"});
		rules.add(term);
		
		queryObject.setCondition(Constants.AND);
		queryObject.setRules(rules);
		
		// create flight and passenger record
		Flight flight = new Flight();
		
		flight.setCarrier("AB");
		flight.setDestination("USA");
		flight.setDirection("O");
		flight.setFlightDate(new Date());
		flight.setFlightNumber("123");
		flight.setOrigin("CAN");
		
		Set<Flight> flights = new HashSet<>();
		flights.add(flight);
		
		Passenger passenger = new Passenger();
		passenger.setDeleted(false);
		passenger.setPassengerType("P");
		passenger.setFirstName("TEST");
		passenger.setLastName("USER");
		
		passenger.setFlights(flights);
		
		Set<Passenger> passengers = new HashSet<Passenger>();
		passengers.add(passenger);
		flight.setPassengers(passengers);
		
		Flight newFlight = flightService.create(flight);
		
		// execute flight query
		List<IQueryResult> result = queryService.runFlightQuery(queryObject);
		
		assertNotNull(result);
		assertEquals(1, result.size());
		
		flightService.delete(newFlight.getId());
	}

	@Test
	public void testRunPassengerQuery() throws InvalidQueryException {
		QueryObject queryObject = new QueryObject();
		
		List<QueryEntity> rules = new ArrayList<>();
		QueryTerm term = new QueryTerm();
		term.setEntity(EntityEnum.PASSENGER.getEntityName());
		term.setField("firstName");
		term.setOperator(OperatorEnum.EQUAL.toString());
		term.setType(TypeEnum.STRING.toString());
		term.setValue(new String[]{"Test"});
		rules.add(term);
		
		queryObject.setCondition(Constants.AND);
		queryObject.setRules(rules);
		
		// create flight and passenger record
		Flight flight = new Flight();
		
		flight.setCarrier("AB");
		flight.setDestination("USA");
		flight.setDirection("O");
		flight.setFlightDate(new Date());
		flight.setFlightNumber("123");
		flight.setOrigin("CAN");
		
		Set<Flight> flights = new HashSet<>();
		flights.add(flight);
		
		Passenger passenger = new Passenger();
		passenger.setDeleted(false);
		passenger.setPassengerType("P");
		passenger.setFirstName("TEST");
		passenger.setLastName("USER");
		
		passenger.setFlights(flights);
		
		Set<Passenger> passengers = new HashSet<Passenger>();
		passengers.add(passenger);
		flight.setPassengers(passengers);
		
		Flight newFlight = flightService.create(flight);
		
		// execute flight query
		List<IQueryResult> result = queryService.runPassengerQuery(queryObject);
		
		assertNotNull(result);
		assertEquals(1, result.size());
		
		flightService.delete(newFlight.getId());
	}
	
	@Transactional
	private void deleteUserQuery(int id) {
		Query deleteQuery = entityManager.createQuery("delete from UserQuery where id = " + id);
		deleteQuery.executeUpdate();
	}

	private static QueryObject buildSimpleBetweenQuery() {
		QueryTerm rule = new QueryTerm();
		List<QueryEntity> rules = new ArrayList<>();
		QueryObject query = new QueryObject();
		List<String> values = new ArrayList<>();
		
		values.add("20");
		values.add("40");
		
		rule.setEntity("Passenger");
		rule.setField("age");
		rule.setOperator("between");
		rule.setType("integer");
		rule.setValue(values.toArray(new String[values.size()]));
		
		rules.add(rule);
		
		query.setCondition("AND");
		query.setRules(rules);
		
		return query;
	}
	
}
