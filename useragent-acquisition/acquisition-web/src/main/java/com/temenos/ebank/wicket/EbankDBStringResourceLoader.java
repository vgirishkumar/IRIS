package com.temenos.ebank.wicket;

import java.util.Locale;

import org.apache.wicket.resource.Properties;
import org.apache.wicket.resource.loader.ComponentStringResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author raduf
 *         ComponentStringResourceLoader customized for loading resources from DB
 *         It works with EbankPropertiesFactory which shares Wicket's cache with the
 *         base PropertyFactory, but loads content from DB, instead of using file loaders.
 * 
 */
public class EbankDBStringResourceLoader extends ComponentStringResourceLoader {
	/** Log. */
	private static final Logger LOG = LoggerFactory.getLogger(ComponentStringResourceLoader.class);

	@Override
	public String loadStringResource(Class<?> clazz, final String key, final Locale locale, final String style) {

		String className = clazz.getName();
		int idx = className.lastIndexOf('.');

		if (idx != -1) {
			className = className.substring(idx + 1, className.length());
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("key: '" + key + "'; class: '" + className + "'; locale: '" + locale + "'; Style: '" + style
					+ "'");
		}

		EbankPropertiesFactory propertiesFactory = (EbankPropertiesFactory) getPropertiesFactory();
		Properties props = propertiesFactory.loadFromDB(className, style, locale.toString());

		if (props != null) {
			// Lookup the value
			String value = props.getString(key);
			if (value != null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Found property '" + key + "' in DB: " + "; value: '" + value + "'");
				}

				return value;
			} else {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Found properties for class: '" + className
							+ "' in DB, but it doesn't contain the property");
				}
			}
		}

		// not found
		return null;
	}

}
