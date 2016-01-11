package com.temenos.interaction.core.resource;

/*
 * #%L
 * interaction-core
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.not;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OLink;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmEntitySet;

import com.temenos.interaction.core.NestedObject;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.hypermedia.Transition;

public class TestEntityResource {

	private @Spy EntityResource<OEntity> oEntityEntityResource;
	private @Spy EntityResource<Entity> entityEntityResource;
	private @Spy EntityResource<Object> otherEntityResource;
	
	private @Mock EntityResource<OEntity> oEntityEntityResourceMock;
	private @Mock EntityResource<Entity> entityEntityResourceMock;
	
	private @Mock OProperty<String> oProperty;
	private @Mock Entity entity;
	private @Mock OEntity oEntity;
	private @Mock Object otherEntity;	
	
	
	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		when(this.entity.getName()).thenReturn("Jim");
		when(this.entity.getProperties()).thenReturn(mock(EntityProperties.class));
		when(this.oEntity.getEntityKey()).thenReturn(mock(OEntityKey.class)); 
		when(this.oEntity.getProperties()).thenReturn(Arrays.asList(new OProperty<?>[]{this.oProperty}));
		when(this.oEntity.getEntitySet()).thenReturn(mock(EdmEntitySet.class));
		when(this.oEntity.getLinks()).thenReturn(Arrays.asList(new OLink[]{mock(OLink.class)}));
		this.oEntityEntityResource = spy(new EntityResource<OEntity>("John", this.oEntity));
		this.entityEntityResource = spy(new EntityResource<Entity>("Jim", this.entity));
		this.otherEntityResource = spy(new EntityResource<Object>("Derek", this.otherEntity));
	}
	
	@After
	public void tearDown(){
		
	}
	
	@Test
	public void testCloneWithDeepCopyOfEntitiesWithOEntity(){		
		//given
		doReturn(this.oEntityEntityResourceMock).when(this.oEntityEntityResource).createNewEntityResource(anyString(), any(OEntity.class));
		//when
		EntityResource<?> result = this.oEntityEntityResource.cloneWithDeepCopyOfEntities();
		assertThat((OEntity)result.getEntity(), not(sameInstance(this.oEntity)));
		//then
		verify(this.oEntity, atLeastOnce()).getEntityKey();
		verify(this.oEntity, atLeastOnce()).getEntitySet();
		verify(this.oEntity, atLeastOnce()).getProperties();
		verify(this.oEntity, atLeastOnce()).getLinks();
		verify(this.oEntityEntityResourceMock).setEmbedded(anyMapOf(Transition.class, RESTResource.class));
		verify(this.oEntityEntityResourceMock).setLinks(anyCollectionOf(Link.class));
		verify(this.oEntityEntityResourceMock).setEntityTag(anyString());
	}
	
	@Test
	public void testCloneWithDeepCopyOfEntitiesWithEntity(){		
		//given
		doReturn(this.entityEntityResourceMock).when(this.entityEntityResource).createNewEntityResource(anyString(), any(Entity.class));
		//when
		EntityResource<?> result = this.entityEntityResource.cloneWithDeepCopyOfEntities();
		assertThat((Entity)result.getEntity(), not(sameInstance(this.entity)));
		//then
		verify(this.entity).getName();
		verify(this.entity).getProperties();
		verify(this.entityEntityResourceMock).setEmbedded(anyMapOf(Transition.class, RESTResource.class));
		verify(this.entityEntityResourceMock).setLinks(anyCollectionOf(Link.class));
		verify(this.entityEntityResourceMock).setEntityTag(anyString());
	}
	
	@Test
	public void testCloneWithDeepCopyOfEntitiesWithAnotherTypeOfEntity(){
		//when
		assertThat(this.otherEntityResource.cloneWithDeepCopyOfEntities().getEntity(), sameInstance(this.otherEntity));
		//then
		verifyZeroInteractions(this.otherEntity);
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEntityObject() throws JAXBException {
		String testXMLString = "<resource><Test/></resource>";
		
		JAXBContext jc = JAXBContext.newInstance(EntityResource.class, NestedObject.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        EntityResource<NestedObject> er = (EntityResource<NestedObject>) unmarshaller.unmarshal(new ByteArrayInputStream(testXMLString.getBytes()));
 
        assertNotNull(er);
        assertNotNull(er.getEntity());
		assertTrue(er.getEntity() instanceof NestedObject);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEntityObjectName() throws JAXBException {
		String testXMLString = "<resource><Test><name>noah</name></Test></resource>";
		
		JAXBContext jc = JAXBContext.newInstance(EntityResource.class, NestedObject.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        EntityResource<NestedObject> er = (EntityResource<NestedObject>) unmarshaller.unmarshal(new ByteArrayInputStream(testXMLString.getBytes()));
 
        assertNotNull(er);
        assertNotNull(er.getEntity());
		assertEquals("noah", ((NestedObject)er.getEntity()).getName());
	}

}
