package com.temenos.interaction.core.media.hal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OLink;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.jayway.jaxrs.hateoas.HateoasLink;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.resource.ResourceTypeHelper;
import com.theoryinpractise.halbuilder.ResourceFactory;
import com.theoryinpractise.halbuilder.spi.ReadableResource;
import com.theoryinpractise.halbuilder.spi.Resource;

@Provider
@Consumes({com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML, com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_JSON})
@Produces({com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML, com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_JSON})
public class HALProvider implements MessageBodyReader<RESTResource>, MessageBodyWriter<RESTResource> {
	private final Logger logger = LoggerFactory.getLogger(HALProvider.class);

	@Context
	private UriInfo uriInfo;
	private EdmDataServices edmDataServices;

	public HALProvider(EdmDataServices edmDataServices) {
		this.edmDataServices = edmDataServices;
		assert(edmDataServices != null);
	}
	
	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return ResourceTypeHelper.isType(type, genericType, EntityResource.class);
	}

	@Override
	public long getSize(RESTResource t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	/**
	 * Writes a Hypertext Application Language (HAL) representation of
	 * {@link EntityResource} to the output stream.
	 * 
	 * @precondition supplied {@link EntityResource} is non null
	 * @precondition {@link EntityResource#getEntity()} returns a valid OEntity, this 
	 * provider only supports serialising OEntities
	 * @postcondition non null HAL XML document written to OutputStream
	 * @invariant valid OutputStream
	 */
	@Override
	public void writeTo(RESTResource resource, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {
		assert (resource != null);
		logger.debug("Writing " + mediaType);
		
		if (!ResourceTypeHelper.isType(type, genericType, EntityResource.class))
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

		// create the hal resource
		ResourceFactory resourceFactory = new ResourceFactory(uriInfo.getBaseUri().toASCIIString());
		Resource halResource = resourceFactory.newResource("");
		if (resource.getGenericEntity() != null) {
			EntityResource<?> entityResource = (EntityResource<?>) resource.getGenericEntity().getEntity();

			// get the links
			Collection<HateoasLink> links= entityResource.getLinks();
			HateoasLink selfLink = findSelfLink(links);
			
			// build the HAL representation with self link
			if (selfLink != null)
				halResource = resourceFactory.newResource(selfLink.getHref());

			// add our links
			if (links != null) {
				for (HateoasLink l : links) {
					logger.debug("Link: id=[" + l.getId() + "] rel=[" + l.getRel() + "] href=[" + l.getHref() + "]");
					halResource.withLink(l.getHref(), l.getRel(), 
							Optional.<Predicate<ReadableResource>>absent(), Optional.of(l.getId()), Optional.<String>absent(), Optional.<String>absent());
				}
			}
			
			// add contents of supplied entity to the property map
			Map<String, Object> propertyMap = new HashMap<String, Object>();
			if (ResourceTypeHelper.isType(type, genericType, EntityResource.class, OEntity.class)) {
				@SuppressWarnings("unchecked")
				EntityResource<OEntity> oentityResource = (EntityResource<OEntity>) entityResource;
				buildFromOEntity(propertyMap, oentityResource.getEntity());
			} else if (entityResource.getEntity() != null) {
				// regular java bean
//				halResource.withBean(entityResource.getEntity());
			}

			// add properties to HAL resource
			for (String key : propertyMap.keySet()) {
				halResource.withProperty(key, propertyMap.get(key));
			}

		}
				
		String representation = null;
		if (halResource != null && mediaType.equals(com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML_TYPE)) {
			representation = halResource.asRenderableResource().renderContent(ResourceFactory.HAL_XML);
		} else {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		assert(representation != null);
		logger.debug("Produced [" + representation + "]");
		// TODO handle requested encoding?
		entityStream.write(representation.getBytes("UTF-8"));
	}

	protected HateoasLink findSelfLink(Collection<HateoasLink> links) {
		HateoasLink selfLink = null;
		if (links != null) {
			for (HateoasLink l : links) {
				if (l.getRel().equals("self"))
					selfLink = l;
			}
		}
		return selfLink;
	}

	protected void buildFromOEntity(Map<String, Object> map, OEntity entity) {
		for (OProperty<?> property : entity.getProperties()) {
			EdmEntitySet ees = edmDataServices.getEdmEntitySet(entity.getEntityType());
			if (ees.getType().findProperty(property.getName()) != null) {
				map.put(property.getName(), property.getValue());				
			}
		}
	}
	
	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		// this class can only deserialise EntityResource with OEntity.
		return ResourceTypeHelper.isType(type, genericType, EntityResource.class);
	}

	/**
	 * Reads a Hypertext Application Language (HAL) representation of
	 * {@link EntityResource} from the input stream.
	 * 
	 * @precondition {@link InputStream} contains a valid HAL <resource/> document
	 * @postcondition {@link EntityResource} will be constructed and returned.
	 * @invariant valid InputStream
	 */
	@Override
	public RESTResource readFrom(Class<RESTResource> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {

		if (!ResourceTypeHelper.isType(type, genericType, EntityResource.class))
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		
		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader parser = factory.createXMLStreamReader(entityStream);

			EdmEntitySet entitySet = null;
			List<OProperty<?>> properties = null;
			boolean seenResource = false;
			for (int event = parser.next(); 
					event != XMLStreamConstants.END_DOCUMENT; 
					event = parser.next()) {
				switch (event) {
				case XMLStreamConstants.START_ELEMENT:
					String elementName = parser.getLocalName();
					logger.debug("Saw element: " + elementName);
					if ("resource".equals(elementName)) {
						seenResource = true;
					} else if (seenResource && !"links".equals(elementName)) {
						logger.debug("Parsing OEntity: " + elementName);
						entitySet = edmDataServices.getEdmEntitySet(elementName);
						properties = processOEntity(elementName, parser);
					}
				} // end switch
			} // end for loop
			parser.close();
			
			OEntity oEntity = null;
			if (entitySet != null) {
				// TODO figure out if we need to do anything with OEntityKey
				OEntityKey key = OEntityKey.create("");
				oEntity = OEntities.create(entitySet, key, properties, new ArrayList<OLink>());
			} else {
				logger.debug("");
			}
			
			return new EntityResource<OEntity>(oEntity);
		} catch (FactoryConfigurationError e) {
			logger.error("Error while parsing xml", e);
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		} catch (XMLStreamException e) {
			logger.error("Error while parsing xml", e);
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		//throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
	}
	
	protected List<OProperty<?>> processOEntity(String entityName, XMLStreamReader parser)
			throws XMLStreamException {
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		for (int event = parser.next(); event != XMLStreamConstants.END_DOCUMENT; event = parser
				.next()) {
			switch (event) {
			case XMLStreamConstants.END_ELEMENT:
				if (entityName.equals(parser.getLocalName())) {
					return properties;
				}
			case XMLStreamConstants.START_ELEMENT:
				String elementName = parser.getLocalName();
				if (!elementName.equals(entityName)) {
					String text = parser.getElementText();
					logger.debug("Processing OEntity: " + elementName + ", " + text);
					properties.add(OProperties.string(elementName, text));
				}
			} // end switch
		} // end for loop

		return properties;
	}
	
	/* Ugly testing support :-( */
	protected void setUriInfo(UriInfo uriInfo) {
		this.uriInfo = uriInfo;
	}

}
