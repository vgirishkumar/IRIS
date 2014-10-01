package com.temenos.interaction.loader.properties.resource.action;

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

import com.temenos.interaction.core.loader.Action;
import com.temenos.interaction.core.loader.PropertiesEvent;
import com.temenos.interaction.springdsl.SpringDSLResourceStateProvider;

/**
 * This class performs the necessary updates to load a new IRIS in memory resource from an underlying resource
 *
 * @author mlambert
 *
 */
public class IRISResourceLoadedAction implements Action<PropertiesEvent> {
	private final Logger logger = LoggerFactory.getLogger(IRISResourceLoadedAction.class);	
	
	private SpringDSLResourceStateProvider resourceStateProvider;
		
	/**
	 * @param resourceStateProvider the resourceStateProvider to set
	 */
	public void setResourceStateProvider(SpringDSLResourceStateProvider resourceStateProvider) {
		this.resourceStateProvider = resourceStateProvider;
	}

	@Override
	public void execute(PropertiesEvent event) {			
		logger.debug("Properties loaded: " + event.getNewProperties());

		for (Object key : event.getNewProperties().keySet()) {
			String name = key.toString();

			resourceStateProvider.addState(name, event.getNewProperties());
		}
	}
}
