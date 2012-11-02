package com.temenos.interaction.core.media.xhtml;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cambridge.ClassPathTemplateLoader;
import cambridge.TemplateFactory;
import cambridge.TemplateLoader;

/**
 * This class provides template factories used to render data as XHTML. 
 */
public class XHTMLTemplateFactories {
	private final Logger logger = LoggerFactory.getLogger(XHTMLTemplateFactories.class);
	private Map<String, TemplateFactory> templateFactories = new HashMap<String, TemplateFactory>();

	//Default template factories
	public final static String TEMPLATE_HEADER = "header.html";
	public final static String TEMPLATE_FOOTER = "footer.html";
	public final static String TEMPLATE_RESOURCE_LINKS = "resourceLinks.html";
	public final static String TEMPLATE_ENTITIES = "entities.html";
	public final static String TEMPLATE_ENTITY = "entity.html";
	private final static String TEMPLATE_PATH = "xhtml";

	public XHTMLTemplateFactories() {
		logger.trace("Loading template factories");
		try {
			TemplateLoader loader = new ClassPathTemplateLoader(XHTMLTemplateFactories.class.getClassLoader());
	
			//Add default template factories
			templateFactories.put(TEMPLATE_HEADER, loader.newTemplateFactory(TEMPLATE_PATH + "/" + TEMPLATE_HEADER));
			templateFactories.put(TEMPLATE_FOOTER, loader.newTemplateFactory(TEMPLATE_PATH + "/" + TEMPLATE_FOOTER));
			templateFactories.put(TEMPLATE_RESOURCE_LINKS, loader.newTemplateFactory(TEMPLATE_PATH + "/" + TEMPLATE_RESOURCE_LINKS));
			templateFactories.put(TEMPLATE_ENTITIES, loader.newTemplateFactory(TEMPLATE_PATH + "/" + TEMPLATE_ENTITIES));
			templateFactories.put(TEMPLATE_ENTITY, loader.newTemplateFactory(TEMPLATE_PATH + "/" + TEMPLATE_ENTITY));
		}
		catch(Exception e) {
			logger.error("Failed to load XHTML templates: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Failed to load XHTML templates: " + e.getMessage());
		}
	}
	
	/**
	 * Returns the specified template factory
	 * @param name template factory name
	 * @return template factory
	 */
	public TemplateFactory getTemplateFactory(String name) {
		return templateFactories.get(name);	
	}
}
