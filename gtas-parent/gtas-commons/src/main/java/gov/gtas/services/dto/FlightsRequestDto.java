package gov.gtas.services.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;

public class FlightsRequestDto implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // e.g. 2015-10-02T18:33:03.412Z
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private int pageNumber;
    private int pageSize;
    
    private String origin;
    private String dest;
    
    private String flightNumber;
    
    private String direction;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)        
    private Date etaStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)        
    private Date etaEnd;
    
    private List<SortOptionsDto> sort;
    
    public FlightsRequestDto() { }
    
    public int getPageNumber() {
        return pageNumber;
    }
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    public int getPageSize() {
        return pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    public Date getEtaStart() {
        return etaStart;
    }
    public void setEtaStart(Date etaStart) {
        this.etaStart = etaStart;
    }
    public Date getEtaEnd() {
        return etaEnd;
    }
    public void setEtaEnd(Date etaEnd) {
        this.etaEnd = etaEnd;
    }        
    public String getOrigin() {
        return origin;
    }
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    public String getDest() {
        return dest;
    }
    public void setDest(String dest) {
        this.dest = dest;
    }
    public String getFlightNumber() {
        return flightNumber;
    }
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    public String getDirection() {
        return direction;
    }
    public void setDirection(String direction) {
        this.direction = direction;
    }
    public List<SortOptionsDto> getSort() {
        return sort;
    }
    public void setSort(List<SortOptionsDto> sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE); 
    }
}
