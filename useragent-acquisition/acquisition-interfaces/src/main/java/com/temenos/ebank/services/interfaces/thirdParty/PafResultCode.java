package com.temenos.ebank.services.interfaces.thirdParty;

public enum PafResultCode {
	OK_RESULT("0"),
	INVALID_STREET_OR_HOUSE_NAME("00090002"),
	HOUSE_NUMBER_IS_OUT_OF_RANGE("00090003"),
	MATCH_FAILURE_ON_ADDRESS_ELEMENT("00090004"),
	AMBIGUOUS_ADDRESS_FAILURE("00090005"),
	INVALID_POST_CODE("00090006"),
	ADDRESS_MANAGER_CURRENTLY_UNAVAILABLE("00090007");
	
	private String code;
	
	private PafResultCode( String code ){
		this.code = code;
	}
	
	public String getCode(){
		return code;
	}
	
	public static PafResultCode getResultByCode(String wantedCode){
		PafResultCode returning = null;
		for( PafResultCode resultCode:values() ){
			if( resultCode.getCode().equals(wantedCode) ){
				returning = resultCode;
				break;
			}
		}
		return returning;
	}
}
