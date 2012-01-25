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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.odata4j.core.OEntity;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.producer.Responses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.CollectionResource;
import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.RESTResource;

@Provider
@Consumes({MediaType.APPLICATION_ATOM_XML})
@Produces({MediaType.APPLICATION_ATOM_XML})
public class AtomXMLProvider implements MessageBodyReader<RESTResource>, MessageBodyWriter<RESTResource> {
	private final Logger logger = LoggerFactory.getLogger(AtomXMLProvider.class);

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
		return type.equals(EntityResource.class) || type.equals(CollectionResource.class);
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
	 * @precondition {@link EntityResource#getOEntity()} returns a valid OEntity, this 
	 * provider only supports serialising OEntities
	 * @postcondition non null Atom (OData) XML document written to OutputStream
	 * @invariant valid OutputStream
	 */
	@Override
	public void writeTo(RESTResource resource, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {
		assert (resource != null);

		if (!type.equals(EntityResource.class) && !type.equals(CollectionResource.class))
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

		assert(uriInfo != null);
		if (resource instanceof EntityResource) {
			EntityResource entityResource = (EntityResource) resource;
			if (entityResource.getEntity() != null) {
				// TODO create GENERICs EntityResource type for jaxb objects and oentity objects.  Provider isWriteable only works the class type
				logger.error("Cannot write a jaxb object to stream with this provider");
				throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
			}
			// TODO add EntityResource Links to OEntity??
			entryWriter.write(uriInfo, new OutputStreamWriter(entityStream), Responses.entity((entityResource).getOEntity()));
		}
		if (resource instanceof CollectionResource) {
			// TODO add writer for Links
			CollectionResource cr = ((CollectionResource) resource);
			if (cr.getEntities() != null) {
				// TODO create GENERICs CollectionResource type for jaxb collection and oentity collection.  Provider isWriteable only works the class type
				logger.error("Cannot write a jaxb object to stream with this provider");
			}
			List<OEntity> entities = cr.getOEntities();
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
		return type.equals(EntityResource.class);
	}

	/**
	 * Reads a Atom (OData) representation of {@link EntityResource} from the input stream.
	 * 
	 * @precondition {@link InputStream} contains a valid Atom (OData) Entity enclosed in a <resource/> document
	 * @postcondition {@link EntityResource} will be constructed and returned.
	 * @invariant valid InputStream
	 */
	@Override
	public EntityResource readFrom(Class<RESTResource> type,
			Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		// TODO implement deserialise
		return null;
	}

}
