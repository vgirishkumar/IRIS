package com.temenos.interaction.rimdsl.generator.launcher;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.inject.Injector;
import com.temenos.interaction.rimdsl.RIMDslStandaloneSetup;

public class Main {

	public static void main(String[] args) {
		// handle command line options
		final Options options = new Options();
		OptionBuilder.withArgName("src");
		OptionBuilder.withDescription("Model source");
		OptionBuilder.hasArg();
		OptionBuilder.isRequired();
		OptionBuilder.withValueSeparator(' ');
		Option optSrc = OptionBuilder.create("src");
				
		OptionBuilder.withArgName("targetdir");
		OptionBuilder.withDescription("Generator target directory");
		OptionBuilder.hasArg();
		OptionBuilder.isRequired();
		OptionBuilder.withValueSeparator(' ');
		Option optTargetDir = OptionBuilder.create("targetdir");

		options.addOption(optSrc); options.addOption(optTargetDir);
		
		// create the command line parser
		final CommandLineParser parser = new GnuParser(); 
		CommandLine line = null;
		try {
			line = parser.parse(options, args);
		} catch (final ParseException exp) {
			System.err.println("Parsing arguments failed.  Reason: " + exp.getMessage()); 
			wrongCall(options); return;
		}
		
		// execute the generator
		Injector injector = new RIMDslStandaloneSetup().createInjectorAndDoEMFRegistration();
		Generator generator = injector.getInstance(Generator.class);
		File srcFile = new File(line.getOptionValue(optSrc.getArgName()));
		if (srcFile.exists()) {
			boolean result = false;
			if (srcFile.isDirectory()) {
				result = generator.runGeneratorDir(srcFile.getPath(), line.getOptionValue(optTargetDir.getArgName()));
			} else {
				result = generator.runGenerator(srcFile.getPath(), line.getOptionValue(optTargetDir.getArgName()));
			}
			System.out.println("Code generation finished ["+result+"]");
		} else {
			System.out.println("Src dir not found.");
		}

	}
	
	/**
	 * Print usage information and terminate the program.
	 * 
	 * @param options
	 */
	private static void wrongCall(final Options options) {
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java " + Launcher.class.getName() + " [OPTIONS]",
				options);
		System.exit(-1);
	}

}
