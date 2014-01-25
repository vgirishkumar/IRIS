package com.temenos.ebank.message;

import java.io.Serializable;

public class AccountDetails implements Serializable {

	private static final long serialVersionUID = 1L;
	private String accountCurrency;
	private String sortCode;
	private String accountNo;
	private String ibanNo;

	public String getAccountCurrency() {
		return accountCurrency;
	}

	public void setAccountCurrency(String accountCurrency) {
		this.accountCurrency = accountCurrency;
	}

	public String getSortCode() {
		return sortCode;
	}

	public void setSortCode(String sortCode) {
		this.sortCode = sortCode;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getIbanNo() {
		return ibanNo;
	}

	public void setIbanNo(String ibanNo) {
		this.ibanNo = ibanNo;
	}

	public AccountDetails() {
		super();
	}

	public AccountDetails(String ccy, String sortCode, String No, String Iban) {
		super();
		setAccountCurrency(ccy);
		setAccountNo(No);
		setSortCode(sortCode);
		setIbanNo(Iban);
	}
}
