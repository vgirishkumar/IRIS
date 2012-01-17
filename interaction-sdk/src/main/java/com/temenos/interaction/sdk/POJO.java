package com.temenos.interaction.sdk;

import org.odata4j.core.OEntity;

/**
 * A POJO is a simple java class that has been generated from an {@link OEntity}
 * @author aphethean
 *
 */
public class POJO {

	private String name;
	
	public POJO(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
