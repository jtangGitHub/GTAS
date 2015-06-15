package gov.cbp.taspd.gtas.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

@Entity
public class ApisMessage extends Message {
    private Date transmissionDate;
    private String transmissionSource;

    @Transient
    Set<ReportingParty> reportingParties = new HashSet<>();
    
    @ManyToMany(
        targetEntity=Flight.class,
        cascade={CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
        name="message_flight",
        joinColumns=@JoinColumn(name="flight_id"),
        inverseJoinColumns=@JoinColumn(name="message_id")
    )        
    private Set<Flight> flights = new HashSet<>();

    public Set<Flight> getFlights() {
        return flights;
    }

    public void setFlights(Set<Flight> flights) {
        this.flights = flights;
    }

    public Set<ReportingParty> getReportingParties() {
        return reportingParties;
    }

    public void setReportingParties(Set<ReportingParty> reportingParties) {
        this.reportingParties = reportingParties;
    }

    public Date getTransmissionDate() {
        return transmissionDate;
    }

    public void setTransmissionDate(Date transmissionDate) {
        this.transmissionDate = transmissionDate;
    }

    public String getTransmissionSource() {
        return transmissionSource;
    }

    public void setTransmissionSource(String transmissionSource) {
        this.transmissionSource = transmissionSource;
    }
}
