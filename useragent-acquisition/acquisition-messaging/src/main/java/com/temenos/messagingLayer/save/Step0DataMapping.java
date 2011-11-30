package com.temenos.messagingLayer.save;

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;

import com.temenos.ebank.domain.Application;
import com.temenos.ebank.message.AcquisitionRequest;
import com.temenos.messagingLayer.mappingpojo.Applicant;
import com.temenos.messagingLayer.mappingpojo.FieldMapping;
import com.temenos.messagingLayer.mappingpojo.Group;
import com.temenos.messagingLayer.mappingpojo.Step;
import com.temenos.messagingLayer.pojo.Ofsml13TransactionInputRequest;
import com.temenos.messagingLayer.requestUtils.T24LangCode;

public class Step0DataMapping {

	private T24LangCode t24LangCode;
	public Step0DataMapping(T24LangCode t24LangCode) {
		this.t24LangCode = t24LangCode;
	}
	
	public Ofsml13TransactionInputRequest step0DataSave(Step getStep, Ofsml13TransactionInputRequest transReq,
			AcquisitionRequest acquisitionRequest) {
		Application a = acquisitionRequest.getApplication();
		List<Applicant> applicant = getStep.getApplicant();
		SetT24FieldValues setFldVal = new SetT24FieldValues();
		DataMapping step0data = new DataMapping();
		String t24Field = null;
		String frontEndValue = null;
		int defaultValue = 1;
		for (Iterator<Applicant> iterApp = applicant.iterator(); iterApp.hasNext();) {
			Applicant applicantTypes = iterApp.next();
			String applicantType = applicantTypes.getSingleOrJoint();
			try {
				if (applicantType.equals("single")) {
					List<Group> groups = applicantTypes.getGroup();
					for (Iterator<Group> iterGroups = groups.iterator(); iterGroups.hasNext();) {
						Group getGroup = iterGroups.next();
						String groupType = getGroup.getType();
						List<FieldMapping> fieldNames = getGroup.getFieldMapping();
						step0data.readConstantFields(transReq, a, getGroup);
						if (groupType.equals("mandatory")) {

							for (Iterator<FieldMapping> iterFieldNames = fieldNames.iterator(); iterFieldNames
									.hasNext();) {

								FieldMapping fieldMapping = iterFieldNames.next();
								String frontEndMethods = fieldMapping.getFrontEndObject();

								List<JAXBElement<String>> t24Fields = fieldMapping.getT24Field();
								for (Iterator<JAXBElement<String>> t24FieldNames = t24Fields.iterator(); t24FieldNames
										.hasNext();) {
									t24Field = t24FieldNames.next().getValue().toString();
									frontEndValue = step0data.callGetMethod(a, frontEndMethods);
									setFldVal.setFieldNameAndValue(transReq, t24Field, frontEndValue, defaultValue,
											defaultValue);
								}
							}
						} else if (groupType.equals("conditional")) {
							String conditionType = getGroup.getCondition();
							if (conditionType.equals("save")) {
								for (Iterator<FieldMapping> iterFieldNames = fieldNames.iterator(); iterFieldNames
										.hasNext();) {

									FieldMapping fieldMapping = iterFieldNames.next();
									List<JAXBElement<String>> t24Fields = fieldMapping.getT24Field();
									for (Iterator<JAXBElement<String>> t24FieldNames = t24Fields.iterator(); t24FieldNames
											.hasNext();) {
										t24Field = t24FieldNames.next().getValue().toString();
										String frontEndMethods = fieldMapping.getFrontEndObject();
										frontEndValue = step0data.callGetMethod(acquisitionRequest, frontEndMethods);
										if (t24Field.equals("LANGUAGE")) {
											frontEndValue = t24LangCode.getT24LangCode(frontEndValue);
										}
										setFldVal.setFieldNameAndValue(transReq, t24Field, frontEndValue, defaultValue,
												defaultValue);
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Error when mapping step 0 data ", e);
			}

		}
		return transReq;
	}

}
