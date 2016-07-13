package com.temenos.interaction.core.resource;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
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

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.hypermedia.Transition;

/**
 * Unit tests for the CollectionResource class.
 *
 * @author dgroves
 *
 */
public class TestCollectionResource {
    
    private @Mock CollectionResource<Entity> copy;
    private @Mock EntityResource<Entity> mockEntityResource;
    private @Spy CollectionResource<Entity> resource;
    private Collection<Link> links;
    private Map<Transition, RESTResource> embedded;
    
    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        resource = spy(new CollectionResource<Entity>("test", getEntities()));
        resource.setLinks(links);
        resource.setEmbedded(embedded);
        resource.setEntityTag("abcd123");
        doReturn(copy).when(resource).createNewCollectionResource(Mockito.<Collection<EntityResource<Entity>>>any());
    }
        
    @Test
    public void testCopyReturnsShallowCopyOfCollectionResource(){
        //when {the collection resource's copy method is invoked}
        CollectionResource<Entity> myCopy = resource.shallowCopy();
        //then {the copy must not be the same instance as the original resource}
        assertThat(myCopy, not(sameInstance(resource)));
        //and {verify all required fields have been set}
        verify(copy).setEntityName("test");
        verify(copy).setEmbedded(same(embedded));
        verify(copy).setEntityTag(eq("abcd123"));
        verify(copy).setLinks(same(links));
    }
    
    private Collection<EntityResource<Entity>> getEntities(){
        List<EntityResource<Entity>> entities = new ArrayList<EntityResource<Entity>>();
        entities.add(mockEntityResource);
        return entities;
    }
}
