package com.temenos.interaction.test.http;

import static org.junit.Assert.*;

import org.apache.http.HttpMessage;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.junit.Test;

import com.temenos.interaction.test.context.ConnectionConfig;
import com.temenos.interaction.test.context.ContextFactory;

public class DefaultHttpClientHelperTest {

	@Test
	public void testBuildRequestHeaders() {
		HttpHeader requestHeader = new HttpHeader();
		requestHeader.set("Content-Type", "application/atom+xml");
		HttpRequest request = new HttpRequestImpl(requestHeader);
		HttpMessage message = new HttpGet();
		DefaultHttpClientHelper.buildRequestHeaders(request, message);
		assertEquals("application/atom+xml",
				message.getFirstHeader("Content-Type").getValue());
	}

	@Test
	public void testRemoveParameter() {
		String contentType = "foo/bar";
		assertEquals("foo/bar",
				DefaultHttpClientHelper.removeParameter(contentType));
		contentType = "foo/bar;";
		assertEquals("foo/bar",
				DefaultHttpClientHelper.removeParameter(contentType));
		contentType = "foo/bar;a=b";
		assertEquals("foo/bar",
				DefaultHttpClientHelper.removeParameter(contentType));
		contentType = "";
		assertEquals("", DefaultHttpClientHelper.removeParameter(contentType));
	}

	@Test
	public void testExtractParameter() {
		String contentType = "foo/bar";
		assertEquals("", DefaultHttpClientHelper.extractParameter(contentType));
		contentType = "foo/bar;";
		assertEquals("", DefaultHttpClientHelper.extractParameter(contentType));
		contentType = "foo/bar;a=b";
		assertEquals("a=b",
				DefaultHttpClientHelper.extractParameter(contentType));
		contentType = "";
		assertEquals("", DefaultHttpClientHelper.extractParameter(contentType));
	}

	@Test
	public void testPrettyPrintXml() {
		String invalidXml = "";
		assertEquals("", DefaultHttpClientHelper.prettyPrintXml(invalidXml));
		invalidXml = "<html><HR1><HR2></html>";
		assertEquals(invalidXml,
				DefaultHttpClientHelper.prettyPrintXml(invalidXml));
		String validXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><foo><bar>text</bar></foo>";
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><foo>\n    <bar>text</bar>\n</foo>", DefaultHttpClientHelper.prettyPrintXml(validXml));

	}
}
