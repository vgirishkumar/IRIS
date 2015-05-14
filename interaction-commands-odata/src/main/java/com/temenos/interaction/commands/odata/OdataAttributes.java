package com.temenos.interaction.commands.odata;

/*
 * #%L
 * interaction-commands-odata
 * %%
 * Copyright (C) 2012 - 2015 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.command.InteractionAttribute;

/**
 * This enum defines InteractionContext attributes used by the OData package.
 */

public enum ODataAttributes implements InteractionAttribute {

	O_DATA_PRODUCER_ATTRIBUTE("ODataProducer", ODataProducer.class);
	
	private String name;
	private Class<?> type;
	
	ODataAttributes(String name, Class<?> type) {
		init(name, type);
	}
	
	public void init(String name, Class<?> type) {
		this.name = name;
		this.type = type;
	}
	
	public Class<?> getType() {
		return type;
	}

	public String getName() {
		return name;
	}
}
