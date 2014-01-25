package com.temenos.messagingLayer.response;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.temenos.ebank.exceptions.ResponseCode;
import com.temenos.ebank.message.AccountCreationResponse;
import com.temenos.ebank.message.AccountDetails;
import com.temenos.ebank.message.AccountDetailsUnderlying;
import com.temenos.ebank.message.AcquisitionRequest;
import com.temenos.ebank.message.AcquisitionResponse;
import com.temenos.messagingLayer.beans.Response;
import com.temenos.messagingLayer.mappingpojo.Applicant;
import com.temenos.messagingLayer.mappingpojo.FieldMapping;
import com.temenos.messagingLayer.mappingpojo.Group;
import com.temenos.messagingLayer.mappingpojo.Step;
import com.temenos.messagingLayer.pojo.Ofsml13ErrorField;
import com.temenos.messagingLayer.pojo.Ofsml13MVField;
import com.temenos.messagingLayer.pojo.Ofsml13OverrideField;
import com.temenos.messagingLayer.pojo.Ofsml13ServiceResponse;
import com.temenos.messagingLayer.pojo.Ofsml13TransactionFailedResponse;
import com.temenos.messagingLayer.pojo.Ofsml13TransactionProcessedResponse;
import com.temenos.messagingLayer.pojo.T24;
import com.temenos.messagingLayer.save.T24MappingObject;
import com.temenos.messagingLayer.util.MappingFactory;

/**
 * Utility class for parsing T42 responses
 * 
 * @author vionescu
 * 
 */
public class T24ResponseUtils {

	// Mentionning the locale is important, in order not to depend on server's default locale.
	// It also needs to match the locale used by T24 when formatting the OFS data as String.
	private static SimpleDateFormat T24_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
	// FIXME is it really necessary to have two date formats? could we keep just one (T24's)?
	private static SimpleDateFormat FRONT_END_DATE_FORMAT = new SimpleDateFormat("dd MM yyyy");

	public static T24 unmarshall(String xmlResponse) {
		T24 t24 = null;
		try {
			JAXBContext jc = JAXBContext.newInstance("com.temenos.messagingLayer.pojo");
			Unmarshaller u = jc.createUnmarshaller();
			StringBuffer xmlStr = new StringBuffer(xmlResponse);
			t24 = (T24) u.unmarshal(new StringReader(xmlStr.toString()));
		} catch (JAXBException e) {
			// e.printStackTrace();
			throw new RuntimeException("Error parsing T24 response", e);
		}
		return t24;
	}

	private final static String[] inEligibleListSingle = { "OP.AGE.SINGLE.INELIGIBLE", "OP.COUNTRY.SINGLE.INELIGIBLE",
			"OP.PRODUCT.SINGLE.INELIGIBLE", "OP.SINGLE.INCOME.INELIGIBLE" };

	private final static String[] inEligibleListJoint = { "OP.AGE.JOINT.INELIGIBLE", "OP.COUNTRY.JOINT.INELIGIBLE",
			"OP.JOINT.INCOME.INELIGIBLE", "OP.PRODUCT.JOINT.INELIGIBLE" };

	public static ResponseCode getResponseCode(Response xmlResponse, String requestCode) {
		ResponseCode responseCode = ResponseCode.EVERYTHING_OK;
		T24 t24 = unmarshall(xmlResponse.getMsg());
		Ofsml13ServiceResponse serviceResp = t24.getServiceResponse();
		if (serviceResp != null) {
			Ofsml13TransactionFailedResponse transactionFail = serviceResp.getOfsTransactionFailed();
			if (transactionFail != null) {
				if (transactionFail.getError() != null) {
					for (Iterator<Ofsml13ErrorField> iterT24FieldError = transactionFail.getError().iterator(); iterT24FieldError
							.hasNext();) {
						String T24FieldError = iterT24FieldError.next().getValue();
						if (T24FieldError.equals("WRONG ALPHANUMERIC CHAR.")) {
							return ResponseCode.INVALID_CHARACTERS;
						}
					}
				}
				return ResponseCode.VALIDATION_ERROR;
			}
			Ofsml13TransactionProcessedResponse transactionResp = serviceResp.getOfsTransactionProcessed();
			if (requestCode.equals("C1")) {
				List<Ofsml13OverrideField> overrideList = transactionResp.getOverride();

				if (!overrideList.isEmpty()) {
					String overrideMsg = overrideList.get(0).getValue();
					if (Arrays.binarySearch(inEligibleListSingle, overrideMsg) >= 0) {
						responseCode = ResponseCode.FIRST_HOLDER_INELIGIBLE;
					} else if (Arrays.binarySearch(inEligibleListJoint, overrideMsg) >= 0) {
						responseCode = ResponseCode.SECOND_HOLDER_INELIGIBLE;
					}
				} else {
					responseCode = ResponseCode.ELIGIBLE_OK;
				}
			} else if (requestCode.startsWith("S")) {

				responseCode = ResponseCode.SAVE_OK;

			}
		} else {
			responseCode = ResponseCode.TECHNICAL_ERROR;
		}
		return responseCode;
	}

