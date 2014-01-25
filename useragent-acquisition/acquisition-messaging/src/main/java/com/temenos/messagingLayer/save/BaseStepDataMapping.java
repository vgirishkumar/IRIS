package com.temenos.messagingLayer.save;

import com.temenos.ebank.message.AcquisitionRequest;
import com.temenos.messagingLayer.mappingpojo.Step;
import com.temenos.messagingLayer.pojo.Ofsml13TransactionInputRequest;

public abstract class BaseStepDataMapping {

	public abstract Ofsml13TransactionInputRequest saveData(Step getStep, Ofsml13TransactionInputRequest transReq,
			AcquisitionRequest acquisitionRequest);
}
