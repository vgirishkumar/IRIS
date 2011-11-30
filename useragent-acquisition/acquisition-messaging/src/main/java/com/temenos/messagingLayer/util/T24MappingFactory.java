package com.temenos.messagingLayer.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

/**
 * Factory used for retrieving mappings from their marshalled format. It looks for the XML files under its
 * <code>mappingDirectory</code> configured folder.
 * <p>
 * By setting the <code>enableCache</code> parameter to <code>true</code>, it is possible to cache in memory the calls
 * to {@link #getMapping(String, String)}. <code>false</code> means that the caching is disabled. By default it is
 * enabled.
 * 
 * @author acirlomanu
 * 
 */
public class T24MappingFactory implements MappingFactory {
	private static final Log logger = LogFactory.getLog(T24MappingFactory.class);
	private Resource mappingDirectory = null;
	/**
	 * In-memory 'cache' of the mapping definitions. Because the JAXB classes are not implementing Serializable, for now we cannot use EhCache for the same purpose.
	 */
	// TODO are the JAXB objects thread-safe? if not, we should either remove the cache, or cache only their String representation; how much gain will that be? (String in memory vs disk access)
	private Map<String, Object> mappings = new HashMap<String, Object>(5);
	private boolean enableCache = true; // default is 'use cache'

	/**
	 * Returns the unmarshalled XML file from the specified path. The path is relative to the
	 * <code>mappingDirectory</code>. Callers need to cast it to its actual type.
	 * 
	 * @param jaxbContextPath
	 *            pass-through parameter for the {@link javax.xml.bind.JAXBContext} instance.
	 * @param xmlPath
	 *            the XML file path
	 * @return unmarshalled XML object.
	 * @throws RuntimeException
	 *             if anything goes wrong during unmarshalling.
	 */
	public Object getMapping(String jaxbContextPath, String relativeMappingFilePath) {
		String cacheKey = null;
		if (enableCache) {
			cacheKey = getCacheKey(jaxbContextPath, relativeMappingFilePath);
			if (mappings.containsKey(cacheKey)) {
				return mappings.get(cacheKey);
			}
		}
		Object mapping = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(jaxbContextPath);
			Unmarshaller u = jc.createUnmarshaller();
			File transresp = mappingDirectory.createRelative(relativeMappingFilePath).getFile();
			mapping = u.unmarshal(transresp); // update the mapping variable so that we can add it to the cache
			return mapping;
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.debug(e.getMessage(), e);
			throw new RuntimeException("An exception has occured while unmarshalling " + relativeMappingFilePath, e);
		} finally {
			// if the cache is used, make sure we store the value for this key, to avoid future lookups on the disk.
			if (enableCache) {
				mappings.put(cacheKey, mapping);
			}
		}
	}

	private static String getCacheKey(String jaxbContextPath, String relativeMappingFilePath) {
		return jaxbContextPath + "###" + relativeMappingFilePath;
	}

	/* Spring setters */
	public void setMappingDirectory(Resource mappingDirectory) {
		try {
			if (!mappingDirectory.getFile().isDirectory()) {
				throw new IllegalArgumentException("This is not a valid directory location: " + mappingDirectory);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException("This is not a valid directory location: " + mappingDirectory);
		}
		this.mappingDirectory = mappingDirectory;
	}

	public void setEnableCache(boolean enableCache) {
		this.enableCache = enableCache;
	}
	/* end Spring setters */
}