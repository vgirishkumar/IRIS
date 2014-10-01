package com.temenos.interaction.loader.resource.action;

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


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.io.Resource;

import com.temenos.interaction.loader.FileEvent;

/**
 * TODO: Document me!
 *
 * @author mlambert
 *
 */
public class AbstractResourceModificationAction {
	private String resourcePatternStr;
	protected Pattern resourcePattern;	
	
	public final void setResourcePattern(String resourcePatternStr) {
		this.resourcePatternStr = resourcePatternStr;
		
		String tmp = resourcePatternStr;

		if(resourcePatternStr.indexOf(":") > -1) {
			// Ignore pattern prefixes like classpath*: as actual the file name in events will not contain them
			tmp = tmp.substring(resourcePatternStr.indexOf(":") + 1);
		}
		
		// Switch from Spring regex to Java regex
		tmp = tmp.replace("*", ".*");

		this.resourcePattern = Pattern.compile(tmp);
	}
	
	public String getResourcePattern() {
		return resourcePatternStr;
	}
	
	protected final boolean matches(FileEvent event) {
		Resource resource = event.getResource(); 
		String filename = resource.getFilename();

		boolean result = false;
		
		if(filename != null) {
			Matcher matcher = resourcePattern.matcher(filename);

			if(matcher.find()) {
				result = true;
			}
		}
		
		return result;
	}				
}
