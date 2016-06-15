package com.temenos.interaction.core.rim;

/*
 * #%L
 * interaction-core
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

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.temenos.interaction.core.hypermedia.Link;

/**
 * TODO: Document me!
 *
 * @author dgroves
 *
 */
public final class ResponseWrapper {
    private final Response response;
    private final Link selfLink;
    private final MultivaluedMap<String, String> requestParameters;
    
    public ResponseWrapper(Response response, Link selfLink, MultivaluedMap<String, String> requestParameters) {
        this.response = response;
        this.selfLink = selfLink;
        this.requestParameters = requestParameters;
    }
    
    public Response getResponse(){
        return response;
    }
    
    public Link getSelfLink(){
        return selfLink;
    }
    
    public MultivaluedMap<String, String> getRequestParameters(){
        return requestParameters;
    }
}
