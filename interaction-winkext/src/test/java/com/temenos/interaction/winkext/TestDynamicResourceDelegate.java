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


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.model.multipart.InMultiPart;
import org.junit.Test;

import com.temenos.interaction.core.UriInfoImpl;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.rim.HTTPHypermediaRIM;

/**
 * TODO: Document me!
 *
 * @author mlambert
 *
 */
public class TestDynamicResourceDelegate {
	@Test
	public void testRegularPost() {
		HTTPHypermediaRIM parent = mock(HTTPHypermediaRIM.class);
		HTTPHypermediaRIM resource = mock(HTTPHypermediaRIM.class);
		
		DynamicResourceDelegate delegate = new DynamicResourceDelegate(parent, resource);
		
		HttpHeaders headers = mock(HttpHeaders.class);
		String id = "123";
		UriInfo uriInfo = mock(UriInfo.class);
		EntityResource<Entity> entityResource = mock(EntityResource.class);
		
		delegate.post(headers, id, uriInfo, entityResource);
		
		verify(resource).post(eq(headers), eq(id), any(UriInfoImpl.class), eq(entityResource));
	}

	@Test
	public void testMultiPartPost() {
		HTTPHypermediaRIM parent = mock(HTTPHypermediaRIM.class);
		HTTPHypermediaRIM resource = mock(HTTPHypermediaRIM.class);
		
		DynamicResourceDelegate delegate = new DynamicResourceDelegate(parent, resource);
		
		HttpHeaders headers = mock(HttpHeaders.class);
		UriInfo uriInfo = mock(UriInfo.class);
		InMultiPart inMP = mock(InMultiPart.class);
		
		delegate.post(headers, uriInfo, inMP);
		
		verify(resource).post(eq(headers), any(UriInfoImpl.class), eq(inMP));
	}

    @Test
    public void testRegularPostWithFormParams() {
        HTTPHypermediaRIM parent = mock(HTTPHypermediaRIM.class);
        HTTPHypermediaRIM resource = mock(HTTPHypermediaRIM.class);

        DynamicResourceDelegate delegate = new DynamicResourceDelegate(parent, resource);

        HttpHeaders headers = mock(HttpHeaders.class);
        String id = "123";
        UriInfo uriInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> formParams = mock(MultivaluedMap.class);

        delegate.post(headers, id, uriInfo, formParams);

        verify(resource).post(eq(headers), eq(id), any(UriInfoImpl.class), eq(formParams));
    }

	@Test
	public void testRegularPut() {
		HTTPHypermediaRIM parent = mock(HTTPHypermediaRIM.class);
		HTTPHypermediaRIM resource = mock(HTTPHypermediaRIM.class);
		
		DynamicResourceDelegate delegate = new DynamicResourceDelegate(parent, resource);
		
		HttpHeaders headers = mock(HttpHeaders.class);
		String id = "123";
		UriInfo uriInfo = mock(UriInfo.class);
		EntityResource<Entity> entityResource = mock(EntityResource.class);
		
		delegate.put(headers, id, uriInfo, entityResource);
		
		verify(resource).put(eq(headers), eq(id), any(UriInfoImpl.class), eq(entityResource));
	}
	
	@Test
	public void testMultiPartPut() {
		HTTPHypermediaRIM parent = mock(HTTPHypermediaRIM.class);
		HTTPHypermediaRIM resource = mock(HTTPHypermediaRIM.class);
		
		DynamicResourceDelegate delegate = new DynamicResourceDelegate(parent, resource);
		
		HttpHeaders headers = mock(HttpHeaders.class);
		UriInfo uriInfo = mock(UriInfo.class);
		InMultiPart inMP = mock(InMultiPart.class);
		
		delegate.put(headers, uriInfo, inMP);
		
		verify(resource).put(eq(headers), any(UriInfoImpl.class), eq(inMP));
	}	
	
    @Test
    public void testRegularGet() {
        HTTPHypermediaRIM parent = mock(HTTPHypermediaRIM.class);
        HTTPHypermediaRIM resource = mock(HTTPHypermediaRIM.class);

        DynamicResourceDelegate delegate = new DynamicResourceDelegate(parent, resource);

        HttpHeaders headers = mock(HttpHeaders.class);
        String id = "123";
        UriInfo uriInfo = mock(UriInfo.class);

        delegate.get(headers, id, uriInfo);

        verify(resource).get(eq(headers), eq(id), any(UriInfoImpl.class));
    }

    @Test
    public void testRegularDelete() {
        HTTPHypermediaRIM parent = mock(HTTPHypermediaRIM.class);
        HTTPHypermediaRIM resource = mock(HTTPHypermediaRIM.class);

        DynamicResourceDelegate delegate = new DynamicResourceDelegate(parent, resource);

        HttpHeaders headers = mock(HttpHeaders.class);
        String id = "123";
        UriInfo uriInfo = mock(UriInfo.class);

        delegate.delete(headers, id, uriInfo);

        verify(resource).delete(eq(headers), eq(id), any(UriInfoImpl.class));
    }

    @Test
    public void testRegularOptions() {
        HTTPHypermediaRIM parent = mock(HTTPHypermediaRIM.class);
        HTTPHypermediaRIM resource = mock(HTTPHypermediaRIM.class);

        DynamicResourceDelegate delegate = new DynamicResourceDelegate(parent, resource);

        HttpHeaders headers = mock(HttpHeaders.class);
        String id = "123";
        UriInfo uriInfo = mock(UriInfo.class);

        delegate.options(headers, id, uriInfo);

        verify(resource).options(eq(headers), eq(id), any(UriInfoImpl.class));
    }

    @Test
    public void testGetBeanNameOfNonDynamicResource() {
        ResourceState rs = mock(ResourceState.class);
        when(rs.getId()).thenReturn("123");

        HTTPHypermediaRIM parent = mock(HTTPHypermediaRIM.class);
        HTTPHypermediaRIM resource = mock(HTTPHypermediaRIM.class);
        when(resource.getCurrentState()).thenReturn(rs);
        
        DynamicResourceDelegate delegate = new DynamicResourceDelegate(parent, resource);

        delegate.getBeanName();

        verify(rs).getId();
        verify(resource).getCurrentState();
    }

}
