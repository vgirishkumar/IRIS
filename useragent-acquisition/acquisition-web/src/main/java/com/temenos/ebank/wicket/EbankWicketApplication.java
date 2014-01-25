package com.temenos.ebank.wicket;

import java.util.Date;

import org.apache.wicket.Application;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycleProcessor;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.request.CryptedUrlWebRequestCodingStrategy;
import org.apache.wicket.protocol.http.request.WebRequestCodingStrategy;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.IRequestCycleProcessor;
import org.apache.wicket.request.target.coding.HybridUrlCodingStrategy;
import org.apache.wicket.settings.IApplicationSettings;
import org.apache.wicket.settings.IExceptionSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.convert.ConverterLocator;
import org.apache.wicket.util.time.Duration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.temenos.ebank.pages.Logout;
import com.temenos.ebank.pages.PingPage;
import com.temenos.ebank.pages.RefreshCachePage;
import com.temenos.ebank.pages.TestPage;
import com.temenos.ebank.pages.TestPageStandalone;
import com.temenos.ebank.pages.clientAquisition.resumeApplication.ResumeApplication;
import com.temenos.ebank.pages.clientAquisition.wizard.CAWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.FTDWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.IASAWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.IBSAWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.RASAWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.RSWizardPage;
import com.temenos.ebank.pages.crossSell.CSAccountOptionsCAPage;
import com.temenos.ebank.pages.crossSell.CSAccountOptionsFTDPage;
import com.temenos.ebank.pages.crossSell.CSAccountOptionsIBSAPage;
import com.temenos.ebank.pages.crossSell.CSAccountOptionsRASAPage;
import com.temenos.ebank.pages.crossSell.CSAccountOptionsRSPage;
import com.temenos.ebank.pages.startPage.ApplyForInternationalAccount;

@Component
public class EbankWicketApplication extends WebApplication implements ApplicationContextAware {
	private ApplicationContext applicationContext;
	
	/**
	 * Activates the lookup of resources in the database
	 */
	private boolean useDbStringResources = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.protocol.http.WebApplication#init()
	 */
	@Override
	protected void init() { 
		super.init();
		mountPage("/resume", ResumeApplication.class);
		mountPage("/apply", ApplyForInternationalAccount.class);
		mountPage("/applyCurrentAccount", CAWizardPage.class);
		mountPage("/applyFixedTermDeposit", FTDWizardPage.class);
		mountPage("/applyRegularSaver", RSWizardPage.class);
		mountPage("/applyIASA", IASAWizardPage.class);
		mountPage("/applyIBSA", IBSAWizardPage.class);
		mountPage("/applyRASA", RASAWizardPage.class);
		// mountWithEncoder("/step1", Step1.class);
		// mountWithEncoder("/step2", Step2.class);
		// mountWithEncoder("/step2Old", Step2Old.class);
		// mountWithEncoder("/step3", Step3.class);
		// mountWithEncoder("/step4", Step4.class);
		// mountWithEncoder("/step5", Step5.class);
		// mountWithEncoder("/step6", Step6.class);
		mountPage("/test", TestPage.class);
		mountPage("/testSA", TestPageStandalone.class);
		mountPage("/refreshCache", RefreshCachePage.class);
		mountPage("/logout", Logout.class );
		mountPage("/alive", PingPage.class );
		
		mountBookmarkablePage("/IASAcs", CSAccountOptionsRASAPage.class);
		mountBookmarkablePage("/IBSAcs", CSAccountOptionsIBSAPage.class);
		mountBookmarkablePage("/RASAcs", CSAccountOptionsRASAPage.class);
		mountBookmarkablePage("/FTDcs", CSAccountOptionsFTDPage.class);
		mountBookmarkablePage("/CAcs", CSAccountOptionsCAPage.class);
		mountBookmarkablePage("/RScs", CSAccountOptionsRSPage.class);

		addComponentInstantiationListener(new SpringComponentInjector(this, applicationContext, true));
		getRequestLoggerSettings().setRequestLoggerEnabled(true);
		if (DEPLOYMENT.equalsIgnoreCase(getConfigurationType())) {
			// in deployment mode do not record session size - this involves serialization and can be costly
			getRequestLoggerSettings().setRecordSessionSize(false);
		} else {
			// let's activate some debugging stuff
			getDebugSettings().setOutputComponentPath(true); // for selenium
			getDebugSettings().setDevelopmentUtilitiesEnabled(true); // for DebugBar
		}
		getMarkupSettings().setStripWicketTags(true);

		// when in deployment mode (as opposed to development), enable the resources(mark-up and properties)
		// to be reloaded if changed after a certain duration
		if (DEPLOYMENT.equalsIgnoreCase(getConfigurationType())) {
			getResourceSettings().setResourcePollFrequency(Duration.ONE_MINUTE);	
		} else {
			getResourceSettings().setResourcePollFrequency(Duration.ONE_SECOND);
		}
		getResourceSettings().setResourceStreamLocator(new EbankStreamLocator());
		
		if (useDbStringResources) {
			EbankPropertiesFactory propertiesFactory = new EbankPropertiesFactory(this);
			getResourceSettings().setPropertiesFactory(propertiesFactory);
			getResourceSettings().addStringResourceLoader(0, new EbankDBStringResourceLoader());
		}
		
		getResourceSettings().addResourceFolder("/WEB-INF/bundle/");
		getResourceSettings().addStringResourceLoader(0, new FlatClassStringResourceLoader(this.getClass()));
		getResourceSettings().addStringResourceLoader(0, new FlatBundleStringResourceLoader());
		//add to the end of the list
		getResourceSettings().addStringResourceLoader(new ContextDependentPropertiesResourceLoader());

		// DO NOT CHANGE this, the thrown exception is used in further logic processing
		getResourceSettings().setThrowExceptionOnMissingResource(true);
		// default resource loading, but without throwing MissingResourceException;
		// thus, the rest of the page will be rendered
		getResourceSettings().setLocalizer(new EbankLocalizer());

		getMarkupSettings().setDefaultMarkupEncoding("UTF-8");

		// if(DEPLOYMENT.equalsIgnoreCase(getConfigurationType())) {
		IApplicationSettings settings = getApplicationSettings();
		settings.setPageExpiredErrorPage(Logout.class);
		// setting the page for handling errors
		//settings.setInternalErrorPage(EbankInternalErrorPage.class);
		getExceptionSettings().setUnexpectedExceptionDisplay(IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE);
		// }
	}

