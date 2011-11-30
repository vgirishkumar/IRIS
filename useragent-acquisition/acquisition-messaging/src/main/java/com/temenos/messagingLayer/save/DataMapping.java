package com.temenos.messagingLayer.save;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;

import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.PreviousAddress;
import com.temenos.ebank.message.AcquisitionRequest;
import com.temenos.messagingLayer.mappingpojo.Applicant;
import com.temenos.messagingLayer.mappingpojo.FieldMapping;
import com.temenos.messagingLayer.mappingpojo.Group;
import com.temenos.messagingLayer.mappingpojo.T24Constant;
import com.temenos.messagingLayer.pojo.Ofsml13TransactionInputRequest;
import com.temenos.messagingLayer.response.T24ResponseUtils;

/**
 * Front end object mapped to T24 fields
 * 
 * @author anitha
 * 
 */
public class DataMapping {

	public static int SINGLE_VALUE = 1;
	SetT24FieldValues setFldVal = new SetT24FieldValues();
	CalculateAddressDuration calAddrDuration = new CalculateAddressDuration();

	public static String startDate;

	public void mapFieldValues(Ofsml13TransactionInputRequest transReq, AcquisitionRequest acquisitionRequest,
			Group group) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
			ParseException {
		List<FieldMapping> fieldMapings = group.getFieldMapping();
		Calendar calendar = Calendar.getInstance();
		String[] multipleValues = null;
		Application a = acquisitionRequest.getApplication();
		for (Iterator<FieldMapping> iterFieldMappings = fieldMapings.iterator(); iterFieldMappings.hasNext();) {
			String frontEndValue = null;
			FieldMapping fieldMapping = iterFieldMappings.next();
			String frontEndMethods = fieldMapping.getFrontEndObject();
			String addressToDate = null;
			List<String> addFromToDateArr = new ArrayList<String>();
			List<JAXBElement<String>> t24Fields = fieldMapping.getT24Field();
			String conversion = (String) fieldMapping.getConversion();
			if (conversion.equals("yes") && fieldMapping.getConversionType().equals("currency")) {
				mapCurrencyFields(a, t24Fields, frontEndMethods, transReq);
			} else {
				int arrayIdx = 0;
				if (frontEndMethods.contains("AcquisitionRequest")) {
					frontEndValue = callGetMethod(acquisitionRequest, frontEndMethods.split("~")[1]);
				} else if (frontEndMethods.contains("~")) {
					// AcquisitionRequest~AdditionalInfo
					frontEndValue = callGetRecursively(a, frontEndMethods);
				} else {
					frontEndValue = callGetMethod(a, frontEndMethods);
				}

				if (conversion.equals("yes") && frontEndValue != null) {
					String conversionType = (String) fieldMapping.getConversionType();
					int monthsToMove = 0;
					if (conversionType.equals("duration")) {
						startDate = T24ResponseUtils.getT24DateFormat().format(calendar.getTime());
						addFromToDateArr.add(startDate);
						int monthsToMoveBack = Integer.parseInt(frontEndValue);
						addressToDate = doConversion(conversionType, frontEndValue, startDate, -monthsToMoveBack);
						addFromToDateArr.add(addressToDate);
						startDate = calAddrDuration.getNextStartDate(addressToDate);
					} else if (conversionType.equals("multiple")) {
						if (frontEndValue != null && frontEndValue.contains(";")) {
							multipleValues = frontEndValue.split(";");
						}
					} else {
						frontEndValue = doConversion(conversionType, frontEndValue, startDate, monthsToMove);
					}
				}
				for (Iterator<JAXBElement<String>> t24FieldNames = t24Fields.iterator(); t24FieldNames.hasNext();) {
					String t24Field = t24FieldNames.next().getValue().toString();

					if (addFromToDateArr.size() != 0) {
						if (t24Fields.size() == 1) {
							frontEndValue = addFromToDateArr.get(1).toUpperCase();
						} else {
							frontEndValue = addFromToDateArr.get(arrayIdx).toUpperCase();
						}
					}
					if (frontEndValue != null) {
						frontEndValue = frontEndValue.replaceAll("_", "\'_\'");
					}
					if (multipleValues != null) {
						for (int arrayPos = 0; arrayPos < multipleValues.length; arrayPos++) {
							setFldVal.setFieldNameAndValue(transReq, t24Field, multipleValues[arrayPos], arrayPos + 1,
									SINGLE_VALUE);
						}
						multipleValues = null;
					} else {
						setFldVal.setFieldNameAndValue(transReq, t24Field, frontEndValue, SINGLE_VALUE, SINGLE_VALUE);
					}
					arrayIdx++;
				}
			}
		}
	}

