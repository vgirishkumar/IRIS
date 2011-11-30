package com.temenos.ebank.common.wicket.components;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.WebApplication;

public class VersionPanel extends Panel{
	protected static Log logger = LogFactory.getLog(VersionPanel.class);
	
	private static final long serialVersionUID = 1L;
	public VersionPanel(String id) {
		super(id);	
		Properties versionFile = new Properties();
		try {
			InputStream is = (((WebApplication) getApplication()).getServletContext().getResourceAsStream(
				"/WEB-INF/bundle/version.properties"));
			if (is !=null) {
				versionFile.load(is);
			}
		} catch (IOException e) {
			logger.warn("Version file not loaded");
		}
		
		add(new Label("id", versionFile.getProperty("application.version")));
	}
}
