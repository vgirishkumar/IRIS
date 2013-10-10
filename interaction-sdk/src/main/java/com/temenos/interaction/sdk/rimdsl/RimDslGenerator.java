package com.temenos.interaction.sdk.rimdsl;

/*
 * #%L
 * interaction-sdk
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


import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.temenos.interaction.sdk.command.Commands;
import com.temenos.interaction.sdk.interaction.InteractionModel;

/**
 * This class generates a resource interaction model DSL (.rim file)
 * from the conceptual interaction model.
 */
public class RimDslGenerator {

	VelocityEngine velocityEngine;		//Velocity engine

	/**
	 * Construct an instance of this class
	 * <pre>velocity engine is not null</pre>
	 * @param velocityEngine velocity engine
	 */
	public RimDslGenerator(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
		assert(velocityEngine != null);
	}

	public String generateRimDsl(InteractionModel interactionModel, Commands commands, boolean strictOData) {
		VelocityContext context = new VelocityContext();
		context.put("rim", interactionModel);
		context.put("commands", commands);
		context.put("strictOData", strictOData);
		
		Template t = velocityEngine.getTemplate("/RIM-DSL.vm");
		StringWriter sw = new StringWriter();
		t.merge(context, sw);
		return sw.toString();
	}
}
