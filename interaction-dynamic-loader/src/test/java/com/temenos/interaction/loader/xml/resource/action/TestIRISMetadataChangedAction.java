package com.temenos.interaction.loader.xml.resource.action;

/*
 * #%L
 * interaction-dynamic-loader
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
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


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.springframework.core.io.Resource;

import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.loader.xml.XmlChangedEventImpl;
import com.temenos.interaction.odataext.entity.MetadataOData4j;


/**
 * TODO: Document me!
 *
 * @author mlambert
 *
 */
public class TestIRISMetadataChangedAction {

	@Test
	public void test() {
		IRISMetadataChangedAction action = new IRISMetadataChangedAction();
		
		Metadata metadata = mock(Metadata.class);
		action.setMetadata(metadata);
		
		MetadataOData4j metadataOData4j = mock(MetadataOData4j.class);
		action.setMetadataOData4j(metadataOData4j);
		
		Resource resource = mock(Resource.class);
		when(resource.getFilename()).thenReturn("meta-test.xml");
		XmlChangedEventImpl event = new XmlChangedEventImpl(resource);
		
		action.execute(event);
		
		verify(metadata).unload("test");
		verify(metadataOData4j).unloadMetadata("test");
		
		Resource resource2 = mock(Resource.class);
		when(resource2.getFilename()).thenReturn("IRIS-test2.properties");
		XmlChangedEventImpl event2 = new XmlChangedEventImpl(resource2);
		
		action.execute(event2);
		
		verify(metadata, never()).unload("test2");
		verify(metadataOData4j, never()).unloadMetadata("test2");		
	}

}
