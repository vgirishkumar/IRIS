package com.temenos.interaction.core.media.atom;

import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.edm.EdmType;
import org.odata4j.internal.InternalUtil;

import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.entity.MetadataOData4j;
import com.temenos.interaction.core.entity.vocabulary.terms.TermValueType;
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

	// Constants for OData
	public static final String d = "http://schemas.microsoft.com/ado/2007/08/dataservices";
	public static final String m = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata";
	public static final String scheme = "http://schemas.microsoft.com/ado/2007/08/dataservices/scheme";
	public static final String atom_entry_content_type = "application/atom+xml;type=entry";
	public static final String href_lang = "en";

	public void write(UriInfo uriInfo, Writer w, Entity entity,
			EntityMetadata entityMetadata, List<Link> links, String modelName) {

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

		writeEntry(writer, entity, links, baseUri, updated, entityMetadata, modelName);
		
		writer.endDocument();
		writer.flush();
	}

	public String writeEntry(StreamWriter writer, Entity entity,
			List<Link> entityLinks, String baseUri, String updated,
			EntityMetadata entityMetadata, String modelName) {

		writer.startEntry();
	    writer.writeNamespace("d", d);
	    writer.writeNamespace("m", m);
	    writer.writeAttribute("xml:base", baseUri);
	    
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
				String href = link.getHrefTransition(baseUri);
				String rel = link.getRel();
				writer.writeLink(href, rel, type, link.getTitle(), href_lang, 0);
			}
		}

		writer.writeCategory(modelName + Metadata.MODEL_SUFFIX + "." + entity.getName(), scheme);
		writer.flush();
		
		writer.startContent(MediaType.APPLICATION_XML);
		
		writer.startElement(new QName(m, "properties", "m"));
		writeProperties(writer, entityMetadata, entity.getProperties(), modelName);
		writer.endElement();
		
		writer.endContent();

		writer.endEntry();
		writer.flush();
		return absid;
	}

	private String writeKey(StreamWriter writer, String baseUri, Entity entity,
			EntityMetadata entityMetadata) {
		String absid = baseUri + uriInfo.getPath();
		writer.writeId(absid);
		return absid;
	}
	
	@SuppressWarnings("unchecked")
	private void writeProperties(StreamWriter writer, EntityMetadata entityMetadata, EntityProperties entityProperties, String modelName) {
		// Loop round all properties writing out fields and MV and SV sets
		Map<String, EntityProperty> properties = entityProperties.getProperties();
		
		for (Map.Entry<String, EntityProperty> property : properties.entrySet()) 
		{
			// Work out what the property looks like by looking at the metadata
			String propertyName = property.getKey(); 
			EntityProperty propertyValue = (EntityProperty) property.getValue();
			boolean isComplex = entityMetadata.isPropertyComplex( propertyName );
	   		if( !isComplex )
	   		{
	   			// Simple field
	   			writeProperty( writer, entityMetadata, propertyName, (EntityProperty) propertyValue  );
	   		}
	   		else
	   		{
	   			// Complex List
	   			propertyName = entityMetadata.getEntityName() + "_" + propertyName;
	   			writePropertyComplexList( writer, entityMetadata, propertyName, (List<EntityProperties>) propertyValue.getValue(), modelName );
	   		}
		}	
	}
	
	private void writeProperty( StreamWriter writer, EntityMetadata entityMetadata, String name, EntityProperty property ) {
		String elementText = entityMetadata.getPropertyValueAsString( property );
		writer.startElement(new QName(d, name, "d"));
		EdmType type = MetadataOData4j.termValueToEdmType(entityMetadata.getTermValue(name, TermValueType.TERM_NAME));
		if(!type.equals(EdmSimpleType.STRING)) {
			writer.writeAttribute(new QName(m, "type", "m"), type.getFullyQualifiedTypeName());
		}

		//Write the property text
		if(type.equals(EdmSimpleType.DATETIME)) {
			//Write dates in UTC format
			SimpleDateFormat formatUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			formatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
			writer.writeElementText(formatUTC.format((Date) property.getValue()));
		}		
		else if (elementText != null) {
			writer.writeElementText(elementText);
		}
		writer.endElement();
	}
	
	private void writePropertyComplexList( StreamWriter writer, EntityMetadata entityMetadata, String name, List<EntityProperties> propertiesList, String modelName ) {
		
		for ( EntityProperties properties : propertiesList )
		{
			writer.startElement(new QName(d, name, "d"));
			String fqTypeName = modelName + Metadata.MODEL_SUFFIX + "." + name;
			writer.writeAttribute(new QName(m, "type", "m"), fqTypeName);
			writeProperties( writer, entityMetadata, properties, modelName );
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
