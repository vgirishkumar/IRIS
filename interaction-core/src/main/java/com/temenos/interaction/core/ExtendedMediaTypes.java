package com.temenos.interaction.core;

/*
 * #%L
 * interaction-core
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


import javax.ws.rs.core.MediaType;

public interface ExtendedMediaTypes {

    /** "application/odata+xml" */
    public final static String APPLICATION_ODATA_XML = "application/odata+xml";
    /** "application/odata+xml" */
    public final static MediaType APPLICATION_ODATA_XML_TYPE = new MediaType("application","odata+xml");

    /** "application/atomsvc+xml" */
    public static final String APPLICATION_ATOMSVC_XML = "application/atomsvc+xml";
    /** "application/atomsvc+xml" */
    public final static MediaType APPLICATION_ATOMSVC_XML_TYPE = new MediaType("application","atomsvc+xml");
    
    /** "application/pdf" */
    public final static String APPLICATION_PDF = "application/pdf";
    /** "application/pdf" */
    public final static MediaType APPLICATION_PDF_TYPE = new MediaType("application","pdf");

}
