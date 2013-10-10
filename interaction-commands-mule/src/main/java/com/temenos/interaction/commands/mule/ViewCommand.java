package com.temenos.interaction.commands.mule;

/*
 * #%L
 * interaction-commands-mule
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


import java.io.ByteArrayInputStream;

import javax.ws.rs.core.MultivaluedMap;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.LocalMuleClient;
import org.mule.transport.NullPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.resource.EntityResource;

/**
 * <p>
 * A ViewCommand that calls a Mule vm://[entity]-view-command endpoint.
 * </p>
 * @author aphethean
 */
public class ViewCommand implements InteractionCommand {
	private final static Logger logger = LoggerFactory.getLogger(ViewCommand.class);

	@Autowired
	private LocalMuleClient client;
	private EntityXMLReader entityReader = new EntityXMLReader();
	
	public ViewCommand() {}
	
	public ViewCommand(LocalMuleClient client) {
		this.client = client;
	}
	
	/**
	 * <p>
	 * IRIS interaction framework main command execution method.
	 * </p>
	 * @precondition LocalMuleClient has been supplied by either the 
	 * 					Spring @Autowired or supplied in the constructor.
	 * @postcondition Result must be returned
	 */
	public Result execute(InteractionContext ctx) {

		MultivaluedMap<String, String> pathParams = ctx.getPathParameters();
		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();
		
		try {
			ViewCommandWrapper commandWrapper = new ViewCommandWrapper(pathParams, queryParams);
			String endpoint = "vm://" + ctx.getCurrentState().getEntityName() + "-view-command";
			MuleMessage result = client.send(endpoint, commandWrapper, null);
			if (result.getExceptionPayload() == null) {
				if (result.getPayload() instanceof NullPayload) {
					logger.info("No result from Mule");
				} else {
					byte[] response = result.getPayloadAsBytes();
					if (logger.isDebugEnabled()) {
						String responseStr = result.getPayloadAsString();
						logger.debug("MuleResponse ["+responseStr+"]");
					}
					// TODO we could get Entity directly from Mule with our custom transformer?
					Entity entity = entityReader.toEntity(new ByteArrayInputStream(response));
					ctx.setResource(new EntityResource<Entity>(entity) {});
				}
				return Result.SUCCESS;
			} else {
				logger.error("Mule returned an exception [", result.getExceptionPayload().getMessage() + "]");
			}
		} catch (MuleException e) {
			logger.error("A unexpected error occurred when calling Mule", e);
			return Result.INVALID_REQUEST;
		} catch (Exception e) {
			logger.error("A unexpected error occurred when calling Mule", e);
			return Result.INVALID_REQUEST;
		}

		return Result.FAILURE;
	}

}
