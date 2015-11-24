package com.temenos.interaction.winkext;

/*
 * #%L
 * interaction-winkext
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


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashSet;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.model.multipart.InMultiPart;
import org.junit.Test;

import com.temenos.interaction.core.command.CommandControllerInterface;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.hypermedia.ResourceStateProvider;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.rim.HTTPHypermediaRIM;

/**
 * TODO: Document me!
 *
 * @author mlambert
 *
 */
public class TestLazyResourceDelegate {
	@Test
	public void testRegularPost() {				
		final HTTPHypermediaRIM realResource = mock(HTTPHypermediaRIM.class);
		
		LazyResourceDelegate lazyResourceDelegate = new LazyResourceDelegate(mock(ResourceStateMachine.class),
				mock(ResourceStateProvider.class), mock(CommandControllerInterface.class), mock(Metadata.class), "test", "/", mock(HashSet.class)) {

			@Override
			HTTPHypermediaRIM getRealResource() {			
				return realResource;
			}						
		};
		
		HttpHeaders headers = mock(HttpHeaders.class);
		String id = "123";
		UriInfo uriInfo = mock(UriInfo.class);
		InMultiPart inMP = mock(InMultiPart.class);
		EntityResource resource = mock(EntityResource.class);
		
		lazyResourceDelegate.post(headers, id, uriInfo, resource);
		
		verify(realResource).post(headers, id, uriInfo, resource);
	}

	@Test
	public void testMultiPartPost() {				
		final HTTPHypermediaRIM realResource = mock(HTTPHypermediaRIM.class);
		
		LazyResourceDelegate lazyResourceDelegate = new LazyResourceDelegate(mock(ResourceStateMachine.class),
				mock(ResourceStateProvider.class), mock(CommandControllerInterface.class), mock(Metadata.class), "test", "/", mock(HashSet.class)) {

			@Override
			HTTPHypermediaRIM getRealResource() {			
				return realResource;
			}						
		};
		
		HttpHeaders headers = mock(HttpHeaders.class);
		UriInfo uriInfo = mock(UriInfo.class);
		InMultiPart inMP = mock(InMultiPart.class);
		
		lazyResourceDelegate.post(headers, uriInfo, inMP);
		
		verify(realResource).post(headers, uriInfo, inMP);
	}

	@Test
	public void testRegularPut() {				
		final HTTPHypermediaRIM realResource = mock(HTTPHypermediaRIM.class);
		
		LazyResourceDelegate lazyResourceDelegate = new LazyResourceDelegate(mock(ResourceStateMachine.class),
				mock(ResourceStateProvider.class), mock(CommandControllerInterface.class), mock(Metadata.class), "test", "/", mock(HashSet.class)) {

			@Override
			HTTPHypermediaRIM getRealResource() {			
				return realResource;
			}						
		};
		
		HttpHeaders headers = mock(HttpHeaders.class);
		String id = "123";
		UriInfo uriInfo = mock(UriInfo.class);
		InMultiPart inMP = mock(InMultiPart.class);
		EntityResource resource = mock(EntityResource.class);
		
		lazyResourceDelegate.put(headers, id, uriInfo, resource);
		
		verify(realResource).put(headers, id, uriInfo, resource);
	}
	
	@Test
	public void testMultiPartPut() {				
		final HTTPHypermediaRIM realResource = mock(HTTPHypermediaRIM.class);
		
		LazyResourceDelegate lazyResourceDelegate = new LazyResourceDelegate(mock(ResourceStateMachine.class),
				mock(ResourceStateProvider.class), mock(CommandControllerInterface.class), mock(Metadata.class), "test", "/", mock(HashSet.class)) {

			@Override
			HTTPHypermediaRIM getRealResource() {			
				return realResource;
			}						
		};
		
		HttpHeaders headers = mock(HttpHeaders.class);
		UriInfo uriInfo = mock(UriInfo.class);
		InMultiPart inMP = mock(InMultiPart.class);
		
		lazyResourceDelegate.put(headers, uriInfo, inMP);
		
		verify(realResource).put(headers, uriInfo, inMP);
	}	
}
