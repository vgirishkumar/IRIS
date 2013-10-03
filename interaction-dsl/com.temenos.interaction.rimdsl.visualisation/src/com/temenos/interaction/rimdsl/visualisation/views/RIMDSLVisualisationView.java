package com.temenos.interaction.rimdsl.visualisation.views;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.action.*;
import org.eclipse.ui.*;
import org.eclipse.xtext.nodemodel.impl.CompositeNode;
import org.eclipse.xtext.nodemodel.impl.LeafNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.swt.widgets.Menu;


/**
 * This class implements the Resource Interaction Model plug-in in a
 * workbench view.
 * @author aphethean
 */
public class RIMDSLVisualisationView extends ViewPart implements IZoomableWorkbenchPart, ISelectionListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.temenos.interaction.rimdsl.visualisation.views.RIMDSLVisualisationView";

	private FormToolkit toolKit = null;
	private ScrolledForm form = null;
	private GraphViewer viewer;
	private ZoomContributionViewItem contextZoomContributionViewItem;
	
	private TransitionForm visualizationForm;


	/**
	 * The constructor.
	 */
	public RIMDSLVisualisationView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		toolKit = new FormToolkit(parent.getDisplay());
		visualizationForm = new TransitionForm(parent, toolKit, this);
		viewer = visualizationForm.getGraphViewer();
		form = visualizationForm.getForm();
		
		hookContextMenu();
		fillToolBar();
		
		// Register selection service listener
        getSite().getWorkbenchWindow().getSelectionService().
            addPostSelectionListener(this);
	}

	/**
	 * Set the toolbar
	 */
	private void fillToolBar() {
		ZoomContributionViewItem toolbarZoomContributionViewItem = new ZoomContributionViewItem(this);
		IActionBars bars = getViewSite().getActionBars();
		bars.getMenuManager().add(toolbarZoomContributionViewItem);

		IToolBarManager toolBarManager = bars.getToolBarManager();
		toolBarManager.add(visualizationForm.showIncomingRelationsAction);
		toolBarManager.add(visualizationForm.showOutgoingRelationsAction);
	}


	/**
	 * Creates the context menu for this view.
	 */
	private void hookContextMenu() {
		contextZoomContributionViewItem = new ZoomContributionViewItem(this);

		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		fillContextMenu(menuMgr);

		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				RIMDSLVisualisationView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);

	}

	/**
	 * Add the items to the context menu
	 * 
	 * @param manager
	 */
	private void fillContextMenu(IMenuManager manager) {
		manager.add(visualizationForm.showIncomingRelationsAction);
		manager.add(visualizationForm.showOutgoingRelationsAction);
		manager.add(new Separator());
		manager.add(contextZoomContributionViewItem);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		form.setFocus();
	}

	public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
		// we ignore our own selections
		if (sourcepart != this) {
	    	if (!selection.isEmpty() && selection instanceof ITextSelection && sourcepart instanceof XtextEditor) {
	    		final ITextSelection textSel = (ITextSelection) selection;
	    		final XtextEditor editor = (XtextEditor)sourcepart;
	    		final IXtextDocument document = editor.getDocument();
	    		
	    		// determine the model element at the offset
	    		// Access to the underlying resource in the Xtext editor need to be encapsulated by an IUnitOfWork
	    		document.readOnly(new IUnitOfWork.Void<XtextResource>() {
    				public void process(XtextResource resource) throws Exception {
    					// parse the whole resource
    					IParseResult parseResult = resource.getParseResult();
    					if(parseResult == null)
    						return;
    					
    					// Get the root of the parsing result
    					CompositeNode rootNode = (CompositeNode) parseResult.getRootNode();
    					
    					// Get the parsing result around the current offset
    					int offset = textSel.getOffset();
    					LeafNode node = (LeafNode) NodeModelUtils.findLeafNodeAtOffset(rootNode, offset);
    					EObject object = NodeModelUtils.findActualSemanticObjectFor(node);
    					
    					visualizationForm.setInput(object);
    				}
	    		});
    		} else {
    			visualizationForm.setInput(null);
    		}
		}
	}

	public AbstractZoomableViewer getZoomableViewer() {
		return viewer;
	}

    public void dispose() {
		super.dispose();
        // Unregister selection service listener
        getSite().getWorkbenchWindow().getSelectionService().
            removePostSelectionListener(this);
    }	
}