package gov.gtas.web.querybuilder.model;

import java.util.Date;

/**
 * 
 * @author GTAS5
 *
 */
public abstract class BaseQueryBuilderResult {
	
	private Long id;
	private String flightNumber;
	private String carrierCode;
	private String origin;
	private String destination;
	private Date departureDt;
	private Date arrivalDt;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFlightNumber() {
		return flightNumber;
	}
	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}
	public String getCarrierCode() {
		return carrierCode;
	}
	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public Date getDepartureDt() {
		return departureDt;
	}
	public void setDepartureDt(Date departureDt) {
		this.departureDt = departureDt;
	}
	public Date getArrivalDt() {
		return arrivalDt;
	}
	public void setArrivalDt(Date arrivalDt) {
		this.arrivalDt = arrivalDt;
	}
}