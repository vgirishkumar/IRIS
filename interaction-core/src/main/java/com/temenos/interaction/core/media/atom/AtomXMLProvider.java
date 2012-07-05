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
import java.util.List;
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

import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OLink;
import org.odata4j.core.OLinks;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.format.Entry;
import org.odata4j.format.xml.AtomEntryFormatParser;
import org.odata4j.format.xml.XmlFormatWriter;
import org.odata4j.producer.Responses;
import org.odata4j.producer.exceptions.ODataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.link.Link;
import com.temenos.interaction.core.link.ResourceRegistry;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.resource.ResourceTypeHelper;
import com.temenos.interaction.core.state.ResourceInteractionModel;
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
	private final ResourceRegistry resourceRegistry;

	/**
	 * Construct the jax-rs Provider for OData media type.
	 * @param edmDataServices
	 * 		The entity metadata for reading and writing OData entities.
	 * @param resourceRegistry
	 * 		The resource registry contains all the resource to entity mappings
	 */
	public AtomXMLProvider(EdmDataServices edmDataServices, ResourceRegistry resourceRegistry) {
		this.edmDataServices = edmDataServices;
		this.resourceRegistry = resourceRegistry;
		assert(edmDataServices != null);
		assert(resourceRegistry != null);
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return ResourceTypeHelper.isType(type, genericType, EntityResource.class, OEntity.class) ||
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
				OEntity oentity = entityResource.getEntity();

				//Convert Links to list of OLink
				RequestContext requestContext = RequestContext.getRequestContext();
				List<OLink> olinks = new ArrayList<OLink>();
				for(Link link : entityResource.getLinks()) {
					String otherEntitySetName = link.getRel();
					String pathOtherResource = link.getHref();
					if(requestContext != null) {
						//Extract the transition fragment from the URI path
						pathOtherResource = link.getHrefTransition(requestContext.getBasePath());
					}
					String rel = XmlFormatWriter.related + otherEntitySetName;
					OLink olink = OLinks.relatedEntity(rel, otherEntitySetName, pathOtherResource);
					olinks.add(olink);
				}
				
				//Write entry
				EdmEntitySet entitySet = edmDataServices.getEdmEntitySet(oentity.getEntitySetName());
				entryWriter.write(uriInfo, new OutputStreamWriter(entityStream, "UTF-8"), Responses.entity(oentity), entitySet, olinks);
			} else if(ResourceTypeHelper.isType(type, genericType, CollectionResource.class, OEntity.class)) {
				CollectionResource<OEntity> cr = ((CollectionResource<OEntity>) resource);
				List<EntityResource<OEntity>> resources = (List<EntityResource<OEntity>>) cr.getEntities();
				List<OEntity> entities = new ArrayList<OEntity>();
				for (EntityResource<OEntity> er : resources) {
					entities.add(er.getEntity());
				}
				EdmEntitySet entitySet = edmDataServices.getEdmEntitySet(cr.getEntitySetName());
				// TODO implement collection properties and get transient values for inlinecount and skiptoken
				Integer inlineCount = null;
				String skipToken = null;
				feedWriter.write(uriInfo, new OutputStreamWriter(entityStream, "UTF-8"), Responses.entities(entities, entitySet, inlineCount, skipToken));
			} else {
				logger.error("Accepted object for writing in isWriteable, but type not supported in writeTo method");
				throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
			}
		} catch (ODataException e) {
			logger.error("An error occurred while writing " + mediaType + " resource representation", e);
		}
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
