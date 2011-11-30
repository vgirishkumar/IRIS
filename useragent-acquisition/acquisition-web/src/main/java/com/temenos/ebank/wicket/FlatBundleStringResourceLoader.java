package com.temenos.ebank.wicket;

import java.util.Locale;

import org.apache.wicket.resource.IPropertiesFactory;
import org.apache.wicket.resource.Properties;
import org.apache.wicket.resource.loader.ComponentStringResourceLoader;
import org.apache.wicket.util.resource.locator.ResourceNameIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author raduf
 *         ComponentStringResourceLoader customized for loading resources from WEB-INF
 *         in a flat file structure as opposed to package structure in which Wicket's
 *         properties bundles live.
 * 
 */
public class FlatBundleStringResourceLoader extends ComponentStringResourceLoader {
	/** Log. */
	private static final Logger LOG = LoggerFactory.getLogger(ComponentStringResourceLoader.class);

	/**
	 * 
	 * @see org.apache.wicket.resource.loader.IStringResourceLoader#loadStringResource(java.lang.Class,
	 *      java.lang.String, java.util.Locale, java.lang.String)
	 */
	public String loadStringResource(Class<?> clazz, final String key, final Locale locale, final String style) {
		if (clazz == null) {
			return null;
		}

		String className = stripPathFromClassName(clazz);

		if (LOG.isDebugEnabled()) {
			LOG.debug("key: '" + key + "'; class: '" + className + "'; locale: '" + locale + "'; Style: '" + style
					+ "'");
		}

		// Load the properties associated with the path
		IPropertiesFactory propertiesFactory = getPropertiesFactory();
		while (true) {
			// Create the base path
			String path = stripPathFromClassName(clazz);

			// Iterator over all the combinations
			ResourceNameIterator iter = new ResourceNameIterator(path, style, locale, null);
			while (iter.hasNext()) {
				String newPath = iter.next();

				final Properties props = propertiesFactory.load(clazz, newPath);
				if (props != null) {
					// Lookup the value
					String value = props.getString(key);
					if (value != null) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("Found property '" + key + "' in: '" + props + "'" + "; value: '" + value + "'");
						}

						return value;
					} else {
						if (LOG.isDebugEnabled()) {
							LOG.debug("Found properties file: '" + newPath + "' but it doesn't contain the property");
						}
					}
				} 
			}

			// Didn't find the key yet, continue searching if possible
			if (isStopResourceSearch(clazz)) {
				break;
			}

			// Move to the next superclass
			clazz = clazz.getSuperclass();

			if (clazz == null) {
				// nothing more to search, done
				break;
			}
		}

		// not found
		return null;
	}

	private String stripPathFromClassName(Class<?> clazz) {
		String className = clazz.getName();
		int idx = className.lastIndexOf('.');

		if (idx != -1) {
			className = className.substring(idx + 1, className.length());
		}
		return className;
	}

}
