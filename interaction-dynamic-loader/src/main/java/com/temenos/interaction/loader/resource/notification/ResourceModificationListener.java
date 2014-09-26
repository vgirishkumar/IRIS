package com.temenos.interaction.loader.resource.notification;

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


import java.util.Iterator;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.temenos.interaction.loader.properties.PropertiesEvent;
import com.temenos.interaction.loader.properties.ReloadablePropertiesListener;

public class ResourceModificationListener implements ReloadablePropertiesListener, ApplicationListener<ContextRefreshedEvent> {
	private boolean applicationInitialized = false;	
	private ResourceModificationNotifier notifier;
	
	public void setNotifier(ResourceModificationNotifier notifier) {
		this.notifier = notifier;
	}

	@Override
	public String getResourcePattern() {
		StringBuilder builder = new StringBuilder();
		
		Iterator<String> patterns = notifier.getPatterns().iterator();
		
		while(patterns.hasNext()) {
			String pattern = patterns.next();
			
			builder.append(pattern);
			
			if(patterns.hasNext()) {
				builder.append(", ");
			}
		}
		
		return builder.toString();
	}

	@Override
	public void propertiesChanged(PropertiesEvent event) {
		if(applicationInitialized) {
			notifier.execute(event);
		}
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		applicationInitialized = true;		
	}	
}
