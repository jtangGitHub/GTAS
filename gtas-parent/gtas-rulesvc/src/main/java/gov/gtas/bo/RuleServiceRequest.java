package gov.gtas.bo;

import java.util.List;

/**
 * An interface for input requests to the Rule Engine.
 * 
 * @author GTAS3 (AB)
 *
 */
public interface RuleServiceRequest {
	/**
	 * Gets objects to be inserted into the working memory before the rule
	 * engine is executed.
	 * 
	 * @return list of objects to be inserted into the working memory.
	 */
	List<?> getRequestObjects();

	/**
	 * Gets the type of the request.<br>
	 * (e.g., APIS_MESSAGE)
	 * 
	 * @return request type.
	 */
	RuleServiceRequestType getRequestType();
}