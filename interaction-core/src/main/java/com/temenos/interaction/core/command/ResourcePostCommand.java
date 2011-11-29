package com.temenos.interaction.core.command;

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
public interface ResourcePostCommand<RESOURCE> extends ResourceStateTransitionCommand {

	public RESTResponse post(String id, RESOURCE resource);

}
