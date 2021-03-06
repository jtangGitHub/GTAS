package gov.gtas.rule.builder;

import java.util.HashSet;
import java.util.Set;

import gov.gtas.constant.RuleServiceConstants;

public class RuleTemplateConstants {
    public static final String SEAT_ENTITY_NAME = "Seat";
    public static final String SEAT_ATTRIBUTE_NAME = "number";
    
    public static final String PASSENGER_VARIABLE_NAME = "$p";
    public static final String DOCUMENT_VARIABLE_NAME = "$d";
    public static final String FLIGHT_VARIABLE_NAME = "$f";
    public static final String ADDRESS_VARIABLE_NAME = "$addr";
    public static final String PHONE_VARIABLE_NAME = "$ph";
    public static final String EMAIL_VARIABLE_NAME = "$e";
    public static final String CREDIT_CARD_VARIABLE_NAME = "$cc";
    public static final String TRAVEL_AGENCY_VARIABLE_NAME = "$ta";
    public static final String FREQUENT_FLYER_VARIABLE_NAME = "$ff";
    public static final String PNR_VARIABLE_NAME = "$pnr";
    public static final String SEAT_VARIABLE_NAME = "$seat";

    public static final String LINK_VARIABLE_SUFFIX = "link";
    public static final String LINK_PNR_ID = "pnrId";
    public static final String LINK_ATTRIBUTE_ID = "linkAttributeId";

    private RuleTemplateConstants() {
        // to prevent instantiation.
    }
    public static final Set<String> YES_SET;
    static{
        YES_SET = new HashSet<String>();
        for(String member: new String[]{"Y", "y", "Yes", "YES"}){
            YES_SET.add(member);
        }
        
    }
    public static final char COLON_CHAR = ':';
    public static final char DOUBLE_QUOTE_CHAR = '"';
    public static final char SINGLE_QUOTE_CHAR = '\'';
    public static final char LEFT_PAREN_CHAR = '(';
    public static final char RIGHT_PAREN_CHAR = ')';
    public static final char SPACE_CHAR = ' ';
    public static final char COMMA_CHAR = ',';

    public static final String REGEX_WILDCARD = ".*";

    public static final String NEW_LINE = "\n";
    public static final String TRUE_STRING = "true";
    public static final String FALSE_STRING = "false";
        
    public static final String RULE_PACKAGE_NAME = "package gov.gtas.rule;\n";
    public static final String IMPORT_PREFIX = "import ";
    public static final String GLOBAL_RESULT_DECLARATION = "global java.util.List "+RuleServiceConstants.RULE_RESULT_LIST_NAME+";\n\n";
    
}
