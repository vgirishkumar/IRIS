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


import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.temenos.interaction.loader.properties.PropertiesChangedEvent;
import com.temenos.interaction.loader.properties.PropertiesLoadedEvent;
import com.temenos.interaction.loader.properties.resource.action.ResourceModificationAction;
import com.temenos.interaction.loader.properties.resource.notification.PropertiesModificationListener;
import com.temenos.interaction.loader.properties.resource.notification.PropertiesModificationNotifier;

public class TestPropertiesModificationListener {

	@Test
	public void testGetPattern() {
		PropertiesModificationNotifier rmn = new PropertiesModificationNotifier();
		
		Map<String, ResourceModificationAction> map = new TreeMap<String, ResourceModificationAction>();
		ResourceModificationAction rma = mock(ResourceModificationAction.class);
		when(rma.getResourcePattern()).thenReturn("classpath*:IRIS-*.properties");		
		map.put("a", rma);
		
		ResourceModificationAction rma2 = mock(ResourceModificationAction.class);		
		map.put("b", rma2);
		when(rma2.getResourcePattern()).thenReturn("classpath*:MyFile.xml");		
		
		ApplicationContext ctx = mock(ApplicationContext.class);
		when(ctx.getBeansOfType(ResourceModificationAction.class)).thenReturn(map);
		rmn.setApplicationContext(ctx);
		
		PropertiesModificationListener listener = new PropertiesModificationListener();
		listener.setNotifier(rmn);
		
		List<String> patterns = Arrays.asList(listener.getResourcePatterns());
		
		assertTrue(patterns.contains("classpath*:IRIS-*.properties"));
		assertTrue(patterns.contains("classpath*:MyFile.xml"));
	}
	
	@Test
	public void testExecute() {
		PropertiesModificationListener listener = new PropertiesModificationListener();		
		PropertiesModificationNotifier notifier = mock(PropertiesModificationNotifier.class);
		listener.setNotifier(notifier);
		
		// Spoof app ctx initialization
		listener.onApplicationEvent(null);
		
		PropertiesChangedEvent changedEvent = mock(PropertiesChangedEvent.class);
		listener.propertiesChanged(changedEvent);
		
		PropertiesLoadedEvent loadedEvent = mock(PropertiesLoadedEvent.class);
		listener.propertiesChanged(loadedEvent);
						
		verify(notifier, times(1)).execute(loadedEvent);		
		verify(notifier, times(1)).execute(changedEvent);		
	}
}
