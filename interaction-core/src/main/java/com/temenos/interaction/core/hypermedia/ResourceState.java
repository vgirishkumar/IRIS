package com.temenos.interaction.core.hypermedia;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.hypermedia.expression.Expression;
import com.temenos.interaction.core.hypermedia.expression.SimpleLogicalExpressionEvaluator;

public class ResourceState implements Comparable<ResourceState> {
	private final static Logger logger = LoggerFactory.getLogger(ResourceState.class);
	private Pattern templatePattern = Pattern.compile("\\{(.*?)\\}");

	/* the parent state (same entity, pseudo state is same path) */
	private final ResourceState parent;
	/* the name of the entity which this is a state of */
	private final String entityName;
	/* the name for this state */
	private final String name;
	/* the path to the create the resource which represents this state of the entity */
	private final String path;
	/* the path parameter to use as the resource identifier */
	private final String pathIdParameter;
	/* a state not represented by a resource, a state of the same entity (see parent) */
	private final boolean pseudo;
	
	/* is an initial state */
	private boolean initial;
	/* is an exception state */
	private boolean exception;

	/* link relations */
	private final String[] rels;
	/* the actions that will be executed upon viewing or entering this state */
	private final List<Action> actions;
	/* the UriSpecification is used to append the path parameter template to the path */
	private final UriSpecification uriSpecification;
	
	private Set<Transition> transitions = new HashSet<Transition>();

	
	/**
	 * Construct a pseudo ResourceState.  A transition to one's self will not create a new resource.
	 * @param parent
	 * @param name
	 */
	public ResourceState(ResourceState parent, String name, List<Action> actions) {
		this(parent, name, actions, null);
	}
	/**
	 * {@link ResourceState(ResourceState, String)}
	 * @param entityName the name of the entity that this object is a state of
	 * @param name this states name
	 * @param path the partial URI to this state, will be prepended with supplied ResourceState path
	 */
	public ResourceState(ResourceState parent, String name, List<Action> actions, String path) {
		this(parent, name, actions, path, null);
	}
	public ResourceState(ResourceState parent, String name, List<Action> actions, String path, String[] rels) {
		this(parent, parent.getEntityName(), name, actions, parent.getPath() + (path == null ? "" : path), null, rels, path == null, null);
	}

	/**
	 * Construct a substate ResourceState.  A transition to a substate state will create a new resource.
	 * @param entityName the name of the entity that this object is a state of
	 * @param name this states name
	 * @param path the fully qualified URI to this state
	 */
	public ResourceState(String entityName, String name, List<Action> actions, String path) {
		this(null, entityName, name, actions, path, null, null, false, null);
	}
	public ResourceState(String entityName, String name, List<Action> actions, String path, String[] rels) {
		this(null, entityName, name, actions, path, null, rels, false, null);
	}
	public ResourceState(String entityName, String name, List<Action> actions, String path, String[] rels, UriSpecification uriSpec) {
		this(null, entityName, name, actions, path, null, rels, false, uriSpec);
	}

	/**
	 * Construct a substate ResourceState.  A transition to a substate state will create a new resource.
	 * @param entityName the name of the entity that this object is a state of
	 * @param name this states name
	 * @param path the uri to this state
	 * @param pathIdParameter override the default {id} path parameter and use the value instead
	 */
	public ResourceState(String entityName, String name, List<Action> actions, String path, String pathIdParameter) {
		this(null, entityName, name, actions, path, pathIdParameter, null, false, null);
	}
	public ResourceState(String entityName, String name, List<Action> actions, String path, String pathIdParameter, String[] rels) {
		this(null, entityName, name, actions, path, pathIdParameter, rels, false, null);
	}

	/**
	 * Construct a ResourceState.  This object contains the instance information required to
	 * create and service a resource.
	 * @param uriSpecification the definition of the pathParameters available to a command bound to
	 * 		this resource state.
	 */
	public ResourceState(String entityName, String name, List<Action> actions, String path, UriSpecification uriSpec) {
		this(null, entityName, name, actions, path, null, null, false, uriSpec);
	}

	private ResourceState(ResourceState parent, String entityName, String name, List<Action> actions, String path, String pathIdParameter, String[] rels, boolean pseudo, UriSpecification uriSpec) {
		assert(name != null);
		assert(path != null && path.length() > 0);
		this.parent = parent;
		this.entityName = entityName;
		this.name = name;
		this.path = path;
		this.pathIdParameter = pathIdParameter;
		this.initial = false;
		this.exception = false;
		this.pseudo = pseudo;
		this.actions = actions;
		this.uriSpecification = uriSpec;
		if (rels == null) {
			this.rels = "item".split(" ");
		} else {
			this.rels = rels;
		}
		assert(this.rels != null);
	}

