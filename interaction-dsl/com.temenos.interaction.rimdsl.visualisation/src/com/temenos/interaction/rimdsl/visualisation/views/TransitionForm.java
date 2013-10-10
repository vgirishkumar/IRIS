package com.temenos.interaction.rimdsl.visualisation.views;

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


import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.DirectedGraphLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;

import com.temenos.interaction.rimdsl.rim.State;
import com.temenos.interaction.rimdsl.visualisation.VisualisationImageManager;
import com.temenos.interaction.rimdsl.visualisation.providers.ResourceInteractionContentProvider;
import com.temenos.interaction.rimdsl.visualisation.providers.ResourceInteractionLabelProvider;


/**
 * This class encapsulates the process of creating the form view in the PDE
 * visualisation tool.
 * @author aphethean
 */
public class TransitionForm {

	// ****************************************************************************
	// DEFAULT VALUES
	// ****************************************************************************
	public static final boolean DEFAULT_SHOW_INCOMING_RELATIONS = true;
	public static final boolean DEFAULT_SHOW_OUTGOING_RELATIONS = true;
	// ****************************************************************************
	
	private static String Plugin_Binding_Analysis = "Relationship Analysis";



	/*
	 * Some parts of the form we may need access to
	 */
	private ScrolledForm form;
	private FormToolkit toolkit;
	private ManagedForm managedForm;
	private GraphViewer viewer;
	private SashForm sash;
	
	public Action showIncomingRelationsAction;
	public Action showOutgoingRelationsAction;

	private ResourceInteractionContentProvider contentProvider;

	
	private EObject currentInput;


	/**
	 * Called whenever the currently selected object has changed. 
	 * @param input The selected object
	 */
	protected void setInput(EObject input) {
		if (input == null) {
			// Haven't found anything useful
			form.setText("Select a Resource");
			
			currentInput = null;
			viewer.setInput(null);
		} else {
			// Has anything changed?
			if (currentInput != input || (currentInput != null && !currentInput.equals(input))) {
				if (input instanceof State) {
					State state = (State)input;
					form.setText(MessageFormat.format("Resource \"{0}\"", new Object[]{state.getName()}));
					
					currentInput = input;
					viewer.setInput(input);						
				} else {
				    setInput(input.eContainer());	
				}
			}
		}
	}

	// ******************************************************************************************

	

