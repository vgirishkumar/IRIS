package com.temenos.interaction.authorization.command;

/*
 * Base class for the Authorization command tests.
 */

/* 
 * #%L
 * interaction-commands-authorization
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

import org.junit.After;
import org.junit.Before;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;

public class AbstractAuthorizationTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() {
	}

	/*
	 * A mock child class that can be called by the AuthorzationCommand.
	 */
	protected class MockCommand implements InteractionCommand {
		// Somewhere to store the context we are passed
		InteractionContext ctx = null;

		public MockCommand() {
		}

		public Result execute(InteractionContext ctx) throws InteractionException {
			this.ctx = ctx;
			return (Result.SUCCESS);
		}
	}
}
