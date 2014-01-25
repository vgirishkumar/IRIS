package com.temenos.messagingLayer.requestUtils;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.temenos.messagingLayer.pojo.ObjectFactory;
import com.temenos.messagingLayer.pojo.Ofsml13ServiceRequest;
import com.temenos.messagingLayer.pojo.T24;

/**
 * Marshaller
 * @author anitha
 *
 */
public class Marshall {

	protected Log logger = LogFactory.getLog(getClass());

	public String doMarshalling(Ofsml13ServiceRequest serReq) {
		StringWriter xmlRequest = new StringWriter();

		try {
			T24 poElement = (new ObjectFactory()).createT24();
			poElement.setServiceRequest(serReq);
			JAXBContext jc = JAXBContext.newInstance("com.temenos.messagingLayer.pojo");
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(poElement, xmlRequest);

		} catch (Exception je) {
			logger.error("Error when marshalling ofsmlpojo " + je);
		}

		return xmlRequest.toString();
	}
}