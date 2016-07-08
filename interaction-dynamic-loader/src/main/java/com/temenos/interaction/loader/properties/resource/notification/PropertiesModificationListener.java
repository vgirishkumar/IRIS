package com.temenos.interaction.loader.properties.resource.notification;

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


import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;

import com.temenos.interaction.core.loader.PropertiesEvent;
import com.temenos.interaction.loader.properties.ReloadablePropertiesListener;

public class PropertiesModificationListener implements ReloadablePropertiesListener<Resource>, ApplicationListener<ContextRefreshedEvent> {
	private boolean applicationInitialized = false;	
	private PropertiesModificationNotifier notifier;
	
	public void setNotifier(PropertiesModificationNotifier notifier) {
		this.notifier = notifier;
	}

	@Override
	public String[] getResourcePatterns() {				
		return notifier.getPatterns().toArray(new String[0]);
	}

	@Override
	public void propertiesChanged(PropertiesEvent<Resource> event) {
		if(applicationInitialized) {
			notifier.execute(event);
		}
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		applicationInitialized = true;		
	}	
}
