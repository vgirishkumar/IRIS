package com.temenos.interaction.media.odata.xml.error;

/*
 * #%L
 * interaction-media-odata-xml
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.httpclient.HttpStatus;

import com.temenos.interaction.core.command.CommandHelper;
import com.temenos.interaction.core.entity.GenericError;
import com.temenos.interaction.core.resource.EntityResource;

/**
 * Marshals an unhandled, unchecked exception thrown by IRIS
 * into a GenericError. 
 *
 * @author dgroves
 *
 */
@Provider
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML})
public class RuntimeExceptionHandler extends InteractionExceptionHandler<RuntimeException> 
        implements ExceptionMapper<RuntimeException> {
    
    private static final Logger logger = LoggerFactory.getLogger(RuntimeExceptionHandler.class);
    
    @Override
    public Response toResponse(RuntimeException exception) {
        int code = HttpStatus.SC_INTERNAL_SERVER_ERROR;
        String stackTrace = generateLogMessage(exception, code);
        EntityResource<?> er = CommandHelper.createEntityResource(new GenericError(Integer.toString(code), 
                stackTrace), GenericError.class);
        return Response.serverError().entity(er.getGenericEntity()).build();
    }
}
