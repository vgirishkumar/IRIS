package com.temenos.interaction.media.xhtml;

/*
 * #%L
 * interaction-media-xhtml
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
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
	public final static String TEMPLATE_HEADER_MINIMAL = "headerMinimal.html";
	public final static String TEMPLATE_FOOTER = "footer.html";
	public final static String TEMPLATE_RESOURCE_LINKS = "resourceLinks.html";
	public final static String TEMPLATE_ENTITIES = "entities.html";
	public final static String TEMPLATE_ENTITIES_MINIMAL = "entitiesMinimal.html";
	public final static String TEMPLATE_ENTITY = "entity.html";
	public final static String TEMPLATE_ENTITY_MINIMAL = "entityMinimal.html";
	private final static String TEMPLATE_PATH = "xhtml";

	public XHTMLTemplateFactories() {
		logger.trace("Loading template factories");
		try {
			TemplateLoader loader = new ClassPathTemplateLoader(XHTMLTemplateFactories.class.getClassLoader());
	
			//Add default template factories
			templateFactories.put(TEMPLATE_HEADER, loader.newTemplateFactory(TEMPLATE_PATH + "/" + TEMPLATE_HEADER));
			templateFactories.put(TEMPLATE_HEADER_MINIMAL, loader.newTemplateFactory(TEMPLATE_PATH + "/" + TEMPLATE_HEADER_MINIMAL));
			templateFactories.put(TEMPLATE_FOOTER, loader.newTemplateFactory(TEMPLATE_PATH + "/" + TEMPLATE_FOOTER));
			templateFactories.put(TEMPLATE_RESOURCE_LINKS, loader.newTemplateFactory(TEMPLATE_PATH + "/" + TEMPLATE_RESOURCE_LINKS));
			templateFactories.put(TEMPLATE_ENTITIES, loader.newTemplateFactory(TEMPLATE_PATH + "/" + TEMPLATE_ENTITIES));
			templateFactories.put(TEMPLATE_ENTITIES_MINIMAL, loader.newTemplateFactory(TEMPLATE_PATH + "/" + TEMPLATE_ENTITIES_MINIMAL));
			templateFactories.put(TEMPLATE_ENTITY, loader.newTemplateFactory(TEMPLATE_PATH + "/" + TEMPLATE_ENTITY));
			templateFactories.put(TEMPLATE_ENTITY_MINIMAL, loader.newTemplateFactory(TEMPLATE_PATH + "/" + TEMPLATE_ENTITY_MINIMAL));
		}
		catch(Exception e) {
			logger.error("Failed to load XHTML templates: ", e);
			throw new RuntimeException("Failed to load XHTML templates: ", e);
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
