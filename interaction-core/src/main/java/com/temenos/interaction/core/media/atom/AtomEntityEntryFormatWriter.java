package com.temenos.interaction.core.media.atom;

import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.writer.StreamWriter;
import org.apache.commons.io.output.WriterOutputStream;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.odata4j.core.OAtomEntity;
import org.odata4j.internal.InternalUtil;

import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.hypermedia.Link;

/**
 * Writes an IRIS Entity out to Atom XML format using the Apache Abdera library.
 * 
 * @author srushworth
 * 
 */
public class AtomEntityEntryFormatWriter {

	protected UriInfo uriInfo = null;
	protected String baseUri = "";

	// Constants as defined by the odata4j XmlFormatWriter class
	private static final String d = "http://schemas.microsoft.com/ado/2007/08/dataservices";
	private static final String m = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata";
	private static final String scheme = "http://schemas.microsoft.com/ado/2007/08/dataservices/scheme";
	private static final String atom_entry_content_type = "application/atom+xml;type=entry";
	private static final String t24_model = "T24Model";
	private static final String href_lang = "en";

	public void write(UriInfo uriInfo, Writer w, Entity entity,
			EntityMetadata entityMetadata, List<Link> links) {

		String entityName = entity.getName();
		this.uriInfo = uriInfo;
		String baseUri = uriInfo.getBaseUri().toString();

		DateTime utc = new DateTime().withZone(DateTimeZone.UTC);
		String updated = InternalUtil.toString(utc);

		Abdera abdera = new Abdera();
		StreamWriter writer = abdera.newStreamWriter();
		writer.setOutputStream(new WriterOutputStream(w));
		//writer.setOutputStream(System.out,"UTF-8");
		writer.setAutoflush(false);
		writer.setAutoIndent(true);
		writer.startDocument();
	    writer.startFeed();

		writer.writeNamespace("m", m);
		writer.writeNamespace("d", d);
		writer.writeAttribute("xml:base", baseUri);
		
		writer.writeTitle(entityName);
		writer.writeId(baseUri + uriInfo.getPath());
		writer.writeUpdated(updated);
		writer.writeLink(entityName, "self", "text", entityName, href_lang, 0);
		writer.flush();

		writeEntry(writer, entity, links, baseUri, updated, entityMetadata);
		
		writer.endFeed();
		writer.endDocument();
		writer.flush();
	}

	public String writeEntry(StreamWriter writer, Entity entity,
			List<Link> entityLinks, String baseUri, String updated,
			EntityMetadata entityMetadata) {

		writer.startEntry();
		
		String absid = writeKey(writer, baseUri, entity, entityMetadata);
		OAtomEntity oae = getAtomInfo(entity);

		writer.writeTitle(oae.getAtomEntityTitle());
		String summary = oae.getAtomEntitySummary();
		if (!summary.isEmpty()) {
			writer.writeSummary(summary);
		}

		LocalDateTime updatedTime = oae.getAtomEntityUpdated();
		if (updatedTime != null) {
			updated = InternalUtil.toString(updatedTime
					.toDateTime(DateTimeZone.UTC));
		}
		
		writer.writeUpdated(updated);
		writer.writeAuthor(oae.getAtomEntityAuthor());

		if (entityLinks != null) {
			for (Link link : entityLinks) {
				String type = atom_entry_content_type;
				String href = link.getHref();
				writer.writeLink(href, link.getRel(), type, link.getTitle(), href_lang, 0);
			}
		}

		String term = entity == null ? t24_model : entity.getName();
		writer.writeCategory(term, scheme);
		writer.flush();
		
		writer.startContent(MediaType.APPLICATION_XML);
		
		writer.startElement(new QName(m, "properties", "m"));
		writeProperties(writer, entityMetadata, entity.getProperties());
		writer.endElement();
		
		writer.endContent();

		writer.endEntry();
		writer.flush();
		return absid;
	}

	private String writeKey(StreamWriter writer, String baseUri, Entity entity,
			EntityMetadata entityMetadata) {
		String relid = null;
		String absid = null;

		List<String> keys = entityMetadata.getIdFields();

		if (keys.size() > 0)
			relid = (String) entity.getProperties().getProperty(keys.get(0))
					.getValue();

		absid = baseUri + uriInfo.getPath() + "/" + relid;
		writer.writeId(absid);
		return absid;
	}

	@SuppressWarnings("unchecked")
	private void writeProperties(StreamWriter writer, EntityMetadata entityMetadata, EntityProperties entityProperties) {
		// Loop round all properties writing out fields and MV and SV sets
		Map<String, EntityProperty> properties = entityProperties.getProperties();
		
		for (Map.Entry<String, EntityProperty> property : properties.entrySet()) 
		{
			// Work out what the property looks like by looking at the metadata
			String propertyName = property.getKey(); 
			EntityProperty propertyValue = (EntityProperty) property.getValue();
			boolean isComplex = entityMetadata.isPropertyComplex( propertyName );
	   		
	   		if ( !isComplex )
	   		{
	   			// Simple field
	   			writeProperty( writer, entityMetadata, propertyName, (EntityProperty) propertyValue  );
	   		}
	   		else if ( isComplex )
	   		{
	   			// Complex List
	   			writePropertyComplexList( writer, entityMetadata, propertyName, (List<EntityProperties>) propertyValue.getValue() );
	   		}
	   		else
	   		{
	   			throw new RuntimeException("Unable to establish type of property to write");
	   		}
		}	
	}
	
	private void writeProperty( StreamWriter writer, EntityMetadata entityMetadata, String name, EntityProperty property ) {
		//writeElement( writer, name, entityMetadata.getPropertyValueAsString( property ) );
		String elementText = entityMetadata.getPropertyValueAsString( property );
		writer.startElement(new QName(d, name, "d"));
		if (elementText != null) {
			writer.writeElementText(elementText);
		}
		writer.endElement();
	}
	
	private void writePropertyComplexList( StreamWriter writer, EntityMetadata entityMetadata, String name, List<EntityProperties> propertiesList ) {
		
		for ( EntityProperties properties : propertiesList )
		{
			writer.startElement(new QName(d, name, "d"));
			writeProperties( writer, entityMetadata, properties );
			writer.endElement(); 
		}
		
	}
	
	private OAtomEntity getAtomInfo(Entity e) {

		return new OAtomEntity() {
			@Override
			public String getAtomEntityTitle() {
				return "";
			}

			@Override
			public String getAtomEntitySummary() {
				return "";
			}

			@Override
			public String getAtomEntityAuthor() {
				return "";
			}

			@Override
			public LocalDateTime getAtomEntityUpdated() {
				return null;
			}
		};
	}
}
