package com.temenos.interaction.core.media.hal.stax;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.RESTResource;
import com.temenos.interaction.core.ResourceTypeHelper;

@Provider
@Consumes({com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML})
@Produces({com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML})
public class HALProvider implements MessageBodyReader<RESTResource>, MessageBodyWriter<RESTResource> {
	private final Logger logger = LoggerFactory.getLogger(HALProvider.class);

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
	 * @precondition {@link EntityResource#getOEntity()} returns a valid OEntity, this 
	 * provider only supports serialising OEntities
	 * @postcondition non null HAL XML document written to OutputStream
	 * @invariant valid OutputStream
	 */
	@SuppressWarnings("unchecked")
	// TODO implment writeTo with Stax
	@Override
	public void writeTo(RESTResource resource, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {
		assert (resource != null);

		if (!ResourceTypeHelper.isType(type, genericType, EntityResource.class))
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			// //////////////////////
			// Creating the XML tree

			// create the root element and add it to the document
			Element root = doc.createElement("resource");
			doc.appendChild(root);

			EntityResource<OEntity> entityResource = (EntityResource<OEntity>) resource;
			if (entityResource.getOEntity() != null) {
				// create child element for data, and add to root
				Element dataObject = doc.createElement(entityResource.getOEntity().getEntitySet().getName());
				for (OProperty<?> property : entityResource.getOEntity()
						.getProperties()) {
					Element dataElement = doc.createElement(property.getName());
					dataElement.appendChild(doc.createTextNode(property.getValue().toString()));
					dataObject.appendChild(dataElement);
				}

				root.appendChild(dataObject);

				// create child element for links, and add to root
				Element links = doc.createElement("links");
				for (OLink link : entityResource.getOEntity().getLinks()) {
					Element linkElement = doc.createElement("link");
					linkElement.setAttribute("href", link.getHref());
					linkElement.setAttribute("rel", link.getRelation());
					linkElement.setAttribute("title", link.getTitle());
					links.appendChild(linkElement);
				}
				root.appendChild(links);
			}

			// ///////////////
			// Output the XML

			// set up a transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "no");

			// create string from xml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);
			final String xmlString = sw.toString();

			entityStream.write(xmlString.getBytes("UTF-8"));
			entityStream.flush();
		} catch (DOMException e) {
			logger.error("Error while generating xml", e);
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		} catch (TransformerConfigurationException e) {
			logger.error("Error while generating xml", e);
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		} catch (IllegalArgumentException e) {
			logger.error("Error while generating xml", e);
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		} catch (ParserConfigurationException e) {
			logger.error("Error while generating xml", e);
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		} catch (TransformerFactoryConfigurationError e) {
			logger.error("Error while generating xml", e);
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		} catch (TransformerException e) {
			logger.error("Error while generating xml", e);
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
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

		if (!ResourceTypeHelper.isType(type, genericType, EntityResource.class, OEntity.class))
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
}
