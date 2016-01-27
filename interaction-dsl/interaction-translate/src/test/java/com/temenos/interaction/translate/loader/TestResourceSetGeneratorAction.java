package com.temenos.interaction.translate.loader;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
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
 * @author hmanchala
 *
 */
public class TestResourceSetGeneratorAction {

    private ResourceSetGeneratorAction resourceSetGeneratorAction;
    private @Mock FileMappingResourceStateProvider resourceStateProvider;
    private @Mock ResourceState alpha, beta, theta;
    private @Mock FileEvent<File> fileEvent;
    
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
        TemporaryFolder tempFolder = new TemporaryFolder();
        tempFolder.create();
        tempFolder.newFile("numbers.rim");
        when(fileEvent.getResource()).thenReturn(tempFolder.getRoot());
        //when
        this.resourceSetGeneratorAction.execute(fileEvent);
        //then
        verify(this.resourceStateProvider, times(1)).loadAndMapFiles(anyCollectionOf(String.class), eq(true));
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
        verify(this.resourceStateProvider, times(0)).loadAndMapFiles(anyCollectionOf(String.class), eq(true));
    }
}
