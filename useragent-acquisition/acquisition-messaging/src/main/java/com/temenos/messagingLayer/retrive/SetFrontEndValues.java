package com.temenos.messagingLayer.retrive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import com.temenos.ebank.domain.Address;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.PreviousAddress;
import com.temenos.messagingLayer.response.T24ResponseUtils;

/**
 * Sets the value to the front end objects
 * @author karthikeyan
 *
 */
public class SetFrontEndValues {
	public static String[] fromDate = null;

	public static void callSetMethod(Application a, String frontEndMethod, String frontEndValue, String dataType,
			String conversionType) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
			SecurityException, IllegalArgumentException, ParseException {
		frontEndMethod = "set" + frontEndMethod;
		Method method = null;
		invokeMethods(method, a, dataType, frontEndMethod, frontEndValue, conversionType);
	}

	public static void callSetRecursively(Application a, String frontEndMethods, String frontEndValue, String dataType,
			String conversionType) throws SecurityException, NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, ParseException {
		String frontEndMethod;
		String[] methodNames = frontEndMethods.split("~");
		Object frontEndInterObj = a;
		int nameCount = methodNames.length;
		Method method = null;
		for (int methodCnt = 0; methodCnt < nameCount; methodCnt++) {
			if (methodCnt != (methodNames.length - 1)) {
				frontEndMethod = "get" + methodNames[methodCnt];
				method = frontEndInterObj.getClass().getMethod(frontEndMethod);
				frontEndInterObj = method.invoke(frontEndInterObj);
			} else {
				frontEndMethod = "set" + methodNames[methodCnt];
				invokeMethods(method, frontEndInterObj, dataType, frontEndMethod, frontEndValue, conversionType);
			}
		}
	}

	public static void callSetAddress(Application a, Address address, PreviousAddress[] previousAddress,
			String frontEndMethods, String[] frontEndValues) throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		String firstMultiValue = null;
		int MultiValueCount = frontEndValues.length;
		int preAddPos, frontEndPos;
		firstMultiValue = frontEndValues[0];
		String frontEndMethod;
		Method method = null;
		String[] methodNames = frontEndMethods.split("~");
		frontEndMethod = "set" + methodNames[2];
		Class[] type = new Class[] { String.class };
		method = address.getClass().getDeclaredMethod(frontEndMethod, type);
		method.invoke(address, firstMultiValue);

		for (frontEndPos = 1, preAddPos = 0; frontEndPos < MultiValueCount; frontEndPos++, preAddPos++) {
			if (previousAddress[preAddPos] == null) {
				previousAddress[preAddPos] = new PreviousAddress();
			}
			method = address.getClass().getDeclaredMethod(frontEndMethod, type);
			method.invoke(previousAddress[preAddPos], frontEndValues[frontEndPos]);
		}

	}

	public static void setAddressPeriods(Application a, Address address, PreviousAddress[] previousAddress,
			String frontEndMethods, String[] frontEndValues) throws ParseException, SecurityException,
			NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		try {
			if (fromDate == null) {
				fromDate = frontEndValues;
			} else {
				Object frontEndInterObj = a;
				int dateCount;
				Method method = null;
				String[] methodNames = frontEndMethods.split("~");
				Class[] type = new Class[] { Integer.class };
				method = frontEndInterObj.getClass().getDeclaredMethod("get" + methodNames[0]);
				frontEndInterObj = method.invoke(frontEndInterObj);
				method = frontEndInterObj.getClass().getDeclaredMethod("set" + methodNames[1], type);
				method.invoke(frontEndInterObj,
						(int) (FrontEndConverter.doDateConversion(fromDate[0], frontEndValues[0])));

				for (dateCount = 1; dateCount < fromDate.length; dateCount++) {
					if (previousAddress[dateCount - 1] == null) {
						previousAddress[dateCount - 1] = new PreviousAddress();
					}
					previousAddress[dateCount - 1].setDuration((int) (FrontEndConverter.doDateConversion(
							fromDate[dateCount], frontEndValues[dateCount])));
				}
				fromDate = null;
			}
		} catch (Exception e) {
			fromDate = null;
			throw new RuntimeException("Error in setting address duration " + e);
		}

	}

	public static void invokeMethods(Method method, Object a, String dataType, String frontEndMethod,
			String frontEndValue, String conversionType) throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
		if (dataType.equals("Long")) {
			Class[] types = new Class[] { Long.class };
			method = a.getClass().getDeclaredMethod(frontEndMethod, types);
			Long frontLong = new Long(frontEndValue);
			method.invoke(a, frontLong);
		} else if (dataType.equals("String")) {
			Class[] types = new Class[] { String.class };
			method = a.getClass().getDeclaredMethod(frontEndMethod, types);
			frontEndValue = FrontEndConverter.doConversion(frontEndValue, conversionType);
			method.invoke(a, frontEndValue);
		} else if (dataType.equalsIgnoreCase("Boolean")) {
			Class[] types = new Class[] { Boolean.class };
			method = a.getClass().getDeclaredMethod(frontEndMethod, types);
			if (conversionType.equals("booleanReverse")) {
				frontEndValue = FrontEndConverter.doConversion(frontEndValue, conversionType);
			}
			boolean frontBoolean = false;
			if (frontEndValue.equals("YES")) {
				frontBoolean = true;
			}
			method.invoke(a, frontBoolean);
		} else if (dataType.equalsIgnoreCase("Integer")) {
			Class[] types = new Class[] { Integer.class };
			method = a.getClass().getDeclaredMethod(frontEndMethod, types);
		} else if (dataType.equalsIgnoreCase("BigDecimal")) {
			Class[] types = new Class[] { BigDecimal.class };
			method = a.getClass().getDeclaredMethod(frontEndMethod, types);
			frontEndValue = (String) FrontEndConverter.doConversion(frontEndValue, conversionType);
			BigDecimal frontBig = new BigDecimal(frontEndValue);
			method.invoke(a, frontBig);

		} else if (dataType.equalsIgnoreCase("Date")) {
			Class[] types = new Class[] { Date.class };
			method = a.getClass().getDeclaredMethod(frontEndMethod, types);
			frontEndValue = (String) FrontEndConverter.doConversion(frontEndValue, conversionType);
			Date dateValue = T24ResponseUtils.getWebDateFormat().parse(frontEndValue);
			method.invoke(a, dateValue);
		} else if (dataType.equalsIgnoreCase("durationMonth")) {
			try {
				if (fromDate == null) {
					fromDate = new String[] { frontEndValue };
				} else {
					Class[] types = new Class[] { Integer.class };
					int frondDuration;
					method = a.getClass().getDeclaredMethod(frontEndMethod, types);
					frondDuration = (int) FrontEndConverter.doDateConversion(fromDate[0], frontEndValue);
					method.invoke(a, frondDuration);
					fromDate = null;
				}
			} catch (Exception e) {
				fromDate = null;
				throw new RuntimeException("Error in setting front end object  " + e);
			}
		}

	}
}
