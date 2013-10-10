/*******************************************************************************
 * Copyright (c) 2009 EclipseSource Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource Corporation - initial API and implementation
 *******************************************************************************/
/*******************************************************************************
 * Modified work Copyright 2013 Temenos Holdings N.V.
 * The example code for XText visualisation has been modified to visualise 
 * the IRIS RIMDSL.
 ******************************************************************************/
package com.temenos.interaction.rimdsl.visualisation;

/*
 * #%L
 * com.temenos.interaction.rimdsl.RimDsl - Visualisation
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


import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * Use this manager to instantiate an image. 
 * It loads the corresponding resource from the plugin and 
 * releasing its resources at the end of the lifecycle.
 * 
 * @author Simon Gerlach
 */
public class VisualisationImageManager {

	/**
	 * Use this method to get an image instance.
	 * @param key The key the image is registered with in the registry 
	 * @return The image instance 
	 */
	public static Image get(String key) {
		if (PLUGIN_REGISTRY == null)
			initialize();
		return PLUGIN_REGISTRY.get(key);
	}

	/**
	 * The path to the images  
	 */
	private static final String PATH_OBJ = "icons/obj16/"; //$NON-NLS-1$

	/**
	 * The key of the image of resources
	 */
	public static final String IMG_RESOURCE = "resource32.png"; //$NON-NLS-1$ 
	public static final ImageDescriptor DESC_ENTITY = create(PATH_OBJ, IMG_RESOURCE);
	
	/**
	 * The key of the image representing incoming transitions
	 */
	public static final String IMG_SHOW_INCOMING_TRANSITIONS = "incoming_transitions.gif"; //$NON-NLS-1$
	public static final ImageDescriptor DESC_INCOMING_TRANSITIONS = create(PATH_OBJ, IMG_SHOW_INCOMING_TRANSITIONS);

	/**
	 * The key of the image representing outgoing transitions
	 */
	public static final String IMG_SHOW_OUTGOING_TRANSITIONS = "outgoing_transitions.gif"; //$NON-NLS-1$
	public static final ImageDescriptor DESC_OUTGOING_TRANSITIONS = create(PATH_OBJ, IMG_SHOW_OUTGOING_TRANSITIONS);

	/**
	 * The key of the view's icon 
	 */
	public static final String IMG_RIM_VISUALIZATION_VIEW_ICON = "rim_visualization_view_icon.png"; //$NON-NLS-1$
	public static final ImageDescriptor DESC_RIM_VISUALIZATION_VIEW_ICON = create(PATH_OBJ, IMG_RIM_VISUALIZATION_VIEW_ICON);
	
	
	/**
	 * The image registry to use for managing the image's lifecycle
	 */
	private static ImageRegistry PLUGIN_REGISTRY;
	
	/**
	 * Initializes the registry by registering all images. 
	 */
	private static final void initialize() {
		PLUGIN_REGISTRY = Activator.getDefault().getImageRegistry();
		
		manage(IMG_RESOURCE, DESC_ENTITY);
		manage(IMG_SHOW_INCOMING_TRANSITIONS, DESC_INCOMING_TRANSITIONS);
		manage(IMG_SHOW_OUTGOING_TRANSITIONS, DESC_OUTGOING_TRANSITIONS);
		manage(IMG_RIM_VISUALIZATION_VIEW_ICON, DESC_RIM_VISUALIZATION_VIEW_ICON);
	}

	/**
	 * Utility method to get a language specific image descriptor for an image
	 * @param prefix The prefix of the image URL, f.ex. the icons folder 
	 * @param name The name of the image
	 * @return The image descriptor
	 */
	private static ImageDescriptor create(String prefix, String name) {
		return ImageDescriptor.createFromURL(makeIconURL(prefix, name));
	}

	/**
	 * If there should be different langauge version of the plugin this 
	 * creates a language dependet URL of the corresponding ressources
	 * @param prefix The prefix of the image URL, f.ex. the icons folder 
	 * @param name The name of the image
	 * @return The complete URL of the image
	 */
	private static URL makeIconURL(String prefix, String name) {
		String path = "$nl$/" + prefix + name; //$NON-NLS-1$
		return FileLocator.find(Activator.getDefault().getBundle(), new Path(path), null);
	}

	/**
	 * Loads an image and registers it in the registry 
	 * @param key The registry key to use
	 * @param desc The image descriptor of the image to load and register
	 * @return The image instance
	 */
	private static Image manage(String key, ImageDescriptor desc) {
		Image image = desc.createImage();
		PLUGIN_REGISTRY.put(key, image);
		return image;
	}

}
