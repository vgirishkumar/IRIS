package com.temenos.interaction.core.media.atom;

import java.io.Writer;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.odata4j.core.ODataConstants;
import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OLink;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.format.Entry;
import org.odata4j.format.FormatWriter;
import org.odata4j.format.xml.XmlFormatWriter;
import org.odata4j.internal.InternalUtil;
import org.odata4j.producer.EntityResponse;
import org.odata4j.stax2.QName2;
import org.odata4j.stax2.XMLFactoryProvider2;
import org.odata4j.stax2.XMLWriter2;

/**
 * Slightly modified version of @link{org.odata4j.format.xml.AtomEntryFormatWriter} that 
 * is more aligned with JAX-RS.
 * @author aphethean
 *
 */
public class AtomEntryFormatWriter extends XmlFormatWriter implements FormatWriter<EntityResponse> {

  protected String baseUri;

  public void writeRequestEntry(Writer w, Entry entry) {

    DateTime utc = new DateTime().withZone(DateTimeZone.UTC);
    String updated = InternalUtil.toString(utc);

    XMLWriter2 writer = XMLFactoryProvider2.getInstance().newXMLWriterFactory2().createXMLWriter(w);
    writer.startDocument();

    writer.startElement(new QName2("entry"), atom);
    writer.writeNamespace("d", d);
    writer.writeNamespace("m", m);
    
    OEntity entity = entry.getEntity();
    writeEntry(writer, null, entity.getProperties(), entity.getLinks(),
        null, updated, entity.getEntitySet(), false);
    writer.endDocument();

  }

  @Override
  public String getContentType() {
    return ODataConstants.APPLICATION_ATOM_XML_CHARSET_UTF8;
  }

  public void write(UriInfo uriInfo, Writer w, EntityResponse target, EdmEntitySet entitySet, List<OLink> olinks) {
    String baseUri = uriInfo.getBaseUri().toString();

    DateTime utc = new DateTime().withZone(DateTimeZone.UTC);
    String updated = InternalUtil.toString(utc);

    XMLWriter2 writer = XMLFactoryProvider2.getInstance().newXMLWriterFactory2().createXMLWriter(w);
    writer.startDocument();

    writer.startElement(new QName2("entry"), atom);
    writer.writeNamespace("m", m);
    writer.writeNamespace("d", d);
    writer.writeAttribute("xml:base", baseUri);

    // this darn writer sometimes uses the EdmEntitySet we pass in and sometimes uses OEntity.getEntitySet
    OEntity origOE = target.getEntity() ;   
    OEntity newOE = OEntities.create(entitySet, origOE.getEntityKey(), origOE.getProperties(), olinks);

    writeEntry(writer, newOE, newOE.getProperties(), newOE.getLinks(), baseUri, updated, entitySet, true);
    writer.endDocument();
  }


  @Override
  // the original implementation that uses the OEntity EdmEntitySet
  public void write(UriInfo uriInfo, Writer w, EntityResponse target) {
	    EdmEntitySet ees = target.getEntity().getEntitySet();
	    write(uriInfo, w, target, ees, target.getEntity().getLinks());
  }
}
