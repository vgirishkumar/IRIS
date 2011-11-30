package com.temenos.ebank.message;

import java.io.Serializable;
import java.util.List;

public class AccountCreationResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<AccountDetails> accountList;
	private AccountDetailsUnderlying underlyingAccount;

	private String userId;
	private String secondUserId;

	private List<String> documentList;
	private List<String> secondDocumentList;

	public List<AccountDetails> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<AccountDetails> accountList) {
		this.accountList = accountList;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSecondUserId() {
		return secondUserId;
	}

	public void setSecondUserId(String secondUserId) {
		this.secondUserId = secondUserId;
	}

	public List<String> getDocumentList() {
		return documentList;
	}

	public void setDocumentList(List<String> documentList) {
		this.documentList = documentList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<String> getSecondDocumentList() {
		return secondDocumentList;
	}

	public void setSecondDocumentList(List<String> secondDocumentList) {
		this.secondDocumentList = secondDocumentList;
	}

	public AccountDetailsUnderlying getUnderlyingAccount() {
		return underlyingAccount;
	}

	public void setUnderlyingAccount(AccountDetailsUnderlying underlyingAccount) {
		this.underlyingAccount = underlyingAccount;
	}
}
