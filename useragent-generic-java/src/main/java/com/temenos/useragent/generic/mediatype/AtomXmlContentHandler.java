package com.temenos.useragent.generic.mediatype;

/*
 * #%L
 * useragent-generic-java
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
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


import static com.temenos.useragent.generic.mediatype.AtomUtil.NS_ODATA;
import static com.temenos.useragent.generic.mediatype.AtomUtil.REGX_VALID_PART_WITH_INDEX;

import java.util.ArrayList;
import java.util.List;

import org.apache.abdera.model.Entry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Handles the Atom XML content part of the {@link Entry entry} for read and
 * update.
 * 
 * @author ssethupathi
 *
 */
public class AtomXmlContentHandler {

	private Document document;

	public AtomXmlContentHandler(Document document) {
		this.document = document;
	}

	public Document getDocument() {
		return document;
	}

	/**
	 * Returns the count of the given element's existence in the document.
	 * 
	 * @param fqPropertyName
	 * @return count
	 * @throws IllegalArgumentException
	 *             if the fully qualified property is invalid
	 */
	public int getCount(String fqPropertyName) {
		String[] pathParts = vadliateAndFlattenPropertyName(fqPropertyName);
		Element parent = identifyParentElement(document, pathParts);
		if (parent == null) {
			return 0;
		}
		String elementName = buildElementName(pathParts[pathParts.length - 1]);
		Element element = identifyChildElement(parent, elementName, 0);
		return countElementSiblings(element);
	}

	/**
	 * Retruns the value for the given element's text node in the document.
	 * 
	 * @param fqPropertyName
	 * @return value
	 * @throws IllegalArgumentException
	 *             if the fully qualified property is invalid
	 */
	public String getValue(String fqPropertyName) {
		String[] pathParts = vadliateAndFlattenPropertyName(fqPropertyName);
		Element parent = identifyParentElement(document, pathParts);
		if (parent == null) {
			return null;
		}
		String elementName = buildElementName(pathParts[pathParts.length - 1]);
		Element element = getFirstChildElement(parent, elementName);
		if (element != null) {
			return element.getTextContent();
		} else {
			return null;
		}
	}

	/**
	 * Sets the given value to the element of the fully qualified property name.
	 * 
	 * @param fqPropertyName
	 * @param value
	 * @throws IllegalArgumentException
	 *             if the fully qualified property is invalid
	 */
	public void setValue(String fqPropertyName, String value) {
		String[] pathParts = vadliateAndFlattenPropertyName(fqPropertyName);
		Element parent = checkAndCreateParent(document, pathParts);
		String elementName = buildElementName(pathParts[pathParts.length - 1]);
		Element element = getFirstChildElement(parent, elementName);
		if (element != null) {
			element.setTextContent(value);
		} else {
			appendNewChildElement(parent, elementName, value);
		}
	}

	private int countElementSiblings(Element parent) {
		if (parent == null) {
			return 0;
		}
		List<Element> elementChildren = getChildElements(parent, "d:element");
		if (elementChildren.isEmpty()) {
			return 1;
		}
		return elementChildren.size();
	}

	private Element cloneExistingElement(Element parent, String fqElementName) {
		Element cloneableElement = getFirstChildElement(parent, fqElementName);
		if (cloneableElement == null) {
			throw new IllegalStateException("Invalid element name '"
					+ fqElementName + "' to set value to");
		}
		Node clonedNode = null;
		List<Element> childElements = getChildElements(cloneableElement);
		if (childElements.isEmpty()) {
			clonedNode = cloneableElement.cloneNode(false);
		} else {
			clonedNode = cloneableElement.cloneNode(true);
			removeNonContainerChildElements(clonedNode);
		}
		parent.appendChild(clonedNode);
		return (Element) clonedNode;
	}

	private void removeNonContainerChildElements(Node parent) {
		NodeList children = parent.getChildNodes();
		for (int idx = 0; idx < children.getLength(); idx++) {
			Node currentNode = children.item(idx);
			if (hasElementChild(currentNode)) {
				removeNonContainerChildElements(currentNode);
			} else if (Node.ELEMENT_NODE == currentNode.getNodeType()) {
				parent.removeChild(currentNode);
			}
		}
	}

	private void appendNewChildElement(Element parent, String fqPropertyName,
			String value) {
		Element newElement = parent.getOwnerDocument().createElementNS(
				NS_ODATA, fqPropertyName);
		newElement.setTextContent(value);
		parent.appendChild(newElement);
	}

