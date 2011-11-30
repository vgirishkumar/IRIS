package com.temenos.ebank.message;

import java.math.BigDecimal;

public class FTDTermsAndRates {
	
	private String periodInMonths;
    private BigDecimal rate;
    
	public void setPeriodInMonths(String periodInMonths) {
		this.periodInMonths = periodInMonths;
	}
	public String getPeriodInMonths() {
		return periodInMonths;
	}
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	public BigDecimal getRate() {
		return rate;
	}
}