	private void mapCurrencyFields(Object a, List<JAXBElement<String>> t24Fields, String frontEndMethods,
			Ofsml13TransactionInputRequest transReq) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, SecurityException, NoSuchMethodException {
		String frontEndMethod = "get" + frontEndMethods;
		Method method = a.getClass().getMethod(frontEndMethod);
		List<String> frontEndValue = (List<String>) method.invoke(a);
		int mvValue = 0;
		int svValue = 0;
		for (int currencyPos = 0; currencyPos < frontEndValue.size(); currencyPos++) {
			setFldVal.setFieldNameAndValue(transReq, t24Fields.get(0).getValue(), frontEndValue.get(currencyPos),
					++mvValue, svValue);
		}
	}

	public void mapFieldValuesForPrevAdd(Ofsml13TransactionInputRequest transReq, PreviousAddress prevAddress,
			Group getGroup, int prevAdd) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, ParseException {
		int mvValue = prevAdd + 2;
		List<FieldMapping> fieldNames = getGroup.getFieldMapping();
		for (Iterator<FieldMapping> iterFieldNames = fieldNames.iterator(); iterFieldNames.hasNext();) {
			String frontEndValue = null;
			String addressToDate = null;
			List<String> addFromToDateArr = new ArrayList<String>();
			FieldMapping fieldMapping = iterFieldNames.next();
			String frontEndMethod = fieldMapping.getFrontEndObject();
			List<JAXBElement<String>> t24Fields = fieldMapping.getT24Field();
			frontEndMethod = "get" + frontEndMethod;
			Method method = prevAddress.getClass().getMethod(frontEndMethod);
			if (method.invoke(prevAddress) != null && method.invoke(prevAddress) != "") {
				frontEndValue = method.invoke(prevAddress).toString();
			}
			String conversion = (String) fieldMapping.getConversion();
			if (conversion.equals("yes")) {
				String conversionType = (String) fieldMapping.getConversionType();
				if (conversionType.equals("duration")) {
					addFromToDateArr.add(startDate);
					int monthsToMoveBack = Integer.parseInt(frontEndValue);
					addressToDate = doConversion(conversionType, frontEndValue, startDate, -monthsToMoveBack);
					addFromToDateArr.add(addressToDate);
					startDate = calAddrDuration.getNextStartDate(addressToDate);
				} else {
					frontEndValue = doConversion(conversionType, frontEndValue, null, 0);
				}
			}
			int arrayIdx = 0;
			for (Iterator<JAXBElement<String>> t24FieldNames = t24Fields.iterator(); t24FieldNames.hasNext();) {
				if (addFromToDateArr.size() != 0) {
					frontEndValue = addFromToDateArr.get(arrayIdx).toUpperCase();
				}
				String t24Field = t24FieldNames.next().getValue().toString();

				setFldVal.setFieldNameAndValue(transReq, t24Field, frontEndValue, mvValue, SINGLE_VALUE);
				arrayIdx++;
			}
		}
	}

