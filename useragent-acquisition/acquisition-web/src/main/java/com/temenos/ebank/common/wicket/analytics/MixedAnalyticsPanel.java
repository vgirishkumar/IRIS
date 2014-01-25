package com.temenos.ebank.common.wicket.analytics;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.string.interpolator.MapVariableInterpolator;
import org.apache.wicket.util.template.PackagedTextTemplate;
import org.springframework.web.util.UriUtils;

/**
 * Panel containing Javascript for Web Analytics.
 * Works like {@link AutomaticAnalyticsPanel}.
 * Additionally, paths to sets of custom parameters can be defined in AutomaticAnalyticsPanel.properties.
 * 
 * @author gcristescu
 */
@SuppressWarnings( { "unchecked" })
public class MixedAnalyticsPanel extends AutomaticAnalyticsPanel {
	protected Properties metaProperties;
	protected Properties inlineProperties;
	protected Properties tagProperties;

	/**
	 * @see AnalyticsPanel#AnalyticsPanel(String, Map)
	 */
	public MixedAnalyticsPanel(String id, Map parameters) {
		this(id, parameters, POSITION_DEFAULT);
	}

	/**
	 * @see AnalyticsPanel#AnalyticsPanel(String, Map, POSITIONS)
	 */
	public MixedAnalyticsPanel(String id, Map parameters, POSITIONS position) {
		super(id, parameters, position);

		// recover the paths to sets of custom parameters
		Properties pathProperties = new Properties();

		try {
			InputStream is = ((WebApplication) getApplication()).getServletContext().getResourceAsStream(
					CONFIG_PATH + "mixed/MixedAnalyticsPath.properties");
			if (is != null) {
				pathProperties.load(is);
			}
		} catch (IOException e) {
			logger.warn(getString("analytics.error"));
		}

		// select the set of custom parameters
		metaProperties = loadProperties(pathProperties, "analytics.meta.filename.", parameters);
		inlineProperties = loadProperties(pathProperties, "analytics.inline.filename.", parameters);
		trackingParameters.putAll(inlineProperties);

		addTagContainers(pathProperties, "analytics.tag.");
	}

	/**
	 * Extract sets of properties from the global map into specific maps
	 * 
	 * @param pathProperties
	 *            global properties
	 * @param baseKey
	 *            base properties key
	 * @param parameters
	 *            map used for interpolation of additional parameters
	 * @return specific properties list
	 */
	private Properties loadProperties(Properties pathProperties, String baseKey, Map parameters) {
		Properties miscProperties = new Properties();
		int i = 1;
		String path = "";

		while (pathProperties.containsKey(baseKey + i)) {
			path = pathProperties.getProperty(baseKey + i++);
			try {
				InputStream is = ((WebApplication) getApplication()).getServletContext().getResourceAsStream(
						CONFIG_PATH + "mixed/" + MapVariableInterpolator.interpolate(path, parameters));
				if (is != null) {
					miscProperties.load(is);
				}
			} catch (IOException e) {
				logger.warn(getString("analytics.error"));
			}
		}
		return miscProperties;
	}

	/**
	 * Add tag containers to analytics panel
	 * 
	 * @param pathProperties
	 *            contains flags for each tag (<code>&lt;tag_name&gt;.enabled</code>)
	 * @param baseKey
	 *            base properties key for flags
	 */
	private void addTagContainers(Properties pathProperties, String baseKey) {
		String key;
		POSITIONS tagPosition;
		String tagName;
		WebMarkupContainer cn;
		Iterator it = pathProperties.keySet().iterator();

		while (it.hasNext()) {
			key = (String) it.next();
			if (key.startsWith(baseKey)) {
				tagPosition = POSITIONS.valueOf(pathProperties.getProperty(key).toUpperCase());
				tagName = StringUtils.substringAfterLast(key, ".");

				cn = new WebMarkupContainer(tagName);
				cn.setVisible("yes".equalsIgnoreCase(inlineProperties.getProperty(tagName + ".enabled"))
						&& position.equals(tagPosition));
				add(cn);
			}
		}
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		if ("yes".equalsIgnoreCase(inlineProperties.getProperty("wbt.enabled"))) {
			// load custom parameters from previously selected set
			Enumeration e = metaProperties.keys();
			// will contain a list of <meta> tags
			StringBuffer sbMeta = new StringBuffer("\n");
			// will contain a list of URL parameters
			StringBuffer sbURLParam = new StringBuffer("");
			String key, value;

			while (e.hasMoreElements()) {
				key = (String) e.nextElement();
				value = metaProperties.getProperty(key);
				sbMeta.append(createMeta(key, value));
				sbURLParam.append(createURLParameter(key, value));
			}

			add(new Label("analyticsMeta", MapVariableInterpolator.interpolate(sbMeta.toString(), trackingParameters))
					.setEscapeModelStrings(false));
			try {
				trackingParameters.put("wbt.noscript", StringEscapeUtils.escapeHtml(UriUtils.encodeFragment(
						MapVariableInterpolator.interpolate(sbURLParam.toString(), trackingParameters), "UTF-8")));
			} catch (UnsupportedEncodingException e1) {
				logger.warn(getString("analytics.error"));
			}
		} else {
			add(new Label("analyticsMeta", "").setVisible(false));
		}
	}

	/**
	 * Create a meta HTML tag, containing the given key and value.
	 * <p>
	 * This method will be called often. KISS.
	 * 
	 * @return tag
	 */
	private StringBuffer createMeta(String key, String value) {
		StringBuffer sb = new StringBuffer();
		sb.append("<meta name=\"");
		sb.append(key);
		sb.append("\" content=\"");
		sb.append(value);
		sb.append("\"/>\n");
		return sb;
	}

	/**
	 * Create an URL parameter, containing the given key and value.
	 * <p>
	 * This method will be called often. KISS.
	 * 
	 * @return tag
	 */
	private StringBuffer createURLParameter(String key, String value) {
		StringBuffer sb = new StringBuffer();
		sb.append("&");
		sb.append(key);
		sb.append("=");
		sb.append(value);
		return sb;
	}

	public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
		PackagedTextTemplate jsTemplate = new PackagedTextTemplate(this.getClass(), ""
				+ this.getClass().getSimpleName() + ".tpl");
		jsTemplate.interpolate(trackingParameters);
		return jsTemplate;
	}
}