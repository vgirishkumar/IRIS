package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
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


import java.util.HashMap;
import java.util.Map;

import com.temenos.interaction.core.hypermedia.expression.Expression;

/**
 * Define how a transition from one state to another should occur.
 * @author aphethean
 */
public class TransitionCommandSpec {

	private final String method;
	private final String path;
	private final int flags;
	// conditional link evaluation expression 
	private final Expression evaluation;
	private final Map<String, String> uriParameters;
	
	protected TransitionCommandSpec(String method, String path) {
		this(method, path, 0);
	}

	protected TransitionCommandSpec(String method, String path, int flags) {
		this(method, path, flags, null);
	}
	
	protected TransitionCommandSpec(String method, String path, int flags, Expression evaluation) {
		this(method, path, flags, evaluation, null);
	}

	protected TransitionCommandSpec(String method, String path, int flags, Expression evaluation, Map<String, String> uriParameters) {
		this.method = method;
		this.path = path;
		this.flags = flags;
		this.evaluation = evaluation;
		this.uriParameters = uriParameters != null ? new HashMap<String, String>(uriParameters) : null;
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

	public Expression getEvaluation() {
		return evaluation;
	}

	public Map<String, String> getUriParameters() {
		return uriParameters;
	}
	
	/**
	 * Is this transition command to be applied to each item in a collection?
	 * @return
	 */
	public boolean isForEach() {
		return ((flags & Transition.FOR_EACH) == Transition.FOR_EACH);
	}
	
	/**
	 * Is this transition an auto transition?
	 * @return
	 */
	public boolean isAutoTransition() {
		return ((flags & Transition.AUTO) == Transition.AUTO);
	}

	/**
	 * Is this transition a redirect transition?
	 * @return
	 */
	public boolean isRedirectTransition() {
		return ((flags & Transition.REDIRECT) == Transition.REDIRECT);
	}

	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof TransitionCommandSpec)) return false;
		TransitionCommandSpec otherObj = (TransitionCommandSpec) other;
		return this.getFlags() == otherObj.getFlags() &&
				((this.getPath() == null && otherObj.getPath() == null) || (this.getPath() != null && this.getPath().equals(otherObj.getPath()))) &&
				((this.getMethod() == null && otherObj.getMethod() == null) || (this.getMethod() != null && this.getMethod().equals(otherObj.getMethod())) &&
				((this.getUriParameters() == null && otherObj.getUriParameters() == null) || (this.getUriParameters() != null && this.getUriParameters().equals(otherObj.getUriParameters()))));
	}
	
	public int hashCode() {
		return this.flags 
				+ (this.path != null ? this.path.hashCode() : 0)
				+ (this.method != null ? this.method.hashCode() : 0)
				+ (this.uriParameters != null ? this.uriParameters.hashCode() : 0);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (isForEach())
			sb.append("*");
		if (isAutoTransition()) {
			sb.append("AUTO");
		} else {
			sb.append(method);
		}
		sb.append(path != null && path.length() > 0 ? " " + path : "");
		if (evaluation != null) {
			sb.append(" (");
			sb.append(evaluation.toString());
			sb.append(")");
		}
		if (uriParameters != null && uriParameters.size() > 0) {
			sb.append(" ");
			for(String key : uriParameters.keySet()) {
				String value = uriParameters.get(key);
				sb.append(key + "=" + value);
			}
		}
		return sb.toString();
	}
}
