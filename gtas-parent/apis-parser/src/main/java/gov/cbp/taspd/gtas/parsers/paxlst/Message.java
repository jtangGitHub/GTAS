package gov.cbp.taspd.gtas.parsers.paxlst;

import gov.cbp.taspd.gtas.model.Flight;
import gov.cbp.taspd.gtas.model.Pax;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Message {
    private byte[] raw;
    
    private String code;
    private String version;
    private String sender;
    private String receiver;
    private String date;
    private String time;
    
    private Flight flight;
    private List<Pax> passengers;
    
    public Message() {
        passengers = new ArrayList<>();
    }
    
    public byte[] getRaw() {
        return raw;
    }

    public void setRaw(byte[] raw) {
        this.raw = raw;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }
    public String getReceiver() {
        return receiver;
    }
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public Flight getFlight() {
        return flight;
    }
    public void setFlight(Flight flight) {
        this.flight = flight;
    }
    public List<Pax> getPassengers() {
        return passengers;
    }
    public void setPassengers(List<Pax> passengers) {
        this.passengers = passengers;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE); 
    }    
}
