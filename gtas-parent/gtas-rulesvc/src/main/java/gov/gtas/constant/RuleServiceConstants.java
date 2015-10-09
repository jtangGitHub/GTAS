package gov.gtas.constant;


/**
 * Constants used in the Rule Service module.
 * 
 * @author GTAS3 (AB)
 *
 */
public class RuleServiceConstants {
	public static final String DEFAULT_RULESET_NAME = "gov/gtas/rule/gts.drl";
	/*
	 * All generated rules depend on this global object for returning results.
	 * When a knowledge session is created the global object should be created and associated with
	 * the session:
	 * ksession.setGlobal(RuleServiceConstants.RULE_RESULT_LIST_NAME, new ArrayList<Object>());
	 * The global can then be accessed after the rules are run by:
	 * (List<?>) ksession.getGlobal(RuleServiceConstants.RULE_RESULT_LIST_NAME);
	 */
	public static final String RULE_RESULT_LIST_NAME = "resultList";
	// //////////////////////////////////////////////////////////////////////////////////////
	// KNOWLEDGE Management
	// //////////////////////////////////////////////////////////////////////////////////////
	/* The Knowledge session name configured in META-INF/module.xml */
	public static final String KNOWLEDGE_SESSION_NAME = "GtasKS";
	
	/* The root path for the KieFileSystem files. */
	public static final String KIE_FILE_SYSTEM_ROOT = "src/main/resources/";
	// //////////////////////////////////////////////////////////////////////////////////////
	// ERROR CODES
	// //////////////////////////////////////////////////////////////////////////////////////
	/*
	 * This is the error code for an internal system error indicating IO error
	 * during the creation of the Knowledge Base.
	 */
	public static final String KB_CREATION_IO_ERROR_CODE = "KB_CREATION_IO_ERROR";
	/*
	 * This is the error code for an internal system error indicating that the
	 * UDR generated rule could not be compiled.
	 */
	public static final String RULE_COMPILE_ERROR_CODE = "RULE_COMPILE_ERROR";

	/*
	 * This is the error code for an internal system error indicating that the
	 * UDR generated knowledge base could not be retrieved from the data base.
	 */
	public static final String KB_NOT_FOUND_ERROR_CODE = "KB_NOT_FOUND";
	/*
	 * This is the error code for an internal system error indicating that 
	 * an API or PNR message intended for processing could not be retrieved from the data base.
	 */
	public static final String MESSAGE_NOT_FOUND_ERROR_CODE = "MESSAGE_NOT_FOUND";
	/*
	 * This is the error code for an internal system error indicating that the
	 * UDR generated knowledge base retrieved from the data base has invalid content.
	 */
	public static final String KB_INVALID_ERROR_CODE = "KB_INVALID_ERROR";
	/*
	 * This is the error code for an internal system error indicating that the
	 * UDR generated knowledge base could not de-serialized from the DB record.
	 */
	public static final String KB_DESERIALIZATION_ERROR_CODE = "KB_DESERIALIZATION_ERROR";

	public static final String INCOMPLETE_TREE_ERROR_CODE = "INCOMPLETE_TREE_ERROR";
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// ERROR Messages
	// //////////////////////////////////////////////////////////////////////////////////////
	/*
	 * This is the error message for an internal system error indicating IO error
	 * during the creation of the Knowledge Base.
	 */
	public static final String KB_CREATION_IO_ERROR_MESSAGE = "IO error while creating KIE Knowledge Base (details:%s).";
	/*
	 * This is the error message for an internal system error indicating that the
	 * UDR generated rule could not be compiled.
	 */
	public static final String RULE_COMPILE_ERROR_MESSAGE = "The rule file '%s' could not be compiled.";
	/*
	 * This is the error message for an internal system error indicating that the
	 * UDR generated knowledge base could not be retrieved from the data base.
	 */
	public static final String KB_NOT_FOUND_ERROR_MESSAGE = "The Knowledge Base named '%s' could not be retrieved from the data base.";
	
	/*
	 * This is the error messsage for an internal system error indicating that 
	 * an API or PNR message intended for processing could not be retrieved from the data base.
	 */
	public static final String MESSAGE_NOT_FOUND_ERROR_MESSAGE= "The API/PNR message with ID '%d' could not be retrieved from the data base.";

	/*
	 * This is the error message for an internal system error indicating that the
	 * UDR generated knowledge base retrieved from the data base has invalid content.
	 */
	public static final String KB_INVALID_ERROR_MESSAGE = "The Knowledge Base with name '%s' has invalid rule or KieBase content.";
	/*
	 * This is the error code for an internal system error indicating that the
	 * UDR generated knowledge base could not de-serialized from the DB record.
	 */
	public static final String KB_DESERIALIZATION_ERROR_MESSAGE = "The Kie Knowledge Base could not be de-serialized from the DB record with ID = $d";

	public static final String INCOMPLETE_TREE_ERROR_MESSAGE = "The query tree is incomplete at level %d.";
	
}
