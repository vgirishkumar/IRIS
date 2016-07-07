package com.temenos.interaction.loader.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.temenos.interaction.core.loader.FileEvent;
import com.temenos.interaction.loader.properties.ReloadablePropertiesFactoryBean.SimplePattern;
import com.temenos.interaction.loader.xml.resource.notification.XmlModificationNotifier;

public class ReloadablePropertiesFactoryBeanTest {

    @Test
    public void testSetListeners() throws Exception {
        List<ReloadablePropertiesListener<Resource>> listeners = new ArrayList<>();
        ReloadablePropertiesListener<Resource> listener1 = mock(ReloadablePropertiesListener.class);
        ReloadablePropertiesListener<Resource> listener2 = mock(ReloadablePropertiesListener.class);
        listeners.add(listener1);
        listeners.add(listener2);
        
        ReloadablePropertiesFactoryBean rp = new ReloadablePropertiesFactoryBean();
        rp.setListeners(listeners);
        
        assertEquals(2, rp.getListeners().size());
        assertTrue(rp.getListeners().contains(listener1));
        assertTrue(rp.getListeners().contains(listener2));
    }

    @Test
    public void testGetListeners() throws Exception {
        List<ReloadablePropertiesListener<Resource>> listeners = new ArrayList<>();
        ReloadablePropertiesListener<Resource> listener1 = mock(ReloadablePropertiesListener.class);
        ReloadablePropertiesListener<Resource> listener2 = mock(ReloadablePropertiesListener.class);
        listeners.add(listener1);
        listeners.add(listener2);
        
        ReloadablePropertiesFactoryBean rp = new ReloadablePropertiesFactoryBean();

        assertTrue(rp.getListeners().isEmpty());

        rp.setListeners(listeners);
        
        assertEquals(2, rp.getListeners().size());
        assertTrue(rp.getListeners().contains(listener1));
        assertTrue(rp.getListeners().contains(listener2));
    }
    
    @Test
    public void testSetProperties() throws Exception {
        Properties properties = new Properties();
        properties.put(1, "one");
        properties.put(2, "two");

        ReloadablePropertiesFactoryBean rp = new ReloadablePropertiesFactoryBean();
        rp.setProperties(properties);
        
        assertEquals(2, rp.getProperties().size());
        assertTrue(rp.getProperties().contains("one"));
        assertTrue(rp.getProperties().containsKey(1));
        assertEquals("one", rp.getProperties().get(1));
        assertTrue(rp.getProperties().contains("two"));
        assertTrue(rp.getProperties().containsKey(2));
        assertEquals("two", rp.getProperties().get(2));
    }

    @Test
    public void testCreateInstance() throws Exception {
        ReloadablePropertiesFactoryBean rp = new ReloadablePropertiesFactoryBean();
        assertTrue(rp.isSingleton());

        ApplicationContext ctx = mock(ApplicationContext.class);
        Resource[] resources = new Resource[0];
        when(ctx.getResources(any(String.class))).thenReturn(resources);
        rp.setApplicationContext(ctx);

        Properties properties = new Properties();
        properties.put(1, "one");
        properties.put(2, "two");
        rp.setProperties(properties);

        List<ReloadablePropertiesListener<Resource>> listeners = new ArrayList<>();
        ReloadablePropertiesListener<Resource> listener1 = mock(ReloadablePropertiesListener.class);
        ReloadablePropertiesListener<Resource> listener2 = mock(ReloadablePropertiesListener.class);
        listeners.add(listener1);
        listeners.add(listener2);
        rp.setListeners(listeners);
        
        ReloadablePropertiesBase newInstance = (ReloadablePropertiesBase) rp.createInstance();
        assertEquals(rp.getProperties(), newInstance.getProperties());
        assertEquals(rp.getListeners(), newInstance.getListeners());
    }

    @Test(expected=RuntimeException.class)
    public void testCreateInstanceNotSingleton() throws Exception {
        ReloadablePropertiesFactoryBean rp = new ReloadablePropertiesFactoryBean();
        rp.setSingleton(false);
        rp.createInstance();
    }

    @Test(expected=AssertionError.class)
    public void testDestroy() throws Exception {
        ReloadablePropertiesFactoryBean rp = new ReloadablePropertiesFactoryBean();

        ApplicationContext ctx = mock(ApplicationContext.class);
        Resource[] resources = new Resource[0];
        when(ctx.getResources(any(String.class))).thenReturn(resources);
        rp.setApplicationContext(ctx);

        ReloadablePropertiesBase newInstance = (ReloadablePropertiesBase) rp.createInstance();
        assertNotNull(newInstance);
        // reload works
        rp.reload(true);

        rp.destroy();
        // cannot reload with a null reloadableProperties 
        rp.reload(true);
    }

