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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
* Wraps a map with read/write locks.
* 
* @author aburgos
*/
public class MapWithReadWriteLock<K, V> implements Map<K, V> {

    private Map<K, V> map = new HashMap<K, V>();

    final private ReadWriteLock rwl = new ReentrantReadWriteLock();
    final private Lock read = rwl.readLock();
    final private Lock write = rwl.writeLock();

    @Override
    public V get(Object k) {
        read.lock();
        try {
            return map.get(k);
        } finally {
            read.unlock();
        }
    }

    @Override
    public V put(K k, V v) {
        write.lock();
        try {
            return map.put(k, v);
        } finally {
            write.unlock();
        }
    }

    @Override
    public int size() {
        read.lock();
        int value = map.size();
        read.unlock();
        return value;
    }

    @Override
    public boolean isEmpty() {
        read.lock();
        boolean value = map.isEmpty();
        read.unlock();
        return value;
    }

    @Override
    public boolean containsKey(Object key) {
        read.lock();
        boolean value = map.containsKey(key);
        read.unlock();
        return value;
    }

    @Override
    public boolean containsValue(Object value) {
        read.lock();
        boolean val = map.containsValue(value);
        read.unlock();
        return val;
    }

    @Override
    public V remove(Object key) {
        write.lock();
        V value = map.remove(key);
        write.unlock();
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        write.lock();
        map.putAll(m);
        write.unlock();
    }

    @Override
    public void clear() {
        write.lock();
        map.clear();
        write.unlock();
    }

    @Override
    public Set<K> keySet() {
        Set<K> result = null;
        
        read.lock();
        result = new HashSet<K>(map.keySet());
        read.unlock();
        
        return result;
    }

    @Override
    public Collection<V> values() {
        Collection<V> values = null;
        
        read.lock();
        values = new HashSet<V>(map.values());
        read.unlock();
        
        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {        
        Set<Entry<K, V>> result = null;
        
        read.lock();
        result = new HashSet<Entry<K, V>>(map.entrySet());
        read.unlock();
        
        return result;
    }
}
