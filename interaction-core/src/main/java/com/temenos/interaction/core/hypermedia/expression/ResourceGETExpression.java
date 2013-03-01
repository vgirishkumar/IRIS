package com.temenos.interaction.core.hypermedia.expression;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;

public class ResourceGETExpression implements Expression {

	public enum Function {
		OK,
		NOT_FOUND
	}
	
	public final Function function;
	public final String state;
	
	public ResourceGETExpression(String state, Function function) {
		this.function = function;
		this.state = state;
	}
	
	public Function getFunction() {
		return function;
	}

	public String getState() {
		return state;
	}
	
	@Override
	public boolean evaluate(ResourceStateMachine hypermediaEngine, InteractionContext ctx) {
		ResourceState target = hypermediaEngine.getResourceStateByName(state);
		if (target == null)
			throw new IllegalArgumentException("Indicates a problem with the RIM, it allowed an invalid state to be supplied");
		assert(target.getActions() != null);
		assert(target.getActions().size() == 1);
		Action viewAction = target.getActions().get(0);
		assert(viewAction != null) : "Indicates a problem with the RIM, can only use a 'regular' state, one with a view action";
		InteractionCommand command = hypermediaEngine.getCommandController().fetchCommand(viewAction.getName());
		assert(command != null) : "Command not bound";
		InteractionCommand.Result result = command.execute(ctx);
		
		return (getFunction().equals(Function.OK) && result == InteractionCommand.Result.SUCCESS) ||
				(getFunction().equals(Function.NOT_FOUND) && result == InteractionCommand.Result.FAILURE);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (getFunction().equals(ResourceGETExpression.Function.OK))
			sb.append("OK(").append(getState()).append(")");
		if (getFunction().equals(ResourceGETExpression.Function.NOT_FOUND))
			sb.append("NOT_FOUND").append(getState()).append(")");
		return sb.toString();
	}
}
