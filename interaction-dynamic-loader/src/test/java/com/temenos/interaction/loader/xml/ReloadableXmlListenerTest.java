package com.temenos.interaction.loader.xml;

/*
 * #%L
 * interaction-dynamic-loader
 * %%
 * Copyright (C) 2012 - 2015 Temenos Holdings N.V.
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


import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import com.temenos.interaction.loader.xml.resource.notification.XmlModificationNotifier;


public class ReloadableXmlListenerTest {

	/**
	 * Test method for {@link com.temenos.interaction.loader.xml.ReloadableXmlListener#setApplicationContext(org.springframework.context.ApplicationContext)}.
	 */
	@Test
	public void test() throws Exception {		
		XmlModificationNotifier notifier = mock(XmlModificationNotifier.class);
		Set<String> patterns = new HashSet<String>();
		patterns.add("*.xml");
		when(notifier.getPatterns()).thenReturn(patterns);
		
		ApplicationContext ctx = mock(ApplicationContext.class);
		Resource resource = mock(Resource.class);
		File file = mock(File.class);
		when(file.lastModified()).thenReturn(1L).thenReturn(2L);
		when(resource.getFile()).thenReturn(file);
		when(ctx.getResources("*.xml")).thenReturn(new Resource[]{resource});
		
		ReloadableXmlListener listener = new ReloadableXmlListener();
		listener.setApplicationContext(ctx);
		listener.setNotifier(notifier);
		
		// Simulate initialization - Last modified = 1L
		listener.reloadConfiguration();
		
		// Simulate update - Last modified = 2L
		listener.reloadConfiguration();
		
		verify(notifier).execute(any(XmlChangedEventImpl.class));
	}
}
