/**
 * 
 */
package com.temenos.ebank.common.wicket.summary;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * @author vionescu
 *
 */
public abstract class SummaryLabel extends Label {

	public SummaryLabel(String id, IModel<?> model) {
		super(id, model);
	}
	
	public SummaryLabel(final String id)
	{
		super(id);
	}	

	@Override
	protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
		replaceComponentTagBody(markupStream, openTag, getDefaultModelObjectAsString(getSummaryLabelModelObject()));
	}

	protected abstract Object getSummaryLabelModelObject();
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
