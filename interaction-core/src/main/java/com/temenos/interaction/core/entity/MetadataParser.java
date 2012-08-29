package com.temenos.interaction.core.entity;

import java.io.InputStream;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.temenos.interaction.core.entity.vocabulary.TermFactory;
import com.temenos.interaction.core.entity.vocabulary.Vocabulary;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexGroup;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexType;

/**
 * Parser to read metadata from an XML file.
 */
public class MetadataParser extends DefaultHandler {
	TermFactory termFactory = new TermFactory();

	Metadata metadata = null;
	EntityMetadata entityMetadata = null;									//Meta data for current entity
	Stack<String> propertyName = new Stack<String>();						//Stack of property names
	Stack<Vocabulary> propertyVocabulary = new Stack<Vocabulary>();			//Stack of property vocabularies
	boolean isComplexProperty = false;										//Indicates if this is a complex property
	String termName = null;													//Name of vocabulary term
	String termValue = null;												//Value of vocabulary term


	/**
	 * Parse an XML document.
	 * @param reader I/O Reader providing the xml document
	 * @return Metadata containing the metadata
	 */
	public Metadata parse(InputStream is) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(is, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return metadata;
	}
	
	@Override
	public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("Metadata")) {
			metadata = new Metadata(attributes.getValue("ModelName"));
		}
		else if (qName.equalsIgnoreCase("Entity")) {
			entityMetadata = new EntityMetadata(attributes.getValue("Name"));
		}
		else if (qName.equalsIgnoreCase("Property")) {
			String name = attributes.getValue("Name");
			Vocabulary voc = new Vocabulary();
			if(propertyName.size() > 0) {
				//This property belongs to a complex property => set complex group term
				try {
					voc.setTerm(termFactory.createTerm(TermComplexGroup.TERM_NAME, propertyName.peek()));
				}
				catch(Exception e) {
					throw new SAXException(e.getMessage());
				}
			}
			propertyName.push(name);			
			propertyVocabulary.push(voc);
			isComplexProperty = false;
		}
		else if (qName.equalsIgnoreCase("Term")) {
			termName = attributes.getValue("Name");
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("Entity")) {
			metadata.setEntityMetadata(entityMetadata);
		}
		else if (qName.equalsIgnoreCase("Property")) {
			String name = propertyName.pop();
			Vocabulary voc = propertyVocabulary.pop();
			if(isComplexProperty) {
				//This is a complex property
				try {
					voc.setTerm(termFactory.createTerm(TermComplexType.TERM_NAME, "true"));
				}
				catch(Exception e) {
					throw new SAXException(e.getMessage());
				}
			}
			entityMetadata.setPropertyVocabulary(name, voc);
			isComplexProperty = (propertyName.size() > 0);
		}
		else if (qName.equalsIgnoreCase("Term")) {
			try {
				propertyVocabulary.peek().setTerm(termFactory.createTerm(termName, termValue));
			}
			catch(Exception e) {
				throw new SAXException(e.getMessage());
			}
			finally {
				termName = null;	//characters() uses termName to avoid reading elements other than Terms			
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (termName != null) {
			termValue = new String(ch, start, length);
		}

	}
}
