package com.temenos.messagingLayer.save;

import java.util.List;

import com.temenos.ebank.message.AcquisitionRequest;
import com.temenos.messagingLayer.mappingpojo.Applicant;
import com.temenos.messagingLayer.mappingpojo.Step;
import com.temenos.messagingLayer.pojo.Ofsml13TransactionInputRequest;

public class Step4DataMapping extends BaseStepDataMapping {
	DataMapping data = new DataMapping();

	public Ofsml13TransactionInputRequest saveData(Step getStep, Ofsml13TransactionInputRequest transReq,
			AcquisitionRequest acquisitionRequest) {
		List<Applicant> applicant = getStep.getApplicant();
		transReq = data.doMapping(applicant, transReq, acquisitionRequest);

		return transReq;

	}
}
