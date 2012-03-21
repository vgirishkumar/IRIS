package com.temenos.interaction.core.link;

import java.util.Collection;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.jayway.jaxrs.hateoas.HateoasLink;
import com.jayway.jaxrs.hateoas.HateoasLinkBean;
import com.jayway.jaxrs.hateoas.HateoasLinkInjector;
import com.jayway.jaxrs.hateoas.HateoasVerbosity;
import com.jayway.jaxrs.hateoas.LinkProducer;

/**
 * This link injector doesn't try very hard to inject the links.  If the entity
 * is not an instance of {@link HateoasLinkBean} it just returns the entity.
 * @author aphethean
 */
public class NaiveLinkInjector implements HateoasLinkInjector<Object> {

    @Override
    public boolean canInject(Object entity) {
        return true;
    }

    @Override
    public Object injectLinks(Object entity, LinkProducer<Object> linkProducer, final HateoasVerbosity verbosity) {

    	if (!(entity instanceof HateoasLinkBean))
    		return entity;
    	
        HateoasLinkBean linkBean = (HateoasLinkBean) entity;

        Collection<Map<String,Object>> links = Collections2.transform(linkProducer.getLinks(entity),
                new Function<HateoasLink, Map<String, Object>>() {
                    @Override
                    public Map<String, Object> apply(HateoasLink from) {
                        return from.toMap(verbosity);
                    }
                });

        linkBean.setLinks(links);

        return linkBean;
    }

}
