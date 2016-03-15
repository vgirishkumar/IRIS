package com.temenos.interaction.example.hateoas.simple;

/*
 * #%L
 * interaction-example-authorization-test
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

/*
 * This is a test command. It can be called multiple times. It records how many times it has been called in the 
 * InteractionContext.
 * 
 * On the first call it also stores a reference to a dummy ODataProduce into the InteractionContext. On subsequent call it
 * checks that the reference is still present.
 * 
 * Result status is returned to the client in a mocked up body.
 */
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.commands.odata.ODataAttributes;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.example.hateoas.simple.model.Note;

public class GETMultipleNoteCommand implements InteractionCommand {

	// Names of Interaction contest attributes changed by this command.
	private static String CALLED_COUNT = "TestCalledCount";

	// Dummy OData producer
	private ODataProducer producer;

	public GETMultipleNoteCommand(ODataProducer producer) {
		this.producer = producer;
	}

	@Override
	public Result execute(InteractionContext ctx) throws InteractionException {
		assert (ctx != null);

		// Retrieve note from a database, etc.
		String id = ctx.getId();

		// Increment the called count attribute to indicate that we have been
		// called.
		Integer count = (Integer) ctx.getAttribute(CALLED_COUNT);
		if (null == count) {
			// First time we have been called count is zero.
			count = 0;

			// First time we are called write dummy producer into attribute.
			ctx.setAttribute(ODataAttributes.O_DATA_PRODUCER_ATTRIBUTE, producer);
		}

		// Increment and set called count
		count++;
		ctx.setAttribute(CALLED_COUNT, count);

		// Check is producer is still present.
		ODataProducer actualProducer = (ODataProducer) ctx.getAttribute(ODataAttributes.O_DATA_PRODUCER_ATTRIBUTE);
		if (actualProducer != producer) {
			// Indicate problem to client by failing.
			return Result.FAILURE;
		}

		// Write result into body so client can read it.
		Note note = new Note(new Long(id), "Called count = " + count);

		// Write resource into response
		ctx.setResource(new EntityResource<Note>(note));

		return Result.SUCCESS;
	}
}
