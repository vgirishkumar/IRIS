package com.interaction.example.odata.northwind;

/*
 * #%L
 * interaction-example-odata-northwind
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
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


import static org.junit.Assert.*;

import org.junit.Test;

public class MetadataITCase extends AbstractNorthwindRuntimeTest {

	public MetadataITCase(RuntimeFacadeType type) {
		super(type);
	}

	@Test
	public void testDefaultXmlContent() {

		String xmlResult = this.rtFacade.getWebResource(endpointUri + "$metadata", "application/xml");
		assertTrue(xmlResult.contains("<?xml version='1.0' encoding='utf-8'?>"));
		String htmlResult = this.rtFacade.getWebResource(endpointUri + "$metadata", "text/html");
		assertTrue(htmlResult.contains("<?xml version='1.0' encoding='utf-8'?>"));
		String xhtmlResult = this.rtFacade.getWebResource(endpointUri + "$metadata", "application/xhtml+xml");
		assertTrue(xhtmlResult.contains("<?xml version='1.0' encoding='utf-8'?>"));
		
	}

}
