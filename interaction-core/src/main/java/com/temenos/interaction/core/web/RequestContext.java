/*
 * Copyright 2011 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.temenos.interaction.core.web;

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


import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mattias Hellborg Arthursson
 * @author Kalle Stenflo
 */
public final class RequestContext {

    public static final String HATEOAS_OPTIONS_HEADER = "x-jax-rs-hateoas-options";
    
    private final static ThreadLocal<RequestContext> currentContext = new ThreadLocal<RequestContext>();

    public static void setRequestContext(RequestContext context) {
        currentContext.set(context);
    }

    public static RequestContext getRequestContext() {
        return currentContext.get();
    }

    public static void clearRequestContext() {
        currentContext.remove();
    }


    private final String basePath;
    private final String requestUri;
    private final String verbosityHeader;
    private final Principal userPrincipal;
    private final Map<String, String> headers = new HashMap<String, String>();

    public RequestContext(String basePath, String requestUri, String verbosityHeader) {
        this.basePath = basePath;
        this.requestUri = requestUri;
        this.verbosityHeader = verbosityHeader;
        this.userPrincipal = null;
    }

    public RequestContext(String basePath, String requestUri, String verbosityHeader, Principal userPrincipal) {
        this.basePath = basePath;
        this.requestUri = requestUri;
        this.verbosityHeader = verbosityHeader;
        this.userPrincipal = userPrincipal;
    }

    public RequestContext(String basePath, String requestUri, String verbosityHeader, Map<String, String> headers) {
        this.basePath = basePath;
        this.requestUri = requestUri;
        this.verbosityHeader = verbosityHeader;
        this.userPrincipal = null;
        this.headers.putAll(headers);
    }
    
    public RequestContext(String basePath, String requestUri, String verbosityHeader, Principal userPrincipal, Map<String, String> headers) {
        this.basePath = basePath;
        this.requestUri = requestUri;
        this.verbosityHeader = verbosityHeader;
        this.userPrincipal = userPrincipal;
        this.headers.putAll(headers);
    }

    public String getBasePath() {
        return basePath;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getVerbosityHeader() {
        return verbosityHeader;
    }
    
    public Principal getUserPrincipal(){
    	return this.userPrincipal;
    }

    public String get(String name) {
        return headers.get(name);
    }

}