	public ResourceState getParent() {
		return parent;
	}
	
	public String getEntityName() {
		return entityName;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return entityName + "." + name;
	}

	public String getPath() {
		return path;
	}

	public String getResourcePath() {
		if (getUriSpecification() != null)
			return getUriSpecification().getTemplate();
		return getPath();
	}
	
	public String getPathIdParameter() {
		return pathIdParameter;
	}

	public boolean isPseudoState() {
		return pseudo;
	}
	
	public boolean isTransientState() {
		return (getAllTargets().size() == 1 
				&& getTransition(getAllTargets().iterator().next()).getCommand().isAutoTransition());
	}

	/**
	 * A transient state is a resource state with a single AUTO transition, get the
	 * auto {@link Transition}.
	 * @return the auto transition for this transient state
	 * @invariant this must be a transient state {@link ResourceState#isTransientState()}
	 */
	public Transition getAutoTransition() {
		assert(isTransientState());
		return getTransition(getAllTargets().iterator().next());
	}
	
	public boolean isInitial() {
		return initial;
	}
	
	public void setInitial(boolean flag) {
		initial = flag;
	}
	
	public boolean isException() {
		return exception;
	}
	
	public void setException(boolean flag) {
		exception = flag;
	}
	
	public String getRel() {
		StringBuffer sb = new StringBuffer();
		for (String r : rels)
			sb.append(r).append(" ");
		return sb.deleteCharAt(sb.lastIndexOf(" ")).toString();
	}
	
	public String[] getRels() {
		return rels;
	}
	
	public List<Action> getActions() {
		return actions;
	}
	
	public Action getViewAction() {
		Action action = null;
		for (Action a : actions) {
			if (a.getType().equals(Action.TYPE.VIEW)) {
				action = a;
			}
		}
		return action;
	}
	
	public UriSpecification getUriSpecification() {
		return uriSpecification;
	}

	/**
	 * Return the transition to get to this state.
	 * @return
	 */	
	public Transition getSelfTransition() {
		return new Transition(this, new TransitionCommandSpec("GET", getPath()), this);
	}
	
	/**
	 * Auto transitions, transition from this resource state to target resource state via HTTP status codes 205 or 303.
	 * @param targetState
	 */
	public void addTransition(ResourceState targetState) {
		addTransition(null, targetState, Transition.AUTO);
	}
	public void addTransition(ResourceState targetState, List<Expression> conditionalExpressions) {
		addTransition(null, targetState, Transition.AUTO, conditionalExpressions);
	}

	/**
	 * Normal transitions, transition from this resource state to target resource state by user agent following link.
	 * @param httpMethod
	 * @param targetState
	 */
	public void addTransition(String httpMethod, ResourceState targetState) {
		addTransition(httpMethod, targetState, 0);
	}
	public void addTransition(String httpMethod, ResourceState targetState, List<Expression> conditionalExpressions) {
		addTransition(httpMethod, targetState, null, null, 0, conditionalExpressions, null);
	}
	public void addTransition(String httpMethod, ResourceState targetState, int transitionFlags) {
		addTransition(httpMethod, targetState, null, null, transitionFlags, null, null);
	}
	public void addTransition(String httpMethod, ResourceState targetState, int transitionFlags, List<Expression> conditionalExpressions) {
		addTransition(httpMethod, targetState, null, null, transitionFlags, conditionalExpressions, null);
	}
	
	/**
	 * Add a transition with a target state and linkage map.
	 * @param httpMethod
	 * @param targetState
	 * @param uriLinkageMap
	 */
	public void addTransition(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap) {
		addTransition(httpMethod, targetState, uriLinkageMap, null, 0, null, null);
	}

	/**
	 * Add a transition with a target state and linkage map.
	 * @param httpMethod
	 * @param targetState
	 * @param uriLinkageMap
	 * @param uriLinkageProperties
	 */
	public void addTransition(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap, Map<String, String> uriLinkageProperties) {
		addTransition(httpMethod, targetState, uriLinkageMap, uriLinkageProperties, 0, null, null);
	}

	/**
	 * Add a transition with a target state and linkage map.
	 * @param httpMethod HTTP method
	 * @param targetState Target state
	 * @param uriLinkageMap map holding entity property values for resource path template parameters 
	 * @param uriLinkageProperties map holding additional property values for resource path template parameters
	 * @param label transition label
	 */
	public void addTransition(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap, Map<String, String> uriLinkageProperties, String label) {
		addTransition(httpMethod, targetState, uriLinkageMap, uriLinkageProperties, 0, null, label);
	}
	
