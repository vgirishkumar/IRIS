package com.temenos.interaction.sdk.plugin;

/*
 * #%L
 * interaction-sdk-rim-plugin Maven Mojo
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


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

public class TestRIMGeneratorMojo {

	@Test (expected = MojoExecutionException.class)
	public void testNullRimSourceFile() throws MojoExecutionException, MojoFailureException {
		RIMGeneratorMojo mojo = new RIMGeneratorMojo();
		mojo.setRimSourceFile(null);
		mojo.execute();
	}

	@Test
	public void testSkipRIMGeneration() throws MojoExecutionException, MojoFailureException {
		RIMGeneratorMojo mojo = new RIMGeneratorMojo();
		mojo.setSkipRIMGeneration("true");
		// will throw error if generation is not skipping properly
		mojo.execute();
	}

}
