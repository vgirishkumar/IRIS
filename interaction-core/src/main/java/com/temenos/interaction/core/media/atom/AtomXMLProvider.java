package com.temenos.interaction.core.media.atom;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
import org.odata4j.format.Entry;
import org.odata4j.format.xml.AtomEntryFormatParser;
import org.odata4j.format.xml.XmlFormatWriter;
import org.odata4j.internal.InternalUtil;
import org.odata4j.producer.Responses;
import org.odata4j.producer.exceptions.ODataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.hypermedia.ResourceRegistry;
import com.temenos.interaction.core.hypermedia.Transformer;
import com.temenos.interaction.core.hypermedia.Transition;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.resource.ResourceTypeHelper;
import com.temenos.interaction.core.rim.ResourceInteractionModel;
import com.temenos.interaction.core.web.RequestContext;

@Provider
@Consumes({MediaType.APPLICATION_ATOM_XML})
@Produces({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_XML})
public class AtomXMLProvider implements MessageBodyReader<RESTResource>, MessageBodyWriter<RESTResource> {
	private final Logger logger = LoggerFactory.getLogger(AtomXMLProvider.class);
	private final static Pattern RESOURCE_PATTERN = Pattern.compile("(.*)/(.+)");
	
	@Context
	private UriInfo uriInfo;
	private AtomEntryFormatWriter entryWriter = new AtomEntryFormatWriter();
	private AtomFeedFormatWriter feedWriter = new AtomFeedFormatWriter();
	
	private final EdmDataServices edmDataServices;
	private final Metadata metadata;
	private final ResourceRegistry resourceRegistry;
//	private final Transformer transformer;

	/**
	 * Construct the jax-rs Provider for OData media type.
	 * @param edmDataServices
	 * 		The entity metadata for reading and writing OData entities.
	 * @param metadata
	 * 		The entity metadata for reading and writing Entity entities.
	 * @param resourceRegistry
	 * 		The resource registry contains all the resource to entity mappings
	 * @param transformer
	 * 		Transformer to convert an entity to a properties map
	 */
	public AtomXMLProvider(EdmDataServices edmDataServices, Metadata metadata, ResourceRegistry resourceRegistry, Transformer transformer) {
		this.edmDataServices = edmDataServices;
		this.metadata = metadata;
		this.resourceRegistry = resourceRegistry;
//		this.transformer = transformer;
		assert(edmDataServices != null);
		assert(metadata != null);
		assert(resourceRegistry != null);
//		assert(transformer != null);
	}
	
	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return ResourceTypeHelper.isType(type, genericType, EntityResource.class, OEntity.class) ||
				ResourceTypeHelper.isType(type, genericType, EntityResource.class, Entity.class) ||
				ResourceTypeHelper.isType(type, genericType, CollectionResource.class, OEntity.class);
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
		