	public void addTransition(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap, Map<String, String> uriLinkageProperties, int transitionFlags, List<Expression> conditionalExpressions, String label) {
		String resourcePath = targetState.getPath();
		addTransition(httpMethod, targetState, uriLinkageMap, uriLinkageProperties, resourcePath, transitionFlags, conditionalExpressions, label);
	}
	
	protected void addTransition(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap, Map<String, String> uriLinkageProperties, String resourcePath, int transitionFlags, List<Expression> conditionalExpressions, String label) {
		assert null != targetState;
		if (httpMethod != null && (transitionFlags & Transition.AUTO) == Transition.AUTO)
			throw new IllegalArgumentException("An auto transition cannot have an HttpMethod supplied");
		
		//Copy linkage properties to ensure they are not overwritten
		uriLinkageMap = uriLinkageMap != null ? new HashMap<String, String>(uriLinkageMap) : null;
		Map<String, String> linkParameters = null;
		if(uriLinkageProperties != null) {
			linkParameters = new HashMap<String, String>(uriLinkageProperties);
			uriLinkageProperties.clear();		//Subsequent invocations to addTransition are expected to provide new link properties
		}

		//Replace uri elements with linkage properties
		String mappedResourcePath = resourcePath;
		if (uriLinkageMap != null) {
			for (String templateElement : uriLinkageMap.keySet()) {
				mappedResourcePath = mappedResourcePath.replaceAll("\\{" + templateElement + "\\}", "\\{" + uriLinkageMap.get(templateElement) + "\\}");
			}
		}
		if (linkParameters != null) {
			for (String templateElement : linkParameters.keySet()) {
				mappedResourcePath = mappedResourcePath.replaceAll("\\{" + templateElement + "\\}", linkParameters.get(templateElement));		//Replace template elements, e.g. {code} in filter=fld eq '{code}'
			}
		}
		
		//Replace templates in link properties with path parameters
		replaceLinkPropertyTemplates(linkParameters, uriLinkageMap);
		
		//Apply link properties to action parameters
		applyLinkPropertiesToActionParameters(linkParameters, targetState);
		
		//Create the transition
		Expression condition = conditionalExpressions != null ? new SimpleLogicalExpressionEvaluator(conditionalExpressions) : null;
		TransitionCommandSpec commandSpec = new TransitionCommandSpec(httpMethod, mappedResourcePath, transitionFlags, condition, resourcePath, linkParameters);
		Transition transition = new Transition(this, commandSpec, targetState, label);
		logger.debug("Putting transition: " + commandSpec + " [" + transition + "]");
		transitions.add(transition);
	}

	public void addTransition(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap, String resourcePath, boolean forEach) {
		addTransition(httpMethod, targetState, uriLinkageMap, null, resourcePath, (forEach ? Transition.FOR_EACH : 0), null, null);
	}

	public void addTransition(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap, Map<String, String> uriLinkageProperties, String resourcePath, boolean forEach, String label) {
		addTransition(httpMethod, targetState, uriLinkageMap, uriLinkageProperties, resourcePath, (forEach ? Transition.FOR_EACH : 0), null, label);
	}
	
	/**
	 * Add transition to another resource interaction model.
	 * @param httpMethod
	 * @param resourceStateModel
	 */
	public void addTransition(String httpMethod, ResourceStateMachine resourceStateModel) {
		assert resourceStateModel != null;
		TransitionCommandSpec commandSpec = new TransitionCommandSpec(httpMethod, resourceStateModel.getInitial().getPath());
		transitions.add(new Transition(this, commandSpec, resourceStateModel.getInitial()));
	}
	
	/**
	 * Replace templates in link properties with path parameters.
	 * e.g. [id=code, filter=fld eq '{code}'] => [filter=fld eq '{id}']
	 * @param linkParameters link properties
	 * @param uriLinkageMap map of properties to path parameters
	 */
	protected void replaceLinkPropertyTemplates(Map<String, String> linkParameters, Map<String, String> uriLinkageMap) {
		//Replace templates in link properties with path parameters, e.g. {code} in filter=fld eq '{code}'
		if (linkParameters != null) {
			for (String propKey : linkParameters.keySet()) {
				String propValue = linkParameters.get(propKey);
				if(propValue != null && uriLinkageMap != null
						&& propValue.contains("{") && propValue.contains("}")) {
					Matcher m = templatePattern.matcher(propValue);
					while(m.find()) {
						String templateElement = m.group(1);	//e.g. code
						for (String key : uriLinkageMap.keySet()) {		//e.g. id=code -> replace templateElement {code} with {id}
							if(!linkParameters.containsKey(templateElement) &&
									templateElement.equals(uriLinkageMap.get(key))) {
								propValue = propValue.replaceAll("\\{" + templateElement + "\\}", "\\{" + key + "\\}");
								linkParameters.put(propKey, propValue);
							}
						}
					}				
				}						
			}
		}		
	}
	
