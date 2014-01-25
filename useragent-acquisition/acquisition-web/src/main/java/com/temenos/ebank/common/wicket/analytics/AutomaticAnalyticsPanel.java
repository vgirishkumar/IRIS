package com.temenos.ebank.common.wicket.analytics;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.template.PackagedTextTemplate;

import com.temenos.ebank.wicket.EbankSession;

/**
 * Panel containing Javascript for Web Analytics.
 * Contents are kept in a single TPL file.
 * Additional parameters can be defined in AutomaticSessionPersistent.properties, setting loading (startStep) and
 * unloading (endStep) points for each.
 * 
 * @author gcristescu
 */
@SuppressWarnings( { "unchecked" })
public class AutomaticAnalyticsPanel extends AnalyticsPanel implements IMarkupResourceStreamProvider {
	// FIXME is it costly in wicket to have a log instantiated for each page ?
	protected static Log logger = LogFactory.getLog(AutomaticAnalyticsPanel.class);
	/**
	 * Map containing parameters supplied by the application, to be used by Analytics services (such as product, step,
	 * ...).
	 */
	protected Map trackingParameters;
	/**
	 * Property map containing the list of session-persistent parameters, and their start and end step.
	 */
	protected Properties sessionProperties;

	/**
	 * @see AnalyticsPanel#AnalyticsPanel(String, Map)
	 */
	public AutomaticAnalyticsPanel(String id, Map parameters) {
		this(id, parameters, POSITION_DEFAULT);
	}

	/**
	 * @see AnalyticsPanel#AnalyticsPanel(String, Map, POSITIONS)
	 */
	public AutomaticAnalyticsPanel(String id, Map parameters, POSITIONS position) {
		super(id, parameters, position);

		this.trackingParameters = parameters;

		this.setEscapeModelStrings(false);

		sessionProperties = new Properties();
		try {
			// TODO: inject with spring
			sessionProperties.load(((WebApplication) getApplication()).getServletContext().getResourceAsStream(
					CONFIG_PATH + "automatic/AutomaticSessionPersistent.properties"));
		} catch (IOException e) {
			logger.warn(getString("analytics.error"));
		}
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		// treatment for any parameter declared as session-persistent
		Enumeration e = sessionProperties.keys();
		String baseKey, nameKey, name, startStep, endStep, sessionParameter, currentStep;
		currentStep = (String) trackingParameters.get("step");

		if (currentStep != null) {
			while (e.hasMoreElements()) {
				nameKey = (String) e.nextElement();
				if (nameKey.endsWith("name")) {
					baseKey = nameKey.substring(0, nameKey.length() - "step".length());

					name = sessionProperties.getProperty(nameKey);
					startStep = sessionProperties.getProperty((baseKey + "startStep"));
					endStep = sessionProperties.getProperty((baseKey + "endStep"));

					if (currentStep.startsWith(startStep)) {
						sessionParameter = this.getPage().getRequest().getParameter(name);
						((EbankSession) this.getSession()).putParameter(name, StringUtils
								.defaultString(sessionParameter));
						// TODO for the moment, we have decided not to use cookies, unless specifically requested
					}

					if (currentStep.startsWith(endStep)) {
						sessionParameter = (String) ((EbankSession) this.getSession()).getParameter(name);
						trackingParameters.put(name, sessionParameter);
					} else {
						trackingParameters.put(name, "");
					}
				}
			}
		}
	}

	public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
		PackagedTextTemplate jsTemplate = new PackagedTextTemplate(this.getClass(), "automatic/"
				+ this.getClass().getSimpleName() + product + "_" + step + ".tpl");
		jsTemplate.interpolate(trackingParameters);
		return jsTemplate;
	}
}