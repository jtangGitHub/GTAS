package gov.gtas.model.udr.json;

import gov.gtas.model.udr.json.QueryObject;

import java.io.Serializable;
/**
 * JSON object format for communicating with the UI.
 * @author GTAS3 (AB)
 *
 */
public class UdrSpecification implements Serializable {
	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = -8100474892751630855L;
    
	private Long id;
	private QueryObject details;
	private MetaData summary;
	
	/**
	 * Default constructor for Spring to use.
	 */
	public UdrSpecification(){
		
	}
	public UdrSpecification(Long id, QueryObject queryObject, MetaData meta){
		this.id = id;
		this.details = queryObject;
		this.summary = meta;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the details
	 */
	public QueryObject getDetails() {
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails(QueryObject details) {
		this.details = details;
	}

	/**
	 * @return the summary
	 */
	public MetaData getSummary() {
		return summary;
	}

	/**
	 * @param summary the summary to set
	 */
	public void setSummary(MetaData summary) {
		this.summary = summary;
	}

}
