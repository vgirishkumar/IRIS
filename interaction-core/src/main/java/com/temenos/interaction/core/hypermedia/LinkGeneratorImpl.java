package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-core
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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.odata4j.core.OEntity;
import org.odata4j.format.xml.XmlFormatWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.web.RequestContext;


/**
 * A {@link LinkGenerator} that generates a {@link Collection} of {@link Link} for a {@link Transition} using data from
 * a resource entity.
 *
 */
public class LinkGeneratorImpl implements LinkGenerator {

    private static final Logger logger = LoggerFactory.getLogger(LinkGeneratorImpl.class);
    private ResourceStateMachine resourceStateMachine;
    private Transition transition;
    private InteractionContext interactionContext;
    private boolean allQueryParameters;

    public LinkGeneratorImpl(ResourceStateMachine resourceStateMachine, Transition transition, InteractionContext interactionContext) {
        this.resourceStateMachine = resourceStateMachine;
        this.transition = transition;
        this.interactionContext = interactionContext;
    }

    public LinkGeneratorImpl setAllQueryParameters(boolean allQueryParameters) {
        this.allQueryParameters = allQueryParameters;
        return this;
    }

    @Override
    public Collection<Link> createLink(MultivaluedMap<String, String> pathParameters, MultivaluedMap<String, String> queryParameters, Object entity) {
        Collection<Link> eLinks = new ArrayList<Link>();
        Map<String, Object> transitionProperties = resourceStateMachine.getTransitionProperties(transition, entity, pathParameters, queryParameters);

        LinkToFieldAssociation linkToFieldAssociation = new LinkToFieldAssociationImpl(transition, transitionProperties);
        if (linkToFieldAssociation.isTransitionSupported()) {
            List<LinkTransitionProperties> linkPropertiesList = linkToFieldAssociation.getTransitionProperties();
            for (LinkTransitionProperties linkTransitionProperties : linkPropertiesList) {
                eLinks.add(createLink(linkTransitionProperties, queryParameters, entity));
            }
        }

        return eLinks;
    }

