package gov.gtas.querybuilder.service;

import gov.gtas.model.Flight;
import gov.gtas.model.Pax;
import gov.gtas.model.udr.json.QueryObject;
import gov.gtas.querybuilder.repository.QueryBuilderRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author GTAS5
 *
 */
@Service
public class QueryBuilderService {
	
	@Autowired
	QueryBuilderRepository queryRepository;
		
	/**
	 * 
	 */
	public List<Flight> runQueryOnFlight(QueryObject queryObject) {
		
		return null;
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	public List<Pax> runQueryOnPassenger(String query) {
		
		return null;
	}
	
	/**
	 * 
	 */
	public void saveQuery() {
		
	}
	
	/**
	 * 
	 */
	public void viewQuery() {
		
	}
	
	/**
	 * 
	 */
	public void editQuery() {
		
	}
	
	/**
	 * 
	 */
	public void deleteQuery() {
		
	}
}
