package com.temenos.interaction.core.workflow;

import java.util.ArrayList;
import java.util.List;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;

/**
 * This command implements a naive workflow where all commands are executed.
 * Commands are added to this workflow and then executed in the same order regardless
 * of their return code {@link Result.SUCCESS} or otherwise.
 * @author aphethean
 */
public class NaiveWorkflowStrategyCommand implements InteractionCommand {

	private List<InteractionCommand> commands = new ArrayList<InteractionCommand>();
	
	public NaiveWorkflowStrategyCommand() {}
	
	/**
	 * Construct with a list of commands to execute.
	 * @param commands
	 * @invariant commands not null
	 */
	public NaiveWorkflowStrategyCommand(List<InteractionCommand> commands) {
		this.commands = commands;
		if (commands == null)
			throw new IllegalArgumentException("No commands supplied");		
	}
	
	public void addCommand(InteractionCommand command) {
		if (command == null)
			throw new IllegalArgumentException("No command supplied");		
		commands.add(command);
	}

	/**
	 * @throws InteractionException 
	 * @precondition at least one command has been added {@link addCommand}
	 * @postcondition returned {@link Result) will always be the result
	 * of the last command.
	 */
	@Override
	public Result execute(InteractionContext ctx) throws InteractionException {
		assert(commands != null);
		assert(commands.size() > 0) : "There must be at least one command in the workflow";
		if (ctx == null)
			throw new IllegalArgumentException("InteractionContext must be supplied");

		Result result = null;
		for (InteractionCommand command : commands) {
			result = command.execute(ctx);
		}
		return result;
	}

}
