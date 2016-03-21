package com.temenos.interaction.test.http;

/**
 * Factory creating {@link HttpClient http client} instances.
 * 
 * @author ssethupathi
 *
 */
public class HttpClientFactory {

	public static HttpClient newClient() {
		return new DefaultHttpClient();
	}
}