	/**
	 * Make the actions that can be called on this viewer. This currently
	 * includes: - Focus on ... - Focus on Selected Node - History action
	 */
	private void makeActions() {
		showIncomingRelationsAction = new Action(null, IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				setShowIncomingRelations(isChecked());
			}			
		};
		showIncomingRelationsAction.setText("Show Incoming Relations");
		showIncomingRelationsAction.setDescription("Show Incoming Relations");
		showIncomingRelationsAction.setToolTipText("Show Incoming Relations");
		showIncomingRelationsAction.setImageDescriptor(VisualisationImageManager.DESC_INCOMING_TRANSITIONS);
		showIncomingRelationsAction.setChecked(DEFAULT_SHOW_INCOMING_RELATIONS);
		
		
		showOutgoingRelationsAction = new Action(null, IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				setShowOutgoingRelations(isChecked());
			}			
		};
		showOutgoingRelationsAction.setText("Show Outgoing Relations");
		showOutgoingRelationsAction.setDescription("Show Outgoing Relations");
		showOutgoingRelationsAction.setToolTipText("Show Outgoing Relations");
		showOutgoingRelationsAction.setImageDescriptor(VisualisationImageManager.DESC_OUTGOING_TRANSITIONS);
		showOutgoingRelationsAction.setChecked(DEFAULT_SHOW_OUTGOING_RELATIONS);
	}
	

	// *****************************************************************************
	// ACTION HANDLING
	// *****************************************************************************
	
	public void setShowIncomingRelations(boolean enable) {
		contentProvider.setShowIncomingRelations(enable);
		viewer.refresh();
	}
	
	public void setShowOutgoingRelations(boolean enable) {
		contentProvider.setShowOutgoingRelations(enable);
		viewer.refresh();
	}
	
	/**
	 * Creates the form.
	 * 
	 * @param toolKit
	 * @return
	 */
	TransitionForm(Composite parent, FormToolkit toolkit, RIMDSLVisualisationView view) {
		makeActions();
		
		this.toolkit = toolkit;
		form = this.toolkit.createScrolledForm(parent);
		managedForm = new ManagedForm(this.toolkit, this.form);
		form.setText(Plugin_Binding_Analysis);
		form.setImage(VisualisationImageManager.get(VisualisationImageManager.IMG_RIM_VISUALIZATION_VIEW_ICON));
		FillLayout layout = new FillLayout();
		layout.marginHeight = 10;
		layout.marginWidth = 4;
		form.getBody().setLayout(layout);

		this.toolkit.decorateFormHeading(this.form.getForm());
		createSash(form.getBody());
		
		
		this.contentProvider = new ResourceInteractionContentProvider(
				TransitionForm.DEFAULT_SHOW_INCOMING_RELATIONS, 
				TransitionForm.DEFAULT_SHOW_OUTGOING_RELATIONS);
		viewer.setContentProvider(this.contentProvider);
		viewer.setLabelProvider(new ResourceInteractionLabelProvider(this.viewer));
		viewer.setInput(null);
		viewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		viewer.setLayoutAlgorithm(new CompositeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING, new LayoutAlgorithm[] { new DirectedGraphLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), new HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING) }));

		FontData fontData = Display.getCurrent().getSystemFont().getFontData()[0];
		fontData.height = 42;

		// Input can only be set after content provider was set
		setInput(null);
	}

	public void setFocusedNodeName(String nodeName) {
		form.setText(Plugin_Binding_Analysis + ": " + nodeName);
		form.reflow(true);
	}

	/**
	 * Creates the header region of the form, with the search dialog, background
	 * and title.  It also sets up the error reporting
	 * @param form
	 */


	String createFormTextContent(IMessage[] messages) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println("<form>");
		for (int i = 0; i < messages.length; i++) {
			IMessage message = messages[i];
			pw.print("<li vspace=\"false\" style=\"image\" indent=\"16\" value=\"");
			switch (message.getMessageType()) {
			case IMessageProvider.ERROR:
				pw.print("error");
				break;
			case IMessageProvider.WARNING:
				pw.print("warning");
				break;
			case IMessageProvider.INFORMATION:
				pw.print("info");
				break;
			}
			pw.print("\"> <a href=\"");
			pw.print(i + "");
			pw.print("\">");
			if (message.getPrefix() != null) {
				pw.print(message.getPrefix());
			}
			pw.print(message.getMessage());
			pw.println("</a></li>");
		}
		pw.println("</form>");
		pw.flush();
		return sw.toString();
	}

	/**
	 * Creates the sashform to separate the graph from the controls.
	 * 
	 * @param parent
	 */
	private void createSash(Composite parent) {
		sash = new SashForm(parent, SWT.NONE);
		this.toolkit.paintBordersFor(parent);

		Section section = this.toolkit.createSection(sash, Section.NO_TITLE);
		viewer = new MyGraphViewer(section, SWT.NONE);
		section.setClient(viewer.getControl());
		
		sash.setWeights(new int[] {1});
	}

	// Blocks automatic size change of the viewer
	private class MyGraphViewer extends GraphViewer {
		public MyGraphViewer(Composite parent, int style) {
			super(parent, style);
			Graph graph = new Graph(parent, style) {
				@Override
				public Point computeSize(int hint, int hint2, boolean changed) {
					return new Point(0, 0);
				}
			};
			setControl(graph);
		}
	}

	/**
	 * Gets the currentGraphViewern
	 * 
	 * @return
	 */
	public GraphViewer getGraphViewer() {
		return viewer;
	}

	/**
	 * Gets the form we created.
	 */
	public ScrolledForm getForm() {
		return form;
	}

	public ManagedForm getManagedForm() {
		return managedForm;
	}

}
