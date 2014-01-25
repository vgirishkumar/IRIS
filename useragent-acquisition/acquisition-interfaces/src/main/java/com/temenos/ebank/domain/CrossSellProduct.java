package com.temenos.ebank.domain;

import java.io.Serializable;
import java.util.List;

public class CrossSellProduct implements Serializable {
	private static final long serialVersionUID = 1L;

	private String productName;
	private List<String> productDetails;
	private String productRef;
	private String detailLink;

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public List<String> getProductDetails() {
		return productDetails;
	}

	public void setProductDetails(List<String> productDetails) {
		this.productDetails = productDetails;
	}

	public String getProductRef() {
		return productRef;
	}

	public void setProductRef(String productRef) {
		this.productRef = productRef;
	}

	public String getDetailLink() {
		return detailLink;
	}

	public void setDetailLink(String detailLink) {
		this.detailLink = detailLink;
	}

	public CrossSellProduct() {
		super();
	}

	public CrossSellProduct(String name, List<String> details, String ref, String link) {
		super();
		productName = name;
		productDetails = details;
		productRef = ref;
		detailLink = link;
	}
}
