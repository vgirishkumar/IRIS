package com.temenos.interaction.commands.odata;

/*
 * #%L
 * interaction-commands-odata
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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;

import com.temenos.interaction.core.MultivaluedMapImpl;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.MetaDataResource;
import com.temenos.interaction.odataext.entity.MetadataOData4j;

public class TestGETMetadataCommand {

	@SuppressWarnings("unchecked")
	private InteractionContext createInteractionContext() {
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
        InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), pathParams, mock(MultivaluedMap.class), mock(ResourceState.class), mock(Metadata.class));
        return ctx;
	}

	@Test
	public void testMetadataResource() {
		Metadata md = mock(Metadata.class);
		GETMetadataCommand command = new GETMetadataCommand("Metadata", getMetadataOData4j(md));
        InteractionContext ctx = createInteractionContext();
        command.execute(ctx);
		assertTrue(ctx.getResource() instanceof MetaDataResource);
	}

	@Test
	public void testServiceDocumentResource() {
		Metadata md = mock(Metadata.class);
		GETMetadataCommand command = new GETMetadataCommand("ServiceDocument", getMetadataOData4j(md));
        InteractionContext ctx = createInteractionContext();
        command.execute(ctx);
		assertTrue(ctx.getResource() instanceof EntityResource);
	}

	@Test
	public void testGETMetadataODataMetadata() {
		Metadata md = mock(Metadata.class);
		GETMetadataCommand command = new GETMetadataCommand("Metadata", getMetadataOData4j(md));
        InteractionContext ctx = createInteractionContext();
        InteractionCommand.Result result = command.execute(ctx);
        assertEquals(InteractionCommand.Result.SUCCESS, result);
		assertTrue(ctx.getResource() instanceof MetaDataResource);
	}
	
	private MetadataOData4j getMetadataOData4j(Metadata metadata) {
		MetadataOData4j metadataOdata4j = 
						new	MetadataOData4j(metadata, 
						new ResourceStateMachine(
						new ResourceState("SD", "ServiceDocument", 
						new ArrayList<Action>(), "/")));
		return metadataOdata4j;
	}	
}
