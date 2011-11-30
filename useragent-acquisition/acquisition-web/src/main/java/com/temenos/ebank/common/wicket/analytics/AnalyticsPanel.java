package com.temenos.ebank.common.wicket.analytics;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.panel.Panel;

import com.temenos.ebank.common.wicket.WicketUtils;

/**
 * Abstract Panel containing Javascript for Web Analytics. Use one of the implementations instead.
 * 
 * @author gcristescu
 */
public abstract class AnalyticsPanel extends Panel {
	/**
	 * Path (relative to web application) to configuration files related to web analytics.
	 */
	protected static final String CONFIG_PATH = "/WEB-INF/analytics/";

	protected String product;
	protected String step;

	public static enum POSITIONS {
		BEGIN, END
	}

	protected POSITIONS position;
	protected static final POSITIONS POSITION_DEFAULT = POSITIONS.END;

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            the id of this component
	 * @param parameters
	 *            map used to customize data collection
	 */
	public AnalyticsPanel(String id, Map parameters) {
		this(id, parameters, POSITION_DEFAULT);
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            the id of this component
	 * @param parameters
	 *            map used to customize data collection
	 * @param position
	 *            position inside &lt;body&gt; element
	 */
	public AnalyticsPanel(String id, Map parameters, POSITIONS position) {
		super(id);

		product = StringUtils.defaultString((String) parameters.get("product"));
		step = StringUtils.defaultString((String) parameters.get("step"));

		WicketUtils.addAnalyticsLib(this);

		this.position = position;
	}
}