package gov.gtas.dto;

import java.util.ArrayList;
import java.util.List;

public class FlightDto {
	private String flightNum;
	private String carrier;
	private String embark;
	private String debark;
	private String startDate;
	private String endDate;
	private String embarkCountry;
	private String debarkCountry;
	private List<PaxDto> paxList=new ArrayList<PaxDto>();
	
	
	public String getEmbarkCountry() {
		return embarkCountry;
	}
	public void setEmbarkCountry(String embarkCountry) {
		this.embarkCountry = embarkCountry;
	}
	public String getDebarkCountry() {
		return debarkCountry;
	}
	public void setDebarkCountry(String debarkCountry) {
		this.debarkCountry = debarkCountry;
	}
	
	public String getFlightNum() {
		return flightNum;
	}
	public void setFlightNum(String flightNum) {
		this.flightNum = flightNum;
	}
	public String getCarrier() {
		return carrier;
	}
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
	public String getEmbark() {
		return embark;
	}
	public void setEmbark(String embark) {
		this.embark = embark;
	}
	public String getDebark() {
		return debark;
	}
	public void setDebark(String debark) {
		this.debark = debark;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public List getPaxList() {
		return paxList;
	}
	public void setPaxList(List paxList) {
		this.paxList = paxList;
	}
	
}