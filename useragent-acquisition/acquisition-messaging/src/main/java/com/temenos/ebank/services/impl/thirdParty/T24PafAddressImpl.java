package com.temenos.ebank.services.impl.thirdParty;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.temenos.ebank.domain.Address;
import com.temenos.ebank.services.interfaces.thirdParty.IServicePafAddresses;
import com.temenos.ebank.services.interfaces.thirdParty.PafAddressResult;
import com.temenos.ebank.services.interfaces.thirdParty.PafResultCode;
import com.temenos.messagingLayer.beans.Response;
import com.temenos.messagingLayer.enquiryrequest.ExtendedEnquiryRequest;
import com.temenos.messagingLayer.mappingpojo.Applicant;
import com.temenos.messagingLayer.mappingpojo.FieldMapping;
import com.temenos.messagingLayer.mappingpojo.Group;
import com.temenos.messagingLayer.mappingpojo.Step;
import com.temenos.messagingLayer.pojo.Ofsml13EnquiryRequest;
import com.temenos.messagingLayer.pojo.Ofsml13EnquiryStatus;
import com.temenos.messagingLayer.pojo.Ofsml13ExtendedEnquiryRecord;
import com.temenos.messagingLayer.pojo.Ofsml13ExtendedEnquiryResponse;
import com.temenos.messagingLayer.pojo.Ofsml13IdRefValue;
import com.temenos.messagingLayer.pojo.Ofsml13SelectionCriteria;
import com.temenos.messagingLayer.pojo.Ofsml13SelectionOperand;
import com.temenos.messagingLayer.pojo.Ofsml13ServiceRequest;
import com.temenos.messagingLayer.pojo.Ofsml13ServiceResponse;
import com.temenos.messagingLayer.pojo.T24;
import com.temenos.messagingLayer.requestUtils.Marshall;
import com.temenos.messagingLayer.response.T24ResponseUtils;
import com.temenos.messagingLayer.save.T24MappingObject;
import com.temenos.messagingLayer.talkToServer.TalkToServer;
import com.temenos.messagingLayer.util.MappingFactory;

public class T24PafAddressImpl implements IServicePafAddresses {

	private TalkToServer talkToServer;

	private MappingFactory mappingFactory;

	public PafAddressResult getAddressByPostCode(String postCode, String houseNoNm) throws SecurityException {
		List<Address> list = new ArrayList<Address>();
		Address[] address = null;
		Response genEnqResponse = postEnquiryRequest(postCode, houseNoNm);
		T24 t24 = T24ResponseUtils.unmarshall(genEnqResponse.getMsg());
		Ofsml13ServiceResponse serviceResp = t24.getServiceResponse();
		Ofsml13ExtendedEnquiryResponse stanenquiryResp = serviceResp.getOfsExtendedEnquiry();
		com.temenos.messagingLayer.mappingpojo.T24 t24RespMappingObj = T24MappingObject.getT24MappingObject(mappingFactory, "T24Mapping.xml");
		List<Step> stepContent = t24RespMappingObj.getStep();
		Step step = stepContent.get(7);
		Applicant applicant = step.getApplicant().get(0);
		Group group = applicant.getGroup().get(0);
		List<FieldMapping> fieldMappingList = group.getFieldMapping();
		PafResultCode errorCode = PafResultCode.OK_RESULT;
		// get enquiry status
		if (stanenquiryResp != null) {
			Ofsml13EnquiryStatus enquiryStatus = stanenquiryResp.getStatus();
			if (enquiryStatus.value().equals("OK")) {
				List<Ofsml13ExtendedEnquiryRecord> enqRecords = stanenquiryResp.getEnquiryRecord();
				address = new Address[enqRecords.size()];
				int valuesCnt = 0;
				for (Iterator<Ofsml13ExtendedEnquiryRecord> iter = enqRecords.iterator(); iter.hasNext();) {
					Ofsml13ExtendedEnquiryRecord enqRecord = iter.next();
					List<Ofsml13IdRefValue> columns = enqRecord.getColumn();

					for (Ofsml13IdRefValue columnsObj : columns) {
						if (columnsObj.getId().equals("ERROR.CODE")) {
							errorCode = PafResultCode.getResultByCode( columnsObj.getValue() );
						}
						for (FieldMapping fieldMapping : fieldMappingList) {
							if (columnsObj.getId().equals(fieldMapping.getT24Field().get(0).getValue())) {
								if (address[valuesCnt] == null) {
									address[valuesCnt] = new Address();
								}
								String colValue = columnsObj.getValue();
								if (colValue != null && colValue.equals("NULL")) {
									colValue = null;
								}
								setFrontEndValue(address, fieldMapping, valuesCnt, colValue);
								break;
							}
						}
					}
					valuesCnt++;

				}
				list = Arrays.asList(address);
			} else {
				errorCode = PafResultCode.MATCH_FAILURE_ON_ADDRESS_ELEMENT;
			}
		}
		PafAddressResult result = new PafAddressResult(errorCode, list);

		return result;
	}

	private void setFrontEndValue(Address[] address, FieldMapping fieldMapping, int valuesCnt, String frontEndValue) {
		String frontEndMethod;
		Method method;
		frontEndMethod = "set" + fieldMapping.getFrontEndObject();
		Class[] type = new Class[] { String.class };
		try {
			method = address[valuesCnt].getClass().getDeclaredMethod(frontEndMethod, type);
			method.invoke(address[valuesCnt], frontEndValue);
		} catch (Exception e) {
			throw new RuntimeException("Error setting address object ", e);
		}
	}

	private Response postEnquiryRequest(String postCode, String houseNoNm) {
		ExtendedEnquiryRequest extEnqReq = new ExtendedEnquiryRequest(talkToServer.getSecurityContext());
		Ofsml13EnquiryRequest enqreq = new Ofsml13EnquiryRequest();
		enqreq.setName("EB.GET.ADDR.FROM.PAF");
		String fieldName = "POST.CODE";
		Ofsml13SelectionOperand operand = Ofsml13SelectionOperand.EQ;
		String value = postCode;
		Ofsml13SelectionCriteria selcriteriaid = extEnqReq.generateSelectionCriteria(fieldName, operand, value);
		enqreq.getSelectionCriteria().add(selcriteriaid);
		String fieldNameHouseNo = "HOUSE.NUMBER";
		Ofsml13SelectionOperand operand2 = Ofsml13SelectionOperand.EQ;
		String value2 = houseNoNm;
		Ofsml13SelectionCriteria selcriteriaemail = extEnqReq.generateSelectionCriteria(fieldNameHouseNo, operand2,
				value2);
		enqreq.getSelectionCriteria().add(selcriteriaemail);
		Ofsml13ServiceRequest serGenReq = new Ofsml13ServiceRequest();
		serGenReq = extEnqReq.setUserDetails(serGenReq, null);
		serGenReq.setOfsExtendedEnquiry(enqreq);
		Marshall mar = new Marshall();
		String genRequestXml = mar.doMarshalling(serGenReq);
		Response genEnqResponse = talkToServer.sendOfsRequestToServer(genRequestXml.toString());
		return genEnqResponse;
	}

	public void setTalkToServer(TalkToServer talkToServer) {
		this.talkToServer = talkToServer;
	}
	
	public void setMappingFactory(MappingFactory mappingFactory) {
		this.mappingFactory = mappingFactory;
	}
}
