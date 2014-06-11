package com.temenos.interaction.rimdsl.generator.launcher;

/*
 * #%L
 * com.temenos.interaction.rimdsl.RimDsl - Generator
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



import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.generator.JavaIoFileSystemAccess;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;

import com.google.inject.Inject;

/**
 * This class generates code from a DSL model.
 * @author aphethean
 *
 */
public class Generator {
	
	@Inject
    private XtextResourceSet resourceSet;
	@Inject
	private IResourceValidator validator;
	@Inject
	private IGenerator generator;
	@Inject
	private JavaIoFileSystemAccess fileAccess;
	
	private ValidatorEventListener listener = new ValidatorEventListener() {		
		public void notify(String msg) {
			System.err.println(msg);
		}
	};

	public boolean runGeneratorDir(String inputDirPath, String outputPath) {
		List<String> files = getFiles(inputDirPath, ".rim");
		for (String modelPath : files) {
			resourceSet.getResources().add(resourceSet.getResource(URI.createFileURI(modelPath), true));
		}
		boolean result = true;
		for (String modelPath : files) {
			boolean fileResult = runGenerator(modelPath, outputPath);
			if (!fileResult) {
				result = fileResult;
			}
		}
		return result;
	}
	
	protected String toSystemFileName(String fileName) {
		return fileName.replace("/", File.separator);
	}

	/**
	 * @param path a folder path
	 * @param extension a file extension
	 * @return a list of files contained in the specified folder and
	 * 		its sub folders filtered by extension
	 */
	protected ArrayList<String> getFiles(String path, String extension) {
		ArrayList<String> result = new ArrayList<String>();
		
		path = toSystemFileName(path);
		getFilesRecursively(path, result, extension);
		
		return result;
	}

	private void getFilesRecursively(String path, ArrayList<String> result, String extension) {
		File file = new File(path);
		if (file.isDirectory()) {
			String[] contents = file.list();
			for (String sub : contents) {
				getFilesRecursively(path + File.separator + sub, result, extension);
			}
		} else {
			if (file.getName().endsWith(extension))
				result.add(file.getAbsolutePath());
		}
	}
	
	public boolean runGenerator(String inputPath, String outputPath) {
		
		// load the resource
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		Resource resource = resourceSet.getResource(URI.createFileURI(inputPath), true);

		// validate the resource
		List<Issue> list = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
		if (!list.isEmpty()) {
			for (Issue issue : list) {								
				listener.notify(issue.toString());
			}
			
			return false;
		}

		// configure and start the generator
		fileAccess.setOutputPath(outputPath);
		generator.doGenerate(resource, fileAccess);
		
		return true;
	}
	
	public void setValidatorEventListener(ValidatorEventListener listener) {
		if(listener != null) {
			this.listener = listener;
		}
	}	
}