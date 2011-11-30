package com.temenos.ebank.wicket;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.Application;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.resource.Properties;
import org.apache.wicket.resource.PropertiesFactory;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.value.ValueMap;

import com.temenos.ebank.domain.TextResource;
import com.temenos.ebank.services.interfaces.resources.IServiceResourceLoader;

public class EbankPropertiesFactory extends PropertiesFactory {

	@SpringBean(name = "serviceResourceLoader")
	private IServiceResourceLoader serviceResourceLoader;

	public EbankPropertiesFactory(Application application) {
		super(application);
		InjectorHolder.getInjector().inject(this);
	}

	public Properties loadFromDB(String component, String style, String locale) {

		StringBuffer path = new StringBuffer(component);
		if (style != null) {
			path.append(style);
		}
		if (locale != null) {
			path.append("_").append(locale);
		}
		String strPath = path.toString();
		// Check the cache
		Properties properties = getCache().get(strPath);

		if (properties == null) {
			TextResource textResourceExample = new TextResource();
			textResourceExample.setParent(component);
			textResourceExample.setStyle(style);
			textResourceExample.setLocale(locale.toString());

			List<TextResource> textResources = serviceResourceLoader.getResources(textResourceExample);

			ValueMap strings = null;
			if (CollectionUtils.isNotEmpty(textResources)) {
				strings = new ValueMap();

				for (TextResource textResource : textResources) {
					strings.put(textResource.getKey(), textResource.getValue());
				}

				properties = new Properties(strPath, strings);
			}

			// Cache the lookup
			if (properties == null) {
				// Could not locate properties, store a placeholder
				getCache().put(strPath, Properties.EMPTY_PROPERTIES);
			} else {
				getCache().put(strPath, properties);
			}
		}

		if (properties == Properties.EMPTY_PROPERTIES) {
			// Translate empty properties placeholder to null prior to returning
			properties = null;
		}

		return properties;
	}
}
