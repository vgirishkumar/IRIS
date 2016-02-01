package com.temenos.interaction.loader.properties.resource.action;

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


import com.temenos.interaction.core.loader.PropertiesEvent;
import com.temenos.interaction.springdsl.EagerSpringDSLResourceStateProvider;
import com.temenos.interaction.springdsl.SpringDSLResourceStateProvider;
import org.junit.Test;

import java.util.Properties;

import static org.mockito.Mockito.*;


/**
 * TODO: Document me!
 *
 * @author mlambert
 *
 */
public class TestIRISResourceLoadedAction {

	@Test
	public void test() {
		IRISResourceLoadedAction action = new IRISResourceLoadedAction();
		
		SpringDSLResourceStateProvider resourceStateProvider = mock(SpringDSLResourceStateProvider.class);
		action.setResourceStateProvider(resourceStateProvider);
		
		Properties props = new Properties();
		props.put("test", "n/a");
		PropertiesEvent event = mock(PropertiesEvent.class);
		when(event.getNewProperties()).thenReturn(props);
		
		action.execute(event);
		
		verify(resourceStateProvider).addState("test", props);
	}

	@Test
	public void testForEagerProvider() {
		IRISResourceLoadedAction action = new IRISResourceLoadedAction();

		SpringDSLResourceStateProvider resourceStateProvider = mock(EagerSpringDSLResourceStateProvider.class);
		action.setResourceStateProvider(resourceStateProvider);

		Properties props = new Properties();
		props.put("test", "n/a");
		PropertiesEvent event = mock(PropertiesEvent.class);
		when(event.getNewProperties()).thenReturn(props);

		action.execute(event);

		verify(resourceStateProvider).addState("test", props);
	}

}
