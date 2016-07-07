package com.temenos.interaction.loader.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.springframework.core.io.Resource;

import com.temenos.interaction.core.loader.PropertiesEvent;

public class ReloadablePropertiesBaseTest {

    @Test
    public void testSetListeners() throws Exception {
        ReloadablePropertiesListener<Resource> listener1 = mock(ReloadablePropertiesListener.class);
        ReloadablePropertiesListener<Resource> listener2 = mock(ReloadablePropertiesListener.class);
        ReloadablePropertiesListener<Resource> listener3 = mock(ReloadablePropertiesListener.class);
        
        List<ReloadablePropertiesListener<Resource>> listeners = new ArrayList<>();
        listeners.add(listener1);
        listeners.add(listener2);
        listeners.add(listener3);
        
        ReloadablePropertiesBase reloadable = new ReloadablePropertiesBase();
        reloadable.setListeners(listeners);
        
        assertEquals(3, reloadable.getListeners().size());
        assertTrue(reloadable.getListeners().contains(listener1));
        assertTrue(reloadable.getListeners().contains(listener2));
        assertTrue(reloadable.getListeners().contains(listener3));
    }

    @Test
    public void testAddReloadablePropertiesListener() throws Exception {
        ReloadablePropertiesListener<Resource> listener1 = mock(ReloadablePropertiesListener.class);
        ReloadablePropertiesListener<Resource> listener2 = mock(ReloadablePropertiesListener.class);
        ReloadablePropertiesListener<Resource> listener3 = mock(ReloadablePropertiesListener.class);
        
        ReloadablePropertiesBase reloadable = new ReloadablePropertiesBase();
        reloadable.addReloadablePropertiesListener(listener1);
        assertEquals(1, reloadable.getListeners().size());
        assertTrue(reloadable.getListeners().contains(listener1));
        
        reloadable.addReloadablePropertiesListener(listener2);
        assertEquals(2, reloadable.getListeners().size());
        assertTrue(reloadable.getListeners().contains(listener2));
        
        reloadable.addReloadablePropertiesListener(listener3);
        assertEquals(3, reloadable.getListeners().size());
        assertTrue(reloadable.getListeners().contains(listener3));
    }

    @Test
    public void testRemoveReloadablePropertiesListener() throws Exception {
        ReloadablePropertiesListener<Resource> listener1 = mock(ReloadablePropertiesListener.class);
        ReloadablePropertiesListener<Resource> listener2 = mock(ReloadablePropertiesListener.class);
        ReloadablePropertiesListener<Resource> listener3 = mock(ReloadablePropertiesListener.class);
        
        List<ReloadablePropertiesListener<Resource>> listeners = new ArrayList<>();
        listeners.add(listener1);
        listeners.add(listener2);
        listeners.add(listener3);
        
        ReloadablePropertiesBase reloadable = new ReloadablePropertiesBase();
        reloadable.setListeners(listeners);
        
        assertEquals(3, reloadable.getListeners().size());
        assertTrue(reloadable.getListeners().contains(listener1));
        assertTrue(reloadable.getListeners().contains(listener2));
        assertTrue(reloadable.getListeners().contains(listener3));
        
        reloadable.removeReloadablePropertiesListener(listener1);
        assertEquals(2, reloadable.getListeners().size());
        assertFalse(reloadable.getListeners().contains(listener1));
        assertTrue(reloadable.getListeners().contains(listener2));
        assertTrue(reloadable.getListeners().contains(listener3));

        reloadable.removeReloadablePropertiesListener(listener2);
        assertEquals(1, reloadable.getListeners().size());
        assertFalse(reloadable.getListeners().contains(listener1));
        assertFalse(reloadable.getListeners().contains(listener2));
        assertTrue(reloadable.getListeners().contains(listener3));

        reloadable.removeReloadablePropertiesListener(listener3);
        assertEquals(0, reloadable.getListeners().size());
        assertFalse(reloadable.getListeners().contains(listener1));
        assertFalse(reloadable.getListeners().contains(listener2));
        assertFalse(reloadable.getListeners().contains(listener3));
    }

