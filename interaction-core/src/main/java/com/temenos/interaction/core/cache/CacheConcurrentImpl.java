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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Implementation of a cache using a ConcurrentHashMap.
 * 
 * @author kwieconkowski
 * @author andres
 * @author dgroves
 */
public class CacheConcurrentImpl<K, V> implements Cache<K, V> {
    private ConcurrentMap<K, V> cache = new ConcurrentHashMap<K, V>();

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public void put(K key, V value, int ageInSeconds) {
        throw new UnsupportedOperationException("Not supported operation for this implementation");
    }

    @Override
    public void putAll(Map<K, V> keyValueMap) {
        cache.putAll(keyValueMap);
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
    }

    @Override
    public void removeAll() {
        cache.clear();
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }
}
