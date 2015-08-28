package gov.gtas.model.udr.json.util;

import gov.gtas.constant.UdrErrorConstants;
import gov.gtas.error.CommonServiceException;
import gov.gtas.model.udr.json.MetaData;
import gov.gtas.util.DateCalendarUtils;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 * Validation utilities for the UDR JSON.
 * @author GTAS3 (AB)
 *
 */
public class JsonValidationUtils {
	
	public static void validateMetaData(final MetaData metaData, final boolean checkStartDate){
		if (metaData == null) {
			throw new CommonServiceException(
					UdrErrorConstants.NO_META_ERROR_CODE,
					UdrErrorConstants.NO_META_ERROR_MESSAGE);
		}
		final String title = metaData.getTitle();
		if (StringUtils.isEmpty(title)) {
			throw new CommonServiceException(
					UdrErrorConstants.NO_TITLE_ERROR_CODE,
					UdrErrorConstants.NO_TITLE_ERROR_MESSAGE);
		}

		final Date startDate = metaData.getStartDate();
		final Date endDate = metaData.getEndDate();
		
		validateDates(startDate, endDate, checkStartDate);
	}
	/**
	 * Checks the start aand end dates of the UDR meta data for validity.
	 * @param startDate the start date.
	 * @param endDate the end date.
	 * @param checkStartDate if true checks that the start date is greater than or equal to today.
	 */
    private static void validateDates(final Date startDate, final Date endDate, final boolean checkStartDate){
    	Date now = new Date();
		if (startDate == null) {
			throw new CommonServiceException(
					UdrErrorConstants.INVALID_START_DATE_ERROR_CODE,
					UdrErrorConstants.INVALID_START_DATE_ERROR_MESSAGE);
		}
        if(checkStartDate && DateCalendarUtils.dateRoundedGreater(now, startDate, Calendar.DATE)){
			throw new CommonServiceException(
					UdrErrorConstants.PAST_START_DATE_ERROR_CODE,
					UdrErrorConstants.PAST_START_DATE_ERROR_MESSAGE);
        	
        }
    	if(DateCalendarUtils.dateRoundedLess(endDate, startDate, Calendar.DATE)){
    			throw new CommonServiceException(
    					UdrErrorConstants.END_LESS_START_DATE_ERROR_CODE,
    					UdrErrorConstants.END_LESS_START_DATE_ERROR_MESSAGE);
            	    		
    	}
    }

}
