package gov.gtas.error;

/**
 * The error Handler
 * 
 * @author GTAS3 (AB)
 *
 */
public interface ErrorHandler {
	/**
	 * Creates the exception message for the indicated error.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param args
	 *            the error arguments providing context for the error.
	 * @return the error exception object.
	 */
	CommonServiceException createException(final String errorCode,
			final Object... args);

	/**
	 * Creates the exception message for the indicated error.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param cause
	 *            The underlying exception.
	 * @param args
	 *            the error arguments providing context for the error.
	 * @return the error exception object.
	 */
	CommonServiceException createException(final String errorCode,
			final Exception cause, final Object... args);

	/**
	 * Adds a error handling delegate to the error handler.
	 * 
	 * @param errorHandler
	 *            the delegate.
	 */
	void addErrorHandlerDelegate(ErrorHandler errorHandler);

	/**
	 * Analyzes the error and produces detailed diagnostics.
	 * 
	 * @param exception
	 *            the error.
	 * @return the diagnostics.
	 */
	ErrorDetailInfo processError(Exception exception);
	
	/**
	 * Records and logs error.
	 * 
	 * @param code
	 *            the error code.
	 * @param description
	 *            the error description.
	 * @param details
	 *            the error details.
	 * @return the diagnostics.
	 */
	ErrorDetailInfo processError(String code, String description, String[] details);
}
