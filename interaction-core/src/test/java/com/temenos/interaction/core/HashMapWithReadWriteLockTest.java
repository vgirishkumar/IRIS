package com.temenos.interaction.core;

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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

public class HashMapWithReadWriteLockTest {
    
    @Before
    public void setup() {
    }
    
    /*
     * Try to write the map while it is being iterated on.
     */
    @Test
    public void testWriteWhileIteraring() throws InterruptedException {
        HashMapWithReadWriteLock<String, String> map = new HashMapWithReadWriteLock<String, String>();
        
        Iterator<Entry<String, String>> it = map.iterator();

        // iterate through states
        while(it.hasNext()) {
            // try to acquire the write lock
            assertFalse(map.tryWriteLock());
            it.next();
        }

        // we should be able to acquire the write lock now
        assertTrue(map.tryWriteLock());
    }
}
