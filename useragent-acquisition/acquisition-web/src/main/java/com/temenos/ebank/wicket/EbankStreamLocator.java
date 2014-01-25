package com.temenos.ebank.wicket;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.UrlResourceStream;
import org.apache.wicket.util.resource.locator.ResourceStreamLocator;

import com.temenos.ebank.common.wicket.analytics.AnalyticsPanel;

public class EbankStreamLocator extends ResourceStreamLocator {

	@Override
	public IResourceStream locate(Class<?> clazz, String path) {
		URL url;
		try {
			// try to load the resource from the web context
			if (AnalyticsPanel.class.isAssignableFrom(clazz)) {
				// special case: analytics files have their own location
				// TODO extract this as external parameter
				// TODO see if this custom locator can be moved to the specific Panel
				url = WebApplication.get().getServletContext()
						.getResource("/WEB-INF/analytics/" + path.substring(path.lastIndexOf('/') + 1));
			} else {
				url = WebApplication.get().getServletContext().getResource("/WEB-INF/pages/" + path);
			}

			if (url != null) {
				return new UrlResourceStream(url);
			}
		} catch (MalformedURLException e) {
			throw new WicketRuntimeException(e);
		}

		return super.locate(clazz, path);
	}
}