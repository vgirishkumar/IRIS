package com.temenos.ebank.message;

public class AccountDetailsUnderlying extends AccountDetails {
	private static final long serialVersionUID = 1L;

	private String category;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public AccountDetailsUnderlying(String ccy, String sortCode, String No, String Iban, String category) {
		super(ccy, sortCode, No, Iban);
		this.category = category;
	}
}
