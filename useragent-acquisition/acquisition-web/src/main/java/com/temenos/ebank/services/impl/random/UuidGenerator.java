package com.temenos.ebank.services.impl.random;

import java.util.UUID;

import com.temenos.ebank.services.interfaces.random.IReferenceGenerator;

public class UuidGenerator implements IReferenceGenerator {

	public String newReference() {
		return UUID.randomUUID().toString();
	}

	public String newReference(int count) {
		return newReference();
	}

}
