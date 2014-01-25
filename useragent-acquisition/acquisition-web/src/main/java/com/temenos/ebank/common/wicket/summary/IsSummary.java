package com.temenos.ebank.common.wicket.summary;

/**
 * This class will be added to the MetaData of the request cycle
 * It simply wraps a boolean. The Wicket MetaDataKey mechanism relies on type,
 * this is why a specially created type was preferred instead of the commonly used Boolean.
 * 
 * @author raduf
 * 
 */
public class IsSummary {
	private Boolean value;

	public IsSummary(Boolean isSummary) {
		this.value = isSummary;
	}

	public boolean getValue() {
		return value;
	}
}