    private Link createLink(LinkTransitionProperties linkTransitionProps, MultivaluedMap<String, String> queryParameters, Object entity) {
        assert (RequestContext.getRequestContext() != null);
        ResourceStateProvider resourceStateProvider = resourceStateMachine.getResourceStateProvider();
        try {
            ResourceState targetState = transition.getTarget();

            if (targetState == null) {
                // a dead link, target could not be found
                logger.error("Dead link to [" + transition.getId() + "]");
                return null;
            }

            if (targetState instanceof LazyResourceState || targetState instanceof LazyCollectionResourceState) {
                targetState = resourceStateProvider.getResourceState(targetState.getName());
            }

            if (targetState != null) {
                processEmbeddedTransitions(targetState, resourceStateProvider);
                setErrorState(targetState, resourceStateProvider);
            }

            UriBuilder linkTemplate = UriBuilder.fromUri(RequestContext.getRequestContext().getBasePath());

            // Add any query parameters set by the command to the response
            if (interactionContext != null) {
                Map<String, String> outQueryParams = interactionContext.getOutQueryParameters();

                for (Map.Entry<String, String> param : outQueryParams.entrySet()) {
                    linkTemplate.queryParam(param.getKey(), param.getValue());
                }
            }

            if (targetState instanceof DynamicResourceState) {
                return createLinkForDynamicResource(linkTemplate, linkTransitionProps, targetState, entity);
            } else {
                return createLinkForResource(linkTemplate, linkTransitionProps, targetState, queryParameters, entity);
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Dead link [" + transition + "]", e);

            return null;

        } catch (UriBuilderException e) {
            logger.error("Dead link [" + transition + "]", e);
            throw e;
        }
    }

    private void setErrorState(ResourceState targetState, ResourceStateProvider resourceStateProvider) {
        // Target can have errorState which is not a normal transition,
        // so resolve and add it here
        if (targetState.getErrorState() != null) {
            ResourceState errorState = targetState.getErrorState();
            if ((errorState instanceof LazyResourceState || errorState instanceof LazyCollectionResourceState) && errorState.getId().startsWith(".")) {
                // We should resolve and overwrite the one already there
                errorState = resourceStateProvider.getResourceState(errorState.getName());
                targetState.setErrorState(errorState);
            }
        }
    }

    private void processEmbeddedTransitions(ResourceState targetState, ResourceStateProvider resourceStateProvider) {
        for (Transition tmpTransition : targetState.getTransitions()) {
            if (tmpTransition.isType(Transition.EMBEDDED)) {
                if (tmpTransition.getTarget() instanceof LazyResourceState || tmpTransition.getTarget() instanceof LazyCollectionResourceState) {
                    if (tmpTransition.getTarget() != null) {
                        ResourceState tt = resourceStateProvider.getResourceState(tmpTransition.getTarget().getName());
                        if (tt == null) {
                            logger.error("Invalid transition [" + tmpTransition.getId() + "]");
                        }
                        tmpTransition.setTarget(tt);
                    }
                }
            }
        }
    }

    private void configureLink(UriBuilder linkTemplate, Map<String, Object> transitionProperties, String targetResourcePath) {
        // Pass uri parameters as query parameters if they are not
        // replaceable in the path, and replace any token.
        Map<String, String> uriParameters = transition.getCommand().getUriParameters();
        if (uriParameters != null) {
            for (String key : uriParameters.keySet()) {
                String value = uriParameters.get(key);
                if (!targetResourcePath.contains("{" + key + "}")) {

                    String paramValue = LinkGeneratorHelper.replaceParamValue(value, transitionProperties);
                    if (paramValue != null) {
                        linkTemplate.queryParam(key, paramValue);
                    }
                }
            }
        }
    }

    private String setSelfRelValue(ResourceState targetState) {
        String rel = targetState.getRel();

        if (transition.getSource() == targetState) {
            rel = "self";
        }

        return rel;
    }

    private String createLinkForState(ResourceState targetState) {
        StringBuilder rel = new StringBuilder(XmlFormatWriter.related);
        rel.append(targetState.getName());

        return rel.toString();
    }

    private String createLinkForProfile(Transition transition) {

        return transition.getLabel() != null && !transition.getLabel().equals("") ? transition.getLabel() : transition.getTarget().getName();
    }

    private void addQueryParams(MultivaluedMap<String, String> queryParameters, boolean allQueryParameters, UriBuilder linkTemplate, String targetResourcePath, Map<String, String> uriParameters) {
        if (queryParameters != null && allQueryParameters) {
            for (String param : queryParameters.keySet()) {
                if (!targetResourcePath.contains("{" + param + "}") && (uriParameters == null || !uriParameters.containsKey(param))) {
                    linkTemplate.queryParam(param, queryParameters.getFirst(param));
                }
            }
        }
    }

    private Link createLinkForDynamicResource(UriBuilder linkTemplate, LinkTransitionProperties linkTransitionProps, ResourceState targetState, Object entity) {
        // We are dealing with a dynamic target
        // Identify real target state

        Map<String, Object> linkTransitionMap = linkTransitionProps.getTransitionProperties();
        ResourceStateAndParameters stateAndParams = resourceStateMachine.resolveDynamicState((DynamicResourceState) targetState, linkTransitionMap, interactionContext);

        if (stateAndParams.getState() == null) {
            // Bail out as we failed to resolve resource
            return null;
        } else {
            targetState = stateAndParams.getState();
        }

        String method = transition.getCommand().getMethod();
        if (targetState.getRel().contains("http://temenostech.temenos.com/rels/new")) {
            method = "POST";
        }

        String targetPath = targetState.getPath();
        configureLink(linkTemplate, linkTransitionMap, targetPath);
        linkTemplate.path(targetPath);
        String rel = setSelfRelValue(targetState);

        if ("item".equals(rel) || "collection".equals(rel)) {
            rel = createLinkForState(targetState);
        }
        if (stateAndParams.getParams() != null) {
            // Add query parameters
            for (ParameterAndValue paramAndValue : stateAndParams.getParams()) {
                String param = paramAndValue.getParameter();
                String value = paramAndValue.getValue();

                if ("id".equalsIgnoreCase(param)) {
                    linkTransitionMap.put(param, value);
                } else {
                    linkTemplate.queryParam(param, value);
                }
            }
        }
        // Links in the transition properties are already encoded so
        // build the href using encoded map.
        URI href = linkTemplate.buildFromEncodedMap(linkTransitionMap);

        Transition linkTransition = rebuildTransitionWithResolvedTarget(targetState);
        return buildLink(linkTransition, linkTransitionProps, entity, rel, href, method);
    }

    private Link createLinkForResource(UriBuilder linkTemplate, LinkTransitionProperties linkTransitionProps, ResourceState targetState, MultivaluedMap<String, String> queryParameters, Object entity) {

        Map<String, Object> linkTransitionMap = linkTransitionProps.getTransitionProperties();
        // We are NOT dealing with a dynamic target
        String targetPath = targetState.getPath();
        linkTemplate.path(targetPath);
        configureLink(linkTemplate, linkTransitionMap, targetPath);
        String rel = setSelfRelValue(targetState);

        // Pass any query parameters
        addQueryParams(queryParameters, allQueryParameters, linkTemplate, targetState.getPath(), transition.getCommand().getUriParameters());

        // Build href from template
        URI href;
        if (entity != null && resourceStateMachine.getTransformer() == null) {
            logger.debug("Building link with entity (No Transformer) [" + entity + "] [" + transition + "]");
            href = linkTemplate.build(entity);
        } else {
            // Links in the transition properties are already encoded so
            // build the href using encoded map.
            href = linkTemplate.buildFromEncodedMap(linkTransitionMap);
        }

        return buildLink(transition, linkTransitionProps, entity, rel, href, transition.getCommand().getMethod());
    }

    private Link buildLink(Transition linkTransition, LinkTransitionProperties linkTransitionProps, Object entity, String rel, URI href, String method) {
        Link link;
        if (linkTransitionProps.getTransitionProperties().containsKey("profileOEntity") && "self".equals(rel) && entity instanceof OEntity) {
            // Create link adding profile to href to be resolved later on AtomXMLProvider
            link = new Link(linkTransition, rel, href.toASCIIString() + "#@" + createLinkForProfile(linkTransition), method);
        } else {
            // Create link as normal behaviour
            String fieldLabel = linkTransitionProps.getTargetFieldFullyQualifiedName();
            String linkFieldLabel = fieldLabel;
            if (fieldLabel != null && fieldLabel.contains(".")) {
                linkFieldLabel = linkTransition.getSource().getEntityName() + "_" + fieldLabel;
            }
            link = new Link(linkTransition, rel, href.toASCIIString(), method, linkFieldLabel);
        }

        logger.debug("Created link for transition [" + linkTransition + "] [title=" + linkTransition.getId() + ", rel=" + rel + ", method=" + method + ", href=" + href.toString() + "(ASCII=" + href.toASCIIString() + ")]");

        return link;
    }

    private Transition rebuildTransitionWithResolvedTarget(ResourceState resolvedTarget) {
        Transition updatedtransition = new Transition.Builder()
                .source(this.transition.getSource())
                .target(resolvedTarget)
                .label(this.transition.getLabel())
                .method(this.transition.getCommand().getMethod())
                .flags(this.transition.getCommand().getFlags())
                .evaluation(this.transition.getCommand().getEvaluation())
                .locator(this.transition.getLocator())
                .uriParameters(this.transition.getCommand().getUriParameters())
                .linkId(this.transition.getLinkId())
                .sourceField(this.transition.getSourceField())
                .build();

        return updatedtransition;
    }

}
