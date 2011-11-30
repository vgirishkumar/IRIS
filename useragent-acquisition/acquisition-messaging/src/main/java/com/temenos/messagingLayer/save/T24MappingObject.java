package com.temenos.messagingLayer.save;

import com.temenos.ebank.domain.Application;
import com.temenos.messagingLayer.mappingpojo.T24;
import com.temenos.messagingLayer.pojo.ObjectFactory;
import com.temenos.messagingLayer.pojo.Ofsml13NillableToken;
import com.temenos.messagingLayer.pojo.Ofsml13OperationCode;
import com.temenos.messagingLayer.pojo.Ofsml13TransactionInputRequest;
import com.temenos.messagingLayer.util.MappingFactory;

public class T24MappingObject {

	public static T24 getT24MappingObject(MappingFactory mappingFactory, String xmlPath) {
		return (T24) mappingFactory.getMapping("com.temenos.messagingLayer.mappingpojo", xmlPath);
	}

	public static void checkNillableToken(Application a, Ofsml13TransactionInputRequest transReq) {
		if (a.getAppRef() != null) {
			Ofsml13NillableToken nillableToken = new Ofsml13NillableToken();
			nillableToken.setValue(a.getAppRef());
			transReq.setTransactionId((new ObjectFactory()).createOfsml13TransactionRequestTransactionId(nillableToken));
		}
	}

	public static void setApplicationAndVersion(T24 t24, Ofsml13TransactionInputRequest transReq) {
		String application = t24.getApplication();
		String version = t24.getVersion();
		String operationValue = t24.getOperation();
		Ofsml13OperationCode operation = Ofsml13OperationCode.valueOf(operationValue);
		transReq.setApplication(application);
		transReq.setVersion(version);
		transReq.setOperation(operation);
	}
}
