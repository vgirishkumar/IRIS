package com.temenos.interaction.core.media.hal;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.odata4j.core.OEntity;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.resource.ResourceTypeHelper;
import com.theoryinpractise.halbuilder.ResourceFactory;
import com.theoryinpractise.halbuilder.spi.ReadableResource;
import com.theoryinpractise.halbuilder.spi.Resource;
import com.theoryinpractise.halbuilder.spi.ResourceException;

@Provider
@Consumes({com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML, com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_JSON})
@Produces({com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML, com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_JSON})
public class HALProvider implements MessageBodyReader<RESTResource>, MessageBodyWriter<RESTResource> {
	private final Logger logger = LoggerFactory.getLogger(HALProvider.class);

	@Context
	private UriInfo uriInfo;
	private EdmDataServices edmDataServices;
	private Metadata metadata = null;
	private ResourceStateMachine hypermediaEngine;
	
	public HALProvider(EdmDataServices edmDataServices, Metadata metadata, ResourceStateMachine hypermediaEngine) {
		this(edmDataServices, metadata);
		this.hypermediaEngine = hypermediaEngine;
	}
	public HALProvider(EdmDataServices edmDataServices, ResourceStateMachine hypermediaEngine) {
		this(edmDataServices);
		this.hypermediaEngine = hypermediaEngine;
	}

	public HALProvider(EdmDataServices edmDataServices, Metadata metadata) {
		this(edmDataServices);
		this.metadata = metadata;
		assert(metadata != null);
	}

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
							Optional.<Predicate<ReadableResource>>absent(), Optional.of(l.getId()), Optional.of(l.getTitle()), Optional.<String>absent());
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
					buildFromBean(propertyMap, entity, entityResource.getEntityName());
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
					String rel = "collection " + cr.getEntityName();
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
					String rel = "collection " + cr.getEntityName();
					// the properties
					Map<String, Object> propertyMap = new HashMap<String, Object>();
					buildFromBean(propertyMap, entity, cr.getEntityName());
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
									Optional.<Predicate<ReadableResource>>absent(), Optional.of(el.getId()), Optional.of(el.getTitle()), Optional.<String>absent());
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
			representation = halResource.renderContent(ResourceFactory.HAL_XML);
		} else if (halResource != null && mediaType.isCompatible(com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_JSON_TYPE)) {
			representation = halResource.renderContent(ResourceFactory.HAL_JSON);
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
			if (ees.getType().findProperty(property.getName()) != null && property.getValue() != null) {
				// call toString on object as a simple why of handling non simple types
				map.put(property.getName(), property.getValue().toString());				
			}
		}
	}
	
	protected void buildFromBean(Map<String, Object> map, Object bean, String entityName) {
		try {
			try {
				// TODO we should look up the entity here, but the entity set is much easier to lookup
				EdmEntitySet ees = edmDataServices.getEdmEntitySet(entityName);
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
					logger.warn("EdmEntitySet not found using bean [" + entityName + "]");
				}
			} catch (RuntimeException re) {
				logger.error("EdmEntitySet not found using bean [" + entityName + "]", re);
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
		
		if (mediaType.isCompatible(com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML_TYPE) 
				|| mediaType.isCompatible(com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_JSON_TYPE)) {
			//Parse hal+json into an OEntity object
			Entity entity = buildEntityFromHal(entityStream);
			return new EntityResource<Entity>(entity);
		} 
		else {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	private Entity buildEntityFromHal(InputStream entityStream) {
		try {
			// create the hal resource
			String baseUri = uriInfo.getBaseUri().toASCIIString();
			ResourceFactory resourceFactory = new ResourceFactory(baseUri);
			ReadableResource halResource = resourceFactory.readResource(new InputStreamReader(entityStream));
			// get the entity name
			String resourcePath = halResource.getResourceLink().getHref();
			if (resourcePath.length() > baseUri.length())
				resourcePath = resourcePath.substring(baseUri.length() - 1);
			String entityName = getEntityName(resourcePath);
			if (metadata == null || metadata.getEntityMetadata(entityName) == null)
				throw new IllegalStateException("Entity metadata could not be found");
			EntityMetadata entityMetadata = metadata.getEntityMetadata(entityName);
			// add properties if they are present on the resolved entity
			EntityProperties entityFields = new EntityProperties();
			Map<String, Optional<Object>> halProperties = halResource.getProperties();
			for (String propName : halProperties.keySet()) {
				if (entityMetadata.getPropertyVocabulary(propName) != null) {
					entityFields.setProperty(new EntityProperty(propName, halProperties.get(propName).get().toString()));
				}
			}
			return new Entity(entityName, entityFields);
		} catch (IllegalStateException e) {
			logger.warn("Malformed request from client", e);
			throw new WebApplicationException(Status.BAD_REQUEST);
		} catch (ResourceException e) {
			logger.warn("Malformed request from client", e);
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
	}
	
	private String getEntityName(String resourcePath) {
		String entityName = null;
		if (resourcePath != null) {
			Map<String, ResourceState> pathToResourceStates = hypermediaEngine.getResourceStatesByPath();
			for (String path : pathToResourceStates.keySet()) {
				ResourceState s = pathToResourceStates.get(path);
				if (s.getPathIdParameter() != null) {
					Matcher matcher = Pattern.compile("(.*)/\\{" + s.getPathIdParameter() + "\\}").matcher(path);
					if (matcher.find()) {
						resourcePath = matcher.group(1);
					}
				}
				if (path.startsWith(resourcePath)) {
					entityName = s.getEntityName();
				}
			}
		}
		return entityName;
	}
	
	/* Ugly testing support :-( */
	protected void setUriInfo(UriInfo uriInfo) {
		this.uriInfo = uriInfo;
	}

}
