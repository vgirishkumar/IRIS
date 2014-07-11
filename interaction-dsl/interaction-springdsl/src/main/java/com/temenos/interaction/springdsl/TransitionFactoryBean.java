package com.temenos.interaction.springdsl;

/*
 * #%L
 * interaction-springdsl
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.springframework.beans.factory.FactoryBean;

import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.Transition;
import com.temenos.interaction.core.hypermedia.expression.Expression;

public class TransitionFactoryBean implements FactoryBean<Transition> {

	private ResourceState source, target;
	private String label;

	// TransitionCommand parameters
	private String method;
	private String path;
	private int flags;
	// conditional link evaluation expression
	private Expression evaluation;
	private Map<String, String> uriParameters;
	private List<String> functionList;

	private static final String ENTITY_NAME = "entityName";
	private static final String NAME = "name";
	private static final String ACTIONS = "actions";
	private static final String PATH = "path";
	private static final String LINK_RELATIONS = "linkRelations";
	private static final String FUNCTION = "function";

	@Override
	public Transition getObject() throws Exception {
		Transition.Builder builder = new Transition.Builder();
		builder.source(source);
		builder.target(target);
		builder.method(method);
		builder.path(path);
		builder.flags(flags);
		builder.evaluation(evaluation);
		builder.uriParameters(uriParameters);
		return builder.build();
	}

	@Override
	public Class<? extends Transition> getObjectType() {
		return Transition.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public ResourceState getSource() {
		return source;
	}

	public void setSource(ResourceState source) {
		this.source = source;
	}

	public ResourceState getTarget() {
		return target;
	}

	public void setTarget(ResourceState target) {
		this.target = target;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public Expression getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(Expression evaluation) {
		this.evaluation = evaluation;
	}

	public Map<String, String> getUriParameters() {
		return uriParameters;
	}

	public void setUriParameters(Map<String, String> uriParameters) {
		this.uriParameters = uriParameters;
	}

	/**
	 * Sets the function list.
	 * 
	 * @param functionList
	 *            the new function list
	 */
	public void setFunctionList(List<String> functionList) {
		this.functionList = functionList;
/*
		String entityName = null;
		String name = null;
		String actions = null;
		String path = null;
		String functionName = null;
		String function = null;
		String linkRelations = null;

		if (functionList != null) {
			List<Expression> expressionsList = new ArrayList<Expression>();
			Expression expression = null;

			for (String expressionTxt : functionList) {

				StringTokenizer tokenizer = new StringTokenizer(expressionTxt, ";");
				while (tokenizer.hasMoreTokens()) {
					String keyValue = tokenizer.nextToken().trim();
					StringTokenizer tokenizer2 = new StringTokenizer(keyValue, "=");
					if (tokenizer2.countTokens() == 2) {
						String key = tokenizer2.nextToken();
						if (key.equals(ENTITY_NAME)) {
							entityName = tokenizer2.nextToken().trim();
						} else if (key.equals(NAME)) {
							name = tokenizer2.nextToken().trim();
						} else if (key.equals(ACTIONS)) {
							actions = tokenizer2.nextToken().trim();
						} else if (key.equals(PATH)) {
							path = tokenizer2.nextToken().trim();
						} else if (key.equals(LINK_RELATIONS)) {
							linkRelations = tokenizer2.nextToken().trim();
						} else if (key.equals(FUNCTION)) {
							function = tokenizer2.nextToken().trim();
						}

					}
				}

				List<Action> actionList = createActions(actions);
				String[] rels = createLinkRelations(linkRelations);

				UriSpecification uriSpec = new UriSpecification(name, path);

				ResourceState resourceState = new ResourceState(entityName, name, actionList, path, rels, uriSpec);
				if (function.contains("OK")) {
					expression = new ResourceGETExpression(resourceState, Function.OK);
				} else {
					expression = new ResourceGETExpression(resourceState, Function.NOT_FOUND);
				}
				expressionsList.add(expression);

			}
			evaluation = new SimpleLogicalExpressionEvaluator(expressionsList);
		}
		*/
	}

	/**
	 * Creates the link relations.
	 *
	 * @param linkRelations the link relations
	 * @return the string[]
	 */
	private static String[] createLinkRelations(String linkRelations) {
		List<String> relationsList = new ArrayList<String>();
		relationsList.add(linkRelations);

		String[] relations = relationsList.toArray(new String[relationsList.size()]);
		return relations;
	}

	/**
	 * Creates the actions.
	 * 
	 * @return the list
	 */
	private static List<Action> createActions(String actions) {
		StringTokenizer tokenizer = new StringTokenizer(actions, ",");
		Properties actionViewProperties = null;

		String entity = tokenizer.nextToken().trim();
		String entry = tokenizer.nextToken().trim();

		List<Action> createdActions = new ArrayList<Action>();
		actionViewProperties = new Properties();
		if (entry.equals("Action.TYPE.ENTRY")) {
			createdActions.add(new Action(entity, Action.TYPE.ENTRY, actionViewProperties));
		} else {
			createdActions.add(new Action(entity, Action.TYPE.VIEW, actionViewProperties));
		}
		return createdActions;
	}
}
