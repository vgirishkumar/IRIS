package com.temenos.interaction.interaction_commands_odata;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.odata4j.core.OProperty;
import org.odata4j.format.xml.AtomFeedFormatParser;
import org.odata4j.format.xml.XmlFormatParser;
import org.odata4j.internal.InternalUtil;
import org.odata4j.stax2.XMLEvent2;
import org.odata4j.stax2.XMLEventReader2;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) {
		Abdera abdera = new Abdera();
		AbderaClient client = new AbderaClient(abdera);
//		ClientResponse resp = client.get("http://localhost:8080/example/rest/notes");
		ClientResponse resp = client.get("http://localhost:8886/JPAProducerExample.svc/Products(49)/Order_Details");
//		ClientResponse resp = client.get("http://localhost:8080/responder/rest/FlightSchedule/1");
		if (resp.getType() == ResponseType.SUCCESS) {
			Document<Feed> doc = resp.getDocument();
			Feed feed = doc.getRoot();
			System.out.println("Feed Title: " + feed.getTitle());
			for (Entry entry : feed.getEntries()) {
				System.out.println("Title: " + entry.getTitle());
				System.out.println("Unique Identifier: " + entry.getBaseUri());
				System.out.println("Updated Date: " + entry.getUpdated());

				// Get the Links
				for (Link link : entry.getLinks()) {
					System.out.println("Link: " + link.getHref() + ", rel: " + link.getRel());
				}           
				
				try {
					// Get the OData OProperties
					String dsXML = entry.getContent();
					entry.getContentElement().getQName();
					// odata4j only support utf-8, so assume it wants a utf-8 stream
					XMLEventReader2 reader = InternalUtil.newXMLEventReader(new InputStreamReader(new ByteArrayInputStream(dsXML.getBytes("UTF-8"))));
					Iterable<OProperty<?>> properties = null;
					while (reader.hasNext()) {
						XMLEvent2 event = reader.nextEvent();
						if (event.isStartElement() && event.asStartElement().getName().equals(XmlFormatParser.M_PROPERTIES)) {
							properties = AtomFeedFormatParser.parseProperties(reader, event.asStartElement());
						}
						
					}
					
					System.out.println("Content: ");
					for (OProperty<?> p : properties) {
						System.out.println("\t" + p.getName() + "=" + p.getValue());
					}

				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				System.out.println("-------------------");
			}

		} else {
			System.out.println("there was an error [" + resp.getStatus() + "]");
		}
	}
}
