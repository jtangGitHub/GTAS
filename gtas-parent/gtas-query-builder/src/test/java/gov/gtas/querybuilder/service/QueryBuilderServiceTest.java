/**
 * 
 */
package gov.gtas.querybuilder.service;

import static org.junit.Assert.assertEquals;
import gov.gtas.model.udr.json.QueryEntity;
import gov.gtas.model.udr.json.QueryObject;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.querybuilder.util.Constants;
import gov.gtas.querybuilder.util.EntityEnum;
import gov.gtas.querybuilder.util.QueryBuilderUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author GTAS5
 *
 */
public class QueryBuilderServiceTest {

	QueryBuilderService service;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		service = new QueryBuilderService();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		service = null;
	}

	
	@Test()
	public void testGetQueryForFlightsWithSimpleQuery() throws Exception {
		final String expectedQuery = Constants.SELECT_DISTINCT + " " + EntityEnum.FLIGHT.getAlias() + 
				" " + Constants.FROM + " " + EntityEnum.FLIGHT.getEntityName() + " " + EntityEnum.FLIGHT.getAlias() +
				QueryBuilderUtil.getJoinCondition(EntityEnum.PAX) + " " + Constants.WHERE + " p.firstName = 'DAVID'";
		
		QueryObject query = buildSimpleQuery();
		
		Method privateGetQueryMethod = QueryBuilderService.class.getDeclaredMethod("getQuery", QueryObject.class, EntityEnum.class);
		privateGetQueryMethod.setAccessible(true);

		String actualQuery = (String) privateGetQueryMethod.invoke(service, query, EntityEnum.FLIGHT);
		
		assertEquals(expectedQuery, actualQuery);
	}
	
	@Test()
	public void testGetQueryForPassengersWithSimpleQuery() throws Exception {
		final String expectedQuery = Constants.SELECT_DISTINCT + " " + EntityEnum.PAX.getAlias() + 
				" " + Constants.FROM + " " + EntityEnum.PAX.getEntityName() + " " + EntityEnum.PAX.getAlias() +
				" " + Constants.WHERE + " p.firstName = 'DAVID'";
		
		QueryObject query = buildSimpleQuery();
		
		Method privateGetQueryMethod = QueryBuilderService.class.getDeclaredMethod("getQuery", QueryObject.class, EntityEnum.class);
		privateGetQueryMethod.setAccessible(true);
		
		String actualQuery = (String) privateGetQueryMethod.invoke(service, query, EntityEnum.PAX);
		
		assertEquals(expectedQuery, actualQuery);
		
	}
	
	@Test()
	public void testGetQueryForFlightsWithSimpleDateQuery() throws Exception {
		final String expectedQuery = Constants.SELECT_DISTINCT + " " + EntityEnum.FLIGHT.getAlias() + 
				" " + Constants.FROM + " " + EntityEnum.FLIGHT.getEntityName() + " " + EntityEnum.FLIGHT.getAlias() +
				QueryBuilderUtil.getJoinCondition(EntityEnum.PAX) + " " + Constants.WHERE + " p.firstName = 'DAVID'";
		
		QueryObject query = buildSimpleDateQuery();
		
		Method privateGetQueryMethod = QueryBuilderService.class.getDeclaredMethod("getQuery", QueryObject.class, EntityEnum.class);
		privateGetQueryMethod.setAccessible(true);

		String actualQuery = (String) privateGetQueryMethod.invoke(service, query, EntityEnum.FLIGHT);
		
		System.out.println("actual Query: " + actualQuery);
		
//		assertEquals(expectedQuery, actualQuery);
	}
	
	@Test()
	public void testGetQueryForFlightsWithSimpleIsNullQuery() throws Exception {
		
		final String expectedQuery = Constants.SELECT_DISTINCT + " " + EntityEnum.FLIGHT.getAlias() + 
				" " + Constants.FROM + " " + EntityEnum.FLIGHT.getEntityName() + " " + EntityEnum.FLIGHT.getAlias() +
				QueryBuilderUtil.getJoinCondition(EntityEnum.PAX) + " " + Constants.WHERE + " p.firstName = 'DAVID'";
		
		QueryObject query = buildSimpleIsNullQuery();
		
		Method privateGetQueryMethod = QueryBuilderService.class.getDeclaredMethod("getQuery", QueryObject.class, EntityEnum.class);
		privateGetQueryMethod.setAccessible(true);

		String actualQuery = (String) privateGetQueryMethod.invoke(service, query, EntityEnum.FLIGHT);
		
		System.out.println("actual Query: " + actualQuery);
		
//		assertEquals(expectedQuery, actualQuery);
	}
	
	@Test()
	public void testGetQueryForFlightsWithSimpleContainsQuery() throws Exception {
		
		final String expectedQuery = Constants.SELECT_DISTINCT + " " + EntityEnum.FLIGHT.getAlias() + 
				" " + Constants.FROM + " " + EntityEnum.FLIGHT.getEntityName() + " " + EntityEnum.FLIGHT.getAlias() +
				QueryBuilderUtil.getJoinCondition(EntityEnum.PAX) + " " + Constants.WHERE + " p.firstName = 'DAVID'";
		
		QueryObject query = buildSimpleContainsQuery();
		
		Method privateGetQueryMethod = QueryBuilderService.class.getDeclaredMethod("getQuery", QueryObject.class, EntityEnum.class);
		privateGetQueryMethod.setAccessible(true);

		String actualQuery = (String) privateGetQueryMethod.invoke(service, query, EntityEnum.FLIGHT);
		
		System.out.println("actual Query: " + actualQuery);
		
//		assertEquals(expectedQuery, actualQuery);
	}
	
	private QueryObject buildSimpleQuery() {
		QueryObject query = new QueryObject();
		QueryTerm rule = new QueryTerm();
		List<QueryEntity> rules = new ArrayList<>();
		
		rule.setEntity("Pax");
		rule.setField("firstName");
		rule.setOperator("equal");
		rule.setType("string");
		rule.setValue("DAVID");
		
		rules.add(rule);
		
		query.setCondition("AND");
		query.setRules(rules);
		
		return query;
	}
	
	private QueryObject buildSimpleDateQuery() {
		QueryObject query = new QueryObject();
		QueryTerm rule = new QueryTerm();
		List<QueryEntity> rules = new ArrayList<>();
		
		rule.setEntity("Flight");
		rule.setField("eta");
		rule.setOperator("equal");
		rule.setType("date");
		rule.setValue("05/11/2014");
		
		rules.add(rule);
		
		query.setCondition("AND");
		query.setRules(rules);
		
		return query;
	}
	
	private QueryObject buildSimpleIsNullQuery() {
		QueryObject query = new QueryObject();
		QueryTerm rule = new QueryTerm();
		List<QueryEntity> rules = new ArrayList<>();
		
		rule.setEntity("Pax");
		rule.setField("middleName");
		rule.setOperator("is_null");
		rule.setType("boolean");
		rule.setValue("");
		
		rules.add(rule);
		
		query.setCondition("AND");
		query.setRules(rules);
		
		return query;
	}
	
	private QueryObject buildSimpleContainsQuery() {
		QueryObject query = new QueryObject();
		QueryTerm rule = new QueryTerm();
		List<QueryEntity> rules = new ArrayList<>();
		
		rule.setEntity("Pax");
		rule.setField("firstName");
		rule.setOperator("contains");
		rule.setType("string");
		rule.setValue("avi");
		
		rules.add(rule);
		
		query.setCondition("AND");
		query.setRules(rules);
		
		return query;
	}
	
	private QueryObject buildNestedQuery() {
		QueryObject query = new QueryObject();
		
		return query;
	}
}