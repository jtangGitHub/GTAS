package gov.gtas.repository;

import java.util.Date;
import java.util.List;

import gov.gtas.model.Flight;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface FlightRepository extends CrudRepository<Flight, Long> {
	@Query("SELECT f FROM Flight f WHERE f.carrier = :carrier AND f.flightNumber = :flightNumber AND f.origin = :origin AND f.destination=:destination AND f.flightDate = :flightDate")
	public List<Flight> getFlightByCriteria(@Param("carrier") String carrier,
			@Param("flightNumber") String flightNumber,@Param("origin") String origin,
			@Param("destination") String destination,@Param("flightDate") Date flightDate);
}