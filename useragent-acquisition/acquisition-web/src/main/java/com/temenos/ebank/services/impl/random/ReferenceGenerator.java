package com.temenos.ebank.services.impl.random;

import org.apache.commons.lang.RandomStringUtils;

import com.temenos.ebank.services.interfaces.random.IReferenceGenerator;

public class ReferenceGenerator implements IReferenceGenerator {

	public String newReference() {
		return newReference(8);
	}

	public String newReference(int count) {
		return RandomStringUtils.randomAlphanumeric(count);
	}

}
