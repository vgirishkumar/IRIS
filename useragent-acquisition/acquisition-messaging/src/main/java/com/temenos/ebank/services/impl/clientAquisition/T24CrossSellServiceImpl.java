package com.temenos.ebank.services.impl.clientAquisition;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.CrossSellProduct;
import com.temenos.ebank.exceptions.ResponseCode;
import com.temenos.ebank.message.AcquisitionRequest;
import com.temenos.ebank.message.AcquisitionResponse;
import com.temenos.ebank.services.interfaces.clientAquisition.IServiceCrossSell;
import com.temenos.messagingLayer.beans.Response;
import com.temenos.messagingLayer.mappingpojo.Step;
import com.temenos.messagingLayer.pojo.Ofsml13SecurityContext;
import com.temenos.messagingLayer.pojo.Ofsml13ServiceRequest;
import com.temenos.messagingLayer.pojo.Ofsml13TransactionInputRequest;
import com.temenos.messagingLayer.requestUtils.Marshall;
import com.temenos.messagingLayer.response.T24ResponseUtils;
import com.temenos.messagingLayer.save.RoutineRequest;
import com.temenos.messagingLayer.save.Step1DataMapping;
import com.temenos.messagingLayer.save.T24MappingObject;
import com.temenos.messagingLayer.securityContext.SecurityContext;
import com.temenos.messagingLayer.talkToServer.TalkToServer;
import com.temenos.messagingLayer.util.MappingFactory;

public class T24CrossSellServiceImpl implements IServiceCrossSell {
	private RoutineRequest routineReq = new RoutineRequest();
	private Log logger = LogFactory.getLog(getClass());
	private TalkToServer talkToServerObj;
	private MappingFactory mappingFactory;

	public AcquisitionResponse getCrossSellProducts(AcquisitionRequest req) {
		String routineRequest = routineReq.formRoutineRequest("EB.GET.CROSS.SELL.PRODUCTS", req.getApplication()
				.getAppRef(), talkToServerObj);
		Response crossSellResponse = talkToServerObj.sendOfsRequestToServer(routineRequest);
		String routineResponse = crossSellResponse.getMsg();
		String routineResp = null;
		routineResp = parseRoutineResponse(routineResponse, routineResp);
		ArrayList<CrossSellProduct> a = new ArrayList<CrossSellProduct>();
		if (routineResp != null) {
			String[] productCodesArr = routineResp.split(",");
			for (int productCnt = 0; productCnt < productCodesArr.length; productCnt++) {
				a.add(new CrossSellProduct(null, null, productCodesArr[productCnt], null));
			}
		}
		AcquisitionResponse response = new AcquisitionResponse();
		response.setCrossSellProducts(a);
		return response;
	}

	private String parseRoutineResponse(String routineResponse, String routineResp) throws FactoryConfigurationError {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(new InputSource(new StringReader(routineResponse)));
			Element domElement = dom.getDocumentElement();
			NodeList routine = domElement.getElementsByTagName("ofsStandardRoutine");
			if (routine != null) {
				routineResp = routine.item(0).getFirstChild().getNodeValue();
			}
		} catch (Exception e) {
			logger.error("Error when parsing routine response " + e);
		}
		return routineResp;
	}

	public AcquisitionResponse createCrossSell(AcquisitionRequest acquisitionRequest) {
		// Forms the crossSell request ,post the same to T24 and parse the response to set it back to front end response
		// objects
		Application a = acquisitionRequest.getApplication();
		AcquisitionResponse acquisitionResponse = null;
		try {
			Ofsml13ServiceRequest serReq = new Ofsml13ServiceRequest();
			SecurityContext Sec = new SecurityContext();
			Ofsml13SecurityContext secContext = Sec.generateSecurityContext(talkToServerObj.getSecurityContext()
					.getUserName(), talkToServerObj.getSecurityContext().getPassword(), null);
			serReq.setSecurityContext(secContext);
			String xmlTransactionReq = transactionInputRequest(acquisitionRequest, serReq);
			Response response = talkToServerObj.sendOfsRequestToServer(xmlTransactionReq);
			String requestCode = acquisitionRequest.getRequestCode();
			ResponseCode responseCode = T24ResponseUtils.getResponseCode(response, requestCode);
			acquisitionResponse = new AcquisitionResponse(a, responseCode);
			acquisitionResponse.setResponseCode(responseCode);
			acquisitionResponse = T24ResponseUtils.parseTransactionResponse(response, acquisitionRequest,
					acquisitionResponse, false, mappingFactory);
		} catch (Exception e) {
			logger.error("Error in CrossSell processing " + e);
		}
		return acquisitionResponse;
	}

	private String transactionInputRequest(AcquisitionRequest acquisitionRequest, Ofsml13ServiceRequest serReq)
			throws JAXBException {
		Application a = acquisitionRequest.getApplication();
		Step1DataMapping dataMapping = new Step1DataMapping();
		Ofsml13TransactionInputRequest transReq = new Ofsml13TransactionInputRequest();
		T24MappingObject.checkNillableToken(a, transReq);
		com.temenos.messagingLayer.mappingpojo.T24 t24 = T24MappingObject.getT24MappingObject(mappingFactory, "T24CrossSellMapping.xml");
		T24MappingObject.setApplicationAndVersion(t24, transReq);
		Step step = t24.getStep().get(0);
		dataMapping.saveData(step, transReq, acquisitionRequest);
		serReq.setOfsTransactionInput(transReq);
		Marshall marTransReq = new Marshall();
		String transInpReq = marTransReq.doMarshalling(serReq);
		return transInpReq;
	}

	public boolean postMeMyDocuments(String appRef, boolean mainApplication) {
		// forms a routine request to send it to t24 for sending email and receives the acknowledge from t24.
		String routineRequest = routineReq.formRoutineRequest("EB.TRIGGER.POST.MY.DOCS",
				appRef + '~' + mainApplication, talkToServerObj);
		Response documentPostResp = talkToServerObj.sendOfsRequestToServer(routineRequest);
		String routineResponse = documentPostResp.getMsg();
		String routineResp = null;
		routineResp = parseRoutineResponse(routineResponse, routineResp);
		if (routineResp != null && routineResp.equals("POSTED")) {
			return true;
		} else {
			return false;
		}
	}

	public void setTalkToServer(TalkToServer talkToServer) {
		this.talkToServerObj = talkToServer;
	}

	public void setMappingFactory(MappingFactory mappingFactory) {
		this.mappingFactory = mappingFactory;
	}
}
