package com.temenos.interaction.sdk.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.google.inject.Injector;
import com.temenos.interaction.rimdsl.RIMDslStandaloneSetupGenerated;
import com.temenos.interaction.rimdsl.generator.launcher.Generator;

import java.io.File;

/**
 * Goal which generates Java classes from a RIM.
 *
 * @goal rim-generate
 * @phase generate-sources
 * @requiresDependencyResolution compile
 */
public class RIMGeneratorMojo extends AbstractMojo {
    /**
     * Location of the RIM file.
     * @parameter
     * @required
     */
    private File rimSourceFile;

	/**
     * Location of the RIM file.
     * @parameter expression="${project.build.directory}/src-gen"
     */
    private File targetDirectory;
    
    /**
     * Skip the RIM generation.  Effectively disable this plugin.
     * @parameter
     */
    private boolean skipRIMGeneration;

    public void setRimSourceFile(File rimSourceFile) {
		this.rimSourceFile = rimSourceFile;
	}

	public void setTargetDirectory(File targetDirectory) {
		this.targetDirectory = targetDirectory;
	}

    public void setSkipRIMGeneration(String skipRIMGeneration) {
		this.skipRIMGeneration = (skipRIMGeneration != null && skipRIMGeneration.equalsIgnoreCase("true"));
	}

    public void execute() throws MojoExecutionException, MojoFailureException {
    	if (skipRIMGeneration) {
    		getLog().info("[skipRIMGeneration] set, not generating any source");
    	} else {
    		// check our configuration
    		if (rimSourceFile == null)
    			throw new MojoExecutionException("[rimSourceFile] not specified in plugin configuration");
    		if (targetDirectory == null) {
    			throw new MojoExecutionException("[targetDirectory] is set to null plugin configuration");
    		}

    		boolean ok = false;
    		if (!targetDirectory.exists()) {
    			getLog().info("Creating [targetDirectory] " + targetDirectory.toString());
    			targetDirectory.mkdirs();
    		}
    		Injector injector = new RIMDslStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
    		Generator generator = injector.getInstance(Generator.class);
    		ok = generator.runGenerator(rimSourceFile.toString(), targetDirectory.toString());

    		if (!ok)
    			throw new MojoFailureException("An unexpected error occurred while generating source");
    	}
    }
}
