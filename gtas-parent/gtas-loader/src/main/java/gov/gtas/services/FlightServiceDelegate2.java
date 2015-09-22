package gov.gtas.services;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import gov.gtas.model.Flight;
import gov.gtas.vo.passenger.FlightVo;

/**
 * 
 * @author GTAS4
 * Class FlightServiceDelegate receives the requests from calling applications and delegates 
 * the request to appropriate Service.The purpose of the class between Service layer and other layers
 * is to accept value objects and return value objects after service invocation.
 * 
 */

@Component
public class FlightServiceDelegate2 {
	
	@Resource
	private FlightService flightService;
	
	public FlightVo saveOrUpdate(FlightVo vo) {
		Flight flight = flightService.getUniqueFlightByCriteria(vo.getCarrier(), vo.getFlightNumber(), vo.getOrigin(), vo.getDestination(), vo.getFlightDate());

		Flight f = new Flight();
		if (flight != null) {
			f = ServiceUtils.mapFlightFromVo(vo, flight);
			f.setUpdatedBy("JUNIT");
			flightService.update(f);
        } else {
            ServiceUtils.mapFlightFromVo(vo, f);
            f = flightService.create(f);
            System.out.println("ID-" + f.getId());
        }

		return ServiceUtils.mapVoFromFlight(f);
    }

}
