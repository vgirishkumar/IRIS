package com.temenos.interaction.translate.loader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.hypermedia.FileMappingResourceStateProvider;
import com.temenos.interaction.core.loader.Action;
import com.temenos.interaction.core.loader.FileEvent;

/**
 * Handle a file change event by loading and mapping created/updated RIM
 * files.
 *  
 * @author dgroves
 * @author hmanchala
 */
public class ResourceSetGeneratorAction implements Action<FileEvent<File>> {

	private static final boolean REGISTER_WITH_APACHE_WINK = true;
    private static final Logger logger = LoggerFactory.getLogger(ResourceSetGeneratorAction.class);
    
    private FileMappingResourceStateProvider resourceStateProvider;
    
    @Override
    public void execute(FileEvent<File> dirEvent) {
        logger.debug("File change or new files detected in {}", 
        		dirEvent.getResource().getAbsolutePath());
        Collection<File> rims = FileUtils.listFiles(
    		dirEvent.getResource(), new String[]{"rim"}, true
		);
        if(rims.isEmpty()){
        	logger.info("Couldn't find any RIM file changes; skipping registration.");
        	return;
        }
        Collection<String> rimFilenames = this.getFilenamesFromFiles(rims);
        this.resourceStateProvider.loadAndMapFiles(rimFilenames, REGISTER_WITH_APACHE_WINK);
    }
	
	private Collection<String> getFilenamesFromFiles(Collection<File> files){
		Collection<String> filenames = new ArrayList<String>();
		for(File file : files){
			filenames.add(file.getName());
		}
		return filenames;
	}
    
    public void setResourceStateProvider(FileMappingResourceStateProvider resourceStateProvider){
        this.resourceStateProvider = resourceStateProvider;
    }
}
