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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.temenos.interaction.springdsl.properties.PropertiesChangedEvent;
import com.temenos.interaction.springdsl.properties.PropertiesLoadedEvent;
import com.temenos.interaction.springdsl.properties.PropertiesReloadedEvent;
import com.temenos.interaction.springdsl.properties.ReloadablePropertiesListener;

/**
 * TODO: Document me!
 *
 * @author aphethean
 *
 */
public class SpringDSLReloadablePropertiesListener implements ReloadablePropertiesListener, ApplicationContextAware, InitializingBean {
	private final Logger logger = LoggerFactory.getLogger(SpringDSLReloadablePropertiesListener.class);

    private ApplicationContext ctx;
	private SpringDSLResourceStateProvider resourceStateProvider;
	
	public SpringDSLReloadablePropertiesListener() {
	}
	
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

	public void setResourceStateProvider(SpringDSLResourceStateProvider resourceStateProvider) {
		this.resourceStateProvider = resourceStateProvider;
	}
	
	@Override
	public void propertiesLoaded(PropertiesLoadedEvent event) {		
		logger.debug("propertiesLoaded " + event.getOldProperties());
		

		if (resourceStateProvider == null)
			resourceStateProvider = ctx.getBean(SpringDSLResourceStateProvider.class);
		for (Object key : event.getOldProperties().keySet()) {
			String name = key.toString();
			
			resourceStateProvider.addState(name, event.getOldProperties());
		}
		
	}

	@Override
	public void propertiesChanged(PropertiesChangedEvent event) {
		logger.debug("propertiesChanged " + event.getNewProperties());
		if (resourceStateProvider == null)
			resourceStateProvider = ctx.getBean(SpringDSLResourceStateProvider.class);
		for (Object key : event.getNewProperties().keySet()) {
			String name = key.toString();
			resourceStateProvider.unload(name);
		}
	}

	@Override
	public void propertiesReloaded(PropertiesReloadedEvent event) {
		logger.debug("propertiesReloaded " + event.getOldProperties());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}
}
