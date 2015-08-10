package gov.gtas.error;
/**
 * Error processing utility functions.
 * 
 * @author GTAS3 (AB)
 *
 */
public class ErrorUtils {
	/**
	 * Searches the exception cause chain to determine if any cause is of the given type.
	 * @param exception the exception to search.
	 * @param typeName the name of the exception class to search for.
	 * @return true if the exception type is found.
	 */
    public static boolean isExceptionOfType(Throwable exception, String typeName){
    	boolean ret = false;
    	Throwable cause = exception;
    	while(cause != null){
    		String name = cause.getClass().getName();
    		ret = name.indexOf(typeName) >= 0;
    		if(ret){
    			break;
    		} else {
    			cause = cause.getCause();
    		}
    	} 
    	return ret;
    }
}
