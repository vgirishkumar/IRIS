/**
 * 
 */
package com.temenos.ebank.common.wicket.components;

import java.util.MissingResourceException;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * Info panel displayed on the rightside of an input field
 * @author vionescu
 *
 */
public class FieldInfoPanel extends Panel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Label infoLabel;
	private String infoId;

	/**
	 * Constructor.
	 * DO NOT CHANGE THE VISIBILITY OF THIS COMPONENT AFTER INSTANTIATION.
	 * 
	 * @param id
	 *            the id of this component
	 * @param infoId
	 *            the id of the corresponding property for the hint
	 * @param isVisible
	 *            suggested visibility of this component; if the resource is not found, the component will be invisible
	 */
	public FieldInfoPanel(String id, String infoId, Boolean isVisible) {
		super(id);
		infoLabel = new Label("infoText");
		add(infoLabel);
		this.infoId = infoId;
		setVisible(isVisible);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		try {
			String infoIdValue = getString(infoId);
			infoLabel.setDefaultModel(new Model<String>(infoIdValue));
		} catch (MissingResourceException e) {
			setVisible(false);
			// TODO find a way to prevent further visibility changes
		}
	}
}
