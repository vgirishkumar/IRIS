package com.temenos.interaction.core.rim;

import java.util.Set;

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
        	return rb.header("Location", target);
    	}
    	return rb;
    }

}
