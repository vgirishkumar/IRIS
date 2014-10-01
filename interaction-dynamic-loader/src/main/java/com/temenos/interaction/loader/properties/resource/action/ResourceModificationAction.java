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

import com.temenos.interaction.core.loader.action.Action;
import com.temenos.interaction.loader.FileEvent;
import com.temenos.interaction.loader.properties.PropertiesChangedEvent;
import com.temenos.interaction.loader.properties.PropertiesEvent;
import com.temenos.interaction.loader.properties.PropertiesEventVisitor;
import com.temenos.interaction.loader.properties.PropertiesLoadedEvent;
import com.temenos.interaction.loader.resource.action.AbstractResourceModificationAction;

public class ResourceModificationAction extends AbstractResourceModificationAction {
	private Action<FileEvent> changedAction;
	private Action<FileEvent> loadedAction;
	
	public void setChangedAction(Action<FileEvent> changedAction) {
		this.changedAction = changedAction;
	}
	
	public void setLoadedAction(Action<FileEvent> loadedAction) {
		this.loadedAction = loadedAction;
	}
				
	public void notify(PropertiesEvent event ) {
		event.accept(new PropertiesEventVisitor() {
			
			@Override
			public void visit(PropertiesChangedEvent event) {
				if(changedAction != null) {
					if(matches(event)) {
						changedAction.execute(event);
					}
				}
			}
						
			@Override
			public void visit(PropertiesLoadedEvent event) {
				if(loadedAction != null) {
					if(matches(event)) {
						loadedAction.execute(event);
					}
				}								
			}
		});								
	}
}
