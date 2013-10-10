package com.temenos.interaction.sdk;

/*
 * #%L
 * interaction-sdk
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
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


import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class TestResponderGen {

	@Test
	public void testSourceGenerationFromEDMX() {
	    File srcTargetDir = new File("./target/integration-test/java");
	    srcTargetDir.mkdirs();
	    File configTargetDir = new File("./target/integration-test/resources");
	    configTargetDir.mkdirs();
	    
		assertEquals(6, countFiles(new File(srcTargetDir, "AirlineModel")));
		assertEquals(3, countFiles(new File(configTargetDir, "META-INF")));
	}
	
	private int countFiles(File dir) {
		int cnt = 0;
		File[] files = dir.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					cnt += countFiles(files[i]);
				} else {
					cnt++;
				}
			}
		}
		return cnt;
	}
}
