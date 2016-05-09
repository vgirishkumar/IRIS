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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
* Map wrapper that adds read/write locks and also acquires a read lock
* to iterate through its pair elements.
* 
* @author aburgos
*/
public class HashMapWithReadWriteLock<K, V> extends HashMap<K, V> {

    private Map<K, V> map = new HashMap<K, V>();

    final private ReadWriteLock rwl = new ReentrantReadWriteLock();
    final private Lock read = rwl.readLock();
    final private Lock write = rwl.writeLock();

    public HashMapWithReadWriteLock() {
    }

    public V get(Object k) {
        read.lock();
        try {
            return map.get(k);
        } finally {
            read.unlock();
        }
    }

    public V put(K k, V v) {
        write.lock();
        try {
            return map.put(k, v);
        } finally {
            write.unlock();
        }
    }

    public boolean tryWriteLock() {
        return write.tryLock();
    }
    
    /**
     * Iterator for the map that acquires a read lock
     * when called and DOES NOT RELEASE IT UNLESS hasNext from
     * the map used to construct the object evaluates to false.
     * 
     * @invariant underlying map is not modified
     */
    public Iterator<Entry<K, V>> iterator() {

        Iterator<Entry<K, V>> wit = new Iterator<Entry<K, V>>() {

            // acquire a read lock
            {
                read.lock();
            }
            final Iterator<Entry<K, V>> it = map.entrySet().iterator();

            public boolean hasNext() {
                if(!it.hasNext()) {
                    read.unlock();
                    return false;
                } else {
                    return true;
                }
            }

            public Entry<K, V> next() {
                return it.next();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        
        return wit;
    }
}
