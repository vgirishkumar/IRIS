package com.temenos.ebank.services.interfaces.random;

public interface IReferenceGenerator {
	public abstract String newReference();

	public abstract String newReference(int count);
}
