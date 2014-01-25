package com.temenos.ebank.common.wicket.components;

import java.util.MissingResourceException;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.temenos.ebank.common.wicket.WicketUtils.SUFFIX;

/**
 * Panel containing additional field information (hints).
 * 
 * @author gcristescu
 */
public class HintPanel extends Panel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fieldId;
	private Component hintImage;
	
	/**
	 * Constructor.
	 * DO NOT CHANGE THE VISIBILITY OF THIS COMPONENT AFTER INSTANTIATION.
	 * 
	 * @param id
	 *            the id of this component
	 * @param hintId
	 *            the id of the corresponding property for the hint
	 * @param isVisible
	 *            suggested visibility of this component; if the resource is not found, the component will be invisible
	 */
	public HintPanel(String id, String fieldId, Boolean isVisible) {
		super(id);
		this.fieldId = fieldId;
		hintImage = new StaticImage("hintImage", "img/icon-information.gif");
		add(hintImage);			
		setVisible(isVisible);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		try {
			String imgAltMessageKey = String.format("%s" + SUFFIX.HINT_IMG_ALT, fieldId);
			String imgTitleMessageKey = String.format("%s" + SUFFIX.HINT_IMG_TITLE, fieldId);
			String imgAltMessageValue = getString(imgAltMessageKey);
			String imgTitleMessageValue = getString(imgTitleMessageKey);
			hintImage.add(new AttributeModifier("alt", true, new Model<String>(imgAltMessageValue)));
			hintImage.add(new AttributeModifier("title", true, new Model<String>(imgTitleMessageValue)));
			add(new HintPanelBehavior());
		} catch (MissingResourceException e) {
			setVisibilityAllowed(false);
			// TODO find a way to prevent further visibility changes; this is better than setVisible(), but still not
			// foolproof
		}
	}

}