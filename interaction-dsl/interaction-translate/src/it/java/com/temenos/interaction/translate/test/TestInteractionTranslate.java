package com.temenos.interaction.translate.test;

/*
 * #%L
 * interaction-translate
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
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


import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.wink.common.internal.registry.ProvidersRegistry;
import org.apache.wink.server.internal.registry.ResourceRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.loader.detector.DirectoryChangeActionNotifier;
import com.temenos.interaction.translate.loader.RIMResourceStateProvider;
import com.temenos.interaction.translate.loader.ResourceSetGeneratorAction;
import com.temenos.interaction.winkext.RegistrarWithSingletons;

/**
 * 
 * @author dgroves
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = {"classpath:spring-translate-config.xml"})
public class TestInteractionTranslate extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	@Resource(name = "updateAfterChangeRIMAction")
	private ResourceSetGeneratorAction resourceSetGeneratorAction;
	
	@Autowired
	@Resource(name = "resourceStateProvider")
	private RIMResourceStateProvider resourceStateProvider;
	
	@Autowired
	@Resource(name = "winkRegistrar")
	private RegistrarWithSingletons registrarWithSingletons;
		
	@Autowired
	@Resource(name = "rimDirWatchdog")
	private DirectoryChangeActionNotifier notifier;
					
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Before
	public void setUp() throws Exception {
		registrarWithSingletons.register(mock(ResourceRegistry.class), mock(ProvidersRegistry.class));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(timeout = 20000L)
	public void testResourceIsCreatedDuringInitialisation(){
		ResourceState resourceState = resourceStateProvider.getResourceState("test-ServiceDocument");
		assertThat(resourceState, notNullValue());
		assertThat(resourceState.getPath(), is(equalTo("/")));
		assertThat(resourceState.getId(), is(equalTo("ServiceDocument.ServiceDocument")));
	}
	
	@Test(timeout = 20000L)
	public void testCreateUpdateResource() throws IOException, URISyntaxException, InterruptedException {
		ResourceState originalResourceState = resourceStateProvider.getResourceState("greetings-Universe");
		assertThat(originalResourceState, is(nullValue()));
		
		File src = new File("Universe.rim");
		File dest = folder.getRoot();
		this.notifier.setResources(Arrays.asList(new File[]{dest}));
		FileUtils.copyFileToDirectory(src, dest);
		invokeResourceStateProviderViaDirectoryListener();
		ResourceState resourceState = resourceStateProvider.getResourceState("greetings-Universe");
		assertThat(resourceState, notNullValue());
		assertThat(resourceState.getId(), is(equalTo("Greeting.Universe")));
		assertThat(resourceState.getName(), is(equalTo("Universe")));
		assertThat(resourceState.getPath(), is(equalTo("/universe")));
	}
	
	private void invokeResourceStateProviderViaDirectoryListener() throws InterruptedException{
		synchronized(resourceSetGeneratorAction){
			if(!this.resourceSetGeneratorAction.isAvailable()){
				while(!resourceSetGeneratorAction.isAvailable()){
					resourceSetGeneratorAction.wait();
				}
			}
		}
	}
}