	private Element identifyParentElement(Document document,
			String... pathParts) {
		Element parent = getPropertiesElement(document);
		int pathIndex = 0;
		while (pathIndex < (pathParts.length - 1)) {
			String pathPart = pathParts[pathIndex];
			parent = identifyChildElement(parent, buildElementName(pathPart), 0);
			parent = identifyChildElement(parent, buildElementName("element"),
					extractIndex(pathPart));
			pathIndex++;
		}
		return parent;
	}

	private Element checkAndCreateParent(Document document, String... pathParts) {
		Element parent = getPropertiesElement(document);
		int pathIndex = 0;
		while (pathIndex < (pathParts.length - 1)) {
			String pathPart = pathParts[pathIndex];
			parent = checkAndCreateChild(parent, buildElementName(pathPart), 0);
			parent = checkAndCreateChild(parent, buildElementName("element"),
					extractIndex(pathPart));
			pathIndex++;
		}
		return parent;
	}

	private int extractIndex(String path) {
		if (path.matches(REGX_VALID_PART_WITH_INDEX)) {
			String indexStr = path.substring(path.indexOf("(") + 1,
					path.indexOf(")"));
			return Integer.parseInt(indexStr);
		}
		return 0;
	}

	private String buildElementName(String path) {
		String elementName = path;
		if (path.matches(REGX_VALID_PART_WITH_INDEX)) {
			elementName = path.substring(0, path.indexOf("("));
		}
		return "d:" + elementName;
	}

	private Element checkAndCreateChild(Element parent, String childName,
			int expectedIndex) {
		if (parent == null) {
			return null;
		}
		List<Element> childElements = getChildElements(parent, childName);
		if (expectedIndex < childElements.size()) {
			return identifyChildElement(parent, childName, expectedIndex);
		} else if (expectedIndex == childElements.size()) {
			return (Element) cloneExistingElement(parent, childName);
		} else {
			throw new IllegalStateException("Invalid index '" + expectedIndex
					+ "' to set value for existing index '"
					+ childElements.size() + "'");
		}
	}

	private String[] vadliateAndFlattenPropertyName(String fqPropertyName) {
		if (fqPropertyName == null || fqPropertyName.isEmpty()) {
			throw new IllegalArgumentException("Invalid property name '"
					+ fqPropertyName + "'");
		}
		String[] pathParts = fqPropertyName.split("/");
		int lastPartIndex = pathParts.length - 1;
		for (int index = 0; index < lastPartIndex; index++) {
			String pathPart = pathParts[index];
			if (!pathPart.matches(REGX_VALID_PART_WITH_INDEX)) {
				throw new IllegalArgumentException("Invalid part '" + pathPart
						+ "' in fully qualified property name '"
						+ fqPropertyName + "'");
			}
		}
		String elementPart = pathParts[lastPartIndex];
		if (!elementPart.matches(AtomUtil.REGX_VALID_ELEMENT)) {
			throw new IllegalArgumentException("Invalid property name '"
					+ elementPart + "'");
		}
		return pathParts;
	}

	private Element getPropertiesElement(Document document) {
		Node contentNode = document.getElementsByTagName("content").item(0);
		return getFirstChildElement(contentNode, "m:properties");
	}

	private Element identifyChildElement(Node node, String childName, int index) {
		return getSpecificElement(node.getChildNodes(), childName, index);
	}

	private Element getFirstChildElement(Node node, String childName) {
		return getSpecificElement(node.getChildNodes(), childName, 0);
	}

	private Element getSpecificElement(NodeList nodes, String childName,
			int expectedIndex) {
		int matchingElementIdx = 0;
		for (int idx = 0; idx < nodes.getLength(); idx++) {
			Node node = nodes.item(idx);
			if (Node.ELEMENT_NODE == node.getNodeType()
					&& node.getNodeName().equals(childName)) {
				if (matchingElementIdx == expectedIndex) {
					return (Element) node;
				}
				matchingElementIdx++;
			}
		}
		return null;
	}

	private boolean hasElementChild(Node parent) {
		return !getChildElements(parent).isEmpty();
	}

	private List<Element> getChildElements(Node parent) {
		List<Element> elements = new ArrayList<Element>();
		NodeList children = parent.getChildNodes();
		for (int idx = 0; idx < children.getLength(); idx++) {
			Node child = children.item(idx);
			if (Node.ELEMENT_NODE == child.getNodeType()) {
				elements.add((Element) child);
			}
		}
		return elements;
	}

	private List<Element> getChildElements(Node parent, String childName) {
		List<Element> elements = new ArrayList<Element>();
		NodeList children = parent.getChildNodes();
		for (int idx = 0; idx < children.getLength(); idx++) {
			Node child = children.item(idx);
			if (Node.ELEMENT_NODE == child.getNodeType()
					&& child.getNodeName().equals(childName)) {
				elements.add((Element) child);
			}
		}
		return elements;
	}
}
