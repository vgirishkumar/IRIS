package com.temenos.interaction.media.odata.xml.atom;

/*
 * #%L
 * interaction-media-odata-xml
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


import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;

import org.junit.Test;

import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.Transition;
import com.temenos.interaction.core.resource.RESTResource;

public class TestODataLinkInterceptor {

	@Test
	public void testLinkRelationCollectionToItem() {
		Transition t = createMockTransition(
				createMockResourceState("FundsTransfers", "FundsTransfer", true), 
				createMockResourceState("customer", "Customer", false));
		
		AtomXMLProvider mockProviderHelper = mock(AtomXMLProvider.class);
		when(mockProviderHelper.getEntitySet(any(ResourceState.class))).thenReturn("FundsTransfers");
		ODataLinkInterceptor linkInterceptor = new ODataLinkInterceptor(mockProviderHelper);
		Link result = linkInterceptor.addingLink(mock(RESTResource.class), new Link(t, t.getTarget().getRel(), "/FundsTransfers()?$filter=DebitAcctNo eq '123'", HttpMethod.GET));
		assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/Customer", result.getRel());
	}

	@Test
	public void testLinkRelationCollectionToItemSameEntity() {
		Transition t = createMockTransition(
				createMockResourceState("FundsTransfers", "FundsTransfer", true), 
				createMockResourceState("fundsTransfer", "FundsTransfer", false));

		AtomXMLProvider mockProviderHelper = mock(AtomXMLProvider.class);
		when(mockProviderHelper.getEntitySet(any(ResourceState.class))).thenReturn("FundsTransfers");
		ODataLinkInterceptor linkInterceptor = new ODataLinkInterceptor(mockProviderHelper);
		Link result = linkInterceptor.addingLink(mock(RESTResource.class), new Link(t, t.getTarget().getRel(), "/FundsTransfers()?$filter=DebitAcctNo eq '123'", HttpMethod.GET));
		assertEquals("self", result.getRel());
	}

	@Test
	public void testLinkRelationItemToCollection() {
		Transition t = createMockTransition(
				createMockResourceState("account", "Account", false), 
				createMockResourceState("FundsTransfers", "FundsTransfer", true));

		AtomXMLProvider mockProviderHelper = mock(AtomXMLProvider.class);
		when(mockProviderHelper.getEntitySet(any(ResourceState.class))).thenReturn("FundsTransfers");
		ODataLinkInterceptor linkInterceptor = new ODataLinkInterceptor(mockProviderHelper);
		Link result = linkInterceptor.addingLink(mock(RESTResource.class), new Link(t, t.getTarget().getRel(), "/FundsTransfers()?$filter=DebitAcctNo eq '123'", HttpMethod.GET));
		assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/FundsTransfers", result.getRel());
	}

	@Test
	public void testLinkRelationItemToItem() {
		Transition t = createMockTransition(
				createMockResourceState("account", "Account", false), 
				createMockResourceState("FundsTransfers_new", "FundsTransfer", false));

		AtomXMLProvider mockProviderHelper = mock(AtomXMLProvider.class);
		when(mockProviderHelper.getEntitySet(any(ResourceState.class))).thenReturn("FundsTransfers");
		ODataLinkInterceptor linkInterceptor = new ODataLinkInterceptor(mockProviderHelper);
		Link result = linkInterceptor.addingLink(mock(RESTResource.class), new Link(t, t.getTarget().getRel(), "/FundsTransfers()?$filter=DebitAcctNo eq '123'", HttpMethod.GET));
		assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/FundsTransfer", result.getRel());
	}

	@Test
	public void testLinkFixedRelation() {
		CollectionResourceState targetState = mock(CollectionResourceState.class);
		when(targetState.getName()).thenReturn("FundsTransfers_new");
		when(targetState.getEntityName()).thenReturn("FundsTransfer");
		when(targetState.getRel()).thenReturn("http://www.temenos.com/rels/new");
		Transition t = createMockTransition(
				createMockResourceState("account", "Account", false), 
				targetState);

		AtomXMLProvider mockProviderHelper = mock(AtomXMLProvider.class);
		when(mockProviderHelper.getEntitySet(any(ResourceState.class))).thenReturn("FundsTransfers");
		ODataLinkInterceptor linkInterceptor = new ODataLinkInterceptor(mockProviderHelper);
		Link result = linkInterceptor.addingLink(mock(RESTResource.class), new Link(t, t.getTarget().getRel(), "/FundsTransfers()?$filter=DebitAcctNo eq '123'", HttpMethod.GET));
		assertEquals("http://www.temenos.com/rels/new", result.getRel());
	}

	@Test
	/*
	 * 'self' link rel should be added
	 */
	public void testLinkFixedRelationSelf() {
		ResourceState targetState = mock(ResourceState.class);
		when(targetState.getName()).thenReturn("FundsTransfer");
		when(targetState.getEntityName()).thenReturn("FundsTransfer");
		when(targetState.getRel()).thenReturn("self");
		Transition t = createMockTransition(
				createMockResourceState("account", "Account", false), 
				targetState);

		AtomXMLProvider mockProviderHelper = mock(AtomXMLProvider.class);
		when(mockProviderHelper.getEntitySet(any(ResourceState.class))).thenReturn("FundsTransfers");
		ODataLinkInterceptor linkInterceptor = new ODataLinkInterceptor(mockProviderHelper);
		Link result = linkInterceptor.addingLink(mock(RESTResource.class), new Link(t, t.getTarget().getRel(), "/FundsTransfers()?$filter=DebitAcctNo eq '123'", HttpMethod.GET));
		assertEquals("self", result.getRel());
	}

	@Test
	/*
	 * 'self' link rel should be added
	 */
	public void testLinkFixedRelationSelfCollection() {
		ResourceState targetState = mock(ResourceState.class);
		when(targetState.getName()).thenReturn("FundsTransfer");
		when(targetState.getEntityName()).thenReturn("FundsTransfer");
		when(targetState.getRel()).thenReturn("self");
		Transition t = createMockTransition(
				createMockResourceState("FundsTransfers", "FundsTransfer", true), 
				targetState);

		AtomXMLProvider mockProviderHelper = mock(AtomXMLProvider.class);
		when(mockProviderHelper.getEntitySet(any(ResourceState.class))).thenReturn("FundsTransfers");
		ODataLinkInterceptor linkInterceptor = new ODataLinkInterceptor(mockProviderHelper);
		Link result = linkInterceptor.addingLink(mock(RESTResource.class), new Link(t, t.getTarget().getRel(), "/FundsTransfers()?$filter=DebitAcctNo eq '123'", HttpMethod.GET));
		assertEquals("self", result.getRel());
	}

	@Test
	/*
	 * 'edit' link rel should be added
	 */
	public void testLinkFixedRelationEditCollection() {
		ResourceState sourceState = createMockResourceState("FundsTransfers", "FundsTransfer", true);
		// edit
		ResourceState targetStateEdit = mock(ResourceState.class);
		when(targetStateEdit.getName()).thenReturn("FundsTransfers_new");
		when(targetStateEdit.getEntityName()).thenReturn("FundsTransfer");
		when(targetStateEdit.getRel()).thenReturn("edit");
		Transition editTransition = createMockTransition(
				sourceState, 
				targetStateEdit);
		Link editLink = new Link(editTransition, editTransition.getTarget().getRel(), "/FundsTransfers('123')", HttpMethod.GET);
		
		List<Link> mockLinks = new ArrayList<Link>();
		mockLinks.add(editLink);
		AtomXMLProvider mockProviderHelper = mock(AtomXMLProvider.class);
		when(mockProviderHelper.getEntitySet(any(ResourceState.class))).thenReturn("FundsTransfers");
		ODataLinkInterceptor linkInterceptor = new ODataLinkInterceptor(mockProviderHelper);
		RESTResource mockResource = mock(RESTResource.class);
		when(mockResource.getLinks()).thenReturn(mockLinks);
		Link result = linkInterceptor.addingLink(mockResource, editLink);
		assertEquals("edit", result.getRel());
	}

	@Test
	/*
	 * 'edit' link rel is a special case that should be treated like 'self'
	 */
	public void testLinkFixedRelationEdit() {
		ResourceState targetState = mock(ResourceState.class);
		when(targetState.getName()).thenReturn("FundsTransfers_new");
		when(targetState.getEntityName()).thenReturn("FundsTransfer");
		when(targetState.getRel()).thenReturn("edit");
		Transition t = createMockTransition(
				createMockResourceState("account", "Account", false), 
				targetState);

		AtomXMLProvider mockProviderHelper = mock(AtomXMLProvider.class);
		when(mockProviderHelper.getEntitySet(any(ResourceState.class))).thenReturn("FundsTransfers");
		ODataLinkInterceptor linkInterceptor = new ODataLinkInterceptor(mockProviderHelper);
		Link result = linkInterceptor.addingLink(mock(RESTResource.class), new Link(t, t.getTarget().getRel(), "/FundsTransfers()?$filter=DebitAcctNo eq '123'", HttpMethod.GET));
		assertEquals("edit", result.getRel());
	}

	@Test
	/*
	 * 'edit' link rel is a special case that should be treated like 'self'
	 * Prefer 'edit' and drop the unwanted 'self' link.
	 */
	public void testLinkFixedRelationEditSelf() {
		ResourceState sourceState = createMockResourceState("account", "Account", false);
		// edit
		ResourceState targetStateEdit = mock(ResourceState.class);
		when(targetStateEdit.getName()).thenReturn("FundsTransfers_new");
		when(targetStateEdit.getEntityName()).thenReturn("FundsTransfer");
		when(targetStateEdit.getRel()).thenReturn("edit");
		Transition editTransition = createMockTransition(
				sourceState, 
				targetStateEdit);
		Link editLink = new Link(editTransition, editTransition.getTarget().getRel(), "/FundsTransfers('123')", HttpMethod.GET);
		// self
		ResourceState targetStateSelf = mock(ResourceState.class);
		when(targetStateSelf.getName()).thenReturn("FundsTransfer");
		when(targetStateSelf.getEntityName()).thenReturn("FundsTransfer");
		when(targetStateSelf.getRel()).thenReturn("self");
		Transition selfTransition = createMockTransition(
				sourceState, 
				targetStateSelf);
		Link selfLink = new Link(selfTransition, selfTransition.getTarget().getRel(), "/FundsTransfers('123')", HttpMethod.GET);
		
		List<Link> mockLinks = new ArrayList<Link>();
		mockLinks.add(selfLink);
		mockLinks.add(editLink);
		AtomXMLProvider mockProviderHelper = mock(AtomXMLProvider.class);
		when(mockProviderHelper.getEntitySet(any(ResourceState.class))).thenReturn("FundsTransfers");
		ODataLinkInterceptor linkInterceptor = new ODataLinkInterceptor(mockProviderHelper);
		RESTResource mockResource = mock(RESTResource.class);
		when(mockResource.getLinks()).thenReturn(mockLinks);
		
		Link resultEdit = linkInterceptor.addingLink(mockResource, editLink);
		assertEquals("edit", resultEdit.getRel());
		Link resultSelf = linkInterceptor.addingLink(mockResource, selfLink);
		assertNull(resultSelf);
	}

	@Test
	/*
	 * 'edit' link rel is a special case that should be treated like 'self'
	 * Prefer 'edit' and drop the unwanted 'self' link.
	 */
	public void testLinkFixedRelationEditSelfCollection() {
		ResourceState sourceState = createMockResourceState("FundsTransfers", "FundsTransfer", true);
		// edit
		ResourceState targetStateEdit = mock(ResourceState.class);
		when(targetStateEdit.getName()).thenReturn("FundsTransfers_new");
		when(targetStateEdit.getEntityName()).thenReturn("FundsTransfer");
		when(targetStateEdit.getRel()).thenReturn("edit");
		Transition editTransition = createMockTransition(
				sourceState, 
				targetStateEdit);
		Link editLink = new Link(editTransition, editTransition.getTarget().getRel(), "/FundsTransfers('123')", HttpMethod.GET);
		// self
		ResourceState targetStateSelf = mock(ResourceState.class);
		when(targetStateSelf.getName()).thenReturn("FundsTransfer");
		when(targetStateSelf.getEntityName()).thenReturn("FundsTransfer");
		when(targetStateSelf.getRel()).thenReturn("self");
		Transition selfTransition = createMockTransition(
				sourceState, 
				targetStateSelf);
		Link selfLink = new Link(selfTransition, selfTransition.getTarget().getRel(), "/FundsTransfers('123')", HttpMethod.GET);
		
		List<Link> mockLinks = new ArrayList<Link>();
		mockLinks.add(selfLink);
		mockLinks.add(editLink);
		AtomXMLProvider mockProviderHelper = mock(AtomXMLProvider.class);
		when(mockProviderHelper.getEntitySet(any(ResourceState.class))).thenReturn("FundsTransfers");
		ODataLinkInterceptor linkInterceptor = new ODataLinkInterceptor(mockProviderHelper);
		RESTResource mockResource = mock(RESTResource.class);
		when(mockResource.getLinks()).thenReturn(mockLinks);
		
		Link resultEdit = linkInterceptor.addingLink(mockResource, editLink);
		assertEquals("edit", resultEdit.getRel());
		Link resultSelf = linkInterceptor.addingLink(mockResource, selfLink);
		assertNull(resultSelf);
	}

	@Test
	/*
	 * 'edit' link rel is a special case that should be treated like 'self'
	 * Prefer 'edit' and drop the unwanted 'self' link.
	 */
	public void testLinkFixedRelationSelfEditCollection() {
		ResourceState sourceState = createMockResourceState("FundsTransfers", "FundsTransfer", true);
		// edit
		ResourceState targetStateEdit = mock(ResourceState.class);
		when(targetStateEdit.getName()).thenReturn("FundsTransfers_new");
		when(targetStateEdit.getEntityName()).thenReturn("FundsTransfer");
		when(targetStateEdit.getRel()).thenReturn("edit");
		Transition editTransition = createMockTransition(
				sourceState, 
				targetStateEdit);
		Link editLink = new Link(editTransition, editTransition.getTarget().getRel(), "/FundsTransfers('123')", HttpMethod.GET);
		// self
		ResourceState targetStateSelf = mock(ResourceState.class);
		when(targetStateSelf.getName()).thenReturn("FundsTransfer");
		when(targetStateSelf.getEntityName()).thenReturn("FundsTransfer");
		when(targetStateSelf.getRel()).thenReturn("self");
		Transition selfTransition = createMockTransition(
				sourceState, 
				targetStateSelf);
		Link selfLink = new Link(selfTransition, selfTransition.getTarget().getRel(), "/FundsTransfers('123')", HttpMethod.GET);
		
		List<Link> mockLinks = new ArrayList<Link>();
		mockLinks.add(editLink);
		mockLinks.add(selfLink);
		AtomXMLProvider mockProviderHelper = mock(AtomXMLProvider.class);
		when(mockProviderHelper.getEntitySet(any(ResourceState.class))).thenReturn("FundsTransfers");
		ODataLinkInterceptor linkInterceptor = new ODataLinkInterceptor(mockProviderHelper);
		RESTResource mockResource = mock(RESTResource.class);
		when(mockResource.getLinks()).thenReturn(mockLinks);
		
		Link resultEdit = linkInterceptor.addingLink(mockResource, editLink);
		assertEquals("edit", resultEdit.getRel());
		Link resultSelf = linkInterceptor.addingLink(mockResource, selfLink);
		assertNull(resultSelf);
	}

	@Test
	/*
	 * When two links have the same rel and the same href we should remove all but the first link
	 */
	public void testLinkRemoveDuplicates() {
		ResourceState sourceState = createMockResourceState("account", "Account", false);
		// edit
		ResourceState targetStateUpdate = mock(ResourceState.class);
		when(targetStateUpdate.getName()).thenReturn("FundsTransfers_new");
		when(targetStateUpdate.getEntityName()).thenReturn("FundsTransfer");
		when(targetStateUpdate.getRel()).thenReturn("edit");
		Transition editTransition = createMockTransition(
				sourceState, 
				targetStateUpdate);
		Link editLink = new Link(editTransition, editTransition.getTarget().getRel(), "/FundsTransfers('123')", HttpMethod.POST);
		// edit2
		ResourceState targetStateDelete = mock(ResourceState.class);
		when(targetStateDelete.getName()).thenReturn("FundsTransfers_delete");
		when(targetStateDelete.getEntityName()).thenReturn("FundsTransfer");
		when(targetStateDelete.getRel()).thenReturn("edit");
		Transition edit2Transition = createMockTransition(
				sourceState, 
				targetStateDelete);
		Link edit2Link = new Link(edit2Transition, edit2Transition.getTarget().getRel(), "/FundsTransfers('123')", HttpMethod.DELETE);

		List<Link> mockLinks = new ArrayList<Link>();
		mockLinks.add(editLink);
		mockLinks.add(edit2Link);
		AtomXMLProvider mockProviderHelper = mock(AtomXMLProvider.class);
		when(mockProviderHelper.getEntitySet(any(ResourceState.class))).thenReturn("FundsTransfers");
		ODataLinkInterceptor linkInterceptor = new ODataLinkInterceptor(mockProviderHelper);
		RESTResource mockResource = mock(RESTResource.class);
		when(mockResource.getLinks()).thenReturn(mockLinks);
		
		Link resultEdit = linkInterceptor.addingLink(mockResource, editLink);
		assertEquals("edit", resultEdit.getRel());
		Link resultEdit2 = linkInterceptor.addingLink(mockResource, edit2Link);
		assertNull(resultEdit2);
	}

	@Test
	public void testLinkRelationInitialCollectionToCollectionSameEntity() {
		Transition t = createMockTransition(
				createMockResourceState("FundsTransfers", "FundsTransfer", true), 
				createMockResourceState("FundsTransfersIAuth", "FundsTransfer", true));

		AtomXMLProvider mockProviderHelper = mock(AtomXMLProvider.class);
		when(mockProviderHelper.getEntitySet(any(ResourceState.class))).thenReturn("FundsTransfers");
		ODataLinkInterceptor linkInterceptor = new ODataLinkInterceptor(mockProviderHelper);
		Link result = linkInterceptor.addingLink(mock(RESTResource.class), new Link(t, t.getTarget().getRel(), "/FundsTransfers()?$filter=DebitAcctNo eq '123'", HttpMethod.GET));
		assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/FundsTransfers", result.getRel());
	}

	@Test
	public void testLinkRelationCollectionToCollectionSameEntity() {
		Transition t = createMockTransition(
				createMockResourceState("FundsTransfersIAuth", "FundsTransfer", true), 
				createMockResourceState("FundsTransfersIHold", "FundsTransfer", true));

		AtomXMLProvider mockProviderHelper = mock(AtomXMLProvider.class);
		when(mockProviderHelper.getEntitySet(any(ResourceState.class))).thenReturn("FundsTransfers");
		ODataLinkInterceptor linkInterceptor = new ODataLinkInterceptor(mockProviderHelper);
		Link result = linkInterceptor.addingLink(mock(RESTResource.class), new Link(t, t.getTarget().getRel(), "/FundsTransfers()?$filter=DebitAcctNo eq '123'", HttpMethod.GET));
		assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/FundsTransfers", result.getRel());
	}

	private ResourceState createMockResourceState(String name, String entityName, boolean isCollection) {
		ResourceState state = null;
		if (isCollection) {
			state = new CollectionResourceState(entityName, name, new ArrayList<Action>(), "/"+name);
		} else {
			state = new ResourceState(entityName, name, new ArrayList<Action>(), "/"+name);
		}
		return state; 
	}

	private Transition createMockTransition(ResourceState source, ResourceState target) {
		Transition.Builder builder = new Transition.Builder();
		builder.source(source);
		builder.target(target);
		builder.method("GET");
		return builder.build();
	}

}
