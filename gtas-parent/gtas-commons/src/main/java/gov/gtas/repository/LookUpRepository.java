package gov.gtas.repository;

import gov.gtas.model.lookup.Airport;
import gov.gtas.model.lookup.Carrier;
import gov.gtas.model.lookup.Country;

import java.util.List;

public interface LookUpRepository {
	List<Country> getAllCountries();
	List<Carrier> getAllCarriers();
	List<Airport> getAllAirports();
	public void clearAllEntitiesCache();
	public void clearEntityFromCache(Long id);
	public void clearHibernateCache();
}