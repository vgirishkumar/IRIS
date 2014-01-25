package com.temenos.messagingLayer.util;

/**
 * Generic unmarshaller interface.
 * 
 * TODO in particular, this is used for loading the mapping definitions (hence its name). But in practice, this is
 * generic enough to consider renaming it and its method(s).
 * 
 * @author acirlomanu
 * 
 */
public interface MappingFactory {

	/**
	 * Returns the unmarshalled XML file from the specified path. Callers need to cast it to its actual type.
	 * 
	 * @param jaxbContextPath
	 *            pass-through parameter for the {@link javax.xml.bind.JAXBContext} instance.
	 * @param xmlPath
	 *            the XML file path
	 * @return unmarshalled XML object.
	 */
	public abstract Object getMapping(String jaxbContextPath, String xmlPath);

}