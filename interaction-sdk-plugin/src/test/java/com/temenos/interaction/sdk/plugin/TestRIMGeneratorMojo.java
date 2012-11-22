package com.temenos.interaction.sdk.plugin;

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

}
