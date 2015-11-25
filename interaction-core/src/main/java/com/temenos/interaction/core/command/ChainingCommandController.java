package com.temenos.interaction.core.command;

import java.util.ArrayList;
import java.util.List;

public class ChainingCommandController implements CommandControllerInterface {
	
	private List<? extends CommandControllerInterface> commandControllers = new ArrayList<CommandControllerInterface>();
	
	@Override
	public InteractionCommand fetchCommand(String name) {
		
		for (CommandControllerInterface commandController : commandControllers) {
			InteractionCommand command = commandController.fetchCommand(name);
			if (command!=null) {
				return command;
			}
		}
		
		return null;
	}

	public List<? extends CommandControllerInterface> getCommandControllers() {
		return commandControllers;
	}

	public void setCommandControllers(List<? extends CommandControllerInterface> commandControllers) {
		this.commandControllers = new ArrayList<CommandControllerInterface>(commandControllers);
	}

}
