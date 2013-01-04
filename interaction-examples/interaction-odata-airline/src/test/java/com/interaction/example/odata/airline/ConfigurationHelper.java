package com.interaction.example.odata.airline;

public class ConfigurationHelper {

	private final static String TEST_ENDPOINT_URI_KEY = "TEST_ENDPOINT_URI";
	
	public static String getTestEndpintUri(String defaultUri) {
		String uri = defaultUri;
		if (System.getProperty(TEST_ENDPOINT_URI_KEY) != null)
			uri = System.getProperty(TEST_ENDPOINT_URI_KEY);
		assert(uri != null);
		return uri;
	}
}
