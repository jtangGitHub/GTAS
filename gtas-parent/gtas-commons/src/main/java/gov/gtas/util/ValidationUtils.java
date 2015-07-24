package gov.gtas.util;

import org.apache.commons.lang3.StringUtils;

import gov.gtas.model.udr.enumtype.EntityLookupEnum;

/**
 * Utility methods for validation.
 * @author GTAS3 (AB)
 *
 */
public class ValidationUtils {
    public static boolean isStringInList(final String target, String... args){
    	boolean ret = false;
    	if(target != null){
    		for(String match:args){
    			if(target.equals(match)){
    				ret = true;
    				break;
    			}
    		}
    	}
    	return ret;
    }
    public static boolean isStringTruthy(final String target){
    	return isStringInList(target, "TRUE","True","T","true","Yes","Y","yes","YES");
    }
    
    public static EntityLookupEnum convertStringToEnum(String enumVal){
    	if(StringUtils.isNotEmpty(enumVal)){
	    	for(EntityLookupEnum ent:EntityLookupEnum.values()){
	    		if(enumVal.equalsIgnoreCase(ent.toString())){
	    			return ent;
	    		}
	    	}
    	}
    	return null;
    }
}
