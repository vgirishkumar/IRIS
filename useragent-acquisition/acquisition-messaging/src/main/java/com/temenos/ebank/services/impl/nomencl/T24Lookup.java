package com.temenos.ebank.services.impl.nomencl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.temenos.ebank.domain.Nomencl;
import com.temenos.ebank.services.interfaces.nomencl.IServiceNomencl;
import com.temenos.messagingLayer.beans.Response;
import com.temenos.messagingLayer.enquiryrequest.ExtendedEnquiryRequest;
import com.temenos.messagingLayer.lookuppojo.Lookup;
import com.temenos.messagingLayer.lookuppojo.LookupMapping;
import com.temenos.messagingLayer.lookuppojo.SelectionCriteria;
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
import com.temenos.messagingLayer.requestUtils.T24LangCode;
import com.temenos.messagingLayer.response.T24ResponseUtils;
import com.temenos.messagingLayer.talkToServer.TalkToServer;
import com.temenos.messagingLayer.util.MappingFactory;

public class T24Lookup implements IServiceNomencl {

	private TalkToServer talkToServer;
	private MappingFactory mappingFactory;
	private T24LangCode t24LangCode;

	protected Log logger = LogFactory.getLog(getClass());

	T24ResponseUtils t24RespUtil = new T24ResponseUtils(); // FIXME what is the purpose of this object?

	public List<Nomencl> getNomencl(String language, String group) {
		ExtendedEnquiryRequest genEnquiryReq = new ExtendedEnquiryRequest(talkToServer.getSecurityContext());
		// Obtain the entire T24Lookup.xml data
		Lookup lookupObj = (Lookup) mappingFactory.getMapping("com.temenos.messagingLayer.lookuppojo", "T24Lookup.xml");
		Boolean checkProductType = false;
		String groupProduct = null;
		checkProductType = group.contains(".");
		// Loop to find the matching group and extract enquiry name and selection criteria
		if (checkProductType) {
			groupProduct = group;
			group = StringUtils.substringAfter(groupProduct, ".");
		}

		Boolean grpFound = false; // Identified enquiry from T24Lookup.xml
		SelectionCriteria selValue = null;
		String enqName = null;
		// Obtain the LookupMapping tags list
		List<LookupMapping> lookupMapList = lookupObj.getLookupMapping();
		for (LookupMapping lookupMappingObj : lookupMapList) {
			String grpName = null;
			grpName = lookupMappingObj.getGroupName();
			if (grpName.equals(group)) {
				enqName = lookupMappingObj.getEnquiryName();
				selValue = lookupMappingObj.getSelectionCriteria();
				if (checkProductType) {
					selValue.setValue(StringUtils.substringBefore(groupProduct, "."));
				}
				grpFound = true;
				break;
			}
		}

		List<Nomencl> genEnqResult = null;
		// If the enquiry is available in T24Lookup.xml, form the enquiry request xml
		if (grpFound) {
			// Enquiry request xml is formed
			language = getT24Language(language);
			String genEnquiryRequest = genEnquiryReq.genEnquiryRequest(language, enqName, selValue);
			Response genEnqResponse = talkToServer.sendOfsRequestToServer(genEnquiryRequest);
			genEnqResult = XmlResponseParsing(genEnqResponse.getMsg(), group, language);
			// Enquiry response xml is obtained and returned
		}
		return genEnqResult;
	}

	public List<Nomencl> XmlResponseParsing(String xmlResponse, String group, String language) {
		List<Nomencl> result = new ArrayList<Nomencl>();
		T24 t24 = T24ResponseUtils.unmarshall(xmlResponse);
		result = parseEnquiryResponse(t24, group, language);
		return result;

	}

	protected List<Nomencl> parseEnquiryResponse(T24 t24, String group, String language) {
		List<Nomencl> result = new ArrayList<Nomencl>();
		Ofsml13ServiceResponse serviceResp = t24.getServiceResponse();
		Ofsml13ExtendedEnquiryResponse stanenquiryResp = serviceResp.getOfsExtendedEnquiry();
		// get enquiry status
		if (stanenquiryResp != null) {
			Ofsml13EnquiryStatus enquiryStatus = stanenquiryResp.getStatus();
			stanenquiryResp.getName();

			String enqStatus = enquiryStatus.value();
			// get the data only when status is OK.
			if (enqStatus.equals("OK")) {

				result = getEnquiryRecord(stanenquiryResp, group, language);
			}
		}
		return result;
	}