	public static AcquisitionResponse parseTransactionResponse(Response xmlResponse,
			AcquisitionRequest acquisitionRequest, AcquisitionResponse acquisitionResponse, boolean mainApplication,
			MappingFactory mappingFactory) throws JAXBException, SecurityException, IllegalArgumentException,
			NoSuchMethodException, IllegalAccessException, InvocationTargetException {

		String requestCode = acquisitionRequest.getRequestCode();
		T24 t24 = unmarshall(xmlResponse.getMsg());
		Ofsml13ServiceResponse serviceResp = t24.getServiceResponse();
		Ofsml13TransactionProcessedResponse transactionResp = null;

		transactionResp = serviceResp.getOfsTransactionProcessed();
		if (transactionResp != null) {
			if (requestCode.equals("C1") || requestCode.equals("S1")) {
				acquisitionRequest.getApplication().setAppRef(transactionResp.getTransactionId().getValue());
			} else if (requestCode.equals("C6") && mainApplication) {
				acquisitionResponse = getResponseMappingData(acquisitionRequest, acquisitionResponse, transactionResp,
						6, mappingFactory, "T24Mapping.xml");

			} else if (!mainApplication) {
				acquisitionResponse = getResponseMappingData(acquisitionRequest, acquisitionResponse, transactionResp,
						1, mappingFactory, "T24CrossSellMapping.xml");
			}
		}
		return acquisitionResponse;
	}

