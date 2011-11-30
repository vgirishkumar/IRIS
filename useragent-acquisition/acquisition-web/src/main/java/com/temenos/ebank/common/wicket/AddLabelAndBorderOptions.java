/**
 * 
 */
package com.temenos.ebank.common.wicket;

import java.io.Serializable;

import org.apache.wicket.model.StringResourceModel;

/**
 * Class used for passing parameters to {@link WicketUtils} when creating labels and 
 * borders for fields. The setters use the fluent interface pattern, making it easy to chain them.
 * @author vionescu
 *
 */
public class AddLabelAndBorderOptions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private StringResourceModel stringResModel;
	private Boolean visibleLabel = true;
	private Boolean visibleHint = true;
	private Boolean visibleFieldInfo = true;
	private String labelWithDifferentResourceKey;
	private Boolean isContainedInComposite = false;
	private Boolean escapeModelStrings = true;
	
	public StringResourceModel getStringResModel() {
		return stringResModel;
	}
	
	/** 
	 * Ads an option to use a {@link StringResourceModel} instead of the default label derived from the field name.
	 * @param stringResModel
	 * @return
	 */
	public AddLabelAndBorderOptions setStringResModel(StringResourceModel stringResModel) {
		this.stringResModel = stringResModel;
		return this;
	}
	public Boolean getVisibleLabel() {
		return visibleLabel;
	}
	
	/**
	 * Sets the option whether the label should be visible or not
	 * @param visibleLabel
	 * @return
	 */
	public AddLabelAndBorderOptions setVisibleLabel(Boolean visibleLabel) {
		this.visibleLabel = visibleLabel;
		return this;
	}
	public Boolean getVisibleHint() {
		return visibleHint;
	}
	/**
	 * Sets the option whether the hint should be visible or not.
	 * The hint is the question mark image near the field, featuring a
	 * tooltip with hints.
	 * @param visibleHint
	 * @return
	 */
	public AddLabelAndBorderOptions setVisibleHint(Boolean visibleHint) {
		this.visibleHint = visibleHint;
		return this;
	}
	
	public Boolean getVisibleFieldInfo() {
		return visibleFieldInfo;
	}

	/**
	 * Sets the option whether the field info should be visible or not. The field info is
	 * the information on the right hand side of the field.
	 * @param visibleFieldInfo
	 */
	public AddLabelAndBorderOptions setVisibleFieldInfo(Boolean visibleFieldInfo) {
		this.visibleFieldInfo = visibleFieldInfo;
		return this;
	}
	
	
	public String getLabelWithDifferentResourceKey() {
		return labelWithDifferentResourceKey;
	}

	/**
	 * Sets the option of adding custom label text when displaying a label.
	 * For example we had a situation for the field "First Name" which used the label "First Name" for validation purposes, but
	 * we needed to display the label "Name" 
	 * @param customLabelText
	 * @return
	 */
	public AddLabelAndBorderOptions setLabelWithDifferentResourceKey(String customLabelText) {
		this.labelWithDifferentResourceKey = customLabelText;
		return this;
	}
	
	/**
	 * Checks whether the label uses custom text (option labelWithDifferentResourceKey)
	 * @return
	 */
	public boolean hasLabelWithDifferentResourceKey() {
		return labelWithDifferentResourceKey != null;
	}

	public AddLabelAndBorderOptions setIsContainedInComposite(Boolean isContainedInComposite) {
		this.isContainedInComposite = isContainedInComposite;
		return this;
	}

	public Boolean isContainedInComposite() {
		return isContainedInComposite;
	}

	public void setEscapeModelStrings(Boolean escapeModelStrings) {
		this.escapeModelStrings = escapeModelStrings;
	}

	public Boolean getEscapeModelStrings() {
		return escapeModelStrings;
	}
}
