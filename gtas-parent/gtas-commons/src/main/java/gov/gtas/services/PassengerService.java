package gov.gtas.services;

import gov.gtas.model.Passenger;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PassengerService {
	
	public Passenger create(Passenger passenger);
    public Passenger delete(Long id);
    public List<Passenger> findAll();
    public Passenger update(Passenger passenger) ;
    public Passenger findById(Long id);
    public Passenger getPassengerByName(String firstName,String lastName);
    public List<Passenger> getPassengersByLastName(String lastName);
    public List<Passenger> getPassengersByFlightId(Long flightId);
    public List<Passenger> getPassengersFromUpcomingFlights(Pageable pageable) ;
    public List<Passenger> getPaxByLastName(String lastName, Pageable pageable);
    public List<Passenger> getPassengersByFlightDates(Date startDate, Date endDate);

}
