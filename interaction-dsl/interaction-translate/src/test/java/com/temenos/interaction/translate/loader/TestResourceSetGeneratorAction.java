package com.temenos.interaction.translate.loader;

/*
 * #%L
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


import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.temenos.interaction.core.hypermedia.FileMappingResourceStateProvider;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.loader.FileEvent;
import com.temenos.interaction.translate.loader.ResourceSetGeneratorAction;


/**
 * TODO: Document me!
 *
 * @author dgroves
 * @author hmanchala
 *
 */
public class TestResourceSetGeneratorAction {

    private ResourceSetGeneratorAction resourceSetGeneratorAction;
    private @Mock FileMappingResourceStateProvider resourceStateProvider;
    private @Mock ResourceState alpha, beta, theta;
    private @Mock FileEvent<File> fileEvent;
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    	resourceSetGeneratorAction = new ResourceSetGeneratorAction();
        resourceSetGeneratorAction.setResourceStateProvider(resourceStateProvider);
    }
    
    @After
    public void tearDown(){
        
    }
    
    @Test
    public void testExecute() throws IOException {
        //given
        tempFolder.newFile("numbers.rim");
        when(fileEvent.getResource()).thenReturn(tempFolder.getRoot());
        //when
        this.resourceSetGeneratorAction.execute(fileEvent);
        //then
        verify(this.resourceStateProvider, times(1)).loadAndMapFileObjects(anyCollectionOf(File.class));
    }
    
    @Test
    public void testExecuteNoRimsFound() throws IOException{
        //given
        TemporaryFolder tempFolder = new TemporaryFolder();
        tempFolder.create();
        tempFolder.newFile("numbers.foobar");
        when(fileEvent.getResource()).thenReturn(tempFolder.getRoot());
        //when
        this.resourceSetGeneratorAction.execute(fileEvent);
        //then
        verify(this.resourceStateProvider, times(0)).loadAndMapFileObjects(anyCollectionOf(File.class));
    }
}
