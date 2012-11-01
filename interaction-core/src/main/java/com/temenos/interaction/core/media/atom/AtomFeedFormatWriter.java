package com.temenos.interaction.core.media.atom;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.odata4j.core.ODataConstants;
import org.odata4j.core.OEntity;
import org.odata4j.core.OLink;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.format.FormatWriter;
import org.odata4j.format.xml.XmlFormatWriter;
import org.odata4j.internal.InternalUtil;
import org.odata4j.producer.EntitiesResponse;
import org.odata4j.stax2.QName2;
import org.odata4j.stax2.XMLFactoryProvider2;
import org.odata4j.stax2.XMLWriter2;

import com.temenos.interaction.core.hypermedia.Link;

public class AtomFeedFormatWriter extends XmlFormatWriter implements FormatWriter<EntitiesResponse> {
	private AtomEntryFormatWriter entryWriter = new AtomEntryFormatWriter();
	
  @Override
  public String getContentType() {
    return ODataConstants.APPLICATION_ATOM_XML_CHARSET_UTF8;
  }

  @Override
  public void write(UriInfo uriInfo, Writer w, EntitiesResponse response) {
	  EdmEntitySet ees = response.getEntitySet();
	  String entitySetName = ees.getName();
	  List<Link> links = new ArrayList<Link>();
	  links.add(new Link(entitySetName, "self", entitySetName, null, null));
	  write(uriInfo, w, links, response, null);
  }
  
  public void write(UriInfo uriInfo, Writer w, Collection<Link> links, EntitiesResponse response, Map<String, List<OLink>> entityOlinks) {
    String baseUri = uriInfo.getBaseUri().toString();

    EdmEntitySet ees = response.getEntitySet();
    String entitySetName = ees.getName();
    DateTime utc = new DateTime().withZone(DateTimeZone.UTC);
    String updated = InternalUtil.toString(utc);

    XMLWriter2 writer = XMLFactoryProvider2.getInstance().newXMLWriterFactory2().createXMLWriter(w);
    writer.startDocument();

    writer.startElement(new QName2("feed"), atom);
    writer.writeNamespace("m", m);
    writer.writeNamespace("d", d);
    writer.writeAttribute("xml:base", baseUri);

    writeElement(writer, "title", entitySetName, "type", "text");
    writeElement(writer, "id", baseUri + uriInfo.getPath());

    writeElement(writer, "updated", updated);

    assert(links != null);
    for (Link link : links) {
    	// only include the self link until we add better integration tests
    	if (link.getRel().equals("self")) {
        	// TODO include href without base path in link
        	String href = link.getHref();
        	// chop the leading base path
        	if (href.startsWith(baseUri)) {
        		href = href.substring(baseUri.length());
        	}
            writeElement(writer, "link", null, "rel", link.getRel(), "title", link.getTitle(), "href", href);
    	}
    }

    Integer inlineCount = response.getInlineCount();
    if (inlineCount != null) {
      writeElement(writer, "m:count", inlineCount.toString());
    }

    for (OEntity entity : response.getEntities()) {
    	//Obtain olinks for this entity
    	List<OLink> olinks;
    	if(entityOlinks != null) {
    		olinks = entityOlinks.get(InternalUtil.getEntityRelId(entity));
    	}
    	else {
    		olinks = new ArrayList<OLink>();    		
    	}
    	
      writer.startElement("entry");
      entryWriter.writeEntry(writer, entity, entity.getProperties(), olinks, baseUri, updated, ees, true);
      writer.endElement("entry");
    }

    if (response.getSkipToken() != null) {
      //<link rel="next" href="https://odata.sqlazurelabs.com/OData.svc/v0.1/rp1uiewita/StackOverflow/Tags/?$filter=TagName%20gt%20'a'&amp;$skiptoken=52" />
      String nextHref = uriInfo.getRequestUriBuilder().replaceQueryParam("$skiptoken", response.getSkipToken()).build().toString();
      writeElement(writer, "link", null, "rel", "next", "href", nextHref);
    }

    writer.endDocument();

  }

}
