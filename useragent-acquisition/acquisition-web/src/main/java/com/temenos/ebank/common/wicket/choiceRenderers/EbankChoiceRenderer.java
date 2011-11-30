package com.temenos.ebank.common.wicket.choiceRenderers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.temenos.ebank.domain.ConfigParamTable.STRING;
import com.temenos.ebank.domain.Nomencl;
import com.temenos.ebank.services.interfaces.clientAquisition.IServiceConfigParam;
import com.temenos.ebank.services.interfaces.nomencl.IServiceNomencl;

/**
 * Choice renderer which calls injected service during construction to get keys and values for drop downs
 * 
 * @author raduf
 * 
 */
public class EbankChoiceRenderer implements IGenericChoiceRenderer {

	private Map<String, String> choicesMap;
	private List<String> choicesList;
	
	@SpringBean
	private IServiceNomencl serviceNomencl;
	
	@SpringBean(name = "serviceConfigParam")
	private IServiceConfigParam serviceConfigParam;
	
	public EbankChoiceRenderer(String prefix, Component component) {
		InjectorHolder.getInjector().inject(this);
		List<Nomencl> nomenclList = serviceNomencl.getNomencl(component == null ? Session.get().getLocale().getLanguage() : component.getLocale().getLanguage(), prefix);
		//This is useful for producing test data for the unit tests
		//In order to run this from site, one must alter the classpath to include xstream libraries
		//SerializeTestObjectsToXmlUtils.serializeNomenclToXml(nomenclList, "D:\\temp\\nomenclsXml");
		if (nomenclList == null) {
			nomenclList = new ArrayList<Nomencl>();
		}
		choicesMap = new LinkedHashMap<String, String>(nomenclList.size());
		for (Nomencl nomencl : nomenclList) {
			choicesMap.put(nomencl.getCode(), nomencl.getLabel());
		}
		choicesList = new ArrayList<String>(choicesMap.keySet());
	}

	public EbankChoiceRenderer(Choices key, Component component) {
		this(key.toString(), component);

		switch (key) {
		case COUNTRY:
		case NATIONALITY:
			String localCountryCode = serviceConfigParam.getConfigParamTable().get(STRING.LOCAL_COUNTRY);
			choicesList.add(0, localCountryCode);
		}
	}

	private static final long serialVersionUID = 1L;

	public Object getDisplayValue(String value) {
		return choicesMap.get(value.toString());
	}

	public String getIdValue(String value, int index) {
		return value;
	}

	public List<String> getChoices() {
		return choicesList;
	}

}
