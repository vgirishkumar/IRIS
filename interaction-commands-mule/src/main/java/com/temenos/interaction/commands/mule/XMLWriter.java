package com.temenos.interaction.commands.mule;

/*
 * #%L
 * interaction-commands-mule
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import java.io.OutputStream;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.temenos.interaction.core.entity.Entity;

public class XMLWriter {

	// Create an output factory
	private XMLOutputFactory xmlof = XMLOutputFactory.newInstance();

	void toXml(ViewCommandWrapper viewCommand, OutputStream out)
			throws XMLStreamException {
		// Create an XML stream writer
		XMLStreamWriter xmlw = xmlof.createXMLStreamWriter(out);

		// Write XML prologue
		xmlw.writeStartDocument();
		// Write a processing instruction
		// xmlw.writeProcessingInstruction("xml-stylesheet href='catalog.xsl' type='text/xsl'");
		// Start with root element
		xmlw.writeStartElement("viewcommand");

		writePathParameters(xmlw, viewCommand.getPathParameters());
		writeQueryParameters(xmlw, viewCommand.getQueryParameters());

		// Write document end. This closes all open structures
		xmlw.writeEndDocument();
		// Close the writer to flush the output
		xmlw.close();
	}

	void toXml(ActionCommandWrapper actionCommand, OutputStream out)
			throws XMLStreamException {
		// Create an XML stream writer
		XMLStreamWriter xmlw = xmlof.createXMLStreamWriter(out);

		// Write XML prologue
		xmlw.writeStartDocument();
		// Start with root element
		xmlw.writeStartElement("actioncommand");

		writePathParameters(xmlw, actionCommand.getPathParameters());
		writeQueryParameters(xmlw, actionCommand.getQueryParameters());
		
		Entity entity = actionCommand.getRequestBody();
		if (entity != null) {
			xmlw.writeStartElement("entity");
			xmlw.writeStartElement("name");
			xmlw.writeCharacters(entity.getName());
			xmlw.writeEndElement();
			for (String paramKey : entity.getProperties().getProperties().keySet()) {
				xmlw.writeStartElement(paramKey);
				xmlw.writeCharacters(entity.getProperties().getProperty(paramKey).getValue().toString());
				xmlw.writeEndElement();
			}
			xmlw.writeEndElement();	
		}
		
		// Write document end. This closes all open structures
		xmlw.writeEndDocument();
		// Close the writer to flush the output
		xmlw.close();
	}

	private void writePathParameters(XMLStreamWriter xmlw, MultivaluedMap<String, String> pathParameters) 
			throws XMLStreamException {
		xmlw.writeStartElement("pathparameters");
		for (String paramKey : pathParameters.keySet()) {
			xmlw.writeStartElement(paramKey);
			xmlw.writeCharacters(pathParameters.getFirst(paramKey));
			xmlw.writeEndElement();
		}
		xmlw.writeEndElement();	
	}
	
	private void writeQueryParameters(XMLStreamWriter xmlw, MultivaluedMap<String, String> queryParameters) 
			throws XMLStreamException {
		xmlw.writeStartElement("queryparameters");
		for (String paramKey : queryParameters.keySet()) {
			xmlw.writeStartElement(paramKey);
			xmlw.writeCharacters(queryParameters.getFirst(paramKey));
			xmlw.writeEndElement();
		}
		xmlw.writeEndElement();
	}
}
