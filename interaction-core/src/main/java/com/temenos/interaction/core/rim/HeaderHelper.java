package com.temenos.interaction.core.rim;

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


import java.util.List;
import java.util.Set;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.ResponseBuilder;

public class HeaderHelper {

    public static ResponseBuilder allowHeader(ResponseBuilder rb, Set<String> httpMethods) {
    	if (httpMethods != null) {
        	StringBuilder result = new StringBuilder();
        	for (String method : httpMethods) {
                result.append(" ");
                result.append(method);
                result.append(",");
        	}
        	return rb.header("Allow", (result.length() > 0 ? result.substring(1, result.length() - 1) : ""));
    	}
    	return rb;
    }

    public static ResponseBuilder locationHeader(ResponseBuilder rb, String target) {
    	// RequestContext.getRequestContext().getBasePath().path(nextState.getPath())
    	if (target != null) {
        	return rb.header(HttpHeaders.LOCATION, target);
    	}
    	return rb;
    }

    /**
     * Add an E-Tag header
     * @param rb response builder
     * @param entityTag etag
     * @return response builder
     */
    public static ResponseBuilder etagHeader(ResponseBuilder rb, String entityTag) {
    	if (entityTag != null && !entityTag.isEmpty()) {
        	return rb.header(HttpHeaders.ETAG, entityTag);
    	}
    	return rb;
    }
    
    public static ResponseBuilder maxAgeHeader(ResponseBuilder rb, int maxAge) {
    	return rb.header(HttpHeaders.CACHE_CONTROL, "max-age=" + maxAge );
    }
    
    /**
     * Returns the first HTTP header entry for the specified header
     * @param headers HTTP headers
     * @param header header to return
     * @return first header entry
     */
    public static String getFirstHeader(HttpHeaders headers, String header) {
    	if (headers != null) {
        	List<String> headerList = headers.getRequestHeader(header);
        	if(headerList != null && headerList.size() > 0) {
        		return headerList.get(0);
        	}
    	}
    	return null;
    }
}
