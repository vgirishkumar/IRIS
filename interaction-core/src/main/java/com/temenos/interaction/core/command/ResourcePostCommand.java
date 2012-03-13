package com.temenos.interaction.core.command;

import javax.ws.rs.core.Response;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.RESTResponse;

/**
 * A #ResourcePostCommand can be used to post a document to a resource for it to process.
 * <p>
 * My favourite example is that of a company stationary department (a resource that you can 
 * post a stationary request form to).  If you post a request for a new pen to the stationary 
 * department they will process the form you filled out and send you another pen.  If you 
 * haven't received your pen within a reasonable amount of time, then you can fill out another 
 * form and risk receiving two pens.
 * </p>
 * @author aphethean
 */
public interface ResourcePostCommand extends ResourceStateTransitionCommand {

	/**
	 * Using the supplied id, POST a resource to the concrete implementations provider.
	 * 
	 * @precondition id not null
	 * @precondition EntityResource not null
	 * @precondition resource should be able to be created in implementing resource manager
	 * @postcondition a valid {@link RESTResponse} will be returned and {@link RESTResponse#getStatus()} 
	 * will return a {@link Response.Status}.  A call to {@link RESTResponse#getResource()} will return
	 * a {@link RESTResource} if a the Response.Status is an OK (200) family of response
	 */
	public RESTResponse post(String id, EntityResource<?> resource);

}
