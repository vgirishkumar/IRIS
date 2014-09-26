package com.temenos.interaction.springdsl;

/*
 * #%L
 * interaction-springdsl
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
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


import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ResourceMapFactory implements FactoryBean<Properties>, ApplicationContextAware {
	private ApplicationContext ctx;
	private Properties defaultProperties;
	private final Logger logger = LoggerFactory.getLogger(SpringDSLResourceStateProvider.class);	
		
	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.ctx = ctx;		
	}

	@Override
	public Properties getObject() throws Exception {
		Properties resourceMap = null;
		if(!ctx.getBeansOfType(DynamicProperties.class).isEmpty()) {
			resourceMap = ((PropertiesFactoryBean)ctx.getBean(DynamicProperties.class)).getObject();
			logger.debug("Using properties from DYNAMIC LOADER - Size: " + resourceMap.size());			
		} else {			
			resourceMap = defaultProperties;																
			logger.debug("Using properties from CLASSPATH - Size: " + resourceMap.size());			
		}
		
		return resourceMap;
	}
	
	public void setDefaultProperties(Properties defaultProperties) {
		this.defaultProperties = defaultProperties;
	}

	@Override
	public Class<?> getObjectType() {
		return Properties.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
		
}