		try {
			if(ResourceTypeHelper.isType(type, genericType, EntityResource.class, OEntity.class)) {
				EntityResource<OEntity> entityResource = (EntityResource<OEntity>) resource;

				//Convert Links to list of OLink
				List<OLink> olinks = new ArrayList<OLink>();
				for(Link link : entityResource.getLinks()) {
					addLinkToOLinks(olinks, link);
				}
				
				//Write entry
				OEntity tempEntity = entityResource.getEntity();
				EdmEntitySet entitySet = edmDataServices.getEdmEntitySet((entityResource.getEntityName() == null ? tempEntity.getEntitySetName() : entityResource.getEntityName()));
	        	// create OEntity with our EdmEntitySet see issue https://github.com/aphethean/IRIS/issues/20
            	OEntity oentity = OEntities.create(entitySet, tempEntity.getEntityKey(), tempEntity.getProperties(), null);
				entryWriter.write(uriInfo, new OutputStreamWriter(entityStream, "UTF-8"), Responses.entity(oentity), entitySet, olinks);
			} else if(ResourceTypeHelper.isType(type, genericType, EntityResource.class, Entity.class)) {
				EntityResource<Entity> entityResource = (EntityResource<Entity>) resource;

				Collection<Link> linksCollection = entityResource.getLinks();
				List<Link> links = Lists.newArrayList(linksCollection);
				
				//Write entry
				Entity entity = entityResource.getEntity();
				EntityMetadata entityMetadata = metadata.getEntityMetadata((entityResource.getEntityName() == null ? entity.getName() : entityResource.getEntityName()));
				// Write Entity object with Abdera implementation
				AtomEntityEntryFormatWriter entityEntryWriter = new AtomEntityEntryFormatWriter();
				entityEntryWriter.write(uriInfo, new OutputStreamWriter(entityStream, "UTF-8"), entity, entityMetadata, links);
			} else if(ResourceTypeHelper.isType(type, genericType, CollectionResource.class, OEntity.class)) {
				CollectionResource<OEntity> collectionResource = ((CollectionResource<OEntity>) resource);
				List<EntityResource<OEntity>> collectionEntities = (List<EntityResource<OEntity>>) collectionResource.getEntities();
				List<OEntity> entities = new ArrayList<OEntity>();
				Map<String, List<OLink>> entityOlinks = new HashMap<String, List<OLink>>();
				for (EntityResource<OEntity> collectionEntity : collectionEntities) {
		        	// create OEntity with our EdmEntitySet see issue https://github.com/aphethean/IRIS/issues/20
					OEntity tempEntity = collectionEntity.getEntity();
					EdmEntitySet entitySet = edmDataServices.getEdmEntitySet((collectionEntity.getEntityName() == null ? tempEntity.getEntitySetName() : collectionEntity.getEntityName()));
	            	OEntity entity = OEntities.create(entitySet, tempEntity.getEntityKey(), tempEntity.getProperties(), null);
					
					//Add entity links
					List<OLink> olinks = new ArrayList<OLink>();
					if (collectionEntity.getLinks() != null) {
						for(Link link : collectionEntity.getLinks()) {
							addLinkToOLinks(olinks, link);		//Link to resource (feed entry) 		
							
							/*
							 * TODO we can remove this way of adding links to other resources once we support multiple transitions 
							 * to a resource state.  https://github.com/aphethean/IRIS/issues/17
							//Links to other resources
					        List<Transition> entityTransitions = resourceRegistry.getEntityTransitions(entity.getEntitySetName());
					        if(entityTransitions != null) {
						        for(Transition transition : entityTransitions) {
						        	//Create Link from transition
									String rel = transition.getTarget().getName();
									UriBuilder linkTemplate = UriBuilder.fromUri(RequestContext.getRequestContext().getBasePath()).path(transition.getCommand().getPath());
									Map<String, Object> properties = new HashMap<String, Object>();
									properties.putAll(transformer.transform(entity));
									URI href = linkTemplate.buildFromMap(properties);
									Link entityLink = new Link(transition, rel, href.toASCIIString(), "GET");
									
									addLinkToOLinks(olinks, entityLink);
								}
					        }
							 */
						}		
					}
					entityOlinks.put(InternalUtil.getEntityRelId(entity), olinks);					
					entities.add(entity);
				}
				EdmEntitySet entitySet = edmDataServices.getEdmEntitySet((collectionResource.getEntityName() == null ? collectionResource.getEntitySetName() : collectionResource.getEntityName()));
				// TODO implement collection properties and get transient values for inlinecount and skiptoken
				Integer inlineCount = null;
				String skipToken = null;
				feedWriter.write(uriInfo, new OutputStreamWriter(entityStream, "UTF-8"), Responses.entities(entities, entitySet, inlineCount, skipToken), entityOlinks);
			} else {
				logger.error("Accepted object for writing in isWriteable, but type not supported in writeTo method");
				throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
			}
		} catch (ODataException e) {
			logger.error("An error occurred while writing " + mediaType + " resource representation", e);
		}
	}
	
	public void addLinkToOLinks(List<OLink> olinks, Link link) {
		RequestContext requestContext = RequestContext.getRequestContext();		//TODO move to constructor to improve performance
		String rel = link.getRel();
		if(!rel.contains("self")) {
			rel = XmlFormatWriter.related + link.getRel();
		}
		String href = link.getHref();
		if(requestContext != null) {
			//Extract the transition fragment from the URI path
			href = link.getHrefTransition(requestContext.getBasePath());
		}
		String title = link.getTitle();
		OLink olink;
		Transition linkTransition = link.getTransition();
		if(linkTransition.getTarget().getClass() == CollectionResourceState.class) {
			olink = OLinks.relatedEntities(rel, title, href);
		}
		else {
			olink = OLinks.relatedEntity(rel, title, href);
		}
		olinks.add(olink);
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		// TODO this class can only deserialise EntityResource with OEntity, but at the moment we are accepting any EntityResource or CollectionResource
		return ResourceTypeHelper.isType(type, genericType, EntityResource.class)
				|| ResourceTypeHelper.isType(type, genericType, CollectionResource.class);
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
		
		// TODO check media type can be handled
		
		if(ResourceTypeHelper.isType(type, genericType, EntityResource.class)) {
			ResourceInteractionModel rim = null;
			OEntityKey entityKey = null;
			/* 
			 * TODO add uritemplate helper class (something like the wink JaxRsUriTemplateProcessor) to 
			 * our project, or use wink directly, will also need it for handling link transitions
			 */
//			JaxRsUriTemplateProcessor processor = new JaxRsUriTemplateProcessor("/{therest}/");
//			UriTemplateMatcher matcher = processor.matcher();
//			matcher.matches(uriInfo.getPath());
//			String entityKey = matcher.getVariableValue("id");
			String path = uriInfo.getPath();
			logger.info("Reading atom xml content for [" + path + "]");
			Matcher matcher = RESOURCE_PATTERN.matcher(path);
			if (matcher.find()) {
				// the resource path
				String resourcePath = matcher.group(1);
				rim = resourceRegistry.getResourceInteractionModel(resourcePath);

				if (rim != null) {
					// at the moment things are pretty simply, the bit after the last slash is the key
					entityKey = OEntityKey.parse(matcher.group(2));
				}
			}
			if (rim == null) {
				// might be a request without an entity key e.g. a POST
				if (!path.startsWith("/")) {
					// TODO remove this hack :-(
					path = "/" + path;
				}
				rim = resourceRegistry.getResourceInteractionModel(path);
				if (rim == null) {
					// give up, we can't handle this request 404
					logger.error("resource not found in registry");
					throw new WebApplicationException(Response.Status.NOT_FOUND);
				}
			}

			// parse the request content
			Reader reader = new InputStreamReader(entityStream);
			Entry e = new AtomEntryFormatParser(edmDataServices, rim.getCurrentState().getEntityName(), entityKey, null).parse(reader);
			
			return new EntityResource<OEntity>(e.getEntity());
		} else {
			logger.error("Unhandled type");
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}

	}

	protected void setUriInfo(UriInfo uriInfo) {
		this.uriInfo = uriInfo;
	}
}
