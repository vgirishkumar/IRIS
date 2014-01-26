package com.temenos.interaction.media.odata.xml.atom;

/*
 * #%L
 * interaction-media-odata-xml
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OLink;
import org.odata4j.core.OLinks;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.exceptions.NotFoundException;
import org.odata4j.exceptions.ODataProducerException;
import org.odata4j.format.Entry;
import org.odata4j.format.xml.AtomEntryFormatParserExt;
import org.odata4j.internal.InternalUtil;
import org.odata4j.producer.Responses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.BeanTransformer;
import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.Event;
import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.hypermedia.Transformer;
import com.temenos.interaction.core.hypermedia.Transition;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.resource.ResourceTypeHelper;
import com.temenos.interaction.core.web.RequestContext;

@Provider
@Consumes({MediaType.APPLICATION_ATOM_XML})
@Produces({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_XML})
public class AtomXMLProvider implements MessageBodyReader<RESTResource>, MessageBodyWriter<RESTResource> {
	private final Logger logger = LoggerFactory.getLogger(AtomXMLProvider.class);
	private final static Pattern STRING_KEY_RESOURCE_PATTERN = Pattern.compile("(\\('.*'\\))");

	@Context
	private UriInfo uriInfo;
	@Context
	private Request requestContext;
	private AtomEntryFormatWriter entryWriter;
	private AtomFeedFormatWriter feedWriter;
	
	private final EdmDataServices edmDataServices;
	private final Metadata metadata;
	private final ResourceStateMachine hypermediaEngine;
	private final Transformer transformer;
	private final LinkInterceptor linkInterceptor = new ODataLinkInterceptor(this);

	/**
	 * Construct the jax-rs Provider for OData media type.
	 * @param edmDataServices
	 * 		The entity metadata for reading and writing OData entities.
	 * @param metadata
	 * 		The entity metadata for reading and writing Entity entities.
	 * @param hypermediaEngine
	 * 		The hypermedia engine contains all the resource to entity mappings
	 * @param transformer
	 * 		Transformer to convert an entity to a properties map
	 */
	public AtomXMLProvider(EdmDataServices edmDataServices, Metadata metadata, ResourceStateMachine hypermediaEngine, Transformer transformer) {
		this.edmDataServices = edmDataServices;
		this.metadata = metadata;
		this.hypermediaEngine = hypermediaEngine;
		assert(edmDataServices != null);
		assert(metadata != null);
		assert(hypermediaEngine != null);
		this.transformer = transformer;
		entryWriter = new AtomEntryFormatWriter();
		feedWriter = new AtomFeedFormatWriter();
	}
	
	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return 	ResourceTypeHelper.isType(type, genericType, EntityResource.class) ||
				ResourceTypeHelper.isType(type, genericType, CollectionResource.class, OEntity.class) ||
				ResourceTypeHelper.isType(type, genericType, CollectionResource.class, Entity.class);
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
		
		//Set response headers
		if(httpHeaders != null) {
			httpHeaders.putSingle(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_ATOM_XML);		//Workaround for https://issues.apache.org/jira/browse/WINK-374
		}
		  		
		try {
			RESTResource restResource = processLinks((RESTResource) resource);
			Collection<Link> processedLinks = restResource.getLinks();
			if(ResourceTypeHelper.isType(type, genericType, EntityResource.class, OEntity.class)) {
				EntityResource<OEntity> entityResource = (EntityResource<OEntity>) resource;
				OEntity tempEntity = entityResource.getEntity();
				String fqName = metadata.getModelName() + Metadata.MODEL_SUFFIX + "." + entityResource.getEntityName();
				EdmEntityType entityType = (EdmEntityType) edmDataServices.findEdmEntityType(fqName);
				EdmEntitySet entitySet = edmDataServices.getEdmEntitySet(entityType);
				List<OLink> olinks = formOLinks(entityResource);
				//Write entry
	        	// create OEntity with our EdmEntitySet see issue https://github.com/aphethean/IRIS/issues/20
            	OEntity oentity = OEntities.create(entitySet, tempEntity.getEntityKey(), tempEntity.getProperties(), null);
				entryWriter.write(uriInfo, new OutputStreamWriter(entityStream, "UTF-8"), Responses.entity(oentity), entitySet, olinks);
			} else if(ResourceTypeHelper.isType(type, genericType, EntityResource.class, Entity.class)) {
				EntityResource<Entity> entityResource = (EntityResource<Entity>) resource;
				//Write entry
				Entity entity = entityResource.getEntity();
				String entityName = entityResource.getEntityName();
				EntityMetadata entityMetadata = metadata.getEntityMetadata((entityResource.getEntityName() == null ? entity.getName() : entityResource.getEntityName()));
				// Write Entity object with Abdera implementation
				AtomEntityEntryFormatWriter entityEntryWriter = new AtomEntityEntryFormatWriter();
				entityEntryWriter.write(uriInfo, new OutputStreamWriter(entityStream, "UTF-8"), entityName, entity, entityMetadata, processedLinks, entityResource.getEmbedded(), metadata.getModelName());
			} else if(ResourceTypeHelper.isType(type, genericType, EntityResource.class)) {
				EntityResource<Object> entityResource = (EntityResource<Object>) resource;
				//Links and entity properties
				Object entity = entityResource.getEntity();
				String entityName = entityResource.getEntityName();
				EntityProperties props = new EntityProperties();
				if(entity != null) {
					Map<String, Object> objProps = (transformer != null ? transformer : new BeanTransformer()).transform(entity);
					if (objProps != null) {
						for(String propName : objProps.keySet()) {
							props.setProperty(new EntityProperty(propName, objProps.get(propName)));
						}
					}
				}
				EntityMetadata entityMetadata = metadata.getEntityMetadata(entityName);
				AtomEntityEntryFormatWriter entityEntryWriter = new AtomEntityEntryFormatWriter();
				entityEntryWriter.write(uriInfo, new OutputStreamWriter(entityStream, "UTF-8"), entityName, new Entity(entityName, props), entityMetadata, processedLinks, entityResource.getEmbedded(), metadata.getModelName());
			} else if(ResourceTypeHelper.isType(type, genericType, CollectionResource.class, OEntity.class)) {
				CollectionResource<OEntity> collectionResource = ((CollectionResource<OEntity>) resource);
				String fqName = metadata.getModelName() + Metadata.MODEL_SUFFIX + "." + collectionResource.getEntityName();
				EdmEntityType entityType = (EdmEntityType) edmDataServices.findEdmEntityType(fqName);
				EdmEntitySet entitySet = edmDataServices.getEdmEntitySet(entityType);
				List<EntityResource<OEntity>> collectionEntities = (List<EntityResource<OEntity>>) collectionResource.getEntities();
				List<OEntity> entities = new ArrayList<OEntity>();
				for (EntityResource<OEntity> collectionEntity : collectionEntities) {
					processLinks(collectionEntity);
		        	// create OEntity with our EdmEntitySet see issue https://github.com/aphethean/IRIS/issues/20
					OEntity tempEntity = collectionEntity.getEntity();
					List<OLink> olinks = formOLinks(collectionEntity);
	            	OEntity entity = OEntities.create(entitySet, tempEntity.getEntityKey(), tempEntity.getProperties(), olinks);
					entities.add(entity);
				}
				// TODO implement collection properties and get transient values for inlinecount and skiptoken
				Integer inlineCount = null;
				String skipToken = null;
				feedWriter.write(uriInfo, new OutputStreamWriter(entityStream, "UTF-8"), processedLinks, Responses.entities(entities, entitySet, inlineCount, skipToken), metadata.getModelName());
			} else if(ResourceTypeHelper.isType(type, genericType, CollectionResource.class, Entity.class)) {
				CollectionResource<Entity> collectionResource = ((CollectionResource<Entity>) resource);
				
				// TODO implement collection properties and get transient values for inlinecount and skiptoken
				Integer inlineCount = null;
				String skipToken = null;
				//Write feed
				EntityMetadata entityMetadata = metadata.getEntityMetadata(collectionResource.getEntityName());
				AtomEntityFeedFormatWriter entityFeedWriter = new AtomEntityFeedFormatWriter();
				entityFeedWriter.write(uriInfo, new OutputStreamWriter(entityStream, "UTF-8"), collectionResource, entityMetadata, inlineCount, skipToken, metadata.getModelName());
			} else {
				logger.error("Accepted object for writing in isWriteable, but type not supported in writeTo method");
				throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
			}
		} catch (ODataProducerException e) {
			logger.error("An error occurred while writing " + mediaType + " resource representation", e);
		}
	}
	
	public RESTResource processLinks(RESTResource restResource) {
		Collection<Link> linksCollection = restResource.getLinks();
		List<Link> processedLinks = new ArrayList<Link>();
		if (linksCollection != null) {
			for (Link linkToAdd : linksCollection) {
				Link link = linkInterceptor.addingLink(restResource, linkToAdd);
				if (link != null) {
					processedLinks.add(link);
				}
			}
		}
		restResource.setLinks(processedLinks);
		return restResource;
	}
	
	public List<OLink> formOLinks(EntityResource<OEntity> entityResource) {
		// Create embedded resources from $expand
		addExpandedLinks(entityResource);
		
		//Add entity links
		List<OLink> olinks = new ArrayList<OLink>();
		if (entityResource.getLinks() != null && entityResource.getLinks().size() > 0) {
			for(Link link : entityResource.getLinks()) {
				addLinkToOLinks(olinks, link, entityResource);
			}		
		}
		
		return olinks;
	}
	
	/*
	 * Using the supplied EntityResource, add the embedded resources
	 * from the OEntity embedded resources.  NB - only an OEntity can
	 * carry OLinks.
	 */
	public void addExpandedLinks(EntityResource<OEntity> entityResource) {
		RequestContext requestContext = RequestContext.getRequestContext();
		Collection<Link> links = entityResource.getLinks();
		if (links != null) {
			OEntity oentity = entityResource.getEntity();
			List<OLink> olinks = oentity.getLinks();
			for (OLink olink : olinks) {
				if (olink.isInline()) {
					String relid = InternalUtil.getEntityRelId(oentity);
					String href = relid + "/" + olink.getTitle();
					for (Link link : links) {
						String linkHref = link.getHref();
						if(requestContext != null) {
							//Extract the transition fragment from the URI path
							linkHref = link.getHrefTransition(requestContext.getBasePath());
						}
						if (href.equals(linkHref)) {
							if (entityResource.getEmbedded() == null) {
								entityResource.setEmbedded(new HashMap<Transition, RESTResource>());
							}
							if (olink.isCollection()) {
								List<OEntity> oentities = olink.getRelatedEntities();
								Collection<EntityResource<OEntity>> entityResources = new ArrayList<EntityResource<OEntity>>();
								for (OEntity oe : oentities) {
									entityResources.add(new EntityResource<OEntity>(oe));
								}
								entityResource.getEmbedded().put(link.getTransition(), new CollectionResource<OEntity>(entityResources));
							} else {
								// replace the OLink's on the current entity
								OEntity inlineOentity = olink.getRelatedEntity();
								List<OLink> inlineResourceOlinks = formOLinks(new EntityResource<OEntity>(inlineOentity));
				            	OEntity newInlineOentity = OEntities.create(inlineOentity.getEntitySet(), inlineOentity.getEntityKey(), inlineOentity.getProperties(), inlineResourceOlinks);
								entityResource.getEmbedded().put(link.getTransition(), new EntityResource<OEntity>(newInlineOentity));
							}
						}
					}
				}
			}
		}
	}

	private void addLinkToOLinks(List<OLink> olinks, Link link, RESTResource resource) {
		RequestContext requestContext = RequestContext.getRequestContext();
		assert(link != null);
		assert(link.getTransition() != null);
		Map<Transition,RESTResource> embeddedResources = resource.getEmbedded();
		String rel = link.getRel();
		String href = link.getHref();
		if(requestContext != null) {
			//Extract the transition fragment from the URI path
			href = link.getHrefTransition(requestContext.getBasePath());
		}
		String title = link.getTitle();
		OLink olink;
		Transition linkTransition = link.getTransition();
		if(linkTransition != null && linkTransition.getTarget() instanceof CollectionResourceState) {
			olink = OLinks.relatedEntities(rel, title, href);
		} else if (embeddedResources != null && embeddedResources.get(linkTransition) != null
				&& embeddedResources.get(linkTransition) instanceof EntityResource) {
			@SuppressWarnings("unchecked")
			EntityResource<OEntity> embeddedResource = (EntityResource<OEntity>) embeddedResources.get(linkTransition);
			List<OLink> embeddedLinks = formOLinks(embeddedResource);
			OEntity embeddedEntity = embeddedResource.getEntity();
			// replace the OLink's on the embedded entity
			OEntity newEmbeddedEntity = OEntities.create(embeddedEntity.getEntitySet(), embeddedEntity.getEntityKey(), embeddedEntity.getProperties(), embeddedLinks);
			olink = OLinks.relatedEntityInline(rel, title, href, newEmbeddedEntity);
		} else {
			olink = OLinks.relatedEntity(rel, title, href);
		}
		olinks.add(olink);
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		// this class can only deserialise EntityResource with OEntity
		return ResourceTypeHelper.isType(type, genericType, EntityResource.class);
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
		
		// check media type can be handled, isReadable must have been called
		assert(ResourceTypeHelper.isType(type, genericType, EntityResource.class));
		assert(mediaType.isCompatible(MediaType.APPLICATION_ATOM_XML_TYPE));
		
		try {
			OEntityKey entityKey = null;

			// work out the entity name using resource path from UriInfo
			String baseUri = uriInfo.getBaseUri().toASCIIString();
			String resourcePath = uriInfo.getPath();
			logger.info("Reading atom xml content for [" + resourcePath + "]");
			if (resourcePath == null)
				throw new IllegalStateException("No resource found");
			// trim the baseuri
			if (resourcePath.length() > baseUri.length() && resourcePath.startsWith(baseUri))
				resourcePath = resourcePath.substring(baseUri.length() - 1);
			/*
			 * add a leading '/' if it needs it (when defining resources we must use a 
			 * full path, but requests can be relative, i.e. without a '/'
			 */
			if (!resourcePath.startsWith("/")) {
				resourcePath = "/" + resourcePath;
			}
			ResourceState currentState = getCurrentState(resourcePath);
			if (currentState == null)
				throw new IllegalStateException("No state found");
			String pathIdParameter = getPathIdParameter(currentState);
			MultivaluedMap<String, String> pathParameters = uriInfo.getPathParameters();
			if (pathParameters != null && pathParameters.getFirst(pathIdParameter) != null) {
				if (STRING_KEY_RESOURCE_PATTERN.matcher(resourcePath).find()) {
					entityKey = OEntityKey.create(pathParameters.getFirst(pathIdParameter));				
				} else {
					entityKey = OEntityKey.parse(pathParameters.getFirst(pathIdParameter));				
				}
			}
			
			if (currentState.getEntityName() == null) {
				throw new IllegalStateException("Entity name could not be determined");
			}
			
			/*
			 *  get the entity set name using the metadata
			 */
			String entitySetName = getEntitySet(currentState);
			
			// Check contents of the stream, if empty or null then return empty resource
			InputStream verifiedStream = verifyContentReceieved(entityStream);
			if (verifiedStream == null) {
				return new EntityResource<OEntity>(); 
			}
			
			// Lets parse the request content
			Reader reader = new InputStreamReader(verifiedStream);
			assert(entitySetName != null) : "Must have found a resource or thrown exception";
			Entry e = new AtomEntryFormatParserExt(edmDataServices, entitySetName, entityKey, null).parse(reader);
			
			return new EntityResource<OEntity>(e.getEntity());
		} catch (IllegalStateException e) {
			logger.warn("Malformed request from client", e);
			throw new WebApplicationException(Status.BAD_REQUEST);
		}

	}

	/*
	 * Find the entity set name for this resource
	 */
	public String getEntitySet(ResourceState state) {
		String entitySetName = null;
		String fqTargetEntityName = metadata.getModelName() + Metadata.MODEL_SUFFIX + "." + state.getEntityName();
		try {
			EdmEntityType targetEntityType = (EdmEntityType) edmDataServices.findEdmEntityType(fqTargetEntityName);
			EdmEntitySet targetEntitySet = edmDataServices.getEdmEntitySet(targetEntityType);
			if (targetEntitySet != null)
				entitySetName = targetEntitySet.getName();
		} catch (NotFoundException nfe) {
			logger.debug("Entity [" + fqTargetEntityName + "] is not an entity set.");
		}
		if (entitySetName == null) {
			entitySetName = state.getName();		
		}
		return entitySetName;
	}
	
	private ResourceState getCurrentState(String resourcePath) {
		ResourceState state = null;
		if (resourcePath != null) {
			MultivaluedMap<String, String> pathParameters = uriInfo.getPathParameters();
			if (pathParameters != null) {
				for (String key : pathParameters.keySet()) {
					List<String> values = pathParameters.get(key);
					for (String value : values) {
						resourcePath = resourcePath.replace(value, "{" + key + "}");
					}
				}
			}
			String httpMethod = requestContext.getMethod();
			Event event = new Event(httpMethod, httpMethod);
			state = hypermediaEngine.determineState(event, resourcePath);
			if (state == null) {
				logger.error("No state found, dropping back to path matching");
				Map<String, Set<ResourceState>> pathToResourceStates = hypermediaEngine.getResourceStatesByPath();
				for (String path : pathToResourceStates.keySet()) {
					for (ResourceState s : pathToResourceStates.get(path)) {
						String pattern = null;
						if (s instanceof CollectionResourceState) {
							pattern = resourcePath + "(|\\(\\))";
							Matcher matcher = Pattern.compile(pattern).matcher(path);
							if (matcher.matches()) {
								state = s;
							}
						}
					}
				}
			}
		}
		return state;
	}

	/*
	 * For a given resource state, get the path parameter used for the id.
	 * @param state
	 * @return
	 */
	private String getPathIdParameter(ResourceState state) {
		String pathIdParameter = InteractionContext.DEFAULT_ID_PATH_ELEMENT;
		if (state.getPathIdParameter() != null) {
			pathIdParameter = state.getPathIdParameter();
		}
		return pathIdParameter;
	}

	
	/* Ugly testing support :-( */
	protected void setUriInfo(UriInfo uriInfo) {
		this.uriInfo = uriInfo;
	}
	protected void setRequestContext(Request request) {
		this.requestContext = request;
	}

	/**
	 * Method to verify if receieved stream has content or its empty
	 * @param stream Stream to check
	 * @return verified stream
	 * @throws IOException
	 */
	private InputStream verifyContentReceieved(InputStream stream) throws IOException {

		if (stream == null) {					// Check if its null
			logger.debug("Request stream received as null");
			return null;
		} else if (stream.markSupported()) {	// Check stream supports mark/reset
			// mark() and read the first byte just to check
			stream.mark(1);
			final int bytesRead = stream.read(new byte[1]);
			if (bytesRead != -1) {
			    //stream not empty
				stream.reset();					// reset the stream as if untouched
				return stream;
			} else {
			    //stream empty
				logger.debug("Request received with empty body");
				return null;
			}
		} else {
			// Panic! this stream does not support mark/reset, try with PushbackInputStream as a last resort
			int bytesRead;
			PushbackInputStream pbs = new PushbackInputStream(stream);
			if ((bytesRead = pbs.read()) != -1) {
				// Contents detected, unread and return
				pbs.unread(bytesRead);
				return pbs;
			} else {
				// Empty stream detected
				logger.debug("Request received with empty body!");
				return null;
			}
		}
	}
	
}
