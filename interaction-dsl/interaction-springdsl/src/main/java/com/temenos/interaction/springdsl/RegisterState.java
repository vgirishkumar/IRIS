package com.temenos.interaction.springdsl;

import java.util.Set;

/**
 * TODO: Document me!
 *
 * @author mlambert
 *
 */
public interface RegisterState {

	public void addService(String stateName, String path, Set<String> methods);

}
