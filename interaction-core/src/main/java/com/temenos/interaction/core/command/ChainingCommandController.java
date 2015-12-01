package com.temenos.interaction.core.command;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2015 Temenos Holdings N.V.
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

/**
 * Implementation of {@link CommandController} delegating the command resolution to a chain of wrapped CommandController implementations. 
 * The first CommandController to return non-null from fetchCommand "wins". If not found on any, return null
 * @author trojanbug
 */
public class ChainingCommandController implements CommandController {

    private List<? extends CommandController> commandControllers = new ArrayList<CommandController>();

    @Override
    public InteractionCommand fetchCommand(String name) {

        for (CommandController commandController : commandControllers) {
            InteractionCommand command = commandController.fetchCommand(name);
            if (command != null) {
                return command;
            }
        }

        return null;
    }

    @Override
    public boolean isValidCommand(String name) {
        for (CommandController commandController : commandControllers) {
            if (commandController.isValidCommand(name)) {
                return true;
            }
        }

        return false;
    }

    public List<? extends CommandController> getCommandControllers() {
        return commandControllers;
    }

    public void setCommandControllers(List<? extends CommandController> commandControllers) {
        this.commandControllers = new ArrayList<CommandController>(commandControllers);
    }

}