    @Test
    public void testNotifyPropertiesLoaded() throws Exception {
        ReloadablePropertiesListener<Resource> listener1 = mock(ReloadablePropertiesListener.class);
        ReloadablePropertiesListener<Resource> listener2 = mock(ReloadablePropertiesListener.class);
        ReloadablePropertiesListener<Resource> listener3 = mock(ReloadablePropertiesListener.class);
        
        ReloadablePropertiesBase reloadable = new ReloadablePropertiesBase();
        reloadable.addReloadablePropertiesListener(listener1);
        reloadable.addReloadablePropertiesListener(listener2);
        reloadable.addReloadablePropertiesListener(listener3);

        Resource resource = mock(Resource.class);
        Properties newProperties = mock(Properties.class);
        
        reloadable.notifyPropertiesLoaded(resource, newProperties);
        
        verify(listener1).propertiesChanged(any(PropertiesEvent.class));
        verify(listener2).propertiesChanged(any(PropertiesEvent.class));
        verify(listener3).propertiesChanged(any(PropertiesEvent.class));
    }

    @Test
    public void testUpdateProperties() throws Exception {
        Properties properties = new Properties();
        properties.put(1, "one");
        properties.put(2, "two");

        ReloadablePropertiesBase reloadable = new ReloadablePropertiesBase();
        reloadable.setProperties(properties);
        
        Properties newProperties = new Properties();
        newProperties.put(2, "two_prime");
        newProperties.put(3, "three");
        newProperties.put(4, "four");

        boolean newAdded = reloadable.updateProperties(newProperties);
        assertTrue(newAdded);
        
        assertEquals(4, reloadable.getProperties().size());
        // old properties
        assertTrue(reloadable.getProperties().contains("one"));
        assertTrue(reloadable.getProperties().containsKey(1));
        assertEquals("one", reloadable.getProperties().get(1));
        assertTrue(reloadable.getProperties().contains("two_prime"));
        assertTrue(reloadable.getProperties().containsKey(2));
        assertEquals("two_prime", reloadable.getProperties().get(2));
        // new properties
        assertTrue(reloadable.getProperties().contains("three"));
        assertTrue(reloadable.getProperties().containsKey(3));
        assertEquals("three", reloadable.getProperties().get(3));
        assertTrue(reloadable.getProperties().contains("four"));
        assertTrue(reloadable.getProperties().containsKey(4));
        assertEquals("four", reloadable.getProperties().get(4));
    }

    @Test
    public void testNotifyPropertiesChanged() throws Exception {
        ReloadablePropertiesListener<Resource> listener1 = mock(ReloadablePropertiesListener.class);
        ReloadablePropertiesListener<Resource> listener2 = mock(ReloadablePropertiesListener.class);
        ReloadablePropertiesListener<Resource> listener3 = mock(ReloadablePropertiesListener.class);
        
        ReloadablePropertiesBase reloadable = new ReloadablePropertiesBase();
        reloadable.addReloadablePropertiesListener(listener1);
        reloadable.addReloadablePropertiesListener(listener2);
        reloadable.addReloadablePropertiesListener(listener3);

        Resource resource = mock(Resource.class);
        Properties newProperties = mock(Properties.class);
        
        reloadable.notifyPropertiesChanged(resource, newProperties);
        
        verify(listener1).propertiesChanged(any(PropertiesEvent.class));
        verify(listener2).propertiesChanged(any(PropertiesEvent.class));
        verify(listener3).propertiesChanged(any(PropertiesEvent.class));
    }

    @Test
    public void testSetProperties() throws Exception {
        Properties properties = new Properties();
        properties.put(1, "one");
        properties.put(2, "two");

        ReloadablePropertiesBase reloadable = new ReloadablePropertiesBase();
        reloadable.setProperties(properties);
        
        assertEquals(2, reloadable.getProperties().size());
        assertTrue(reloadable.getProperties().contains("one"));
        assertTrue(reloadable.getProperties().containsKey(1));
        assertEquals("one", reloadable.getProperties().get(1));
        assertTrue(reloadable.getProperties().contains("two"));
        assertTrue(reloadable.getProperties().containsKey(2));
        assertEquals("two", reloadable.getProperties().get(2));
    }

}
