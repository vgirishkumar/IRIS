package com.temenos.interaction.rimdsl.generator.launcher.translators;

import com.google.common.collect.Iterables;
import com.temenos.interaction.core.hypermedia.*;
import com.temenos.interaction.core.hypermedia.expression.ResourceGETExpression;
import com.temenos.interaction.core.hypermedia.expression.SimpleLogicalExpressionEvaluator;
import com.temenos.interaction.rimdsl.rim.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import javax.inject.Inject;
import java.util.*;

/**
 * @author kwieconkowski
 */

/* Class containing useful methods to implement RimResourceTranslatorObject interface with `PURE` concept.
  * The `PURE` concept is just a name, which suggest that translator don`t use origin translator delegating the work.
  * Using this concept we get usually faster translator. */
public abstract class RimResourceTranslatorPureAbstract {

    @Inject
    @Extension
    protected IQualifiedNameProvider qualifiedNameProvider;

    protected ResourceState createResourceStateOrCollectionDependingOfType(State state, String entityName, String name, List<Action> actionList, String path, String[] relations, UriSpecification uriSpecification, ResourceState errorState) {
        if (state.getType().isIsCollection()) {
            return new CollectionResourceState(name, entityName, actionList, path, relations, uriSpecification, errorState);
        } else {
            return new ResourceState(entityName, name, actionList, path, relations, uriSpecification, errorState);
        }
    }

    protected String transitionTargetStateVariableName(TransitionRef transition) {
        if (transition.getState() != null) {
            return getStateName(transition.getState());
        } else {
            String targetState = "";

            if (transition.getName() != null && !transition.getName().isEmpty() && transition.getName().lastIndexOf(".") > 1) {
                // Construct string of format: domain_resource-state
                targetState = transition.getName().substring(0, transition.getName().lastIndexOf(".")) + "-" + transition.getName().substring(transition.getName().lastIndexOf(".") + 1);
                targetState = targetState.replaceAll("\\.", "_");
            } else if (transition.getName() != null) {
                targetState = transition.getName();
            }

            return targetState;
        }
    }

    protected com.temenos.interaction.core.hypermedia.expression.Expression getEvaluation(TransitionRef transition) {
        if (isTransitionExpressionAvailable(transition)) {
            List<com.temenos.interaction.core.hypermedia.expression.Expression> expressions = new ArrayList<com.temenos.interaction.core.hypermedia.expression.Expression>();
            for (Function expression : transition.getSpec().getEval().getExpressions()) {
                ResourceState expressionTargetResourceState = getExpressionTargetResourceState(expression);
                ResourceGETExpression.Function expressionFunction = getExpressionFunction(expression);
                expressions.add(new ResourceGETExpression(expressionTargetResourceState, expressionFunction));
            }

            if (!expressions.isEmpty()) {
                return new SimpleLogicalExpressionEvaluator(expressions);
            }
        }

        return null;
    }

    protected ResourceGETExpression.Function getExpressionFunction(Function expression) {
        ResourceGETExpression.Function expressionFunction;
        if (expression instanceof OKFunction) {
            expressionFunction = ResourceGETExpression.Function.OK;
        } else {
            expressionFunction = ResourceGETExpression.Function.NOT_FOUND;
        }
        return expressionFunction;
    }

    protected ResourceState getExpressionTargetResourceState(Function expression) {
        ResourceState expressionTargetResourceState = null;

        State expressionTargetState = getExpressionTargetState(expression);
        if (IsLazyCollectionResourceState(expressionTargetState)) {
            expressionTargetResourceState = new LazyCollectionResourceState(getStateName(expressionTargetState));
        } else {
            expressionTargetResourceState = new LazyResourceState(getStateName(expressionTargetState));
        }
        return expressionTargetResourceState;
    }

    protected boolean IsLazyCollectionResourceState(State expressionTargetState) {
        return expressionTargetState != null && expressionTargetState.getType() != null && expressionTargetState.getType().isIsCollection();
    }

    protected boolean isTransitionExpressionAvailable(TransitionRef transition) {
        return transition.getSpec() != null && transition.getSpec().getEval() != null;
    }

    protected Map<String, String> getUriParameters(TransitionRef transition) {
        Map<String, String> uriParameters = new HashMap<String, String>();
        if (transition.getSpec() != null && transition.getSpec().getUriLinks() != null) {
            for (UriLink link : transition.getSpec().getUriLinks()) {
                uriParameters.put(link.getTemplateProperty(), link.getEntityProperty().getName());
            }
        }
        return uriParameters;
    }

