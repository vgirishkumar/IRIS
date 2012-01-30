package com.temenos.interaction.core.state;

import java.util.Set;

import javax.ws.rs.core.Response.ResponseBuilder;

import com.temenos.interaction.core.RESTResponse;

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

}
