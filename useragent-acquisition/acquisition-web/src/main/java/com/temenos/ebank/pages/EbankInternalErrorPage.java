package com.temenos.ebank.pages;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.markup.html.link.Link;


/**
 * Page for displaying unexpected error messages
 * @author vionescu
 *
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class EbankInternalErrorPage extends BasePage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EbankInternalErrorPage() {
		add(new Link("linkHomePage") {
			@Override
			public void onClick() {
				setResponsePage(getApplication().getHomePage());
			}
		});
	}
	
	/**
	 * @see org.apache.wicket.markup.html.WebPage#configureResponse()
	 */
	@Override
	protected void configureResponse()
	{
		super.configureResponse();
		getWebRequestCycle().getWebResponse().getHttpServletResponse().setStatus(
			HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * @see org.apache.wicket.Component#isVersioned()
	 */
	@Override
	public boolean isVersioned()
	{
		return false;
	}

	/**
	 * @see org.apache.wicket.Page#isErrorPage()
	 */
	@Override
	public boolean isErrorPage()
	{
		return true;
	}	
}
