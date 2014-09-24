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


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.temenos.interaction.loader.properties.PropertiesEvent;

public class TestSequentialAction {

	@Test
	public void test() {
		List<Action> actions = new ArrayList<Action>();
		Action action1 = mock(Action.class);
		actions.add(action1);
		Action action2 = mock(Action.class);
		actions.add(action2);
		
		SequentialAction action = new SequentialAction(actions);
		PropertiesEvent event = mock(PropertiesEvent.class);
		
		action.execute(event);
		
		verify(action1).execute(event);
		verify(action2).execute(event);		
	}

}
