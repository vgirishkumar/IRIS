package com.temenos.interaction.rimdsl.generator.launcher.translators;

import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.UriSpecification;
import com.temenos.interaction.rimdsl.rim.*;
import com.temenos.interaction.springdsl.TransitionFactoryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kwieconkowski
 */

/* Class translating RimResource to ResourceStates list with 'PURE' concept. The PURE concept is explained in RimResourceTranslatorPureAbstract abstract class */
public class RimResourceTranslatorToResourceStatesPure extends RimResourceTranslatorPureAbstract implements RimResourceTranslatorObject<List<ResourceState>> {

    @Override
    public List<ResourceState> translate(ResourceInteractionModel rimResource) {
        List<ResourceState> resourceStatesList = new ArrayList<ResourceState>();
        for (final State state : rimResource.getStates()) {
            addResourceStateToList(rimResource, state, resourceStatesList);
        }
        return resourceStatesList;
    }

    protected void addResourceStateToList(final ResourceInteractionModel rim, final State state, List<ResourceState> resourceStates) {
        String entityName = state.getEntity().getName();
        String name = state.getName();
        List<Action> actionList = getActionList(state, state.getImpl());
        String path = getPath(rim, state);
        String[] relations = getRelations(state.getRelations());
        UriSpecification uriSpecification = getUriSpecification(rim, state);
        ResourceState errorState = getErrorState(rim, state);

        ResourceState resourceState = createResourceStateOrCollectionDependingOfType(state, entityName, name, actionList, path, relations, uriSpecification, errorState);
        populateResourceStateSetters(resourceState, rim, state);

        resourceStates.add(resourceState);
    }

    protected void populateResourceStateSetters(ResourceState resourceState, final ResourceInteractionModel rim, final State state) {
        resourceState.setInitial(state.isIsInitial());
        resourceState.setException(state.isIsException());
        if (state.getCache() > 0) {
            resourceState.setMaxAge(state.getCache());
        }
        resourceState.setTransitions(populateTransitions(state));
    }

    protected List<com.temenos.interaction.core.hypermedia.Transition> populateTransitions(State state) {
        List<com.temenos.interaction.core.hypermedia.Transition> transitionsList = new ArrayList<com.temenos.interaction.core.hypermedia.Transition>();

        if (state.getTransitions() == null) {
            return transitionsList;
        }
        for (TransitionRef transition : state.getTransitions()) {
            if (isTransitionFilledCorrectly(transition)) {
                if (transition instanceof Transition) {
                    addTransitionToList(state, transition, transitionsList);
                } else if (transition instanceof TransitionForEach) {
                    addTransitionForEachToList(state, (TransitionForEach) transition, transitionsList);
                } else if (transition instanceof TransitionAuto) {
                    addTransitionAutoToList(state, (TransitionAuto) transition, transitionsList);
                } else if (transition instanceof TransitionRedirect) {
                    addTransitionRedirectToList(state, (TransitionRedirect) transition, transitionsList);
                } else if (transition instanceof TransitionEmbedded) {
                    addTransitionEmbeddedToList(state, (TransitionEmbedded) transition, transitionsList);
                } else if (transition instanceof TransitionEmbeddedForEach) {
                    addTransitionEmbeddedForEach(state, (TransitionEmbeddedForEach) transition, transitionsList);
                }
            }
        }
        return transitionsList;
    }

    protected boolean isTransitionFilledCorrectly(TransitionRef transition) {
        return (transition.getState() != null && transition.getState().getName() != null) || transition.getName() != null || transition.getLocator() != null;
    }

    protected void addTransitionToList(State state, TransitionRef transition, List<com.temenos.interaction.core.hypermedia.Transition> transitionsList) {
        TransitionFactoryBean transitionFactoryBean = new TransitionFactoryBean();

        transitionFactoryBean.setLabel(getTransitionLabel(transition));
        commonPartOfSettingTransitionFactoryBean(state, transition, transitionFactoryBean);

        insertTransitionToList(transitionsList, transitionFactoryBean);
    }

