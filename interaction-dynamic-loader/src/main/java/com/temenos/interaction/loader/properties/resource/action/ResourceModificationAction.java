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

import com.temenos.interaction.core.loader.Action;
import com.temenos.interaction.core.loader.PropertiesChangedEvent;
import com.temenos.interaction.core.loader.PropertiesEvent;
import com.temenos.interaction.core.loader.PropertiesEventVisitor;
import com.temenos.interaction.core.loader.PropertiesLoadedEvent;
import com.temenos.interaction.core.loader.PropertiesResourceModificationAction;
import com.temenos.interaction.loader.resource.action.AbstractResourceModificationAction;

public class ResourceModificationAction<Resource> extends AbstractResourceModificationAction implements PropertiesResourceModificationAction<Resource> {
	private Action<PropertiesChangedEvent<Resource>> changedAction;
	private Action<PropertiesLoadedEvent<Resource>> loadedAction;
	
	public void setChangedAction(Action<PropertiesChangedEvent<Resource>> changedAction) {
		this.changedAction = changedAction;
	}
	
	public void setLoadedAction(Action<PropertiesLoadedEvent<Resource>> loadedAction) {
		this.loadedAction = loadedAction;
	}
				
	/* (non-Javadoc)
	 * @see com.temenos.interaction.loader.properties.resource.action.PropertiesResourceModificationAction#notify(com.temenos.interaction.core.loader.PropertiesEvent)
	 */
	@Override
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
