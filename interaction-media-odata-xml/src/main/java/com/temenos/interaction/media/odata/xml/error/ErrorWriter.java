package com.temenos.interaction.media.odata.xml.error;

/*
 * #%L
 * interaction-media-odata-xml
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


import java.io.Writer;

import org.odata4j.format.xml.XmlFormatWriter;
import org.odata4j.stax2.QName2;
import org.odata4j.stax2.XMLFactoryProvider2;
import org.odata4j.stax2.XMLWriter2;

import com.temenos.interaction.core.entity.GenericError;

/**
 * This class writes OData xml error responses.  
 */
public class ErrorWriter extends XmlFormatWriter {

	public static void write(GenericError error, Writer w) {

		XMLWriter2 writer = XMLFactoryProvider2.getInstance()
				.newXMLWriterFactory2().createXMLWriter(w);
		writer.startDocument();

		writer.startElement(new QName2("error"), m);

		writer.startElement(new QName2("code"));
		writer.writeText(error.getCode());
		writer.endElement("code");
		writer.startElement(new QName2("message"));
		writer.writeAttribute("xml:lang", "en-US");
		writer.writeText(error.getMessage());
		writer.endElement("message");

		writer.endElement("error");

		writer.endDocument();
	}

}