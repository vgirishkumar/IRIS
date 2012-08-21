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
     * @parameter property="srcTargetDirectory"
     */
    private String srcTargetDirectory;

    /**
     * @parameter property="configTargetDirectory"
     */
    private String configTargetDirectory;

	public void setEdmxFile(String edmxFileStr) {
		this.edmxFileStr = edmxFileStr;
	}

	public void setSrcTargetDirectory(String targetDirectory) {
		this.srcTargetDirectory = targetDirectory;
	}

	public void setConfigTargetDirectory(String targetDirectory) {
		this.configTargetDirectory = targetDirectory;
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		// check our configuration
		if (edmxFileStr == null)
			throw new MojoExecutionException("[edmxFilePath] not specified in plugin configuration");
		if (srcTargetDirectory == null)
			throw new MojoExecutionException("[srcTargetDirectory] not specified in plugin configuration");
		if (configTargetDirectory == null) {
			getLog().warn("[configTargetDirectory] not set, using [srcTargetDirectory]");
			configTargetDirectory = srcTargetDirectory;
		}
		File edmxFile = new File(edmxFileStr);
		File srcTargetDir = new File(srcTargetDirectory);
		File configTargetDir = new File(configTargetDirectory);
		execute(edmxFile, srcTargetDir, configTargetDir);
	}
	
	protected void execute(File edmxFile, File srcTargetDir, File configTargetDir) throws MojoExecutionException, MojoFailureException {
		if (!edmxFile.exists()) {
			getLog().error("EDMX file not found [" + edmxFileStr + "]");
			throw new MojoExecutionException("EDMX file not found");
		}
		if (!srcTargetDir.exists()) {
			getLog().info("Source target directory does not exist, creating it [" + srcTargetDirectory + "]");
			srcTargetDir.mkdirs();
		}
		if (!srcTargetDir.isDirectory()) {
			getLog().error("Source target directory is invalid [" + srcTargetDirectory + "]");
			throw new MojoExecutionException("Source target directory is invalid");
		}
		if (!configTargetDir.exists()) {
			getLog().info("Configuration target directory does not exist, creating it [" + configTargetDir + "]");
			configTargetDir.mkdirs();
		}
		if (!configTargetDir.isDirectory()) {
			getLog().error("Configuration target directory is invalid [" + configTargetDir + "]");
			throw new MojoExecutionException("Configuration target directory is invalid");
		}

		
		JPAResponderGen rg = new JPAResponderGen();
		boolean ok = rg.generateArtifacts(edmxFile.getAbsolutePath(), srcTargetDir, configTargetDir);
		if (!ok)
			throw new MojoFailureException("An unexpected error occurred while generating artifacts");
	}

}
