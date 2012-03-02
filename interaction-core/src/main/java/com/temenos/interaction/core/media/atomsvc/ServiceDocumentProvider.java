package com.temenos.interaction.core.media.atomsvc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;

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

import org.odata4j.edm.EdmDataServices;
import org.odata4j.format.FormatWriter;
import org.odata4j.format.FormatWriterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.ServiceDocumentResource;
import com.temenos.interaction.core.RESTResource;
import com.temenos.interaction.core.ExtendedMediaTypes;

/**
 * JAX-RS Provider class for marshalling Service document resources.
 * 
 * Service document representations are atomsvc+xml media types but we allow 
 * atom+xml accept headers to cater for OData clients which provide '* / *' 
 * accept headers.
 */
@Provider
@Consumes({ExtendedMediaTypes.APPLICATION_ATOMSVC_XML})
@Produces({ExtendedMediaTypes.APPLICATION_ATOMSVC_XML, MediaType.APPLICATION_ATOM_XML})
public class ServiceDocumentProvider implements MessageBodyReader<RESTResource>, MessageBodyWriter<RESTResource> {
	private final Logger logger = LoggerFactory.getLogger(ServiceDocumentProvider.class);

	@Context
	private UriInfo uriInfo;
	
	public ServiceDocumentProvider() {
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return type.equals(ServiceDocumentResource.class);
	}

	@Override
	public long getSize(RESTResource t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	/**
	 * Writes representation of {@link ServiceDocumentResource} to the output stream.
	 * 
	 * @precondition supplied {@link ServiceDocumentResource} is non null
	 * @precondition {@link ServiceDocumentResource#getServiceDocument()} returns a valid EdmDataServices
	 * @postcondition non null service document written to OutputStream
	 * @invariant valid OutputStream
	 */
	@Override
	public void writeTo(RESTResource resource, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream outputStream) throws IOException,
			WebApplicationException {
		assert (resource != null);

		if (!type.equals(ServiceDocumentResource.class))
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

		ServiceDocumentResource serviceDocumentResource = (ServiceDocumentResource) resource;
		if (serviceDocumentResource.getServiceDocument() != null) {
			// TODO create GENERICs ServiceDocumentResource type for jaxb objects and EdmDataServices objects
			logger.error("Cannot write a jaxb object to stream with this provider");
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}

	    EdmDataServices metadata = serviceDocumentResource.getEdmx();
	    StringWriter sw = new StringWriter();
	    MediaType[] acceptedMediaTypes = { ExtendedMediaTypes.APPLICATION_ATOMSVC_XML_TYPE };
	    FormatWriter<EdmDataServices> fw = FormatWriterFactory.getFormatWriter(EdmDataServices.class, Arrays.asList(acceptedMediaTypes), "atom", null);
	    fw.write(uriInfo, sw, metadata);
		final String svcDocString = sw.toString();
		
		outputStream.write(svcDocString.getBytes("UTF-8"));
		outputStream.flush();
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return type.equals(ServiceDocumentResource.class);
	}

	/**
	 * Reads a representation of {@link ServiceDocumentResource} from the input stream.
	 * 
	 * @precondition {@link InputStream} contains a valid service document representation
	 * @postcondition {@link ServiceDocumentResource} will be constructed and returned.
	 * @invariant valid InputStream
	 */
	@Override
	public EntityResource readFrom(Class<RESTResource> type,
			Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		return null;
	}

	protected void setUriInfo(UriInfo uriInfo) {
		this.uriInfo = uriInfo;
	}
}