    protected void commonPartOfSettingTransitionFactoryBean(State state, TransitionRef transition, TransitionFactoryBean transitionFactoryBean) {
        transitionFactoryBean.setMethod(transition.getEvent().getHttpMethod());
        transitionFactoryBean.setTarget(getTransitionTarget(state, transition));
        transitionFactoryBean.setUriParameters(getUriParameters(transition));
        transitionFactoryBean.setEvaluation(getEvaluation(transition));
    }

    protected void insertTransitionToList(List<com.temenos.interaction.core.hypermedia.Transition> transitionsList, TransitionFactoryBean transitionFactoryBean) {
        try {
            transitionsList.add(transitionFactoryBean.getObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void addTransitionForEachToList(State state, TransitionForEach transition, List<com.temenos.interaction.core.hypermedia.Transition> transitionsList) {
        TransitionFactoryBean transitionFactoryBean = new TransitionFactoryBean();

        transitionFactoryBean.setFlags(com.temenos.interaction.core.hypermedia.Transition.FOR_EACH);
        transitionFactoryBean.setLabel(getTransitionLabel(transition));
        transitionFactoryBean.setLinkId(getTransitionLinkId(transition));
        commonPartOfSettingTransitionFactoryBean(state, transition, transitionFactoryBean);

        insertTransitionToList(transitionsList, transitionFactoryBean);
    }

    protected void addTransitionEmbeddedForEach(State state, TransitionEmbeddedForEach transition, List<com.temenos.interaction.core.hypermedia.Transition> transitionsList) {
        TransitionFactoryBean transitionFactoryBean = new TransitionFactoryBean();

        transitionFactoryBean.setFlags(com.temenos.interaction.core.hypermedia.Transition.FOR_EACH_EMBEDDED);
        transitionFactoryBean.setLabel(getTransitionLabel(transition));
        transitionFactoryBean.setLinkId(getTransitionLinkId(transition));
        commonPartOfSettingTransitionFactoryBean(state, transition, transitionFactoryBean);

        insertTransitionToList(transitionsList, transitionFactoryBean);
    }

    protected void addTransitionEmbeddedToList(State state, TransitionEmbedded transition, List<com.temenos.interaction.core.hypermedia.Transition> transitionsList) {
        TransitionFactoryBean transitionFactoryBean = new TransitionFactoryBean();

        transitionFactoryBean.setFlags(com.temenos.interaction.core.hypermedia.Transition.EMBEDDED);
        transitionFactoryBean.setLabel(getTransitionLabel(transition));
        commonPartOfSettingTransitionFactoryBean(state, transition, transitionFactoryBean);

        insertTransitionToList(transitionsList, transitionFactoryBean);
    }

    protected void addTransitionRedirectToList(State state, TransitionRedirect transition, List<com.temenos.interaction.core.hypermedia.Transition> transitionsList) {
        TransitionFactoryBean transitionFactoryBean = new TransitionFactoryBean();

        transitionFactoryBean.setFlags(com.temenos.interaction.core.hypermedia.Transition.REDIRECT);
        commonPartOfSettingTransitionFactoryBean(state, transition, transitionFactoryBean);

        insertTransitionToList(transitionsList, transitionFactoryBean);
    }

    protected void addTransitionAutoToList(State state, TransitionAuto transition, List<com.temenos.interaction.core.hypermedia.Transition> transitionsList) {
        TransitionFactoryBean transitionFactoryBean = new TransitionFactoryBean();

        transitionFactoryBean.setFlags(com.temenos.interaction.core.hypermedia.Transition.AUTO);
        commonPartOfSettingTransitionFactoryBean(state, transition, transitionFactoryBean);

        insertTransitionToList(transitionsList, transitionFactoryBean);
    }
}
