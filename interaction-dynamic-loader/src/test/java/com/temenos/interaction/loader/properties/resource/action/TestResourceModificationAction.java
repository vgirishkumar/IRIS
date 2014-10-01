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


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.springframework.core.io.Resource;

import com.temenos.interaction.core.loader.action.Action;
import com.temenos.interaction.loader.properties.PropertiesChangedEvent;
import com.temenos.interaction.loader.properties.PropertiesEvent;
import com.temenos.interaction.loader.properties.PropertiesLoadedEvent;

public class TestResourceModificationAction {

	@Test
	public void testExplicitFilename() {
		String resourcePattern = "myfile.properties";
		
		Action action = mock(Action.class);		
		ResourceModificationAction rma = new ResourceModificationAction();
		rma.setResourcePattern(resourcePattern);
		rma.setChangedAction(action);
		
		Resource resource = mock(Resource.class);
		when(resource.getFilename()).thenReturn(resourcePattern);
		
		PropertiesChangedEvent event = new PropertiesChangedEvent(null, resource, null);
		
		rma.notify(event);
		
		verify(action).execute(event);
		
		Resource resource2 = mock(Resource.class);
		when(resource2.getFilename()).thenReturn("myfile2.properties");
		
		PropertiesChangedEvent event2 = new PropertiesChangedEvent(null, resource2, null);

		rma.notify(event2);
		
		verify(action, never()).execute(event2);
	}

	@Test
	public void testMatchesFilenamePattern() {
		String resourcePattern = "IRIS-*.properties";
		
		Action action = mock(Action.class);
		
		ResourceModificationAction rma = new ResourceModificationAction();
		rma.setResourcePattern(resourcePattern);
		rma.setChangedAction(action);
		
		Resource resource = mock(Resource.class);
		when(resource.getFilename()).thenReturn("IRIS-blah.properties");
		
		PropertiesChangedEvent event = new PropertiesChangedEvent(null, resource, null);
		
		rma.notify(event);
		
		verify(action).execute(event);
		
		Resource resource2 = mock(Resource.class);
		when(resource2.getFilename()).thenReturn("metadata-blah.properties");
		
		PropertiesChangedEvent event2 = new PropertiesChangedEvent(null, resource2, null);
				
		rma.notify(event2);
		
		verify(action, never()).execute(event2);
	}
	
	@Test
	public void testMatchesEventType() {
		String resourcePattern = "IRIS-*.properties";
		
		Action changedAction = mock(Action.class);
		Action loadedAction = mock(Action.class);
		
		ResourceModificationAction rma = new ResourceModificationAction();
		rma.setResourcePattern(resourcePattern);
		rma.setChangedAction(changedAction);
		rma.setLoadedAction(loadedAction);
		
		Resource resource = mock(Resource.class);
		when(resource.getFilename()).thenReturn("IRIS-blah.properties");				
		
		PropertiesEvent event = new PropertiesChangedEvent(null, resource, null);
				
		rma.notify(event);
		
		Resource resource2 = mock(Resource.class);
		when(resource2.getFilename()).thenReturn("IRIS-blah.properties");
		PropertiesEvent event2 = new PropertiesLoadedEvent(null, resource2, null);		
		
		rma.notify(event2);
		
		verify(changedAction, times(1)).execute(event);
		verify(loadedAction, times(1)).execute(event2);		
	}	
	
	@Test
	public void testNotify() {
		String resourcePattern = "IRIS-*.properties";
		
		Action changedAction = mock(Action.class);
		Action loadedAction = mock(Action.class);
		
		ResourceModificationAction rma = new ResourceModificationAction();
		rma.setResourcePattern(resourcePattern);
		rma.setChangedAction(changedAction);
		rma.setLoadedAction(loadedAction);
		
		Resource resource = mock(Resource.class);
		when(resource.getFilename()).thenReturn("IRIS-blah.properties");
		
		PropertiesEvent event = new PropertiesLoadedEvent(null, resource, null);
		
		rma.notify(event);
		
		verify(changedAction, never()).execute(event);		
		
		Resource resource2 = mock(Resource.class);
		when(resource2.getFilename()).thenReturn("metadata-blah.properties");
		
		PropertiesEvent event2 = new PropertiesLoadedEvent(null, resource2, null);
		
		rma.notify(event2);
		
		verify(changedAction, never()).execute(event2);
		verify(loadedAction, never()).execute(event2);
	}
}
