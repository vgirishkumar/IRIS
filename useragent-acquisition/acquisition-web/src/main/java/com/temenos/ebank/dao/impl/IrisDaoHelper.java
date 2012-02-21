package com.temenos.ebank.dao.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.format.xml.AtomFeedFormatParser;
import org.odata4j.format.xml.EdmxFormatParser;
import org.odata4j.format.xml.XmlFormatParser;
import org.odata4j.internal.InternalUtil;
import org.odata4j.stax2.XMLEvent2;
import org.odata4j.stax2.XMLEventReader2;

import com.temenos.ebank.domain.Nomencl;

/**
 * Helper class to perform an HTTP GET request on the REST resources.
 *  
 * NOTE: This class uses the Apache Abdera client to access the service and expects the edmx.xml to be located
 * under /edmx.xml. The latter won't be necessary once we expose the metadata service document and it will
 * enable to use an OData client library.
 */
public class IrisDaoHelper {
	Abdera abdera;
	AbderaClient client;
	EdmDataServices metadata = null;
	String irisUrl;
	
	public IrisDaoHelper(String irisUrl) {
		abdera = new Abdera();
		client = new AbderaClient(abdera);

		//Read meta data
		try {
			InputStream is = IrisDaoHelper.class.getResourceAsStream("/edmx.xml");
			XMLEventReader2 reader =  InternalUtil.newXMLEventReader(new BufferedReader(new InputStreamReader(is)));
			metadata = new EdmxFormatParser().parseMetadata(reader);
		} catch (Exception e) {
			throw new RuntimeException("Failed to read /edmx.xml", e);
		}
		
		this.irisUrl = irisUrl;
	}
	
	public List<Nomencl> getNomenclEntities(String resourcePath, String filter, String orderby) {
		List<Nomencl> entities = new ArrayList<Nomencl>();
		
		String url = irisUrl + resourcePath + "?" + filter + "&" + orderby;
		url = url.replaceAll(" ", "%20");
		ClientResponse resp = client.get(url);
		if (resp.getType() == ResponseType.SUCCESS) {
			Document<Feed> doc = resp.getDocument();
			Feed feed = doc.getRoot();
			for (Entry entry : feed.getEntries()) {
				try {
					String dsXML = entry.getContent();
					entry.getContentElement().getQName();
					XMLEventReader2 reader = InternalUtil.newXMLEventReader(new InputStreamReader(new ByteArrayInputStream(dsXML.getBytes("UTF-8"))));
					Iterable<OProperty<?>> properties = null;
					while (reader.hasNext()) {
						XMLEvent2 event = reader.nextEvent();
						if (event.isStartElement() && event.asStartElement().getName().equals(XmlFormatParser.M_PROPERTIES)) {
							properties = AtomFeedFormatParser.parseProperties(reader, event.asStartElement(), metadata);
						}
						
					}
					
					//Create Nomencl instance
					Nomencl entity = new Nomencl();
					for (OProperty<?> p : properties) {
						String pName = p.getName();
						if(pName.equals("id")) entity.setId((Long) p.getValue());
						else if(pName.equals("code")) entity.setCode((String) p.getValue());
						else if(pName.equals("groupCode")) entity.setGroupCode((String) p.getValue());
						else if(pName.equals("label")) entity.setLabel((String) p.getValue());
						else if(pName.equals("language")) entity.setLanguage((String) p.getValue());
						else if(pName.equals("sortOrder")) entity.setSortOrder((Integer) p.getValue());
					}
					entities.add(entity);

				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			throw new RuntimeException("Error " + resp.getStatus() + " while reading data from URL " + url);
		}
		return entities;
	}
}
