package com.temenos.interaction.core.link;

import com.jayway.jaxrs.hateoas.HateoasLinkInjector;
import com.jayway.jaxrs.hateoas.HateoasVerbosity;
import com.jayway.jaxrs.hateoas.LinkProducer;

/**
 * This link injector is the last resort - give up.  It accepts every entity, but
 * doesn't make any attempt to inject any links.
 * @author aphethean
 */
public class NaiveLinkInjector implements HateoasLinkInjector<Object> {

	/**
	 * Accepts everything
	 */
    @Override
    public boolean canInject(Object entity) {
    	return true;
    }

    @Override
    public Object injectLinks(Object entity, LinkProducer<Object> linkProducer, final HateoasVerbosity verbosity) {
        return entity;
    }

}
