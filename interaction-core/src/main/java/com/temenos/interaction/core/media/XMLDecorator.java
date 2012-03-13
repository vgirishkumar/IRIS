package com.temenos.interaction.core.media;

import java.io.StringWriter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.odata4j.core.OEntity;
import org.odata4j.core.OLink;
import org.odata4j.core.OProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.RESTResponse;

public class XMLDecorator implements Decorator<Response> {
	private final Logger logger = LoggerFactory.getLogger(XMLDecorator.class);
	
	public XMLDecorator() {}
	
	@SuppressWarnings("unchecked")
	public Response decorateRESTResponse(final RESTResponse r) {
		if (r == null)
			throw new WebApplicationException(Response.Status.BAD_REQUEST);

		StatusType status = r.getStatus();
		if (status == Response.Status.OK) {
			String xmlString = null;
    		try {
				if (r.getResource() instanceof EntityResource) {
					EntityResource<OEntity> resource = (EntityResource<OEntity>) r.getResource();
					OEntity entity = resource.getOEntity();

					DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
				    DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
				    Document doc = docBuilder.newDocument();

				    ////////////////////////
				    //Creating the XML tree

				    //create the root element and add it to the document
				    Element root = doc.createElement("resource");
				    doc.appendChild(root);

				    //create child element for data, and add to root
				    Element dataObject = doc.createElement(entity.getEntitySet().getName());
					for (OProperty<?> property : resource.getOEntity().getProperties()) {
				        Element dataElement = doc.createElement(property.getName());
						dataElement.appendChild(doc.createTextNode(property.getValue().toString()));
				        dataObject.appendChild(dataElement);
					}
			        root.appendChild(dataObject);

				    //create child element for links, and add to root
				    Element links = doc.createElement("links");
					for (OLink link : resource.getOEntity().getLinks()) {
				        Element linkElement = doc.createElement("link");
				        linkElement.setAttribute("href", link.getHref());
				        linkElement.setAttribute("rel", link.getRelation());
				        linkElement.setAttribute("title", link.getTitle());
				        dataObject.appendChild(linkElement);
					}
			        root.appendChild(links);

				    /////////////////
				    //Output the XML

				    //set up a transformer
				    TransformerFactory transfac = TransformerFactory.newInstance();
				    Transformer trans = transfac.newTransformer();
				    trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				    trans.setOutputProperty(OutputKeys.INDENT, "yes");

				    //create string from xml tree
				    StringWriter sw = new StringWriter();
				    StreamResult result = new StreamResult(sw);
				    DOMSource source = new DOMSource(doc);
				    trans.transform(source, result);
				    xmlString = sw.toString();
				}
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
			return Response.ok(xmlString, MediaType.APPLICATION_XML).build();
		} else {
			return Response.status(status).build();
		}
	}

}