	/**
	 * Apply link properties to action parameters.
	 * e.g. [GETEntities filter=myfilter] where [myfilter=fld eq '{code}', code="mycode"] => [filter=fld eq '{mycode}']
	 * @param linkParameters link properties
	 * @param targetState target resource state
	 */
	protected void applyLinkPropertiesToActionParameters(Map<String, String> linkParameters, ResourceState targetState) {
		if (linkParameters != null) {
			for(Action action  : targetState.getActions()) {
				for(Entry<Object, Object> actionParameter : action.getProperties().entrySet()) {
					Object paramValue = actionParameter.getValue();				//reference to link property (e.g. myfilter) or the actual link property  
					if(paramValue != null) {
						//Reference to a linkage property, e.g. filter=myfilter where myfilter references myfilter=fld eq '{code}'
						if(paramValue instanceof String && linkParameters.containsKey(paramValue)) {
							actionParameter.setValue(new ActionPropertyReference((String) paramValue));	
						}
						if(actionParameter.getValue() instanceof ActionPropertyReference) {
							ActionPropertyReference actionRefProperty = (ActionPropertyReference) actionParameter.getValue();
							String paramRefValue = linkParameters.get(actionRefProperty.getKey());
							if (paramRefValue != null) {
								String paramRefKey = "_";		
								Matcher m = templatePattern.matcher(paramRefValue);
								while(m.find()) {
									String param = m.group(1);								//e.g. code
									String linkParameter = linkParameters.get(param);		//e.g. mycode
									if(linkParameter != null) {
										//replace template parameter with uri linkage properties (e.g. code => mycode if code="mycode")
										paramRefValue = m.replaceAll("{" + linkParameter + "}");		//e.g. a eq {code1} && b eq {code2}
										paramRefKey += "_" + linkParameter;								//e.g. _code1_code2
									}
								}
								actionRefProperty.addProperty(paramRefKey, paramRefValue);
							} else {
								logger.error("You appear to have specified a transition to a resource that requires command parameters, but you have not specified any parameters in your trasition");
							}
						}
					}
				}
			}
		}		
	}

	/**
	 * Get the transition to the supplied target state.
	 * @param targetState
	 * @return
	 */
	public Transition getTransition(ResourceState targetState) {
		Transition foundTransition = null;
		for (Transition t : transitions) {
			if (t.getTarget().equals(targetState)) {
				if (foundTransition != null)
					logger.error("Duplicate transition definition [" + t + "]");
				assert(foundTransition == null);  // transition must be defined more than once
				foundTransition = t;
			}
		}
		return foundTransition;
	}

	/**
	 * Get the transitions to the supplied target state.
	 * @param targetState
	 * @return transitions
	 */
	public List<Transition> getTransitions(ResourceState targetState) {
		List<Transition> transitionList = new ArrayList<Transition>();
		for (Transition t : transitions) {
			if (t.getTarget().equals(targetState)) {
				transitionList.add(t);
			}
		}
		return transitionList;
	}
	
	public Collection<ResourceState> getAllTargets() {
		List<ResourceState> result = new ArrayList<ResourceState>();
		for (Transition t : transitions) {
			ResourceState targetState = t.getTarget();
			if(!result.contains(targetState)) {
				result.add(targetState);
			}
		}
		return result;
	}
	
	/**
	 * A final state has no transitions.
	 * @return
	 */
	public boolean isFinalState() {
		return transitions.isEmpty();
	}
	
	public boolean equals(Object other) {
		//check for self-comparison
	    if ( this == other ) return true;
	    if ( !(other instanceof ResourceState) ) return false;
	    ResourceState otherState = (ResourceState) other;
	    return entityName.equals(otherState.entityName) &&
	    	name.equals(otherState.name) &&
	    	((path == null && otherState.path == null) || (path != null && path.equals(otherState.path))) &&
	    	transitions.equals(otherState.transitions);
	}
	
	public int hashCode() {
		// TODO proper implementation of hashCode, important as we intend to use the in our DSL validation
		return entityName.hashCode() +
			name.hashCode() +
			(path != null ? path.hashCode() : 0) +
			transitions.hashCode();
	}
	
	public String toString() {
		return getId();
	}

	@Override
	public int compareTo(ResourceState other) {
	    if ( this == other ) return 0;
		return other.getId().compareTo(getId());
	}
}
