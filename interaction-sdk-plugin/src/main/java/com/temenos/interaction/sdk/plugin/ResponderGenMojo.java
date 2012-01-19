package com.temenos.interaction.sdk.plugin;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.temenos.interaction.sdk.JPAResponderGen;

/**
 * A Maven plugin that generates a responder from a given EDMX file.
 * @goal gen
 */
public class ResponderGenMojo extends AbstractMojo {

    /**
     * @parameter property="edmxFile"
     */
    private String edmxFileStr;

    /**
     * @parameter property="targetDirectory"
     */
    private String targetDirectory;

	public void setEdmxFile(String edmxFileStr) {
		this.edmxFileStr = edmxFileStr;
	}

	public void setTargetDirectory(String targetDirectory) {
		this.targetDirectory = targetDirectory;
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		// check our configuration
		if (edmxFileStr == null)
			throw new MojoExecutionException("[edmxFilePath] not specified in plugin configuration");
		if (targetDirectory == null)
			throw new MojoExecutionException("[targetDirectory] not specified in plugin configuration");
		
		File edmxFile = new File(edmxFileStr);
		File targetDir = new File(targetDirectory);
		if (!edmxFile.exists()) {
			getLog().error("EDMX file not found [" + edmxFileStr + "]");
			throw new MojoExecutionException("EDMX file not found");
		}
		if (!targetDir.exists()) {
			getLog().info("Target directory does not existing, creating it [" + targetDirectory + "]");
			targetDir.mkdirs();
		}
		if (!targetDir.isDirectory()) {
			getLog().error("Target directory is invalid [" + targetDirectory + "]");
			throw new MojoExecutionException("Target directory is invalid");
		}

		
		JPAResponderGen rg = new JPAResponderGen();
		boolean ok = rg.generateArtifacts(edmxFile, targetDir);
		if (!ok)
			throw new MojoFailureException("An unexpected error occurred while generating artifacts");
	}

}
