package com.temenos.ebank.services.interfaces.nomencl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.temenos.ebank.domain.Nomencl;

public interface IServiceNomencl {
	List<Nomencl> getNomencl(String language, String group);
	Map<String, BigDecimal> getFTDTermsAndRates(String currency);
}
