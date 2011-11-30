package com.temenos.ebank.pages;

import static com.temenos.ebank.common.wicket.WicketUtils.JQUERY_1_5;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Application;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.TextTemplateHeaderContributor;

import com.temenos.ebank.common.wicket.WicketUtils;
import com.temenos.ebank.common.wicket.analytics.AnalyticsPanel.POSITIONS;
import com.temenos.ebank.common.wicket.analytics.ManualAnalyticsPanel;
import com.temenos.ebank.common.wicket.analytics.MixedAnalyticsPanel;
import com.temenos.ebank.common.wicket.components.VersionPanel;
import com.temenos.ebank.domain.ConfigParamTable.BOOLEAN;
import com.temenos.ebank.domain.ConfigParamTable.INTEGER;
import com.temenos.ebank.services.interfaces.clientAquisition.IServiceConfigParam;
import com.temenos.ebank.wicket.EbankSession;

/**
 * @author vionescu
 * 
 */
@SuppressWarnings( { "rawtypes", "unchecked" })
public abstract class BasePage extends WebPage implements IAjaxIndicatorAware {
	// FIXME is it costly in wicket to have a log instantiated for each page ?
	protected static Log logger = LogFactory.getLog(BasePage.class);
	
	/**
	 * Map containing parameters passed to web analytics.
	 */
	private Map analyticsParameters;

	@SpringBean(name = "serviceConfigParam")
	private IServiceConfigParam serviceConfigParam;
	private WebMarkupContainer ajaxImage;

	public BasePage() {
		addLanguageInfo();

		add(CSSPackageResource.getHeaderContribution("css/default.css", "screen"));
		add(CSSPackageResource.getHeaderContribution("css/jquery.fancybox-1.3.4.css", "screen"));
		add(CSSPackageResource.getHeaderContribution("css/acquisition.css", "screen"));
		add(CSSPackageResource.getHeaderContribution("css/print.css", "print"));
		
		add(JavascriptPackageResource.getHeaderContribution(JQUERY_1_5));
		add(JavascriptPackageResource.getHeaderContribution("js/lib/jquery-ui.min.js"));

		// Realise js
		add(JavascriptPackageResource.getHeaderContribution("js/lib/jquery.tooltip.modified.js"));
		add(JavascriptPackageResource.getHeaderContribution("js/lib/jquery.dimensions.min.js"));
		add(JavascriptPackageResource.getHeaderContribution("js/lib/jquery.fancybox-1.3.4.pack.js"));
		add(JavascriptPackageResource.getHeaderContribution("js/lib/default.js"));
		// this is used for client side validation using Json in class attribute of HTML element
		add(JavascriptPackageResource.getHeaderContribution("js/lib/jquery.metadata.js"));

		addModalSessionScript();
		add(new WebMarkupContainer("analyticsPanelBegin"));
		add(new WebMarkupContainer("analyticsPanelEnd"));
		analyticsParameters = new HashMap();

		ajaxImage = new WebMarkupContainer("ajaxImage");
		ajaxImage.setOutputMarkupId(true);
		add(ajaxImage);
		boolean development = Application.DEVELOPMENT.equalsIgnoreCase(getApplication().getConfigurationType());
		add(development ? new VersionPanel("appVersion") : new Label("appVersion", ""));
		//add(development ? new DebugBar("debugBar") : new Label("debugBar", "")); // in theory, DebugBar is not visible unless debug utilities are enabled, but what would be the point of instantiating an object that is never used?
		add(new Label("debugBar", "")); 
	}
	
