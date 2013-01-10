package com.temenos.interaction.core.hypermedia;

import java.util.Map;

import com.temenos.interaction.core.hypermedia.expression.ResourceGETExpression;

/**
 * Define how a transition from one state to another should occur.
 * @author aphethean
 */
public class TransitionCommandSpec {

	private final String method;
	private final String path;
	private final int flags;
	// conditional link evaluation expression 
	private final ResourceGETExpression evaluation;
	private final Map<String, String> parameters;
	
	protected TransitionCommandSpec(String method, String path) {
		this(method, path, 0);
	}

	protected TransitionCommandSpec(String method, String path, int flags) {
		this(method, path, flags, null);
	}
	
	protected TransitionCommandSpec(String method, String path, int flags, ResourceGETExpression evaluation) {
		this(method, path, flags, evaluation, null);
	}

	protected TransitionCommandSpec(String method, String path, int flags, ResourceGETExpression evaluation, Map<String, String> parameters) {
		this.method = method;
		this.path = path;
		this.flags = flags;
		this.evaluation = evaluation;
		this.parameters = parameters;
	}
	
	public String getPath() {
		return path;
	}
	
	public int getFlags() {
		return flags;
	}

	public String getMethod() {
		return method;
	}

	public ResourceGETExpression getEvaluation() {
		return evaluation;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}
	
	/**
	 * Is this transition command to be applied to each item in a collection?
	 * @return
	 */
	public boolean isForEach() {
		return ((flags & Transition.FOR_EACH) == Transition.FOR_EACH);
	}
	
	/**
	 * Is this transition and auto transition?
	 * @return
	 */
	public boolean isAutoTransition() {
		return ((flags & Transition.AUTO) == Transition.AUTO);
	}

	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof TransitionCommandSpec)) return false;
		TransitionCommandSpec otherObj = (TransitionCommandSpec) other;
		return this.getFlags() == otherObj.getFlags() &&
				((this.getPath() == null && otherObj.getPath() == null) || (this.getPath() != null && this.getPath().equals(otherObj.getPath()))) &&
				((this.getMethod() == null && otherObj.getMethod() == null) || (this.getMethod() != null && this.getMethod().equals(otherObj.getMethod())) &&
				((this.getParameters() == null && otherObj.getParameters() == null) || (this.getParameters() != null && this.getParameters().equals(otherObj.getParameters()))));
	}
	
	public int hashCode() {
		return this.flags 
				+ (this.path != null ? this.path.hashCode() : 0)
				+ (this.method != null ? this.method.hashCode() : 0)
				+ (this.parameters != null ? this.parameters.hashCode() : 0);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (isForEach())
			sb.append("*");
		sb.append(method + (path != null && path.length() > 0 ? " " + path : ""));
		if (evaluation != null) {
			sb.append(" (");
			if (evaluation.getFunction().equals(ResourceGETExpression.Function.OK))
				sb.append("OK(").append(evaluation.getState()).append(")");
			if (evaluation.getFunction().equals(ResourceGETExpression.Function.NOT_FOUND))
				sb.append("NOT_FOUND").append(evaluation.getState()).append(")");
			sb.append(")");
		}
		if (parameters != null) {
			sb.append(" ");
			for(String key : parameters.keySet()) {
				String value = parameters.get(key);
				sb.append(key + "=" + value);
			}
		}
		return sb.toString();
	}
}