    protected ResourceState getTransitionTarget(State state, TransitionRef transition) {
        ResourceState transitionTarget = null;
        if (transition.getLocator() == null) {
            if (IsLazyCollectionResourceState(transition.getState())) {
                transitionTarget = new LazyCollectionResourceState(transitionTargetStateVariableName(transition));
            } else {
                transitionTarget = new LazyResourceState(transitionTargetStateVariableName(transition));
            }
        } else {
            String entityName = state.getEntity().getName();
            String resourceLocatorName = transition.getLocator().getName();

            String[] resourceLocatorArgs = new String[transition.getLocator().getArgs().size()];
            for (int i = 0; i <= transition.getLocator().getArgs().size(); i++) {
                resourceLocatorArgs[i] = transition.getLocator().getArgs().get(i);
            }
            transitionTarget = new DynamicResourceState(entityName, "dynamic", resourceLocatorName, resourceLocatorArgs);
        }
        return transitionTarget;
    }

    protected boolean isTransitionAvailable(TransitionRef transition) {
        return (transition.getState() != null && transition.getState().getName() != null) || transition.getName() != null || transition.getLocator() != null;
    }

    protected State getExpressionTargetState(Function expression) {
        if (expression instanceof OKFunction) {
            return ((OKFunction) expression).getState();
        } else {
            return ((NotFoundFunction) expression).getState();
        }
    }

    protected ResourceState getErrorState(final ResourceInteractionModel rim, final State state) {
        ResourceState errorResourceState = null;
        if (state.getErrorState() != null) {
            if (state.getType() != null && state.getType().isIsCollection()) {
                errorResourceState = new LazyCollectionResourceState(getStateName(state.getErrorState()));
            } else {
                errorResourceState = new LazyResourceState(getStateName(state.getErrorState()));
            }
        }
        return errorResourceState;
    }

    protected UriSpecification getUriSpecification(final ResourceInteractionModel rim, final State state) {
        UriSpecification uriSpecification = null;
        if (state.getPath() != null) {
            String name = state.getName();
            String template = getPath(rim, state);
            uriSpecification = new UriSpecification(name, template);
        }
        return uriSpecification;
    }

    protected String[] getRelations(final EList<RelationRef> relations) {
        if (relations == null || relations.size() == 0) {
            return null;
        }

        RelationRef relation = null;
        String[] relationsNames = new String[relations.size()];
        for (int i = 0; i < relations.size(); i++) {
            relation = relations.get(i);
            if (relation instanceof RelationConstant) {
                relationsNames[i] = ((RelationConstant) relation).getName();
            } else {
                relationsNames[i] = relation.getRelation().getFqn();
            }
        }
        return relationsNames;
    }


    protected List<Action> getActionList(final State state, final ImplRef implRef) {
        if (implRef == null) {
            return null;
        }

        List<Action> actionList = new ArrayList<Action>();
        addViewAction(implRef, actionList);
        addEntryActions(implRef, actionList);
        addMethodsActions(implRef, actionList);

        return returnListWithEntriesOrNull(actionList);
    }

    protected List returnListWithEntriesOrNull(List actionList) {
        return actionList.size() != 0 ? actionList : null;
    }

    protected void addMethodsActions(ImplRef implRef, List<Action> actionList) {
        if (implRef.getMethods() != null) {
            for (MethodRef method : implRef.getMethods()) {
                actionList.add(getAction(method.getCommand(), determineActionTypeFromHttpMethod(method), method.getEvent().getHttpMethod()));
            }
        }
    }

    protected void addEntryActions(ImplRef implRef, List<Action> actionList) {
        if (implRef.getActions() != null) {
            for (ResourceCommand action : implRef.getActions()) {
                actionList.add(getAction(action, Action.TYPE.ENTRY));
            }
        }
    }

    protected void addViewAction(ImplRef implRef, List<Action> actionList) {
        ResourceCommand view = implRef.getView();
        if (view != null) {
            actionList.add(getAction(view, Action.TYPE.VIEW));
        }
    }

    protected Action getAction(ResourceCommand resourceCommand, Action.TYPE actionType) {
        return getAction(resourceCommand, actionType, null);
    }

