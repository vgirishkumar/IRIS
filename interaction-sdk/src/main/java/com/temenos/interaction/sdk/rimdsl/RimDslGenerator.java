package com.temenos.interaction.sdk.rimdsl;

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
