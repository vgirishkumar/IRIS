package com.temenos.interaction.loader.xml;

/*
 * #%L
 * interaction-dynamic-loader
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


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import com.temenos.interaction.loader.properties.ReconfigurableBean;
import com.temenos.interaction.loader.xml.resource.notification.XmlModificationNotifier;

public class ReloadableXmlListener implements ReconfigurableBean, ApplicationContextAware {
	private Map<Resource,Long> locations;	
	private XmlModificationNotifier notifier;
	private ApplicationContext ctx;	
	
	public void setNotifier(XmlModificationNotifier notifier) {
		this.notifier = notifier;
	}

	@Override
	public void reloadConfiguration() throws Exception {
		String[] patterns = notifier.getPatterns().toArray(new String[0]);
			
		List<Resource> tmpLocations = new ArrayList<Resource>();
				
		for(String pattern: patterns) {
			tmpLocations.addAll(Arrays.asList(ctx.getResources(pattern)));				
		}
		
		if(locations == null) {
			// Uninitialized - Load everything
			locations = new HashMap<Resource, Long>();
			
			for(Resource location : tmpLocations) {			
				// Add entry to locations			
				File file = location.getFile();
				locations.put(location, file.lastModified());
			}
		} else {
			// Process new and modified
			for(Resource location : tmpLocations) {
				
				if(locations.containsKey(location)) {
					// Existing location
					File file = location.getFile();
					long lastModified = file.lastModified();
					
					if( lastModified > locations.get(location)) {
						// Identified modification
						
						// Update entry in locations
						locations.put(location, lastModified);
						
						notifier.execute(new XmlChangedEventImpl(location));
					} else {
						locations.put(location, file.lastModified());
					}
				}
			}						
		}		
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.ctx = ctx;		
	}
}