	/**
	 * Default constructor, called by the framework
	 */
	public EbankWicketApplication() {
	}

	/**
	 * Returns the customized wicket application (this instance)
	 * 
	 * @return
	 */
	public static EbankWicketApplication get() {
		return (EbankWicketApplication) Application.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends Page> getHomePage() {

		// return Index.class;
		return ApplyForInternationalAccount.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.protocol.http.WebApplication#newSession(org.apache.wicket.Request,
	 * org.apache.wicket.Response)
	 */
	@Override
	public Session newSession(Request request, Response response) {
		return new EbankSession(request);
	}

	@Override
	protected IConverterLocator newConverterLocator() {
		ConverterLocator converterLocator = new ConverterLocator();
		converterLocator.set(Date.class, new PatternDateConverter("dd MMMM yyyy", false));
		return converterLocator;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;

	}

	/**
	 * Constructs and mounts the Ebank chosen encoder at the given path.
	 * Use this instead of {@link WebApplication#mountBookmarkablePage(String, Class)}.
	 * And remember: final is evil.
	 * <p>
	 * During development, it will continue to mount a standard bookmarkable page. 
	 * 
	 * @param mountPath
	 *            the path to mount the page class on
	 * @param pageClass
	 *            the page class to mount
	 */
	private void mountPage(final String mountPath, final Class<? extends Page> pageClass) {
		// to remove version numbers from the URL:
		// extend HybridUrlCodingStrategy and add an empty addPageInfo()
		// WARNING: it will kill the "Back" browser button

		if (DEVELOPMENT.equals(getConfigurationType())) {
			mountBookmarkablePage(mountPath, pageClass);
		} else {
			getRequestCycleProcessor().getRequestCodingStrategy().mount(
					new HybridUrlCodingStrategy(mountPath, pageClass));
		}
	}
	
	@Override
	protected IRequestCycleProcessor newRequestCycleProcessor() {
		// to beautify links in pages (using the encoder from mountPage):
		// use BookmarkableLink() instead of Link() in those specific pages

		if (DEVELOPMENT.equals(getConfigurationType())) {
			return super.newRequestCycleProcessor();
		} else {
			return new WebRequestCycleProcessor() {
				@Override
				protected IRequestCodingStrategy newRequestCodingStrategy() {
					return new CryptedUrlWebRequestCodingStrategy(new WebRequestCodingStrategy());
				}
			};
		}
	}
	
	@Override
	public final RequestCycle newRequestCycle(final Request request, final Response response) {
		if (DEVELOPMENT.equals(getConfigurationType())) {
			return new EbankRequestCycle (this, (WebRequest)request, (WebResponse)response);
		}
		else{
			return super.newRequestCycle(request, response);
		}
	}

	public void setUseDbStringResources(boolean useDbStringResources) {
		this.useDbStringResources = useDbStringResources;
	}

}