    @Test
    public void testGetMoreRecentThan() throws Exception {
        ReloadablePropertiesFactoryBean rp = new ReloadablePropertiesFactoryBean();

        final long timestamp = System.currentTimeMillis();

        String rootPath = "src/test/resources/root";
        Path root = Paths.get(rootPath);
        Files.createDirectories(root);
        Files.setAttribute(root, "lastModifiedTime", FileTime.fromMillis(timestamp));
        
        Path tmp1 = Paths.get(rootPath + "/tmp1");
        if(!Files.exists(tmp1)) Files.createFile(tmp1);
        Files.setAttribute(tmp1, "lastModifiedTime", FileTime.fromMillis(timestamp-1));
        
        Path tmp2 = Paths.get(rootPath + "/tmp2");
        if(!Files.exists(tmp2)) Files.createFile(tmp2);
        Files.setAttribute(tmp2, "lastModifiedTime", FileTime.fromMillis(timestamp+1));

        String dirPath = rootPath + "/dir";
        Path dir = Paths.get(dirPath);
        Files.createDirectories(dir);
        Files.setAttribute(dir, "lastModifiedTime", FileTime.fromMillis(timestamp));

        Path dirtmp1 = Paths.get(dirPath + "/tmp1");
        if(!Files.exists(dirtmp1)) Files.createFile(dirtmp1);
        Files.setAttribute(dirtmp1, "lastModifiedTime", FileTime.fromMillis(timestamp+1));
        
        Path dirtmp2 = Paths.get(dirPath + "/tmp2");
        if(!Files.exists(dirtmp2)) Files.createFile(dirtmp2);
        Files.setAttribute(dirtmp2, "lastModifiedTime", FileTime.fromMillis(timestamp-1));

        List<Resource> resources = new ArrayList<Resource>();
        List<SimplePattern> patterns = new ArrayList<SimplePattern>();
        SimplePattern pattern = rp.new SimplePattern("*");
        patterns.add(pattern);
        
        File rootFile = root.toFile();
        rp.getMoreRecentThan(rootFile, timestamp, resources, patterns);

        assertEquals(2, resources.size());
        FileSystemResource rtmp1 = new FileSystemResource(tmp1.toFile());
        assertFalse(resources.contains(rtmp1));
        FileSystemResource rtmp2 = new FileSystemResource(tmp2.toFile());
        assertTrue(resources.contains(rtmp2));
        FileSystemResource rdirtmp1 = new FileSystemResource(dirtmp1.toFile());
        assertTrue(resources.contains(rdirtmp1));
        FileSystemResource rdirtmp2 = new FileSystemResource(dirtmp2.toFile());
        assertFalse(resources.contains(rdirtmp2));
        
        // clean-up
        Files.deleteIfExists(dirtmp1);
        Files.deleteIfExists(dirtmp2);
        Files.deleteIfExists(dir);
        Files.deleteIfExists(tmp1);
        Files.deleteIfExists(tmp2);
        Files.deleteIfExists(root);
    }

    @Test
    public void testReload() throws Exception {
        ReloadablePropertiesFactoryBean rp = new ReloadablePropertiesFactoryBean();

        final long timestamp = System.currentTimeMillis();

        String rootPath = "models-gen/src/generated/iris";
        Path root = Paths.get(rootPath);
        Files.createDirectories(root);
        
        Path tmp1 = Paths.get(rootPath + "/metadata-customer.xml");
        if(!Files.exists(tmp1)) Files.createFile(tmp1);
        
        Path tmp2 = Paths.get(rootPath + "/IRIS-customer.properties");
        if(!Files.exists(tmp2)) Files.createFile(tmp2);

        Resource[] resources = new Resource[2];
        Resource resource1 = mock(Resource.class);
        when(resource1.getURI()).thenReturn(tmp1.toUri());
        Resource resource2 = mock(Resource.class);
        when(resource2.getURI()).thenReturn(tmp2.toUri());
        resources[0] = resource1;
        resources[1] = resource2;

        ApplicationContext ctx = mock(ApplicationContext.class);
        when(ctx.getResources(any(String.class))).thenReturn(resources);

        rp.setApplicationContext(ctx);

        // set last modified time of the lastChange file to the maximum long
        // so that the class believes that new changes should be processed
        Path lastChange = Paths.get("models-gen/lastChange");
        if(Files.exists(lastChange)) Files.delete(lastChange);
        Files.createFile(lastChange);
        // add the files file to the lastChange file index
        byte[] tmp1FileNameBytes = (tmp1.toString() + System.getProperty("line.separator")).getBytes();
        Files.write(lastChange, tmp1FileNameBytes, StandardOpenOption.APPEND);
        byte[] tmp2FileNameBytes = tmp2.toString().getBytes();
        Files.write(lastChange, tmp2FileNameBytes, StandardOpenOption.APPEND);
        Files.setAttribute(lastChange, "lastModifiedTime", FileTime.fromMillis(timestamp+10000000));
        
        XmlModificationNotifier xmlNotifier = new XmlModificationNotifier();
        XmlModificationNotifier spy = Mockito.spy(xmlNotifier);
        Mockito.doNothing().when(spy).execute(any(FileEvent.class));
        rp.setXmlNotifier(spy);
        
        // this is the way to create an instance of the private variable reloadableProperties...
        rp.createInstance();
        
        rp.reload(true);
        
        verify(spy).execute(any(FileEvent.class));
        // TODO: we should verify that the properties are reloaded, but currently
        // there's no way to do this...
        
        rp.destroy();
        
        // clean-up
        Files.deleteIfExists(lastChange);
        Files.deleteIfExists(tmp1);
        Files.deleteIfExists(tmp2);
        Files.deleteIfExists(root);
    }

}
