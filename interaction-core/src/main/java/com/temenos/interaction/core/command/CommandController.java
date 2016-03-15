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

/**
 * Interface of registry of {@link InteractionCommand}, returning an instance for a given name.
 * Abstracting the command registry initialisation and command resolution logic from the upper layers.
 * @author pblair
 * @author trojanbug
 */
public interface CommandController {

    /**
     * 
     * @param name The name of the command as declared in RIM, derived from the request
     * @return Instance of {@link InteractionCommand} corresponding to name argument, or null, if not found. Implementations are discouraged from throwing exceptions if the name cannot be found, to support composition.
     */
    public InteractionCommand fetchCommand(String name);
    public boolean isValidCommand(String name);
}