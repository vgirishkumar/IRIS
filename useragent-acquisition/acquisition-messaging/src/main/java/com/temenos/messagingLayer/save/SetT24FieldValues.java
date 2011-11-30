package com.temenos.messagingLayer.save;

import java.math.BigInteger;

import com.temenos.messagingLayer.pojo.Ofsml13MVField;
import com.temenos.messagingLayer.pojo.Ofsml13TransactionInputRequest;

/**
 * Sets the t24 fields with front end object values
 * 
 * @author anitha
 * 
 */

public class SetT24FieldValues {

	public Ofsml13TransactionInputRequest setFieldNameAndValue(Ofsml13TransactionInputRequest transReq,
			String fieldName, String fieldValue, int mvValue, int svValue) {
		// Set the field names and values
		Ofsml13MVField field1 = new Ofsml13MVField();
		field1.setName(fieldName);
		field1.setValue(fieldValue);
		field1.setMv(BigInteger.valueOf((long) mvValue));
		field1.setSv(BigInteger.valueOf((long) svValue));
		transReq.getField().add(field1);
		return transReq;
	}
}
