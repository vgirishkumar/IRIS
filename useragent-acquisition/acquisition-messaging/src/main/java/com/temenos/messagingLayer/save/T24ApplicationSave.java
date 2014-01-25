package com.temenos.messagingLayer.save;

import java.util.Iterator;
import java.util.List;

import com.temenos.ebank.domain.Application;
import com.temenos.ebank.message.AcquisitionRequest;
import com.temenos.messagingLayer.beans.Response;
import com.temenos.messagingLayer.mappingpojo.Step;
import com.temenos.messagingLayer.mappingpojo.T24;
import com.temenos.messagingLayer.pojo.Ofsml13SecurityContext;
import com.temenos.messagingLayer.pojo.Ofsml13ServiceRequest;
import com.temenos.messagingLayer.pojo.Ofsml13TransactionInputRequest;
import com.temenos.messagingLayer.requestUtils.Marshall;
import com.temenos.messagingLayer.requestUtils.T24LangCode;
import com.temenos.messagingLayer.securityContext.SecurityContext;
import com.temenos.messagingLayer.talkToServer.TalkToServer;
import com.temenos.messagingLayer.util.MappingFactory;

public class T24ApplicationSave {

	private MappingFactory mappingFactory;
	private T24LangCode t24LangCode;

	/**
	 * Constructor.
	 * 
	 * @param mappingFactory
	 * @param t24LangCode
	 */
	public T24ApplicationSave(MappingFactory mappingFactory, T24LangCode t24LangCode) {
		this.mappingFactory = mappingFactory;
		this.t24LangCode = t24LangCode;
	}
	
	public Response ofsTransactionRequest(TalkToServer server, AcquisitionRequest acquisitionRequest) {
		Ofsml13ServiceRequest serReq = new Ofsml13ServiceRequest();
		SecurityContext Sec = new SecurityContext();
		String language = t24LangCode.getT24LangCode(acquisitionRequest.getLanguage());
		Ofsml13SecurityContext secContext = Sec.generateSecurityContext(server.getSecurityContext().getUserName(),
				server.getSecurityContext().getPassword(), language);
		serReq.setSecurityContext(secContext);
		String xmlTransactionReq = transactionInputRequest(acquisitionRequest, serReq);
		Response resp = server.sendOfsRequestToServer(xmlTransactionReq);
		return resp;
	}

	protected String transactionInputRequest(AcquisitionRequest acquisitionRequest, Ofsml13ServiceRequest serReq) {

		Application a = acquisitionRequest.getApplication();
		Ofsml13TransactionInputRequest transReq = new Ofsml13TransactionInputRequest();
		T24MappingObject.checkNillableToken(a, transReq);
		try {
			T24 t24 = T24MappingObject.getT24MappingObject(mappingFactory, "T24Mapping.xml");
			T24MappingObject.setApplicationAndVersion(t24, transReq);
			List<Step> stepNo = t24.getStep();
			for (Iterator<Step> iter = stepNo.iterator(); iter.hasNext();) {
				Step step = iter.next();
				int stepValue = (step.getValue()).intValue();
				int currentStep = a.getResumeStep();
				if (stepValue == 0) {
					Step0DataMapping step0data = new Step0DataMapping(t24LangCode);
					transReq = step0data.step0DataSave(step, transReq, acquisitionRequest);
				} else if (stepValue == 1 && (currentStep == 1 || currentStep == 6)) {
					Step1DataMapping step1data = new Step1DataMapping();
					transReq = step1data.saveData(step, transReq, acquisitionRequest);
				} else if (stepValue == 2 && (currentStep == 2 || currentStep == 6)) {
					Step2DataMapping step2data = new Step2DataMapping();
					transReq = step2data.saveData(step, transReq, acquisitionRequest);
				} else if (stepValue == 3 && (currentStep == 3 || currentStep == 6)) {
					Step3DataMapping step3data = new Step3DataMapping();
					transReq = step3data.saveData(step, transReq, acquisitionRequest);
				} else if (stepValue == 4 && (currentStep == 4 || currentStep == 6)) {
					Step4DataMapping step4data = new Step4DataMapping();
					transReq = step4data.saveData(step, transReq, acquisitionRequest);
				} else if (stepValue == 5 && (currentStep == 5 || currentStep == 6)) {
					Step5DataMapping step5data = new Step5DataMapping();
					transReq = step5data.saveData(step, transReq, acquisitionRequest);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error when forming transaction input request ", e);
		}

		serReq.setOfsTransactionInput(transReq);
		Marshall marTransReq = new Marshall();
		String transInpReq = marTransReq.doMarshalling(serReq);
		return transInpReq;
	}
}