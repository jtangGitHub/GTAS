package gov.gtas.error;

/**
 * Constants used in the Rule Service module.
 * 
 * @author GTAS3 (AB)
 *
 */
public class CommonErrorConstants {
	// //////////////////////////////////////////////////////////////////////////////////////
	// ERROR CODES
	// //////////////////////////////////////////////////////////////////////////////////////
	/*
	 * This is the error code for an internal system error indicating invalid
	 * program logic causing a service API method being called with a null
	 * argument.
	 */
	public static final String NULL_ARGUMENT_ERROR_CODE = "NULL_ARGUMENT";
	/*
	 * This is the error code indicating that the
	 * user indicated by a supplied userId cannot be found.
	 */
	public static final String INVALID_USER_ID_ERROR_CODE = "INVALID_USER_ID";

	/*
	 * This is the error code for an Unexpected internal system error.
	 */
	public static final String SYSTEM_ERROR_CODE = "SYSTEM_ERROR_CODE";

	// //////////////////////////////////////////////////////////////////////////////////////
	// ERROR Messages
	// //////////////////////////////////////////////////////////////////////////////////////
	/*
	 * This is the error message for an internal system error indicating invalid
	 * program logic causing a service API method being called with a null
	 * argument.
	 */
	public static final String NULL_ARGUMENT_ERROR_MESSAGE = "The parameter '%s' passed to the method '%s' should not be null.";
	/*
	 * This is the error message indicating that the
	 * user indicated by a supplied userId cannot be found.
	 */
	public static final String INVALID_USER_ID_ERROR_MESSAGE = "The user id '%s' does not represent a valid user.";

	/*
	 * This is the error message for an Unexpected internal system error.
	 */
	public static final String SYSTEM_ERROR_MESSAGE = "There was an Internal System Error with ID %s. Please contact the HelpDesk for details.";
}
