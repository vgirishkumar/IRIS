/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC,
 * Canada. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: The Chisel Group, University of Victoria IBM CAS, IBM Toronto
 * Lab
 ******************************************************************************/
package com.temenos.interaction.rimdsl.visualisation.providers;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.zest.core.widgets.ZestStyles;

import com.temenos.interaction.rimdsl.rim.State;
import com.temenos.interaction.rimdsl.visualisation.VisualisationImageManager;

/**
 * Example of a label provider for a ZEST graph viewer
 * @author Simon Gerlach 
 */
public class ResourceInteractionLabelProvider implements ILabelProvider, IConnectionStyleProvider, IEntityStyleProvider {

	// The color used for the root node of the diagram
	private static final Color ROOT_NODE_FOREGROUND_COLOR = ColorConstants.blue;
	private static final Color ROOT_NODE_BACKGROUND_COLOR = ColorConstants.white;
	// The color used for all other node
	private static final Color NODE_FOREGROUND_COLOR = ColorConstants.black;
	private static final Color NODE_BACKGROUND_COLOR = ColorConstants.white;

	private static final Color RELATION_SELECTED_COLOR = ColorConstants.red;
	private static final Color RELATION_DEFAULT_COLOR = ColorConstants.gray;

	private GraphViewer viewer;
	
	/**
	 * Create a new Abstract Visualization Label Provider
	 * 
	 * @param viewer
	 */
	public ResourceInteractionLabelProvider(GraphViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public Image getImage(Object element) {
		// Get the 
		if (element instanceof State) {
			return VisualisationImageManager.get(VisualisationImageManager.IMG_RESOURCE);
		} else {
			return null;
		}
	}

	@Override
	public String getText(Object element) {
		
		if (element instanceof EntityConnectionData) { // = Relations
			EntityConnectionData connection = (EntityConnectionData) element;
			
			// Retrieve the original relationships model element from the content provider 
			IContentProvider contentProvider = viewer.getContentProvider();
			if (contentProvider instanceof ResourceInteractionContentProvider) {
				ResourceInteractionContentProvider bindingGraphContentProvider = (ResourceInteractionContentProvider)contentProvider;
				List<String> descriptions = bindingGraphContentProvider.getTransitionDescription(connection.source, connection.dest);
				
				// Append all transition titles
				StringBuilder sb = new StringBuilder();
				boolean first = true;
				for (String description : descriptions) {
					if (first) {
						first = false;
					} else {
						sb.append("\n");
					}
					sb.append(description);
				}
				return sb.toString();
			} else {
				throw new RuntimeException("The content provider need to be an instance of " + ResourceInteractionContentProvider.class.getCanonicalName());
			}
			
		} else if (element instanceof State) {// States
			State state = (State)element;
			return state.getName();
		} else {
			return null;
		}
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// Not required
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// Not required
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public Color getHighlightColor(Object rel) {
		return RELATION_SELECTED_COLOR;
	}

	@Override
	public Color getColor(Object rel) {
		return RELATION_DEFAULT_COLOR;
	}

	@Override
	public int getConnectionStyle(Object rel) {
		return ZestStyles.CONNECTIONS_DIRECTED;
	}

	@Override
	public int getLineWidth(Object rel) {
		return 1;
	}
	
	@Override
	public Color getNodeHighlightColor(Object entity) {
		return null;
	}

	@Override
	public Color getBorderColor(Object entity) {
		return null;
	}

	@Override
	public Color getBorderHighlightColor(Object entity) {
		return null;
	}

	@Override
	public int getBorderWidth(Object entity) {
		return 1;
	}

	@Override
	public Color getBackgroundColour(Object entity) {
		if ( entity == viewer.getInput() ) {
			return ROOT_NODE_BACKGROUND_COLOR;
		
		} else {
			return NODE_BACKGROUND_COLOR;
		}
	}

	@Override
	public Color getForegroundColour(Object entity) {
		if ( entity == viewer.getInput() ) {
			return ROOT_NODE_FOREGROUND_COLOR;
			
		} else {
			return NODE_FOREGROUND_COLOR;
		}
	}

	@Override
	public boolean fisheyeNode(Object entity) {
		return false;
	}
	
	@Override
	public IFigure getTooltip(Object entity) {
		return null;
	}

	@Override
	public void dispose() {
	}
}