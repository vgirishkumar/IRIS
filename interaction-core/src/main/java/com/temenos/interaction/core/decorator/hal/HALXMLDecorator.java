package com.temenos.interaction.core.decorator.hal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
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

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.decorator.Decorator;

public class HALXMLDecorator implements Decorator<StreamingOutput> {
	private final Logger logger = LoggerFactory.getLogger(HALXMLDecorator.class);

	public HALXMLDecorator() {
	}

	/**
	 * Return a Hypertext Application Language (HAL) representation of
	 * com.temenos.interaction.core.RESTResponse.
	 * 
	 * @precondition RESTResponse must extend an EntityResponse, this class only
	 * supports the decoration of Entities (EntityResponse)
	 * @precondition RESTResponse#getStatus is OK
	 * @postcondition non null HAL XML decorated Response
	 * @invariant RESTResponse is not null
	 */
	public StreamingOutput decorateRESTResponse(final RESTResponse r) {
		assert (r != null);

		if (r.getStatus() == Response.Status.OK
				&& r.getResource() instanceof EntityResource) {
			try {

				EntityResource resource = (EntityResource) r.getResource();
				OEntity entity = resource.getEntity();

				DocumentBuilderFactory dbfac = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
				Document doc = docBuilder.newDocument();

				// //////////////////////
				// Creating the XML tree

				// create the root element and add it to the document
				Element root = doc.createElement("resource");
				doc.appendChild(root);

				// create child element for data, and add to root
//				Element dataObject = doc.createElement(entity.getEntitySet().name);
				for (OProperty<?> property : resource.getEntity()
						.getProperties()) {
					Element dataElement = doc.createElement(property.getName());
					dataElement.setTextContent(property.getValue().toString());
					root.appendChild(dataElement);
				}

				//root.appendChild(dataObject);

				// create child element for links, and add to root
				Element links = doc.createElement("links");
				for (OLink link : resource.getEntity().getLinks()) {
					Element linkElement = doc.createElement("link");
					linkElement.setAttribute("href", link.getHref());
					linkElement.setAttribute("rel", link.getRelation());
					linkElement.setAttribute("title", link.getTitle());
					links.appendChild(linkElement);
				}
				root.appendChild(links);

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

		        return new StreamingOutput() {
		            public void write(OutputStream output) throws IOException, WebApplicationException {
		            	output.write(xmlString.getBytes("UTF-8"));
		            	output.flush();
		            }
		        };

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
			

//			return Response.ok(xmlString, com.temenos.interaction.core.decorator.hal.MediaType.APPLICATION_HAL_XML).build();
		}

//		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		return null;
	}

}
