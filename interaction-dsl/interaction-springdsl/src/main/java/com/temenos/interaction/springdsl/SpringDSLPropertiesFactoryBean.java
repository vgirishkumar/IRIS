package com.temenos.interaction.springdsl;

/*
 * #%L
 * interaction-springdsl
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


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * This class enhances the Spring PropertiesFactoryBean class to provide support for loading IRIS configuration files 
 * from a directory defined as a system property.
 */
public class SpringDSLPropertiesFactoryBean extends PropertiesFactoryBean {
	// System property defining the location of the unpacked IRIS configuration files
	private static final String IRIS_CONFIG_DIR_PROP = "com.temenos.interaction.config.dir";
	
	private String filenamePattern;
	
	/**
	 * @param filenamePattern The file name pattern to use if properties files are being loaded from the file system
	 */
	public SpringDSLPropertiesFactoryBean(String filenamePattern) {
		this.filenamePattern = filenamePattern;
	}


	@Override
	public void setLocations(Resource[] locations) {
		List<Resource> tmpLocations = new ArrayList<Resource>();
		tmpLocations.addAll(Arrays.asList(locations));
		
		if(System.getProperty(IRIS_CONFIG_DIR_PROP) != null) {
			// Try and load the properties from the file system as a resource directory has been specified
			String irisResourceDirPath = System.getProperty(IRIS_CONFIG_DIR_PROP);
			File irisResourceDir = new File(irisResourceDirPath);
			
			if(irisResourceDir.exists() && irisResourceDir.isDirectory()) {
				File[] files = irisResourceDir.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.matches(filenamePattern);
					}
				});
				
				for(File file: files) {
					// Create a resource for a current file and add it to the collection of properties resources
					tmpLocations.add(new FileSystemResource(file));
				}
			}		
		}
		
		super.setLocations(tmpLocations.toArray(new Resource[0]));		
	}
}