	public String callGetMethod(Object a, String frontEndMethods) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		// there is no point in trying to access a null's properties (it's rather impossible)
		if (a == null) {
			return null;
		}
		String frontEndValue = null;
		String frontEndMethod = "get" + frontEndMethods;
		Method method = a.getClass().getMethod(frontEndMethod);
		// avoid multiple invocations of the reflected method!
		Object methodInvocationResult = method.invoke(a);
		if (methodInvocationResult != null && !"".equals(methodInvocationResult)) { // TODO why test for empty String ?
			frontEndValue = objectToString(methodInvocationResult);
		}
		return frontEndValue;
	}

	private String callGetRecursively(Object a, String frontEndMethods) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		// there is no point in trying to access a null's properties (it's rather impossible)
		if (a == null) {
			return null;
		}
		String[] methodNames = frontEndMethods.split("~");
		Object frontEndInterObj = a;
		for (int methodCnt = 0; methodCnt < methodNames.length - 1; methodCnt++) {
			String frontEndMethod = "get" + methodNames[methodCnt];
			Method method = frontEndInterObj.getClass().getMethod(frontEndMethod);
			frontEndInterObj = method.invoke(frontEndInterObj);
			if (frontEndInterObj == null) {
				// when we first encounter a null, there is no point in continuing on the method chain
				return null;
			}
		}

		// if we reach this point, frontEndInterObj is not null and we need to invoke just one last method, converting its result to String
		return callGetMethod(frontEndInterObj, methodNames[methodNames.length - 1]);
	}

	private static String objectToString(Object frontEndInterObj) {
		if (frontEndInterObj instanceof Date) {
			return T24ResponseUtils.getWebDateFormat().format((Date) frontEndInterObj);
		} else {
			// TODO returned value relies on toString implementation. Could be problematic for decimals? 
			return frontEndInterObj.toString();
		}
	}

	private String doConversion(String conversionType, String frontEndValue, String startDate, int month)
			throws ParseException {
		if (conversionType.equals("date")) {
			Date dateValue = T24ResponseUtils.getWebDateFormat().parse(frontEndValue);
			frontEndValue = ((T24ResponseUtils.getT24DateFormat().format(dateValue))).toUpperCase();
		} else if (conversionType.equals("gender")) {
			if (frontEndValue == "F") {
				frontEndValue = "FEMALE";
			} else {
				frontEndValue = "MALE";
			}
		} else if (conversionType.equals("boolean") && frontEndValue != null) {
			if (frontEndValue.equals("false")) {
				frontEndValue = "NO";
			} else {
				frontEndValue = "YES";
			}
		} else if (conversionType.equals("string")) {
			if (frontEndValue != null && frontEndValue != "") {
				frontEndValue = frontEndValue.toString();
			}
		} else if (conversionType.equals("booleanReverse") && frontEndValue != null) {
			if (frontEndValue.equals("false")) {
				frontEndValue = "YES";
			} else {
				frontEndValue = "NO";
			}
		} else if (conversionType.equals("duration")) {
			frontEndValue = calAddrDuration.getAddressDuration(startDate, month);
		} else if (conversionType.equals("booleanSingle")) {
			if (frontEndValue != null && frontEndValue.equals("true")) {
				frontEndValue = "YES";
			} else {
				frontEndValue = "";
			}
		}

		return frontEndValue;
	}

	public Ofsml13TransactionInputRequest doMapping(List<Applicant> applicant, Ofsml13TransactionInputRequest transReq,
			AcquisitionRequest acquisitionRequest) {
		for (Iterator<Applicant> iterApp = applicant.iterator(); iterApp.hasNext();) {
			Application a = acquisitionRequest.getApplication();

			Applicant applicantTypes = iterApp.next();
			String applicantType = applicantTypes.getSingleOrJoint();
			try {
				if (applicantType.equals("single")) {
					List<Group> groups = applicantTypes.getGroup();
					for (Iterator<Group> iterGroups = groups.iterator(); iterGroups.hasNext();) {
						Group group = iterGroups.next();
						String groupType = group.getType();
						if (groupType.equals("mandatory")) {
							readConstantFields(transReq, a, group);
							mapFieldValues(transReq, acquisitionRequest, group);
						} else {
							String condition = group.getCondition();
							checkConditions(transReq, acquisitionRequest, group, condition);
						}
					}
				} else if (applicantType.equals("joint")) {
					List<Group> groups = applicantTypes.getGroup();
					if (!a.getIsSole()) {
						for (Iterator<Group> iterGroups = groups.iterator(); iterGroups.hasNext();) {
							Group group = iterGroups.next();
							String groupType = group.getType();
							if (groupType.equals("mandatory")) {
								mapFieldValues(transReq, acquisitionRequest, group);
							} else {
								String condition = group.getCondition();
								checkConditions(transReq, acquisitionRequest, group, condition);
							}

						}
					} else {
						for (Iterator<Group> iterGroups = groups.iterator(); iterGroups.hasNext();) {
							Group group = iterGroups.next();
							mapChangedFields(transReq, group);
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Error mapping front end objects with t24 fields ", e);
			}
		}
		return transReq;
	}

	public void readConstantFields(Ofsml13TransactionInputRequest transReq, Application a, Group group) {
		List<T24Constant> t24Constants = group.getT24Constant();
		for (Iterator<T24Constant> iterT24Constants = t24Constants.iterator(); iterT24Constants.hasNext();) {
			T24Constant t24Constant = iterT24Constants.next();
			String field = t24Constant.getT24Field();
			String value = t24Constant.getT24Value();
			setFldVal.setFieldNameAndValue(transReq, field, value, SINGLE_VALUE, SINGLE_VALUE);
		}
	}

	private void checkConditions(Ofsml13TransactionInputRequest transReq, AcquisitionRequest acquisitionRequest,
			Group getGroup, String condition) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, ParseException {
		Application a = acquisitionRequest.getApplication();
		String conditionValue;
		if (condition.contains("~")) {
			conditionValue = callGetRecursively(a, condition);
		} else {
			conditionValue = callGetMethod(a, condition);
		}
		String conditionOperand = getGroup.getOperand();
		String evaluationValue = getGroup.getValue();
		boolean conditionPassed = false;
		if (conditionOperand.equals("boolean") && evaluationValue != null && evaluationValue.equals("not")
				&& conditionValue != "true") {
			conditionPassed = true;
		} else if (conditionOperand.equals("boolean") && conditionValue != null && conditionValue.equals("true")) {
			conditionPassed = true;
		} else if (conditionOperand.equals("notEquals") && conditionValue != evaluationValue) {
			conditionPassed = true;
		} else if (conditionOperand.equals("equals") && conditionValue != null
				&& conditionValue.equals(evaluationValue)) {
			conditionPassed = true;
		}
		if (conditionPassed) {
			if (condition.equals("Customer~PreviousAddresses")) {
				PreviousAddress[] prevAddresses = a.getCustomer().getPreviousAddresses();
				if (prevAddresses != null) {
					int previousAddresses = prevAddresses.length;
					for (int prevAdd = 0; prevAdd < previousAddresses; prevAdd++) {
						if (prevAddresses[prevAdd] != null) {
							mapFieldValuesForPrevAdd(transReq, prevAddresses[prevAdd], getGroup, prevAdd);
						}
					}
				}
			} else {
				mapFieldValues(transReq, acquisitionRequest, getGroup);
			}
		} else {
			mapChangedFields(transReq, getGroup);
		}
	}

	private void mapChangedFields(Ofsml13TransactionInputRequest transReq, Group group) {
		List<FieldMapping> fieldMapings = group.getFieldMapping();
		for (Iterator<FieldMapping> iterFieldMappings = fieldMapings.iterator(); iterFieldMappings.hasNext();) {
			FieldMapping fieldMapping = iterFieldMappings.next();
			List<JAXBElement<String>> t24Fields = fieldMapping.getT24Field();
			for (Iterator<JAXBElement<String>> t24FieldNames = t24Fields.iterator(); t24FieldNames.hasNext();) {
				String t24Field = t24FieldNames.next().getValue().toString();
				setFldVal.setFieldNameAndValue(transReq, t24Field, "NULL", SINGLE_VALUE, SINGLE_VALUE);
			}
		}
	}

}
