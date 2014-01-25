package com.temenos.messagingLayer.enquiryrequest;

import com.temenos.messagingLayer.lookuppojo.SelectionCriteria;
import com.temenos.messagingLayer.pojo.Ofsml13EnquiryRequest;
import com.temenos.messagingLayer.pojo.Ofsml13SecurityContext;
import com.temenos.messagingLayer.pojo.Ofsml13SelectionCriteria;
import com.temenos.messagingLayer.pojo.Ofsml13SelectionOperand;
import com.temenos.messagingLayer.pojo.Ofsml13ServiceRequest;
import com.temenos.messagingLayer.requestUtils.Marshall;
import com.temenos.messagingLayer.securityContext.SecurityContext;

public class ExtendedEnquiryRequest {
	private SecurityContext securityContext;

	public ExtendedEnquiryRequest(SecurityContext securityContext) {
		this.securityContext = securityContext;
	}

	// form the enquiry request
	public String getEnquiryRequestForApplicationByRef(String reference, String email) {
		String enquiryRequest = null;
		Ofsml13ServiceRequest serReq = new Ofsml13ServiceRequest();
		serReq = setUserDetails(serReq, null);
		Ofsml13EnquiryRequest enqReq = generateEnquiryXml(reference, email);
		serReq.setOfsExtendedEnquiry(enqReq);
		Marshall mar = new Marshall();
		// create a Marshaller and marshal to System.out
		enquiryRequest = mar.doMarshalling(serReq);
		return enquiryRequest;
	}

	// set user details for the enquiry request
	public Ofsml13ServiceRequest setUserDetails(Ofsml13ServiceRequest serReq, String language) {
		SecurityContext secContext = new SecurityContext();
		Ofsml13SecurityContext contextgenerated = secContext.generateSecurityContext(securityContext.getUserName(),
				securityContext.getPassword(), language);
		serReq.setSecurityContext(contextgenerated);
		return serReq;
	}

	// Form enquiry / validation list request
	public String genEnquiryRequest(String language, String group, SelectionCriteria selectionValue) {

		Ofsml13ServiceRequest serGenReq = new Ofsml13ServiceRequest();
		serGenReq = setUserDetails(serGenReq, language);
		String genRequestXml = null;
		Ofsml13EnquiryRequest enqGenReq = new Ofsml13EnquiryRequest();
		enqGenReq.setName(group);

		SelectionCriteria selCrit = selectionValue;
		if (selCrit != null) {
			String fldName = selCrit.getContent();
			String fldOperand = selCrit.getOperand();
			String fldValue = selCrit.getValue();
			Ofsml13SelectionOperand fldOperandObj = generateOperand(fldOperand);
			Ofsml13SelectionCriteria selcriteria = generateSelectionCriteria(fldName, fldOperandObj, fldValue);
			enqGenReq.getSelectionCriteria().add(selcriteria);
		}
		serGenReq.setOfsExtendedEnquiry(enqGenReq);
		Marshall mar = new Marshall();
		// create a Marshaller and marshal to System.out
		genRequestXml = mar.doMarshalling(serGenReq);
		return genRequestXml;
	}

	protected Ofsml13EnquiryRequest generateEnquiryXml(String reference, String email) {
		Ofsml13EnquiryRequest enqreq = new Ofsml13EnquiryRequest();
		enqreq.setName("RETRIEVE.FORM");
		String fieldName = "@ID";
		Ofsml13SelectionOperand operand = Ofsml13SelectionOperand.EQ;
		String value = reference;
		Ofsml13SelectionCriteria selcriteriaid = generateSelectionCriteria(fieldName, operand, value);
		enqreq.getSelectionCriteria().add(selcriteriaid);
		String fieldNameemail = "EMAIL";
		Ofsml13SelectionOperand operand2 = Ofsml13SelectionOperand.EQ;
		String value2 = email;
		Ofsml13SelectionCriteria selcriteriaemail = generateSelectionCriteria(fieldNameemail, operand2, value2);
		enqreq.getSelectionCriteria().add(selcriteriaemail);
		return enqreq;
	}

	// Generate operand as Ofsml13SelectionOperand object
	protected Ofsml13SelectionOperand generateOperand(String Operator) {
		Ofsml13SelectionOperand OperandObj = null;
		if (Operator.equals("EQ")) {
			OperandObj = Ofsml13SelectionOperand.EQ;
		} else if (Operator.equals("LK")) {
			OperandObj = Ofsml13SelectionOperand.LK;
		} else if (Operator.equals("GE")) {
			OperandObj = Ofsml13SelectionOperand.GE;
		} else if (Operator.equals("GT")) {
			OperandObj = Ofsml13SelectionOperand.GT;
		} else if (Operator.equals("LE")) {
			OperandObj = Ofsml13SelectionOperand.LE;
		} else if (Operator.equals("LT")) {
			OperandObj = Ofsml13SelectionOperand.LT;
		} else if (Operator.equals("NE")) {
			OperandObj = Ofsml13SelectionOperand.NE;
		} else if (Operator.equals("NR")) {
			OperandObj = Ofsml13SelectionOperand.NR;
		} else if (Operator.equals("RG")) {
			OperandObj = Ofsml13SelectionOperand.RG;
		} else if (Operator.equals("UL")) {
			OperandObj = Ofsml13SelectionOperand.UL;
		}
		return OperandObj;
	}

	// Generate selection criteria as Ofsml13SelectionCriteria
	public Ofsml13SelectionCriteria generateSelectionCriteria(String fieldName, Ofsml13SelectionOperand operand,
			String value) {
		Ofsml13SelectionCriteria selcriteria = new Ofsml13SelectionCriteria();
		selcriteria.setFieldName(fieldName);
		selcriteria.setOperand(operand);
		selcriteria.getValue().add(value);
		return selcriteria;
	}

	public String getEnquiryRequestForApplicationById(Long appID) {
		String enquiryRequest = null;
		Ofsml13ServiceRequest serReq = new Ofsml13ServiceRequest();
		serReq = setUserDetails(serReq, null);
		Ofsml13EnquiryRequest enqreq = generateEnquiryXmlById(appID);
		serReq.setOfsExtendedEnquiry(enqreq);
		Marshall mar = new Marshall();
		// create a Marshaller and marshal to System.out
		enquiryRequest = mar.doMarshalling(serReq);
		return enquiryRequest;
	}

	private Ofsml13EnquiryRequest generateEnquiryXmlById(Long appID) {
		Ofsml13EnquiryRequest enqreq = new Ofsml13EnquiryRequest();
		enqreq.setName("RETRIEVE.FORM");
		String fieldName = "@ID";
		Ofsml13SelectionOperand operand = Ofsml13SelectionOperand.EQ;
		String value = appID.toString();
		Ofsml13SelectionCriteria selcriteriaid = generateSelectionCriteria(fieldName, operand, value);
		enqreq.getSelectionCriteria().add(selcriteriaid);
		return enqreq;
	}

}
