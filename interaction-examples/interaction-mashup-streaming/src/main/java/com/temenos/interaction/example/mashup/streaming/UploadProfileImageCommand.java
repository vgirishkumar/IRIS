package com.temenos.interaction.example.mashup.streaming;

/*
 * #%L
 * interaction-example-mashup-streaming
 * %%
 * Copyright (C) 2012 - 2015 Temenos Holdings N.V.
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


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OLink;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmSimpleType;

import com.temenos.interaction.commands.odata.CommandHelper;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.core.entity.StreamingInput;
import com.temenos.interaction.core.resource.EntityResource;

public class UploadProfileImageCommand implements InteractionCommand {

	@Override
	public Result execute(InteractionContext ctx) throws InteractionException {
		if(ctx == null) {
			throw new IllegalArgumentException("ctx cannot be null");
		}
				
		if(ctx.getResource() == null) {
			throw new IllegalArgumentException("resource cannot be null");			
		}
		
		EntityResource<StreamingInput> input = (EntityResource<StreamingInput>)ctx.getResource();
		
		StreamingInput streamingInput = input.getEntity();
		
		if(streamingInput.getHeaders().containsKey("Content-Type")) {						
			InputStream in = streamingInput.getInput();
			
			FileOutputStream out = null;
			
			String filename = ctx.getQueryParameters().getFirst("filename");
			
			File file = new File(System.getProperty("java.io.tmpdir"), filename);
			
			try {	
				out = new FileOutputStream(file);
				
				IOUtils.copy(in, out);
				
				if( in != null) {
                    in.close();
                }                
                out.close();
			} catch (IOException e) {
                throw new InteractionException(Status.INTERNAL_SERVER_ERROR, e);
            } catch (Exception e) {
				throw new InteractionException(Status.INTERNAL_SERVER_ERROR,"error", e);
			}
			
			// We'll use the filename as the entity key
			OEntityKey entityKey = OEntityKey.create(filename);
			
			// Create an entity from the info we have
			List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
			properties.add(OProperties.parseSimple("Id", EdmSimpleType.getSimple("Edm.String"), filename));
			EdmEntitySet entitySet = EdmEntitySet.newBuilder().setName("UploadImages").setEntityTypeName("UploadImage").build();
			OEntity entity = OEntities.create(entitySet, entitySet.getType(), entityKey, "", properties, new ArrayList<OLink>());			
			EntityResource<OEntity> er = CommandHelper.createEntityResource(entity);
			ctx.setResource(er);
		}
		
		return Result.SUCCESS;
	}

}
