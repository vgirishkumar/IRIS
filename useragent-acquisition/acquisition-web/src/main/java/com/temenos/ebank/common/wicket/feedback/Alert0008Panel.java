package com.temenos.ebank.common.wicket.feedback;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.panel.Panel;

import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

/**
 * Alert panel for save ok
 * 
 * @author vionescu
 * 
 */
public class Alert0008Panel extends Panel {
	protected static Log logger = LogFactory.getLog(Alert0008Panel.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// @SuppressWarnings({ "rawtypes", "unchecked" })
	public Alert0008Panel(String id, Alert alert) {
		super(id);
		if (logger.isDebugEnabled()) {
			ApplicationWicketModelObject awm = (ApplicationWicketModelObject)alert.getPageModel().getObject();
			if (awm == null) {
				logger.debug("display wicket model: model is null!");	
			} else {
				logger.debug("display appRef: " + awm.getAppRef());
				if (awm.getCustomer() != null) {
					logger.debug("display customer email: " + awm.getCustomer().getEmailAddress());
				} else {
					logger.debug("display customer email: customer is null!");
				}
			}
		}
		setDefaultModel(alert.getPageModel());
	}

}
