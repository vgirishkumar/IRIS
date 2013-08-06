package com.temenos.interaction.media.odata.xml.error;

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