package com.temenos.interaction.core.decorator.hal;

import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public abstract class Representation {

	/* 
	 * Relations URI identifies a behaviour within a domain, therefore 
	 * relations should not be declared in a common library.
	 */
//    public static final String RELATIONS_URI = "http://relations.restbucks.com/";
//    public static final String RESTBUCKS_NAMESPACE = "http://schemas.restbucks.com";
//    public static final String DAP_NAMESPACE = RESTBUCKS_NAMESPACE + "/dap";
    public static final String HAL_MEDIA_TYPE = "application/hal+xml";
    public static final String SELF_REL_VALUE = "self";

//    @XmlElement(name = "link", namespace = DAP_NAMESPACE)
    @XmlElement(name = "link")
    protected List<Link> links;

    protected Link getLinkByName(String uriName) {
        if (links == null) {
            return null;
        }

        for (Link l : links) {
            if (l.getRelValue().toLowerCase().equals(uriName.toLowerCase())) {
                return l;
            }
        }
        return null;
    }
}
