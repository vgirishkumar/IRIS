package com.temenos.interaction.core.media.hal;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
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
import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.resource.CollectionResource;
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
		return ResourceTypeHelper.isType(type, genericType, EntityResource.class)
				|| ResourceTypeHelper.isType(type, genericType, CollectionResource.class);
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
		
		if (!ResourceTypeHelper.isType(type, genericType, EntityResource.class)
				&& !ResourceTypeHelper.isType(type, genericType, CollectionResource.class))
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

		// create the hal resource
		ResourceFactory resourceFactory = new ResourceFactory(uriInfo.getBaseUri().toASCIIString());
		Resource halResource = resourceFactory.newResource("");
		if (resource.getGenericEntity() != null) {
			RESTResource rResource = (RESTResource) resource.getGenericEntity().getEntity();

			// get the links
			Collection<Link> links = rResource.getLinks();
			Link selfLink = findSelfLink(links);
			
			// build the HAL representation with self link
			if (selfLink != null)
				halResource = resourceFactory.newResource(selfLink.getHref());

			// add our links
			if (links != null) {
				for (Link l : links) {
					logger.debug("Link: id=[" + l.getId() + "] rel=[" + l.getRel() + "] method=[" + l.getMethod() + "] href=[" + l.getHref() + "]");
					String href = l.getHref();
					// TODO add support for 'method' to HAL link.  this little hack passes the method in the href '[method] [href]'
					if (l.getMethod() != null && !l.getMethod().equals("GET")) {
						href = l.getMethod() + " " + href;
					}
					halResource.withLink(href, l.getRel(), 
							Optional.<Predicate<ReadableResource>>absent(), Optional.of(l.getId()), Optional.<String>absent(), Optional.<String>absent());
				}
			}
			
			// add contents of supplied entity to the property map
			if (ResourceTypeHelper.isType(type, genericType, EntityResource.class, OEntity.class)) {
				@SuppressWarnings("unchecked")
				EntityResource<OEntity> oentityResource = (EntityResource<OEntity>) resource;
				Map<String, Object> propertyMap = new HashMap<String, Object>();
				buildFromOEntity(propertyMap, oentityResource.getEntity());
				// add properties to HAL resource
				for (String key : propertyMap.keySet()) {
					halResource.withProperty(key, propertyMap.get(key));
				}
			} else if (ResourceTypeHelper.isType(type, genericType, EntityResource.class)) {
				EntityResource<?> entityResource = (EntityResource<?>) resource;
				Object entity = entityResource.getEntity();
				if (entity != null) {
					/*
					 * // regular java bean
					 * halResource.withBean(entity);
					 */
					// java bean, now limited to just the properties specified in the metadata entity model
					Map<String, Object> propertyMap = new HashMap<String, Object>();
					buildFromBean(propertyMap, entity);
					for (String key : propertyMap.keySet()) {
						halResource.withProperty(key, propertyMap.get(key));
					}
				}
			} else if(ResourceTypeHelper.isType(type, genericType, CollectionResource.class, OEntity.class)) {
				@SuppressWarnings("unchecked")
				CollectionResource<OEntity> cr = (CollectionResource<OEntity>) resource;
				List<EntityResource<OEntity>> entities = (List<EntityResource<OEntity>>) cr.getEntities();
				for (EntityResource<OEntity> er : entities) {
					OEntity entity = er.getEntity();
					// the subresource is a collection
					String rel = "collection " + cr.getEntitySetName();
					// the properties
					Map<String, Object> propertyMap = new HashMap<String, Object>();
					buildFromOEntity(propertyMap, entity);
					// create hal resource and add link for self
					Link itemSelfLink = findSelfLink(er.getLinks());
					if (itemSelfLink != null) {
						Resource subResource = resourceFactory.newResource(itemSelfLink.getHref());
						for (Link el : er.getLinks()) {
							String itemHref = el.getHref();
							// TODO add support for 'method' to HAL link.  this little hack passes the method in the href '[method] [href]'
							if (el.getMethod() != null && !el.getMethod().equals("GET")) {
								itemHref = el.getMethod() + " " + itemHref;
							}
							subResource.withLink(itemHref, el.getRel());
						}
						// add properties to HAL sub resource
						for (String key : propertyMap.keySet()) {
							subResource.withProperty(key, propertyMap.get(key));
						}
						halResource.withSubresource(rel, subResource);
					}
					
				}
			} else if (ResourceTypeHelper.isType(type, genericType, CollectionResource.class)) {
				@SuppressWarnings("unchecked")
				CollectionResource<Object> cr = (CollectionResource<Object>) resource;
				List<EntityResource<Object>> entities = (List<EntityResource<Object>>) cr.getEntities();
				for (EntityResource<Object> er : entities) {
					Object entity = er.getEntity();
					// the subresource is part of a collection (maybe this link rel should be an 'item')
					String rel = "collection " + cr.getEntitySetName();
					// the properties
					Map<String, Object> propertyMap = new HashMap<String, Object>();
					buildFromBean(propertyMap, entity);
					// create hal resource and add link for self
					Link itemSelfLink = findSelfLink(er.getLinks());
					if (itemSelfLink != null) {
						Resource subResource = resourceFactory.newResource(itemSelfLink.getHref());
						for (Link el : er.getLinks()) {
							String itemHref = el.getHref();
							// TODO add support for 'method' to HAL link.  this little hack passes the method in the href '[method] [href]'
							if (el.getMethod() != null && !el.getMethod().equals("GET")) {
								itemHref = el.getMethod() + " " + itemHref;
							}
							subResource.withLink(itemHref, el.getRel(), 
									Optional.<Predicate<ReadableResource>>absent(), Optional.of(el.getId()), Optional.<String>absent(), Optional.<String>absent());
						}
						// add properties to HAL sub resource
						for (String key : propertyMap.keySet()) {
							subResource.withProperty(key, propertyMap.get(key));
						}
						halResource.withSubresource(rel, subResource);
					}
					
				}
				
			} else {
				logger.error("Accepted object for writing in isWriteable, but type not supported in writeTo method");
				throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
			}

		}
				
		String representation = null;
		if (halResource != null && mediaType.isCompatible(com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML_TYPE)) {
			representation = halResource.asRenderableResource().renderContent(ResourceFactory.HAL_XML);
		} else if (halResource != null && mediaType.isCompatible(com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_JSON_TYPE)) {
			representation = halResource.asRenderableResource().renderContent(ResourceFactory.HAL_JSON);
		} else {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		assert(representation != null);
		logger.debug("Produced [" + representation + "]");
		// TODO handle requested encoding?
		entityStream.write(representation.getBytes("UTF-8"));
	}

	protected Link findSelfLink(Collection<Link> links) {
		Link selfLink = null;
		if (links != null) {
			for (Link l : links) {
				if (l.getRel().equals("self")) {
					selfLink = l;
					break;
				}
			}
		}
		return selfLink;
	}

	protected void buildFromOEntity(Map<String, Object> map, OEntity entity) {
		for (OProperty<?> property : entity.getProperties()) {
			EdmEntitySet ees = edmDataServices.getEdmEntitySet(entity.getEntitySetName());
			if (ees.getType().findProperty(property.getName()) != null) {
				map.put(property.getName(), property.getValue());				
			}
		}
	}
	
	protected void buildFromBean(Map<String, Object> map, Object bean) {
		try {
			// TODO we should look up the entity here, but the entity set is much easier to lookup
			String beanName = bean.getClass().getSimpleName(); 
			try {
				EdmEntitySet ees = edmDataServices.getEdmEntitySet(beanName);
				if (ees != null) {
					BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
					for (PropertyDescriptor propertyDesc : beanInfo.getPropertyDescriptors()) {
					    String propertyName = propertyDesc.getName();
						if (ees.getType().findProperty(propertyName) != null) {
						    Object value = propertyDesc.getReadMethod().invoke(bean);
							map.put(propertyName, value);				
						}
					}
				} else {
					logger.warn("EdmEntitySet not found using bean [" + beanName + "]");
				}
			} catch (RuntimeException re) {
				logger.error("EdmEntitySet not found using bean [" + beanName + "]", re);
			}
		} catch (IllegalArgumentException e) {
			logger.error("Error accessing bean property", e);
		} catch (IntrospectionException e) {
			logger.error("Error accessing bean property", e);
		} catch (IllegalAccessException e) {
			logger.error("Error accessing bean property", e);
		} catch (InvocationTargetException e) {
			logger.error("Error accessing bean property", e);
		}
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		// this class can only deserialise EntityResource
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
		
		if (mediaType.isCompatible(com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML_TYPE)) {
			// TODO implement this properly
		} else if (mediaType.isCompatible(com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_JSON_TYPE)) {
			// TODO implement this
			return new EntityResource<Object>(null);
		} else {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
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
