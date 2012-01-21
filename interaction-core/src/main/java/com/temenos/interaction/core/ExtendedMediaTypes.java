package com.temenos.interaction.core;

import javax.ws.rs.core.MediaType;

public interface ExtendedMediaTypes {

    /** "application/odata+xml" */
    public final static String APPLICATION_ODATA_XML = "application/odata+xml";
    /** "application/odata+xml" */
    public final static MediaType APPLICATION_ODATA_XML_TYPE = new MediaType("application","odata+xml");

    /** "application/pdf" */
    public final static String APPLICATION_PDF = "application/pdf";
    /** "application/pdf" */
    public final static MediaType APPLICATION_PDF_TYPE = new MediaType("application","pdf");

}
