package com.temenos.ebank.wicket;

import org.apache.wicket.Page;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;

import com.temenos.ebank.pages.DevelopmentErrorPage;
import com.temenos.ebank.pages.Logout;

public class EbankRequestCycle extends WebRequestCycle  {

	public EbankRequestCycle(WebApplication application, WebRequest request, Response response) {
		super(application, request, response);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Page onRuntimeException(final Page cause, final RuntimeException e) {
		// obviously you can check the instanceof the exception and return the appropriate page if desired
		if (e instanceof PageExpiredException) {
			return new Logout();
		}
		return new DevelopmentErrorPage(e);
	}
	
}
