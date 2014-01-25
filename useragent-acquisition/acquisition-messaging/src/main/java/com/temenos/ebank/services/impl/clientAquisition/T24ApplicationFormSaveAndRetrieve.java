/**
 * 
 */
package com.temenos.ebank.services.impl.clientAquisition;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.temenos.ebank.domain.Address;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.Customer;
import com.temenos.ebank.domain.PreviousAddress;
import com.temenos.ebank.exceptions.ResponseCode;
import com.temenos.ebank.message.AcquisitionRequest;
import com.temenos.ebank.message.AcquisitionResponse;
import com.temenos.ebank.services.interfaces.clientAquisition.IServiceClientAquistion;
import com.temenos.messagingLayer.beans.Response;
import com.temenos.messagingLayer.enquiryrequest.ExtendedEnquiryRequest;
import com.temenos.messagingLayer.pojo.Ofsml13EnquiryStatus;
import com.temenos.messagingLayer.pojo.Ofsml13ExtendedEnquiryRecord;
import com.temenos.messagingLayer.pojo.Ofsml13ExtendedEnquiryResponse;
import com.temenos.messagingLayer.pojo.Ofsml13Fault;
import com.temenos.messagingLayer.pojo.Ofsml13IdRefValue;
import com.temenos.messagingLayer.pojo.Ofsml13ServiceResponse;
import com.temenos.messagingLayer.pojo.T24;
import com.temenos.messagingLayer.requestUtils.T24LangCode;
import com.temenos.messagingLayer.response.T24ResponseUtils;
import com.temenos.messagingLayer.retrive.SetFrontEndValues;
import com.temenos.messagingLayer.retrivepojo.RetriveEnquiry;
import com.temenos.messagingLayer.retrivepojo.RetriveFieldMapping;
import com.temenos.messagingLayer.save.T24ApplicationSave;
import com.temenos.messagingLayer.talkToServer.TalkToServer;
import com.temenos.messagingLayer.util.MappingFactory;

/**
 * Customer Acquisition
 * 
 * @author vionescu
 * 
 */
public class T24ApplicationFormSaveAndRetrieve implements IServiceClientAquistion {

	protected Log logger = LogFactory.getLog(getClass());

	private TalkToServer talkToServer;
	private MappingFactory mappingFactory;
	private T24LangCode t24LangCode;