	private static AcquisitionResponse getResponseMappingData(AcquisitionRequest acquisitionRequest,
			AcquisitionResponse acquisitionResponse, Ofsml13TransactionProcessedResponse transactionResp,
			int stepNumber, MappingFactory mappingFactory, String xmlPath) throws JAXBException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		List<Ofsml13MVField> t24Fields = transactionResp.getField();
		com.temenos.messagingLayer.mappingpojo.T24 t24RespMappingObj = T24MappingObject.getT24MappingObject(
				mappingFactory, xmlPath);
		List<Step> stepContent = t24RespMappingObj.getStep();
		Step step = stepContent.get(stepNumber);
		Applicant applicant = step.getApplicant().get(0);
		acquisitionResponse = setResponseBean(t24Fields, applicant, acquisitionResponse, acquisitionRequest);
		return acquisitionResponse;
	}

	// builds the response with account and customer information
	private static AcquisitionResponse setResponseBean(List<Ofsml13MVField> t24Fields, Applicant applicant,
			AcquisitionResponse acquisitionResponse, AcquisitionRequest acquisitionReq) throws SecurityException,
			IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		List<Group> groups = applicant.getGroup();
		List<AccountDetails> accountDetailsList = new ArrayList<AccountDetails>();
		AccountCreationResponse accountCreationResp = new AccountCreationResponse();
		AccountDetails accountDetails = new AccountDetails();

		List<String> currency = new ArrayList<String>();
		List<String> t24account = new ArrayList<String>();
		List<String> t24iban = new ArrayList<String>();
		List<String> t24sortcode = new ArrayList<String>();
		List<String> underlyingacctno = new ArrayList<String>();
		List<String> underlyingibanno = new ArrayList<String>();
		List<String> underlyingsortcode = new ArrayList<String>();
		List<String> underlyingcateg = new ArrayList<String>();
		List<String> documentList = new ArrayList<String>();
		List<String> secondDocumentList = new ArrayList<String>();
		for (Iterator<Group> iterGroup = groups.iterator(); iterGroup.hasNext();) {
			Group group = iterGroup.next();
			List<FieldMapping> fieldMappings = group.getFieldMapping();
			for (Iterator<FieldMapping> iterFieldMapping = fieldMappings.iterator(); iterFieldMapping.hasNext();) {
				FieldMapping fieldMapping = iterFieldMapping.next();
				Iterator<Ofsml13MVField> iterField = t24Fields.iterator();
				while (iterField.hasNext()) {
					Ofsml13MVField t24MVField = iterField.next();
					String t24MappingField = fieldMapping.getT24Field().get(0).getValue();
					String frontEndMapping = fieldMapping.getFrontEndObject();
					if (t24MVField.getName().equals(t24MappingField)) {
						String t24Value = t24MVField.getValue();
						int mvField = t24MVField.getMv().intValue();
						String[] frontEndObjects = frontEndMapping.split("~");
						String frontEndObjName = frontEndObjects[1];
						if (frontEndMapping.contains("AccountCreationResponse")) {
							if (t24MappingField.equals("DOCUMENTS")) {
								documentList.add(mvField - 1, t24Value);
							} else if (t24MappingField.equals("JOINT.DOCUMENTS")) {
								secondDocumentList.add(mvField - 1, t24Value);
							} else {
								accountCreationResp = (AccountCreationResponse) setResponse(accountCreationResp,
										frontEndObjName, t24Value);
							}
						} else {
							if (t24MappingField.equals("CURRENCY")) {
								currency.add(mvField - 1, t24Value);
							} else if (t24MappingField.equals("T24.ACCOUNT")) {
								t24account.add(mvField - 1, t24Value);
							} else if (t24MappingField.equals("T24.SORTCODE")) {
								t24sortcode.add(mvField - 1, t24Value);
							} else if (t24MappingField.equals("T24.IBAN")) {
								t24iban.add(mvField - 1, t24Value);
							} else if (t24MappingField.equals("UNDRLYING.ACCT.NO")) {
								underlyingacctno.add(mvField - 1, t24Value);
							} else if (t24MappingField.equals("UNDRLYING.IBAN.NO")) {
								underlyingibanno.add(mvField - 1, t24Value);
							} else if (t24MappingField.equals("UNDRLYING.SORTCODE")) {
								underlyingsortcode.add(mvField - 1, t24Value);
							} else if (t24MappingField.equals("UNDRLYING.CATEG")
									|| t24MappingField.equals("UNDRLYING.CATEGORY")) {
								underlyingcateg.add(mvField - 1, t24Value);
							}
						}

					}
				}

			}
		}
		for (int currencyPos = 0; currencyPos < currency.size(); currencyPos++) {
			accountDetails.setAccountCurrency(currency.get(currencyPos));
			accountDetails.setAccountNo(t24account.get(currencyPos));
			if (!acquisitionReq.getApplication().getProductRef().equals("FTD")) {
				accountDetails.setIbanNo(t24iban.get(currencyPos));
			}
			accountDetails.setSortCode(t24sortcode.get(currencyPos));
			accountDetailsList.add(currencyPos, accountDetails);
			accountDetails = new AccountDetails();

		}
		for (int underlyingAcPos = 0; underlyingAcPos < underlyingacctno.size(); underlyingAcPos++) {
			AccountDetailsUnderlying acDetailsUnderlying = new AccountDetailsUnderlying(currency.get(underlyingAcPos),
					underlyingsortcode.get(underlyingAcPos), underlyingacctno.get(underlyingAcPos),
					underlyingibanno.get(underlyingAcPos), underlyingcateg.get(underlyingAcPos));
			accountCreationResp.setUnderlyingAccount(acDetailsUnderlying);
		}
		accountCreationResp.setAccountList(accountDetailsList);
		accountCreationResp.setDocumentList(documentList);
		accountCreationResp.setSecondDocumentList(secondDocumentList);
		acquisitionResponse.setAdditionalInfo(accountCreationResp);

		return acquisitionResponse;
	}

	public static Object setResponse(Object accountResp, String frontEndObjName, String t24Value)
			throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		frontEndObjName = "set" + frontEndObjName;
		Class[] type = new Class[] { String.class };
		Method method = accountResp.getClass().getMethod(frontEndObjName, type);
		method.invoke(accountResp, t24Value);
		return accountResp;
	}

	public static DateFormat getT24DateFormat() {
		// date formats are not thread-safe, so we clone them
		return (DateFormat) T24_DATE_FORMAT.clone();
	}

	public static DateFormat getWebDateFormat() {
		// date formats are not thread-safe, so we clone them
		return (DateFormat) FRONT_END_DATE_FORMAT.clone();
	}
}
