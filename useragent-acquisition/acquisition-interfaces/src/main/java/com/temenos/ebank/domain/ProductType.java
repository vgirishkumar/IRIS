package com.temenos.ebank.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Product types for the client acquisition.
 * 
 * @author vionescu
 */
public enum ProductType {
	INTERNATIONAL("CA"), 
	FIXED_TERM_DEPOSIT("FTD"),
	REGULAR_SAVER("RS"),
	IASA("IASA"),
	IBSA("IBSA"),
	RASA("RASA");

	// Reverse-lookup map for getting a ProductType from the corresponding code
    private static final Map<String, ProductType> lookup = new HashMap<String, ProductType>();
    static {
        for (ProductType pt : ProductType.values())
            lookup.put(pt.getCode(), pt);
    }
	
	private final String code;

	/**
	 * Returns the product code
	 * 
	 * @return
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Constructs an enum element
	 * @param code
	 */
	private ProductType(String code) {
		this.code = code;
	}
	
    /**
     * Looks up a {@link ProductType} associated to the provided code
     * @param code The product code
     * @return
     */
    public static ProductType get(String code) {
        return lookup.get(code);
    }
	
	
}
