package com.temenos.interaction.core.resource;

/*
 * #%L
 * interaction-core
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides an abstraction from the underlying mechanism used to load config files  
 *
 */
public class ConfigLoader { 
	private String irisConfigDirPath;
	
	// Webapp context param defining the location of the unpacked IRIS configuration files
	public static final String IRIS_CONFIG_DIR_PARAM = "com.temenos.interaction.config.dir";
	
	private final static Logger logger = LoggerFactory.getLogger(ConfigLoader.class);	
		
	/**
	 * Overrides the default IRIS configuration location with the path given
	 * 
	 * @param irisConfigDirPath The IRIS configuration location
	 */
	public void setIrisConfigDirPath(String irisConfigDirPath) {
		this.irisConfigDirPath = irisConfigDirPath;
	}	

	/**
	 * @return The path of the overridden IRIS configuration location
	 */
	public String getIrisConfigDirPath() {
		return irisConfigDirPath;
	}



	public boolean isExist(String filename) {
		if(irisConfigDirPath == null) {
			return getClass().getClassLoader().getResource(filename) != null;
		} else {
			File file = formResourceFile(filename);
			return file != null && file.exists();
		}
	}

	public InputStream load(String filename) throws FileNotFoundException, Exception {
		InputStream is = null;
		
		if(irisConfigDirPath == null) {
			is = getClass().getClassLoader().getResourceAsStream(filename);
			
			if(is == null) {
				logger.error("Unable to load " + filename + " from classpath.");
				throw new Exception("Unable to load " + filename + " from classpath.");
			}
		} else {
			File file = formResourceFile(filename);
			if (file != null) {
				if(file.exists()) {
					is = new FileInputStream(file);
				} else {
					logger.error("Unable to load " + filename + " from directory " + irisConfigDirPath + " (specified by " 
							+ IRIS_CONFIG_DIR_PARAM + "system property)");
					throw new Exception("Unable to load " + filename + " from file system.");
				}
			} else {
				throw new Exception("The IRIS resource config directory specified (" + IRIS_CONFIG_DIR_PARAM + ") does not exist.");
			}
		}
		
		return is;
	}
	
	private File formResourceFile(String filename) {
		File irisResourceDir = new File(irisConfigDirPath);
		if (irisResourceDir.exists() && irisResourceDir.isDirectory()) {
			return new File(irisResourceDir, filename);
		}
		return null;
	}
}
