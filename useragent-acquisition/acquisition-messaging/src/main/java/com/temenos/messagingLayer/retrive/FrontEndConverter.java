package com.temenos.messagingLayer.retrive;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.temenos.messagingLayer.response.T24ResponseUtils;

/**
 * Data type conversion is done before setting the data to front end object
 * @author karthikeyan
 *
 */

public class FrontEndConverter {

	public static String doConversion(String frontEndValue, String conversionType) throws ParseException {
		if (conversionType.equals("date")) {
			Date dateValue = T24ResponseUtils.getT24DateFormat().parse(frontEndValue);
			frontEndValue = ((T24ResponseUtils.getWebDateFormat().format(dateValue))).toUpperCase();
		} else if (conversionType.equals("gender")) {
			if (frontEndValue.equalsIgnoreCase("FEMALE")) {
				frontEndValue = "F";
			} else {
				frontEndValue = "M";
			}
		} else if (conversionType.equals("booleanReverse")) {
			if (frontEndValue.equals("YES")) {
				frontEndValue = "NO";
			} else {
				frontEndValue = "YES";
			}
		} else if (conversionType.equals("String")) {
			if (frontEndValue != null && frontEndValue != "") {
				frontEndValue = frontEndValue.toString();
			}
		} else if (conversionType.equals("booleanSingle")) {
			if (frontEndValue.equals("true")) {
				frontEndValue = "YES";
			} else {
				frontEndValue = "";
			}
		} else if (conversionType.equals("Currency")) {
			frontEndValue = frontEndValue.replace("~~", ";");
		}
		return frontEndValue;
	}

	// FIXME why use double for the return type?!?!?
	public static double doDateConversion(String resFromDate, String resToDate) throws ParseException {
		DateFormat dateFormat = T24ResponseUtils.getT24DateFormat();
		Date date1 = dateFormat.parse(resFromDate);
		Date date2 = dateFormat.parse(resToDate);
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date2);
		double monthDiff = Math.abs(calendar2.get(Calendar.MONTH) - calendar1.get(Calendar.MONTH)
				+ (calendar2.get(Calendar.YEAR) - calendar1.get(Calendar.YEAR)) * 12);
		return monthDiff;
	}

	public static String[] doCurrencyConversion(String frontEndValues) {
		String[] currencies = frontEndValues.split("~~");
		return currencies;
	}

}
