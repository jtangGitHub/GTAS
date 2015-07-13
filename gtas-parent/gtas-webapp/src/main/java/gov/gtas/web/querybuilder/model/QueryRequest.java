package gov.gtas.web.querybuilder.model;

import gov.gtas.model.udr.json.QueryObject;

public class QueryRequest {
	
	private int id;
	private String userId;
	private String title;
	private String description;
	private QueryObject query;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public QueryObject getQuery() {
		return query;
	}
	
	public void setQuery(QueryObject query) {
		this.query = query;
	}
	
}