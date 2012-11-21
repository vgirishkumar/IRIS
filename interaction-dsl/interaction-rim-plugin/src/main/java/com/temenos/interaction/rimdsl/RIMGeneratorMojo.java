package com.temenos.interaction.rimdsl;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.google.inject.Injector;
import com.temenos.interaction.rimdsl.generator.Generator;

import java.io.File;

/**
 * Goal which generates Java classes from a RIM.
 *
 * @goal generate
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
    
    public void setRimSourceFile(File rimSourceFile) {
		this.rimSourceFile = rimSourceFile;
	}


	public void setTargetDirectory(File targetDirectory) {
		this.targetDirectory = targetDirectory;
	}
    
    public void execute() throws MojoExecutionException, MojoFailureException {
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
