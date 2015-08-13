package gov.gtas.parsers.paxlst;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import gov.gtas.parsers.edifact.MessageVo;
import gov.gtas.parsers.vo.passenger.FlightVo;
import gov.gtas.parsers.vo.passenger.PassengerVo;
import gov.gtas.parsers.vo.passenger.ReportingPartyVo;

public class ApisMessageVo extends MessageVo {
    private List<ReportingPartyVo> reportingParties = new ArrayList<>();
    private List<FlightVo> flights = new ArrayList<>();
    private List<PassengerVo> passengers = new ArrayList<>();
    
    public ApisMessageVo() { }
    
    public void addFlight(FlightVo f) {
        flights.add(f);
    }
    public void addPax(PassengerVo p) {
        passengers.add(p);
    }
    public void addReportingParty(ReportingPartyVo rp) {
        reportingParties.add(rp);
    }
    public List<FlightVo> getFlights() {
        return flights;
    }
    public List<PassengerVo> getPassengers() {
        return passengers;
    }
    public List<ReportingPartyVo> getReportingParties() {
        return reportingParties;
    }
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE); 
    }
}