    protected Action getAction(ResourceCommand resourceCommand, Action.TYPE actionType, String httpMethodName) {
        String name = resourceCommand.getCommand().getName();
        Properties properties = new Properties();
        extractPropertiesFromSpecAndCopyExistingProperties(resourceCommand, properties);
        return new Action(name, actionType, properties, httpMethodName);
    }

    protected Action.TYPE determineActionTypeFromHttpMethod(MethodRef method) {
        return method.getEvent().getHttpMethod().equals("GET") ? Action.TYPE.VIEW : Action.TYPE.ENTRY;
    }

    protected void extractPropertiesFromSpecAndCopyExistingProperties(ResourceCommand resourceCommand, Properties properties) {
        if ((resourceCommand.getCommand().getSpec() != null && resourceCommand.getCommand().getSpec().getProperties().size() > 0)) {
            for (CommandProperty property : resourceCommand.getCommand().getSpec().getProperties()) {
                properties.put(property.getName(), property.getValue());
            }
        }
        for (CommandProperty property : resourceCommand.getProperties()) {
            properties.put(property.getName(), property.getValue());
        }
    }

    protected String getStateName(final State state) {
        String stateName = null;
        if (state != null && state.getName() != null) {
            String fullyQualifiedName = qualifiedNameProvider.getFullyQualifiedName(state).toString("_");
            stateName = replaceUnderscoreBeforeStateNameWithLine(fullyQualifiedName, state.getName());
        }
        return stateName;
    }

    protected String getMethods(final ResourceInteractionModel rim, final State state) {
        StringBuilder methodsString = new StringBuilder();
        EList<MethodRef> methods = state.getImpl().getMethods();
        if (methods == null || methods.size() == 0) {
            if (state.getImpl().getView() != null) {
                methodsString.append("GET");
            } else if (state.getImpl().getActions() != null) {
                methodsString.append("POST");
            }
        }

        if (methods != null && methods.size() > 0) {
            for (int i = 1; i < methods.size(); i++) {
                MethodRef method = methods.get(i);
                methodsString.append("," + method.getEvent().getHttpMethod());

            }
        }
        return methodsString.toString();
    }

    protected String getPath(final ResourceInteractionModel rim, final State state) {
        if (rim.getBasepath() != null) {
            return getFullPathOrBasepathWithStateName(rim, state);
        }
        return getSlashOrSlashWithStateName(state);
    }

    protected String getFullPathOrBasepathWithStateName(ResourceInteractionModel rim, State state) {
        return state.getPath() != null ? rim.getBasepath().getName() + state.getPath().getName() : String.format("%s/%s", rim.getBasepath().getName(), state.getName());
    }

    protected String getSlashOrSlashWithStateName(State state) {
        return state.getPath() != null ? state.getPath().getName() : String.format("/%s", state.getName());
    }

    protected String getTransitionLabel(final TransitionRef transition) {
        String unescapedLabel = null;
        if (transition.getSpec() != null && transition.getSpec().getTitle() != null) {
            unescapedLabel = transition.getSpec().getTitle().getName();
        } else if (transition.getState() != null) {
            unescapedLabel = transition.getState().getName();
        } else {
            unescapedLabel = transition.getName();
        }

        return StringEscapeUtils.escapeXml(unescapedLabel);
    }

    protected String getTransitionLinkId(final TransitionRef transition) {
        return getSpecNameOrNull(transition);
    }

    protected String getSpecNameOrNull(TransitionRef transition) {
        return (transition.getSpec() != null
                && transition.getSpec().getId() != null
                && !transition.getSpec().getId().getName().isEmpty())
                ? transition.getSpec().getId().getName()
                : null;
    }

    protected Iterable<ResourceInteractionModel> getResourceInteractionModels(Resource resource) {
        TreeIterator<EObject> _allContents = resource.getAllContents();
        Iterable<EObject> _iterable = IteratorExtensions.<EObject>toIterable(_allContents);
        return Iterables.<ResourceInteractionModel>filter(_iterable, ResourceInteractionModel.class);
    }

    protected String replaceUnderscoreBeforeStateNameWithLine(String fullString, String stateName) {
        int underscoreIndex = fullString.lastIndexOf("_" + stateName);
        if (underscoreIndex >= 0) {
            return String.format("%s-%s", fullString.substring(0, underscoreIndex), stateName);
        }
        return fullString;
    }
}
