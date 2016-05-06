package com.temenos.interaction.core;

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
public class MapWithReadWriteLock<K, V> {

    private Map<K, V> map = new HashMap<K, V>();

    final private ReadWriteLock rwl = new ReentrantReadWriteLock();
    final private Lock read = rwl.readLock();
    final private Lock write = rwl.writeLock();

    public MapWithReadWriteLock() {
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

            final Iterator<Entry<K, V>> it = map.entrySet().iterator();
            // acquire a read lock
            {
                read.lock();
            }

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
