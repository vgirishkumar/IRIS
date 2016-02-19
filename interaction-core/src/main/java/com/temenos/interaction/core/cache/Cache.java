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

/**
 * Interface for caching objects with the option of specifying their life time.
 * 
 * @author kwieconkowski
 * @author andres
 * @author dgroves
 */
public interface Cache<K, V> {
    void put(K key, V value);
    void put(K key, V value, int ageInSeconds);
    void putAll(Map<K, V> keyValueMap);
    V get(K key);
    void remove(K key);
    void removeAll();
    boolean isEmpty();
}
