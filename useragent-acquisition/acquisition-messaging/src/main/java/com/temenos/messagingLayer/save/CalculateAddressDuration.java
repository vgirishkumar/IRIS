package com.temenos.messagingLayer.save;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Utility class for computing the period a client resided to an address
 * 
 * @author anitha
 * 
 */
public class CalculateAddressDuration {

	// TODO: unify this with the T24ResponseUtils date format
	public static SimpleDateFormat T24_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

	/**
	 * TODO: comment what is this method supposed to do?
	 * 
	 * @param startDate
	 *            Starting date
	 * @param months
	 *            Number of months the client resided at the specified address
	 * @return
	 */
	Calendar calendar = Calendar.getInstance();

	public String getAddressDuration(String startDate, int month) throws ParseException {
		String calculatedDuration = null;
		calendar.setTime(T24_DATE_FORMAT.parse(startDate));
		calendar.add(Calendar.MONTH, month);
		calculatedDuration = T24_DATE_FORMAT.format(calendar.getTime());
		return calculatedDuration;
	}

	public String getNextStartDate(String currentStartDate) throws ParseException {
		String nextStartDate = null;
		calendar.setTime(T24_DATE_FORMAT.parse(currentStartDate));
		calendar.add(Calendar.DATE, -1);
		nextStartDate = T24_DATE_FORMAT.format(calendar.getTime());
		return nextStartDate;
	}

}