	public static List<Nomencl> getEnquiryRecord(Ofsml13ExtendedEnquiryResponse stanenquiryResp, String group,
			String language) {
		List<Ofsml13ExtendedEnquiryRecord> enqRecords = stanenquiryResp.getEnquiryRecord();
		List<Nomencl> result = new ArrayList<Nomencl>();
		for (Iterator<Ofsml13ExtendedEnquiryRecord> iter = enqRecords.iterator(); iter.hasNext();) {
			Ofsml13ExtendedEnquiryRecord enqRecord = iter.next();
			List<Ofsml13IdRefValue> columns = enqRecord.getColumn();

			Ofsml13IdRefValue Label = (Ofsml13IdRefValue) columns.get(0);
			Ofsml13IdRefValue Code = (Ofsml13IdRefValue) columns.get(1);

			Nomencl nomencl = new Nomencl();
			nomencl.setGroupCode(group);
			nomencl.setLabel(Label.getValue().trim());
			nomencl.setCode(Code.getValue().trim());
			nomencl.setLanguage(language);
			result.add(nomencl);
		}
		return result;
	}

	public Map<String, BigDecimal> getFTDTermsAndRates(String currency) {
		ExtendedEnquiryRequest genEnquiryReq = new ExtendedEnquiryRequest(talkToServer.getSecurityContext());
		String language = getT24Language(null); // FIXME why null? why is this not sent as a parameter? apparently, this method is not called at all...
		Map<String, BigDecimal> result = new HashMap<String, BigDecimal>();
		Ofsml13ServiceRequest serGenReq = new Ofsml13ServiceRequest();
		serGenReq = genEnquiryReq.setUserDetails(serGenReq, language);
		Ofsml13EnquiryRequest enqreq = new Ofsml13EnquiryRequest();
		enqreq.setName("EB.GET.TERM.AND.INTEREST");
		String fieldName = "CURRENCY";
		Ofsml13SelectionOperand operand = Ofsml13SelectionOperand.EQ;
		String value = currency;
		Ofsml13SelectionCriteria selcriteria = genEnquiryReq.generateSelectionCriteria(fieldName, operand, value);
		enqreq.getSelectionCriteria().add(selcriteria);
		serGenReq.setOfsExtendedEnquiry(enqreq);
		Marshall mar = new Marshall();
		String genRequestXml = mar.doMarshalling(serGenReq);
		Response genEnqResponse = talkToServer.sendOfsRequestToServer(genRequestXml);
		result = parseFTDTermAndInterestResponse(result, genEnqResponse);
		return result;
	}

	/**
	 * Converts the web language code to T24 language code.
	 */
	private String getT24Language(String webLanguage) {
		return t24LangCode.getT24LangCode(webLanguage);
	}

	private Map<String, BigDecimal> parseFTDTermAndInterestResponse(Map<String, BigDecimal> result,
			Response genEnqResponse) {
		T24 t24 = T24ResponseUtils.unmarshall(genEnqResponse.getMsg());
		Ofsml13ServiceResponse serviceResp = t24.getServiceResponse();
		Ofsml13ExtendedEnquiryResponse stanenquiryResp = serviceResp.getOfsExtendedEnquiry();

		// get enquiry status
		if (stanenquiryResp != null) {
			Ofsml13EnquiryStatus enquiryStatus = stanenquiryResp.getStatus();
			stanenquiryResp.getName();

			String enqStatus = enquiryStatus.value();
			// get the data only when status is OK.
			if (enqStatus.equals("OK")) {
				List<Ofsml13ExtendedEnquiryRecord> enqRecords = stanenquiryResp.getEnquiryRecord();
				for (Iterator<Ofsml13ExtendedEnquiryRecord> iter = enqRecords.iterator(); iter.hasNext();) {
					Ofsml13ExtendedEnquiryRecord enqRecord = iter.next();
					List<Ofsml13IdRefValue> columns = enqRecord.getColumn();
					result.put(columns.get(0).getValue(), new BigDecimal(columns.get(2).getValue()));
				}

			}
		}
		return result;
	}

	/* Spring setters */
	/**
	 * Injects a talk to server instance
	 * 
	 * @param talkToServer
	 */
	public void setTalkToServer(TalkToServer talkToServer) {
		this.talkToServer = talkToServer;
	}

	public void setMappingFactory(MappingFactory mappingFactory) {
		this.mappingFactory = mappingFactory;
		t24LangCode = new T24LangCode(mappingFactory);
	}
	/* end Spring setters */
}