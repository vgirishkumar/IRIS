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

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

/**
 * This class provides an abstraction from the underlying mechanism used to load config files  
 *
 */
public class ConfigLoader implements ServletContextAware  { 
	private ServletContext context;
	
	// Webapp context param defining the location of the unpacked IRIS configuration files
	public static final String IRIS_CONFIG_DIR_PARAM = "com.temenos.interaction.config.dir";
	
	private final static Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
		
	public boolean isExist(String filename) {
		if(getIrisConfigDirPath() == null) {
			return getClass().getClassLoader().getResource(filename) != null;
		} else {
			File file = formResourceFile(filename);
			return file != null && file.exists();
		}
	}

	public String getIrisConfigDirPath() {		
		return context == null ? null : context.getInitParameter(IRIS_CONFIG_DIR_PARAM);
	}
	
	public InputStream load(String filename) throws FileNotFoundException, Exception {
		InputStream is = null;
		
		if(getIrisConfigDirPath() == null) {
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
					logger.error("Unable to load " + filename + " from directory " + getIrisConfigDirPath() + " (specified by " 
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
		String irisResourceDirPath = getIrisConfigDirPath();
		File irisResourceDir = new File(irisResourceDirPath);
		if (irisResourceDir.exists() && irisResourceDir.isDirectory()) {
			return new File(irisResourceDir, filename);
		}
		return null;
	}

	@Override
	public void setServletContext(ServletContext context) {
		this.context = context;
	}
}
