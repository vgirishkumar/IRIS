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
	private final Map<String, String> parameters;
	
	// the original unmapped resourcePath (required to form a correct interaction map by paths)
	private final String originalPath;
		protected TransitionCommandSpec(String method, String path) {
		this(method, path, 0);
	}

	protected TransitionCommandSpec(String method, String path, int flags) {
		this(method, path, flags, null, path);
	}
	
	protected TransitionCommandSpec(String method, String path, int flags, Expression evaluation, String originalPath) {
		this(method, path, flags, evaluation, originalPath, null);
	}

	protected TransitionCommandSpec(String method, String path, int flags, Expression evaluation, Map<String, String> parameters) {
		this(method, path, flags, evaluation, path, parameters);
	}
	
	protected TransitionCommandSpec(String method, String path, int flags, Expression evaluation, String originalPath, Map<String, String> parameters) {
		this.method = method;
		this.path = path;
		this.flags = flags;
		this.evaluation = evaluation;
		this.originalPath = originalPath;		
		this.parameters = parameters;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getOriginalPath() {
		return originalPath;
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
			sb.append(evaluation.toString());
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
