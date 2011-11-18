package com.temenos.interaction.example.country;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.odata4j.core.OEntityKey;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * This simple class was hacked together to load OData Entities from
 * JAXB objects
 * NOTE - not threadsafe, like I said just hacked together
 * @author aphethean
 */
public class JAXBODataEntityStore implements ContentHandler {


	private List<OProperty<?>> properties;
	public List<OProperty<?>> loadProperties(final OEntityKey key) {
		try {
			
			JAXBContext jc = JAXBContext.newInstance(Country.class);
			Marshaller m = jc.createMarshaller();

			Country country = new Country();
			country.setBusinessCentre("newBusinessCentre_" + key.asSingleValue());
			country.setCentralBankCode("newCentralBankCode_" + key.asSingleValue());

			// this is an instanceof ContentHandler
			m.marshal(country, this);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return properties;
	}

	/*
	class CountryAdapter extends XmlAdapter<MXEntity, Country> {
		public Country unmarshal(MXEntity s) {
			// do nothing
			return null;
		}

		public MXEntity marshal(Country c) {
			return null;
		}
	}
	 */
	
	private String currentElement;
	private StringBuffer currentValue = new StringBuffer();
	public void setDocumentLocator(Locator locator) {
		// TODO Auto-generated method stub
		
	}

	public void startDocument() throws SAXException {
		properties = new ArrayList<OProperty<?>>();
		
	}

	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		System.out.println("qName=" + qName);
		currentElement = qName;
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (currentValue.length() > 0) {
			properties.add(OProperties.string(currentElement, currentValue.toString()));
			currentValue.delete(0, currentValue.length());
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		currentValue.append(ch, start, length);
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void skippedEntity(String name) throws SAXException {
		// TODO Auto-generated method stub
		
	}

}
