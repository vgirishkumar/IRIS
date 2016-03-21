package com.temenos.interaction.test.http;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.http.HttpMessage;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.BasicCredentialsProvider;

import com.temenos.interaction.test.Result;
import com.temenos.interaction.test.context.ConnectionConfig;
import com.temenos.interaction.test.context.ContextFactory;

/**
 * Helper for the Http Client operations.
 * 
 * @author ssethupathi
 *
 */
public class DefaultHttpClientHelper {

	/**
	 * Builds and returns the {@link HttpMessage http message} with the request
	 * headers.
	 * 
	 * @param request
	 * @param message
	 */
	public static void buildRequestHeaders(HttpRequest request,
			HttpMessage message) {
		HttpHeader header = request.headers();
		for (String name : header.names()) {
			message.addHeader(name, header.get(name));
		}
	}

	/**
	 * Builds and returns the {@link HttpHeader http header} from the response
	 * message.
	 * 
	 * @param response
	 *            message
	 * @param http
	 *            header
	 */

	public static HttpHeader buildResponseHeaders(
			CloseableHttpResponse httpResponse) {
		HttpHeader header = new HttpHeader();
		for (org.apache.http.Header httpHeader : httpResponse.getAllHeaders()) {
			header.set(httpHeader.getName(), httpHeader.getValue());
		}
		return header;
	}

	/**
	 * Builds and returns http interaction execution result.
	 * 
	 * @param httpResponse
	 * @return interaction execution result
	 */
	public static Result buildResult(CloseableHttpResponse httpResponse) {
		StatusLine statusLine = httpResponse.getStatusLine();
		return new HttpResult(statusLine.getStatusCode(),
				statusLine.getReasonPhrase());
	}

	/**
	 * Builds and returns the basic authentication provider.
	 * 
	 * @return
	 */
	public static CredentialsProvider getBasicCredentialProvider() {
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		ConnectionConfig config = ContextFactory.get().getContext()
				.connectionCongfig();
		credentialsProvider.setCredentials(
				AuthScope.ANY,
				new UsernamePasswordCredentials(config
						.getValue(ConnectionConfig.USER_NAME), config
						.getValue(ConnectionConfig.PASSWORD)));
		return credentialsProvider;
	}

	/**
	 * Builds the pretty print XML for logging.
	 * <p>
	 * This method is safe for non XML content as it returns the invalid XML
	 * content as-is.
	 * </p>
	 * 
	 * @param xmlDoc
	 * @return pretty print XML
	 */
	public static String prettyPrintXml(String xmlDoc) {
		try {
			Source xmlInput = new StreamSource(new StringReader(xmlDoc));
			StringWriter stringWriter = new StringWriter();
			StreamResult xmlOutput = new StreamResult(stringWriter);
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			transformerFactory.setAttribute("indent-number", 4);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(xmlInput, xmlOutput);
			return xmlOutput.getWriter().toString();
		} catch (Exception e) {
			// Not a valid XML document so return as-is
			return xmlDoc;
		}
	}
}
