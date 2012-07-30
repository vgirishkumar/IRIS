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

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;

/**
 * @author Mattias Hellborg Arthursson
 * @author Kalle Stenflo
 */
public class RequestContextFilter implements Filter {
	
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {


        final HttpServletRequest servletRequest = (HttpServletRequest) request;


        String requestURI = servletRequest.getRequestURI();
        requestURI = StringUtils.removeStart(requestURI, servletRequest.getContextPath() + servletRequest.getServletPath());
        String baseURL = StringUtils.removeEnd(servletRequest.getRequestURL().toString(), requestURI);
        UriBuilder uriBuilder = UriBuilder.fromUri(baseURL);

        RequestContext ctx;
        Principal userPrincipal = servletRequest.getUserPrincipal();
        if (userPrincipal != null) {
        	ctx = new RequestContext(uriBuilder, servletRequest.getRequestURI(), servletRequest.getHeader(RequestContext.HATEOAS_OPTIONS_HEADER), userPrincipal);
        } else {
        	ctx = new RequestContext(uriBuilder, servletRequest.getRequestURI(), servletRequest.getHeader(RequestContext.HATEOAS_OPTIONS_HEADER));
        }
        	
        RequestContext.setRequestContext(ctx);
        try {
            chain.doFilter(request, response);
        } finally {
            RequestContext.clearRequestContext();
        }
    }

    @Override
    public void destroy() {

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
}
