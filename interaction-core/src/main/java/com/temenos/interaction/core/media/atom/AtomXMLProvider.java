package com.temenos.interaction.core.media.atom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.odata4j.core.OEntity;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.producer.Responses;

import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.resource.ResourceTypeHelper;

@Provider
@Consumes({MediaType.APPLICATION_ATOM_XML})
@Produces({MediaType.APPLICATION_ATOM_XML})
public class AtomXMLProvider implements MessageBodyReader<RESTResource>, MessageBodyWriter<RESTResource> {

	@Context
	private UriInfo uriInfo;
	private AtomEntryFormatWriter entryWriter = new AtomEntryFormatWriter();
	private AtomFeedFormatWriter feedWriter = new AtomFeedFormatWriter();
	
	private EdmDataServices edmDataServices;

	public AtomXMLProvider(EdmDataServices edmDataServices) {
		this.edmDataServices = edmDataServices;
		assert(edmDataServices != null);
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return ResourceTypeHelper.isType(type, genericType, EntityResource.class) ||
				ResourceTypeHelper.isType(type, genericType, CollectionResource.class);
	}

	@Override
	public long getSize(RESTResource t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	/**
	 * Writes a Atom (OData) representation of {@link EntityResource} to the output stream.
	 * 
	 * @precondition supplied {@link EntityResource} is non null
	 * @precondition {@link EntityResource#getEntity()} returns a valid OEntity, this 
	 * provider only supports serialising OEntities
	 * @postcondition non null Atom (OData) XML document written to OutputStream
	 * @invariant valid OutputStream
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void writeTo(RESTResource resource, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {
		assert (resource != null);
		assert(uriInfo != null);
		
		if(ResourceTypeHelper.isType(type, genericType, EntityResource.class, OEntity.class)) {
			EntityResource<OEntity> entityResource = (EntityResource<OEntity>) resource;
			entryWriter.write(uriInfo, new OutputStreamWriter(entityStream), Responses.entity(entityResource.getEntity()));
		}
		if(ResourceTypeHelper.isType(type, genericType, CollectionResource.class, OEntity.class)) {
			CollectionResource<OEntity> cr = ((CollectionResource<OEntity>) resource);
			List<OEntity> entities = (List<OEntity>) cr.getEntities();
			EdmEntitySet entitySet = edmDataServices.getEdmEntitySet(cr.getEntitySetName());
			// TODO implement collection properties and get transient values for inlinecount and skiptoken
			Integer inlineCount = null;
			String skipToken = null;
			feedWriter.write(uriInfo, new OutputStreamWriter(entityStream), Responses.entities(entities, entitySet, inlineCount, skipToken));
		}
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		// this class can only deserialise EntityResource with OEntity.
		return ResourceTypeHelper.isType(type, genericType, EntityResource.class, OEntity.class);
	}

	/**
	 * Reads a Atom (OData) representation of {@link EntityResource} from the input stream.
	 * 
	 * @precondition {@link InputStream} contains a valid Atom (OData) Entity enclosed in a <resource/> document
	 * @postcondition {@link EntityResource} will be constructed and returned.
	 * @invariant valid InputStream
	 */
	@Override
	public EntityResource<OEntity> readFrom(Class<RESTResource> type,
			Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		// TODO implement deserialise
		return null;
	}
}
