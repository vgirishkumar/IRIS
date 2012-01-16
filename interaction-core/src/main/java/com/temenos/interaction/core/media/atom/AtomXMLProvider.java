package com.temenos.interaction.core.media.atom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

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

import org.odata4j.producer.Responses;

import com.temenos.interaction.core.EntityResource;

@Provider
@Consumes({MediaType.APPLICATION_ATOM_XML})
@Produces({MediaType.APPLICATION_ATOM_XML})
public class AtomXMLProvider implements MessageBodyReader<EntityResource>, MessageBodyWriter<EntityResource> {

	@Context
	private UriInfo uriInfo;
	private AtomEntryFormatWriter writer = new AtomEntryFormatWriter();
	
	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return type.equals(EntityResource.class);
	}

	@Override
	public long getSize(EntityResource t, Class<?> type, Type genericType,
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
	public void writeTo(EntityResource resource, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {
		assert (resource != null);

		if (!type.equals(EntityResource.class))
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

		// TODO add EntityResource Links to OEntity??
		assert(uriInfo != null);
	    String baseUri = uriInfo.getBaseUri().toString();
		writer.write(baseUri, new OutputStreamWriter(entityStream), Responses.entity(resource.getOEntity()));
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
	public EntityResource readFrom(Class<EntityResource> type,
			Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		// TODO Auto-generated method stub
		return null;
	}

}
