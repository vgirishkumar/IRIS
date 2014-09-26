package com.temenos.interaction.loader.resource.action;

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
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.temenos.interaction.loader.properties.PropertiesEvent;
import com.temenos.interaction.springdsl.SpringDSLResourceStateProvider;

public class IRISResourceChangedAction implements Action, ApplicationContextAware {
	private final Logger logger = LoggerFactory.getLogger(IRISResourceChangedAction.class);	
	private SpringDSLResourceStateProvider resourceStateProvider;
	
	private ApplicationContext ctx;

	@Override
	public void execute(PropertiesEvent event) {
			if (resourceStateProvider == null) {
				resourceStateProvider = ctx.getBean(SpringDSLResourceStateProvider.class);
			}
			
			logger.debug("properties changed: " + event.getNewProperties());
			
			for (Object key : event.getNewProperties().keySet()) {
				String name = key.toString();
				resourceStateProvider.unload(name);
			}			
		}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.ctx = ctx;	
	}		
}