	/**
	 * indicates whether this page should have a session expire script added to it
	 */
	protected boolean supportsModalSessionScript() {
		return false;
	}
	/**
	 * WCAG 2.0: specify the language of the page.
	 * 
	 * Internationalization requires properly specifying the dir and lang HTML attributes.
	 * 
	 * @see http://www.w3.org/TR/2008/NOTE-WCAG20-TECHS-20081211/H57.html
	 * @see http://www.w3.org/TR/html4/struct/dirlang.html
	 */
	private void addLanguageInfo() {
		String language = getLocale().getLanguage();
		add(new WebMarkupContainer("html") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isTransparentResolver() {
				return true;
			};
		}.add(new SimpleAttributeModifier("lang", language)) // HTML
		 .add(new SimpleAttributeModifier("xml:lang", language))); // XHTML 1.0
		/*
		 * Internationalization requires properly specifying the dir and lang HTML attributes:
		 * http://www.w3.org/TR/html4/struct/dirlang.html
		 * 
		 * But this is not required by the client, so we left out the 'dir' attribute.
		 */
	}
	
	private void addModalSessionScript(){
		if (supportsModalSessionScript()) {
			WicketUtils.addJQueryAndMetadataLibs(this);
			WicketUtils.addJQueryUILib(this);
			add(JavascriptPackageResource.getHeaderContribution("js/lib/ebank.sessionTimeOut.js"));
	
			IModel variablesModel = new AbstractReadOnlyModel() {
				private static final long serialVersionUID = 1L;
	
				public Map getObject() {
					Map<String, CharSequence> variables = new HashMap<String, CharSequence>(7);
					Integer inactiveWarningTime = serviceConfigParam.getConfigParamTable().get(INTEGER.SESSION_INACTIVE_WARNING_TIME);
					Integer inactivityMaxTime = ((WebRequest) RequestCycle.get().getRequest()).getHttpServletRequest().getSession()
							.getMaxInactiveInterval()
							* 1000 - inactiveWarningTime;
					
				    variables.put("inactivityMillis", inactivityMaxTime.toString());
				    variables.put("noConfirmMillis", inactiveWarningTime.toString());
				    variables.put("alive_url", getRequestCycle().urlFor(PingPage.class, null));
				    variables.put("logout_url", getRequestCycle().urlFor(Logout.class, null));
					return variables;
				}
			};
			add(TextTemplateHeaderContributor.forJavaScript(BasePage.class, "ebank.sessionTimeOut.ready.js", variablesModel));
		}
	}

	/**
	 * Helper method for retrieving the propper session class
	 * 
	 * @return
	 */
	public EbankSession getEbankSession() {
		return ((EbankSession) getSession());
	}
	
	protected CharSequence getPageUrl() {
		return getRequestCycle().urlFor(this);
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();

		// retrieve config value
		Boolean analyticsEnabled = serviceConfigParam.getConfigParamTable().get(BOOLEAN.ANALYTICS_ENABLED);
		Boolean analyticsManual = serviceConfigParam.getConfigParamTable().get(BOOLEAN.ANALYTICS_MANUAL);

		// despite recommendations, this part actually needs to be after the
		// onBeforeRender cascade
		if (analyticsEnabled && !analyticsParameters.isEmpty()) {
			if (analyticsManual) {
				addOrReplace(new ManualAnalyticsPanel("analyticsPanelBegin", analyticsParameters, POSITIONS.BEGIN));
				addOrReplace(new ManualAnalyticsPanel("analyticsPanelEnd", analyticsParameters, POSITIONS.END));
			} else {
				addOrReplace(new MixedAnalyticsPanel("analyticsPanelBegin", analyticsParameters, POSITIONS.BEGIN));
				addOrReplace(new MixedAnalyticsPanel("analyticsPanelEnd", analyticsParameters, POSITIONS.END));
			}
		}
	}

	/**
	 * Add analytics parameters for the base page.
	 * 
	 * @param analyticsParameters
	 *            parameters to add
	 */
	public void addAnalyticsParameters(Map analyticsParameters) {
		this.analyticsParameters.putAll(analyticsParameters);
	}

	public String getAjaxIndicatorMarkupId() {
		return ajaxImage.getMarkupId();
	}
	
	
	
 /** 
  * Prevents browser caching - the pages should be dynamic. This proves to be useful whend doing ajax in the page,
  * see http://www.richardnichols.net/2010/03/apache-wicket-force-page-reload-to-fix-ajax-back/
 * @see org.apache.wicket.markup.html.WebPage#configureResponse()
 */
@Override
   protected void configureResponse() {
       super.configureResponse();
       WebResponse response = getWebRequestCycle().getWebResponse();
       response.setHeader("Cache-Control",
             "no-cache, max-age=0,must-revalidate, no-store");
       response.setHeader("Expires","-1");
       response.setHeader("Pragma","no-cache");
   }	
}