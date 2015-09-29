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
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private int pageNumber;
    private int pageSize;
    
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

