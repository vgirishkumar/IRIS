package com.temenos.ebank.common.wicket.analytics;

import java.util.Map;

/**
 * Panel containing Javascript for Web Analytics.
 * Contents for each combination of parameters are kept in HTML's defined though Wicket's variation mechanism.
 * 
 * @author gcristescu
 */
public class ManualAnalyticsPanel extends AnalyticsPanel {
	/**
	 * @see AnalyticsPanel#AnalyticsPanel(String, Map)
	 */
	public ManualAnalyticsPanel(String id, Map parameters) {
		this(id, parameters, POSITION_DEFAULT);
	}

	/**
	 * @see AnalyticsPanel#AnalyticsPanel(String, Map, POSITIONS)
	 */
	public ManualAnalyticsPanel(String id, Map parameters, POSITIONS position) {
		super(id, parameters, position);
	}

	@Override
	public String getVariation() {
		return product + "_" + step;
	}
}