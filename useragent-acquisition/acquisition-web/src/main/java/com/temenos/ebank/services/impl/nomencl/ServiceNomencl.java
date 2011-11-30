package com.temenos.ebank.services.impl.nomencl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.spring.injection.annot.SpringBean;

import com.temenos.ebank.dao.interfaces.nomencl.INomenclDao;
import com.temenos.ebank.domain.Nomencl;
import com.temenos.ebank.services.interfaces.nomencl.IServiceNomencl;

public class ServiceNomencl implements IServiceNomencl {
	
	@SpringBean
	private INomenclDao nomenclDao;

	public List<Nomencl> getNomencl(String language, String group) {
		return nomenclDao.getNomencl(language, group);
	}

	public void setNomenclDao(INomenclDao nomenclDao) {
		this.nomenclDao = nomenclDao;
	}

	public Map<String, BigDecimal> getFTDTermsAndRates(String currency) {
		Map<String, BigDecimal> result = new HashMap<String, BigDecimal>();
		result.put("6", BigDecimal.ONE);
		result.put("12", new BigDecimal(2));
		result.put("24", new BigDecimal(3));
		result.put("36", new BigDecimal(4));
		return result;
	}
}
