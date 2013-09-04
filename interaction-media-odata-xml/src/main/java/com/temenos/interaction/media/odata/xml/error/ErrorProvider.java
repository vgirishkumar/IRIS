package com.temenos.interaction.media.odata.xml.error;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.entity.GenericError;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.resource.ResourceTypeHelper;

/**
 * JAX-RS Provider class for marshalling errors as OData errors.
 * 
 * http://msdn.microsoft.com/en-gb/library/dd541497.aspx
 */
@Provider
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML})
public class ErrorProvider implements MessageBodyWriter<RESTResource> {
	private final Logger logger = LoggerFactory.getLogger(ErrorProvider.class);

	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return ResourceTypeHelper.isType(type, genericType, EntityResource.class, GenericError.class);
	}

	@Override
	public long getSize(RESTResource t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	/**
	 * Writes representation of {@link GenericError} to the output stream.
	 * 
	 * @precondition supplied {@link GenericError} is non null
	 * @postcondition non null meta data document written to OutputStream
	 * @invariant valid OutputStream
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void writeTo(RESTResource resource, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream outputStream) throws IOException,
			WebApplicationException {
		assert (resource != null);

		//Set response headers
		if(httpHeaders != null) {
			httpHeaders.putSingle(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML);
		}
		
		final String errorResponse;
		if(ResourceTypeHelper.isType(type, genericType, EntityResource.class, GenericError.class)) {
			EntityResource<GenericError> errorResource = (EntityResource<GenericError>) resource;
			StringWriter sw = new StringWriter();
			ErrorWriter.write(errorResource.getEntity(), sw);
			errorResponse = sw.toString();
		}
		else {
			logger.error("JAX-RS provider for OData errors does not support resources of type " + type.toString() + " / " + genericType.toString());
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		outputStream.write(errorResponse.getBytes("UTF-8"));
		outputStream.flush();
	}
}
