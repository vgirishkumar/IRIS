package com.temenos.interaction.loader.resource.action;

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

import com.temenos.interaction.core.loader.Action;
import com.temenos.interaction.core.loader.FileEvent;

public class SequentialAction implements Action<FileEvent> {
	List<Action> actions = new ArrayList<Action>();
	
	public SequentialAction(List<Action> actions) {
		this.actions = actions;
	}

	@Override
	public void execute(FileEvent event) {
		for (Action action : actions) {
			action.execute(event);
		}
	}
}
