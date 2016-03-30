package com.temenos.useragent.generic.http;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.temenos.useragent.generic.internal.RequestData;
import com.temenos.useragent.generic.internal.ResponseData;

public class DefaultHttpExecutorTest {

	private RequestData mockRequestData;
	private HttpClient mockHttpClient;
	private HttpResponse mockResponse;
	private HttpHeader mockHeader;

	@Before
	public void setUp() {
		mockHttpClient = mock(HttpClient.class);
		mockRequestData = mock(RequestData.class);
		mockResponse = mock(HttpResponse.class);
		mockHeader = mock(HttpHeader.class);
	}

	@Test
	public void testExecuteForGet() throws Exception {
		when(mockHttpClient.get(anyString(), any(HttpRequest.class)))
				.thenReturn(mockResponse);
		when(mockResponse.headers()).thenReturn(mockHeader);
		when(mockHeader.get("Content-Type")).thenReturn("application/atom+xml");
		when(mockResponse.payload())
				.thenReturn(
						IOUtils.toString(DefaultHttpExecutorTest.class
								.getResourceAsStream("/atom_feed_with_single_entry.txt")));
		DefaultHttpExecutor executor = new DefaultHttpExecutor(mockHttpClient,
				"http://myserver:8080/myservice/Test.svc", mockRequestData);
		ResponseData responseData = executor.execute(HttpMethod.GET);
		assertNotNull(responseData);
		assertEquals("application/atom+xml",
				responseData.header().get("Content-Type"));
		assertTrue(responseData.body().isCollection());
		assertEquals(1, responseData.body().entities().size());
	}

	@Test
	public void testExecuteForPost() throws Exception {
		when(mockHttpClient.post(anyString(), any(HttpRequest.class)))
				.thenReturn(mockResponse);
		when(mockResponse.headers()).thenReturn(mockHeader);
		when(mockHeader.get("Content-Type")).thenReturn("application/atom+xml");
		when(mockResponse.payload())
				.thenReturn(
						IOUtils.toString(DefaultHttpExecutorTest.class
								.getResourceAsStream("/atom_entry_with_xml_content.txt")));
		DefaultHttpExecutor executor = new DefaultHttpExecutor(mockHttpClient,
				"http://myserver:8080/myservice/Test.svc", mockRequestData);
		ResponseData responseData = executor.execute(HttpMethod.POST);
		assertNotNull(responseData);
		assertEquals("application/atom+xml",
				responseData.header().get("Content-Type"));
		assertFalse(responseData.body().isCollection());
		assertNotNull(responseData.body().entity());
	}

	@Test
	public void testExecuteForPut() {
		when(mockHttpClient.put(anyString(), any(HttpRequest.class)))
				.thenReturn(mockResponse);
		when(mockResponse.headers()).thenReturn(mockHeader);
		when(mockHeader.get("Content-Type")).thenReturn("text/plain");
		when(mockResponse.payload()).thenReturn("plain text payload");
		DefaultHttpExecutor executor = new DefaultHttpExecutor(mockHttpClient,
				"http://myserver:8080/myservice/Test.svc", mockRequestData);
		ResponseData responseData = executor.execute(HttpMethod.PUT);
		assertNotNull(responseData);
		assertEquals("text/plain", responseData.header().get("Content-Type"));
		assertFalse(responseData.body().isCollection());
	}
}
