package com.temenos.interaction.rimdsl.runtime.loader;

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

import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.loader.FileEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


/**
 * TODO: Document me!
 *
 * @author hmanchala
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TestResourceSetGeneratorAction {

    private ResourceSetGeneratorAction resourceSetGeneratorAction;
    
    @Mock
    private ResourceStateMachine resourceStateMachine;
    
    @Mock
    private TranslatorDrivenResourceStateProvider resourceStateProvider;
    
    private @Mock ResourceState alpha, beta, theta;
    
    @Mock
    private FileEvent<File> fileEvent;
    
    @Before
    public void setUp() throws IOException{
        TemporaryFolder tempFolder = new TemporaryFolder();
        tempFolder.create();
        tempFolder.newFile("Alpha.rim");
        tempFolder.newFile("Beta.rim");
        tempFolder.newFile("Theta.rim");
        when(resourceStateProvider.getResourceMethodsByState()).thenReturn(this.resourceNamesToMethodsStub());
        when(resourceStateProvider.getAllResourceStates(any(URI.class))).thenReturn(this.resourceStatesStub());
        when(fileEvent.getResource()).thenReturn(tempFolder.getRoot());
        when(alpha.getName()).thenReturn("Alpha");
        when(beta.getName()).thenReturn("Beta");
        when(theta.getName()).thenReturn("Theta");
        this.resourceSetGeneratorAction = new ResourceSetGeneratorAction();
        this.resourceSetGeneratorAction.setResourceStateMachine(resourceStateMachine);
        this.resourceSetGeneratorAction.setResourceStateProvider(resourceStateProvider);
    }
    
    @After
    public void tearDown(){
        
    }
    
    @Test
    public void testExecuteGetsAndRegistersResourceStatesFromRIMFilesWithRSM(){
        //given
        
        //when
        this.resourceSetGeneratorAction.execute(this.fileEvent);
        //then
        verify(this.resourceStateProvider, times(3)).getAllResourceStates(any(URI.class));
        verify(this.resourceStateProvider, times(3)).getResourceMethodsByState();
        verify(this.resourceStateMachine, times(5)).register(any(ResourceState.class), anyString());
    }
    
    private Map<String, Set<String>> resourceNamesToMethodsStub(){
        Map<String, Set<String>> resourceNamesToMethods = new HashMap<String, Set<String>>(){
            private static final long serialVersionUID = 1L;
        {
            this.put("Alpha", new HashSet<String>(Arrays.asList(new String[]{"GET"})));
            this.put("Beta", new HashSet<String>(Arrays.asList(new String[]{"GET", "POST"})));
            this.put("Theta", new HashSet<String>(Arrays.asList(new String[]{"PUT", "DELETE"})));
        }};
        return resourceNamesToMethods;
    }
    
    private List<ResourceState> resourceStatesStub(){
        return Arrays.asList(new ResourceState[]{
            alpha, beta, theta
        });
    }
}