	public boolean addrStart; // FIXME potential multi-thread issue !!!

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.temenos.ebank.services.interfaces.clientAquisition.IServiceClientAquistion#getApplicationByReference(java.lang.String, java.lang.String)
	 */
	public AcquisitionResponse getApplicationByReferenceAndEmail(String reference, String email) {
		// TODO: trebuie tot cu aquisition response facut
		AcquisitionResponse acquisitionResponse = new AcquisitionResponse();
		try {
			ExtendedEnquiryRequest enqReq = new ExtendedEnquiryRequest(talkToServer.getSecurityContext());
			String enquiryxml = enqReq.getEnquiryRequestForApplicationByRef(reference, email);
			Ofsml13ServiceResponse serviceResp = getServiceResponse(enquiryxml);
			Ofsml13Fault fault = serviceResp.getOfsFault();
			if (fault != null) {
				List<Serializable> faultMessage = serviceResp.getOfsFault().getOfsFaultMessage().getContent();
				if (faultMessage.get(0).equals("COMPLETED")) {
					acquisitionResponse.setResponseCode(ResponseCode.REFNO_UNAVAILABLE);
					return acquisitionResponse;
				} else if (faultMessage.get(0).equals("EXPIRED")) {
					acquisitionResponse.setResponseCode(ResponseCode.REFNO_EXPIRED);
					return acquisitionResponse;
				}
			}
			Ofsml13ExtendedEnquiryResponse stanenquiryResp = serviceResp.getOfsExtendedEnquiry();
			Ofsml13EnquiryStatus enquiryStatus = stanenquiryResp.getStatus();
			String enqStatus = enquiryStatus.value();
			Application application = new Application();
			if (enqStatus.equals("OK")) {
				application.setCustomer(new Customer());
				application.setSecondCustomer(new Customer());
				mapT24ResponseToApplication(application, stanenquiryResp);
				acquisitionResponse.setApplication(application);
				acquisitionResponse.setResponseCode(ResponseCode.EVERYTHING_OK);
			} else if (enqStatus.equals("NO-RECORDS")) {
				acquisitionResponse.setResponseCode(ResponseCode.INCORRECT_EMAIL_OR_REFNO);
				return acquisitionResponse;
			}
		} catch (Exception e) {
			acquisitionResponse.setResponseCode(ResponseCode.TECHNICAL_ERROR);
		}
		return acquisitionResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.temenos.ebank.services.impl.com.temenos.ebank.pages.clientAquisition.IServiceClientAquistion#saveApplication
	 * (com.temenos.ebank.message.AcquisitionRequest)
	 */
	public AcquisitionResponse saveApplication(AcquisitionRequest acquisitionRequest) {
		Application a = acquisitionRequest.getApplication();

		if (Boolean.TRUE.equals(a.getIsSole())) {
			// in case of sole application, explicitly remove the joint customer
			Customer secondCustomer = a.getSecondCustomer();
			if (secondCustomer != null && secondCustomer.getCustId() != null) {
				// From the hibernate doc : Note that single valued, many-to-one and one-to-one, associations do not
				// support orphan delete.
				a.setSecondApplicantId(null);
				a.setSecondCustomer(null);
			}
		}
		T24ApplicationSave appSave = new T24ApplicationSave(mappingFactory, t24LangCode); // FIXME do not create a new object on each method call
		Response resp = appSave.ofsTransactionRequest(talkToServer, acquisitionRequest);
		String requestCode = acquisitionRequest.getRequestCode();
		ResponseCode responseCode = T24ResponseUtils.getResponseCode(resp, requestCode);
		// create a dummy AcquisitionResponse to comply with the interface
		AcquisitionResponse acquisitionResponse = new AcquisitionResponse(a, responseCode);
		acquisitionResponse.setResponseCode(responseCode);
		try {
			acquisitionResponse = T24ResponseUtils.parseTransactionResponse(resp, acquisitionRequest,
					acquisitionResponse, true, mappingFactory);
		} catch (Exception e) {
			logger.error("Error when parsing the transaction response "+ e);
		}
		acquisitionResponse.setApplication(a);
		return acquisitionResponse;
	}

	@SuppressWarnings("rawtypes")
	private void mapT24ResponseToApplication(Application application, Ofsml13ExtendedEnquiryResponse stanenquiryResp)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, SecurityException,
			IllegalArgumentException, ParseException {
		List enqRecords = stanenquiryResp.getEnquiryRecord();
		Address address = null;
		PreviousAddress[] previousAddress = null;
		Ofsml13ExtendedEnquiryRecord enqRecord = (Ofsml13ExtendedEnquiryRecord) enqRecords.get(0);
		List columns = enqRecord.getColumn();
		try {
			RetriveEnquiry retriver = (RetriveEnquiry) mappingFactory.getMapping("com.temenos.messagingLayer.retrivepojo", "RetriveEnquiry.xml");
			List<RetriveFieldMapping> fieldMapes = retriver.getRetriveFieldMapping();
			for (Iterator fieldMap = fieldMapes.iterator(); fieldMap.hasNext();) {
				RetriveFieldMapping t24FieldMap = (RetriveFieldMapping) fieldMap.next();
				String addrPosition = t24FieldMap.getAddPosition();
				if (addrPosition.equals("START")) {
					addrStart = true;
					address = new Address();
				}
				List<String> T24FieldNames = t24FieldMap.getT24Field();
				for (Iterator<String> iterT24Name = T24FieldNames.iterator(); iterT24Name.hasNext();) {
					String T24FieldName = iterT24Name.next();
					for (Iterator itercol = columns.iterator(); itercol.hasNext();) {
						Ofsml13IdRefValue col = (Ofsml13IdRefValue) itercol.next();
						String colId = col.getId();
						String colValue = col.getValue().trim();
						if (colId.equals(T24FieldName) && !(colValue.equals("")) || T24FieldName.equals("TODAY")
								&& SetFrontEndValues.fromDate != null) {
							String frontEndMethods = t24FieldMap.getFrontEndObject();
							itercol.remove();
							String dataType = t24FieldMap.getDataType();
							String ConversionTtype = t24FieldMap.getConversionType();
							if (!frontEndMethods.contains("~")) {
								SetFrontEndValues.callSetMethod(application, frontEndMethods, colValue, dataType,
										ConversionTtype);
							} else {
								if (T24FieldName.equals("TODAY")) {
									Calendar currentDate = Calendar.getInstance();
									colValue = (String) T24ResponseUtils.getT24DateFormat().format(currentDate.getTime());
								}
								if (addrPosition.equals("NILL") && !(addrStart)) {
									SetFrontEndValues.callSetRecursively(application, frontEndMethods, colValue,
											dataType, ConversionTtype);
								} else {
									String[] frontEndValues = null;
									// to set FrontEnd values for Address Fields
									if (colValue.contains("~~")) {
										frontEndValues = colValue.split("~~");
										if (previousAddress == null) {
											previousAddress = new PreviousAddress[frontEndValues.length - 1];
										}
									} else {
										frontEndValues = new String[] { colValue };
									}
									if (dataType.equals("durationMonth")) {
										SetFrontEndValues.setAddressPeriods(application, address, previousAddress,
												frontEndMethods, frontEndValues);
									} else {
										SetFrontEndValues.callSetAddress(application, address, previousAddress,
												frontEndMethods, frontEndValues);
									}
								}
							}
							break;
						}
					}
				}
				if (addrPosition.equals("END") && address != null) {
					addrStart = false;
					String frontEndMethods = t24FieldMap.getFrontEndObject();
					String[] customerType = frontEndMethods.split("~");
					if (customerType[0].equals("Customer")) {
						if (customerType[1].equals("CorrespondenceAddress")) {
							application.getCustomer().setCorrespondenceAddress(address);
						} else if (customerType[1].equals("EmployerAddress")) {
							application.getCustomer().setEmployerAddress(address);
						} else {
							application.getCustomer().setResidentialAddress(address);
						}
						address = null;
					} else if (customerType[1].equals("CorrespondenceAddress")) {
						application.getSecondCustomer().setCorrespondenceAddress(address);
					} else if (customerType[1].equals("EmployerAddress")) {
						application.getSecondCustomer().setEmployerAddress(address);
					} else {
						application.getSecondCustomer().setResidentialAddress(address);
					}
					if (previousAddress != null) {
						if (customerType[0].equals("Customer")) {
							application.getCustomer().setPreviousAddresses(previousAddress);
							previousAddress = null;
						} else {
							application.getSecondCustomer().setPreviousAddresses(previousAddress);
						}
					}
				}
			}
		} finally {
			addrStart = false;
		}
	}

	private Ofsml13ServiceResponse getServiceResponse(String enquiryxml) {
		Response resp = talkToServer.sendOfsRequestToServer(enquiryxml);
		T24 t24 = T24ResponseUtils.unmarshall(resp.getMsg());
		Ofsml13ServiceResponse serviceResp = t24.getServiceResponse();
		return serviceResp;
	}

	/**
	 * Injects a talk to server instance
	 * 
	 * @param talkToServer
	 */
	public void setTalkToServer(TalkToServer talkToServer) {
		this.talkToServer = talkToServer;
	}

	public void setMappingFactory(MappingFactory mappingFactory) {
		this.mappingFactory = mappingFactory;
		t24LangCode = new T24LangCode(mappingFactory);
	}
	/* end Spring setters */
}
