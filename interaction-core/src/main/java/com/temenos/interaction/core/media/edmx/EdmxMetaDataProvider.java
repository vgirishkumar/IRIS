package com.temenos.interaction.core.media.edmx;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
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

import org.odata4j.format.xml.EdmxFormatWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.MetaDataResource;
import com.temenos.interaction.core.RESTResource;

/**
 * JAX-RS Provider class for marshalling EDMX metadata resources.
 * 
 * EDMX representations have an xml media type but we allow atom+xml accept headers to cater
 * for OData clients which provide '* / *' accept headers.
 */
@Provider
@Consumes({MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML})
public class EdmxMetaDataProvider implements MessageBodyReader<RESTResource>, MessageBodyWriter<RESTResource> {
	private final Logger logger = LoggerFactory.getLogger(EdmxMetaDataProvider.class);

	@Context
	private UriInfo uriInfo;
	
	public EdmxMetaDataProvider() {
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return type.equals(MetaDataResource.class);
	}

	@Override
	public long getSize(RESTResource t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	/**
	 * Writes representation of {@link MetaDataResource} to the output stream.
	 * 
	 * @precondition supplied {@link MetaDataResource} is non null
	 * @precondition {@link MetaDataResource#getMetadata()} returns a valid EdmDataServices
	 * @postcondition non null meta data document written to OutputStream
	 * @invariant valid OutputStream
	 */
	@Override
	public void writeTo(RESTResource resource, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream outputStream) throws IOException,
			WebApplicationException {
		assert (resource != null);

		if (!type.equals(MetaDataResource.class))
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

		MetaDataResource metadataResource = (MetaDataResource) resource;
		if (metadataResource.getMetadata() != null) {
			// TODO create GENERICs MetaDataResource type for jaxb objects and EdmDataServices objects
			logger.error("Cannot write a jaxb object to stream with this provider");
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		StringWriter sw = new StringWriter();
		EdmxFormatWriter.write(metadataResource.getEdmx(), sw);
		final String edmxString = sw.toString();
		
		outputStream.write(edmxString.getBytes("UTF-8"));
		outputStream.flush();
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return type.equals(MetaDataResource.class);
	}

	/**
	 * Reads a representation of {@link MetaDataResource} from the input stream.
	 * 
	 * @precondition {@link InputStream} contains a valid meta data representation
	 * @postcondition {@link MetaDataResource} will be constructed and returned.
	 * @invariant valid InputStream
	 */
	@Override
	public EntityResource readFrom(Class<RESTResource> type,
			Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		return null;
	}

}
