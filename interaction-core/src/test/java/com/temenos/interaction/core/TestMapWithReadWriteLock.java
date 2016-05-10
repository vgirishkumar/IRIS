package com.temenos.interaction.core;

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


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

public class TestMapWithReadWriteLock {

	@Test
	public void testGet() throws Exception {
	    Map<Integer, String> map = new MapWithReadWriteLock<Integer, String>();
	    map.put(0, "val0");
	    map.put(6, "val6");
	    assertTrue(map.get(0) == "val0");
	    assertTrue(map.get(6) == "val6");
        assertTrue(map.get(1) == null);
	}
	
    @Test
    public void testPut() throws Exception {
        Map<Integer, String> map = new MapWithReadWriteLock<Integer, String>();
        map.put(0, "val0");
        assertTrue(map.get(0) == "val0");
        map.put(0, "val00");
        assertTrue(map.get(0) == "val00");
    }
    
    @Test
    public void testSize() throws Exception {
        Map<Integer, String> map = new MapWithReadWriteLock<Integer, String>();
        assertTrue(map.size() == 0);
        map.put(0, "val0");
        assertTrue(map.size() == 1);
        map.put(6, "val6");
        assertTrue(map.size() == 2);
        map.put(6, "val66");
        assertTrue(map.size() == 2);
    }
    
    @Test
    public void testIsEmpty() throws Exception {
        Map<Integer, String> map = new MapWithReadWriteLock<Integer, String>();
        assertTrue(map.isEmpty());
        map.put(0, "val0");
        assertFalse(map.isEmpty());
        map.remove(0);
        assertTrue(map.isEmpty());
    }
    
    @Test
    public void testContainsKey() throws Exception {
        Map<Integer, String> map = new MapWithReadWriteLock<Integer, String>();
        assertFalse(map.containsKey(0));       
        map.put(0, "val0");
        map.put(6, "val6");
        assertTrue(map.containsKey(0));       
        assertTrue(map.containsKey(6));
        map.remove(0);
        assertFalse(map.containsKey(0));
    }
    
    @Test
    public void testContainsValue() throws Exception {
        Map<Integer, String> map = new MapWithReadWriteLock<Integer, String>();
        assertFalse(map.containsValue("val0"));       
        map.put(0, "val0");
        map.put(6, "val6");
        assertTrue(map.containsValue("val0"));       
        assertTrue(map.containsValue("val6"));
    }
    
    @Test
    public void testRemoveKey() throws Exception {
        Map<Integer, String> map = new MapWithReadWriteLock<Integer, String>();
        assertTrue(map.remove(0) == null);       
        map.put(0, "val0");
        map.put(6, "val6");
        assertTrue(map.remove(0) == "val0");       
        assertTrue(map.remove(6) == "val6");        
    }
    
    @Test
    public final void testPutAll() throws Exception {
        Map<Integer, String> map1 = new MapWithReadWriteLock<Integer, String>();
        assertTrue(map1.remove(0) == null);       
        map1.put(0, "val0");
        map1.put(6, "val6");
        
        Map<Integer, String> map2 = new MapWithReadWriteLock<Integer, String>();
        assertTrue(map2.isEmpty());
        map2.putAll(map1);
        assertTrue(map2.get(0) == "val0");
        assertTrue(map2.get(6) == "val6");
    }

    @Test
    public final void testClear() throws Exception {
        Map<Integer, String> map = new MapWithReadWriteLock<Integer, String>();
        assertTrue(map.isEmpty());       
        map.put(0, "val0");
        map.put(6, "val6");
        assertFalse(map.isEmpty());
        map.clear();
        assertTrue(map.isEmpty());       
    }

    @Test
    public final void testKeySet() throws Exception {
        Map<Integer, String> map = new MapWithReadWriteLock<Integer, String>();
        assertTrue(map.values().isEmpty());
        map.put(0, "val0");
        map.put(6, "val6");
        Set<Integer> keys = map.keySet();
        assertFalse(keys.contains(1));
        assertTrue(keys.contains(0));
        assertTrue(keys.contains(6));
        map.put(0, "val1");
        assertTrue(keys.size() == 2);
        // modifying the underlying map doesn't modify the set
        map.remove(0);
        assertTrue(keys.contains(0));
        assertTrue(map.size() == 1);
        assertTrue(keys.size() == 2);
    }

    @Test
    public final void testValues() throws Exception {
        Map<Integer, String> map = new MapWithReadWriteLock<Integer, String>();
        assertTrue(map.values().isEmpty());
        map.put(0, "val0");
        map.put(6, "val6");
        Collection<String> values = map.values();
        assertFalse(values.contains("val"));
        assertTrue(values.contains("val0"));
        assertTrue(values.contains("val6"));
        // modifying the underlying map doesn't modify the collection
        map.remove(0);
        assertTrue(values.contains("val0"));
        assertTrue(values.contains("val6"));
        map.put(0, "val00");
        assertFalse(values.contains("val00"));
        map.put(6, "val66");
        assertFalse(values.contains("val66"));
    }

    @Test
    public final void testEntrySet() throws Exception {
        Map<Integer, String> map = new MapWithReadWriteLock<Integer, String>();
        assertTrue(map.entrySet().isEmpty());
        map.put(0, "val0");
        map.put(6, "val6");
        Set<Entry<Integer, String>> entries = map.entrySet();
        assertFalse(entries.contains(new SimpleEntry<Integer, String>(0, "val")));
        assertTrue(entries.contains(new SimpleEntry<Integer, String>(0, "val0")));
        assertTrue(entries.contains(new SimpleEntry<Integer, String>(6, "val6")));
        // modifying the underlying map doesn't modify the set
        map.remove(0);
        assertTrue(entries.contains(new SimpleEntry<Integer, String>(0, "val0")));
        assertTrue(entries.contains(new SimpleEntry<Integer, String>(6, "val6")));
    }
}
