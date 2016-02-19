package com.temenos.interaction.core.cache;

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

import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.ResourceState;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author kwieconkowski
 */
public class TestCacheConcurrentImpl {

    private Cache<String, ResourceState> cache;

    @Before
    public void setUp() throws Exception {
        cache = new CacheConcurrentImpl();
    }

    private ResourceState createResourceState(String resourceName) {
        return createResourceState(resourceName, resourceName, null, "/");
    }

    private ResourceState createResourceState(String entityName, String name, List<Action> actions, String path) {
        return new ResourceState(entityName, name, actions, path);
    }

    @Test
    public void testPut() {
        String id;
        ResourceState example, valueFromCache;

        for (int i = 0; i < 10; i++) {
            id = "example_" + i;
            example = createResourceState(id);
            cache.put(id, example);

            valueFromCache = cache.get(id);
            assertFalse(cache.isEmpty());
            assertSame(example, valueFromCache);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPutWithAge() {
        cache.put("example", createResourceState("example"), 10);
    }

    @Test
    public void testPutAll() throws Exception {
        String id;
        ResourceState tmp;
        final int maxIterations = 10;
        Map<String, ResourceState> example = new HashMap<String, ResourceState>();

        for (int i = 0; i < maxIterations; i++) {
            id = "example_" + i;
            tmp = createResourceState(id);
            example.put(id, tmp);
        }
        cache.putAll(example);

        assertFalse(cache.isEmpty());
        for (int i = 0; i < maxIterations; i++) {
            id = "example_" + i;
            assertNotNull(cache.get(id));
        }
    }

    @Test
    public void testGet() throws Exception {
        final String id = "example";
        final ResourceState example = createResourceState(id);
        assertNull(cache.get(id));
        cache.put(id, example);
        assertFalse(cache.isEmpty());
        assertSame(example, cache.get(id));
    }

    @Test
    public void testRemove() throws Exception {
        final String id = "example";
        assertTrue(cache.isEmpty());
        cache.put(id, createResourceState(id));
        assertFalse(cache.isEmpty());
        assertNotNull(cache.get(id));
        cache.remove(id);
        assertTrue(cache.isEmpty());
        assertNull(cache.get(id));
    }

    @Test
    public void testRemoveAll() throws Exception {
        String id;

        assertTrue(cache.isEmpty());
        for (int i = 0; i < 10; i++) {
            id = "example_" + i;
            cache.put(id, createResourceState(id));
        }
        assertFalse(cache.isEmpty());
        cache.removeAll();
        assertTrue(cache.isEmpty());
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertTrue(cache.isEmpty());
        cache.put("example", createResourceState("example"));
        assertFalse(cache.isEmpty());
        cache.removeAll();
        assertTrue(cache.isEmpty());
    }
}