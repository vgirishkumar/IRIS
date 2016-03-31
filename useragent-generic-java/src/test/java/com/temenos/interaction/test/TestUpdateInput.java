package com.temenos.interaction.test;

/*
 * #%L
 * useragent-generic-java
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
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

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.temenos.useragent.generic.InteractionSession;
import com.temenos.useragent.generic.internal.DefaultInteractionSession;
import com.temenos.useragent.generic.mediatype.AtomPayloadHandler;

@Ignore
// TODO: EXISTS ONLY FOR REFERENCE. To be removed and replaced with integration
// tests against services through an embedded server.
public class TestUpdateInput {

	@Test
	public void testGetAllEntitiesUsingAVersion() {
		InteractionSession session = DefaultInteractionSession.newSession();
		session.basicAuth("INPUTT", "123456")
				.url()
				.baseuri(
						"http://localhost:9089/t24interactiontests-iris/t24interactiontests.svc/GB0010001")
				.path("verFundsTransfers()").get();
	}

	@Test
	public void testCreateNewEntityAndUpdate() {
		InteractionSession session = DefaultInteractionSession.newSession();
		session.registerHandler("application/atom+xml",
				AtomPayloadHandler.class)
				.basicAuth("INPUTT", "123456")
				.header("Content-Type", "application/atom+xml")
				.url()
				.baseuri(
						"http://localhost:9089/t24interactiontests-iris/t24interactiontests.svc/GB0010001")
				.path("verCustomer_Inputs()/new").post();

		assertEquals(201, session.result().code());

		String id = session.reuse().entity().get("CustomerCode");
		session.set("Mnemonic", "C" + id)
				.set("verCustomer_Input_Name1MvGroup/Name1",
						"Mr Robin Peterson" + id)
				.set("verCustomer_Input_ShortNameMvGroup/ShortName", "Rob" + id)
				.set("Sector", "1001").set("Gender", "MALE").set("Title", "MR")
				.set("FamilyName", "Peterson" + id).entity().links()
				.byRel("http://temenostech.temenos.com/rels/input").url()
				.post();

		assertEquals(201, session.result().code());

		session.reuse().basicAuth("AUTHOR", "123456").links()
				.byRel("http://temenostech.temenos.com/rels/authorise").url()
				.put();

		assertEquals(200, session.result().code());
	}

	@Test
	public void testForConflictWithConcurrentModificationOfAResource() {

		InteractionSession setupSession = DefaultInteractionSession
				.newSession();
		setupSession
				.header("Content-Type", "application/atom+xml")
				.url()
				.baseuri(
						"http://localhost:9089/t24interactiontests-iris/t24interactiontests.svc/GB0010001")
				.path("verCustomer_Inputs()/new").post();
		assertEquals(201, setupSession.result().code());

		String id = setupSession.reuse().entity().get("CustomerCode");
		setupSession
				.set("Mnemonic", "C" + id)
				.set("verCustomer_Input_Name1MvGroup/Name1",
						"Mr Robin Peterson" + id)
				.set("verCustomer_Input_ShortNameMvGroup/ShortName", "Rob" + id)
				.set("Sector", "1001").set("Gender", "MALE").set("Title", "MR")
				.set("FamilyName", "Peterson" + id).entity().links()
				.byRel("http://temenostech.temenos.com/rels/input").url()
				.post();
		assertEquals(201, setupSession.result().code());

		setupSession.reuse().basicAuth("AUTHOR", "123456").links()
				.byRel("http://temenostech.temenos.com/rels/authorise").url()
				.put();
		assertEquals(200, setupSession.result().code());

		InteractionSession session1 = DefaultInteractionSession.newSession();
		session1.url()
				.baseuri(
						"http://localhost:9089/t24interactiontests-iris/t24interactiontests.svc/GB0010001")
				.path("verCustomer_Inputs('" + id + "')").get();
		String session1Etag = session1.header("ETag");

		InteractionSession session2 = DefaultInteractionSession.newSession();
		session2.url()
				.baseuri(
						"http://localhost:9089/t24interactiontests-iris/t24interactiontests.svc/GB0010001")
				.path("verCustomer_Inputs('" + id + "')").get();
		String session2Etag = session2.header("ETag");

		session1.reuse().header("If-Match", session1Etag)
				.header("Content-Type", "application/atom+xml")
				.set("Gender", "FEMALE").set("Title", "MS").links()
				.byRel("http://temenostech.temenos.com/rels/input").url()
				.post();
		assertEquals(200, setupSession.result().code());

		session2.reuse().header("If-Match", session2Etag)
				.header("Content-Type", "application/atom+xml")
				.set("FamilyName", "Peter" + id).links()
				.byRel("http://temenostech.temenos.com/rels/input").url()
				.post();

		assertEquals(
				"EB-RESOURCE.MODIFIED",
				session2.reuse().links()
						.byRel("http://temenostech.temenos.com/rels/errors")
						.embedded().entity().get("Errors_ErrorsMvGroup/Code"));
	}

	@Test
	public void testForConflictOnReInputOfHeldResource() {
		InteractionSession setupSession = DefaultInteractionSession
				.newSession();
		setupSession
				.header("Content-Type", "application/atom+xml")
				.url()
				.baseuri(
						"http://localhost:9089/t24interactiontests-iris/t24interactiontests.svc/GB0010001")
				.path("verCustomer_Inputs()/new").post();
		assertEquals(201, setupSession.result().code());

		String id = setupSession.reuse().entity().get("CustomerCode");
		setupSession
				.set("Mnemonic", "C" + id)
				.set("verCustomer_Input_Name1MvGroup/Name1",
						"Mr Robin Peterson" + id)
				.set("verCustomer_Input_ShortNameMvGroup/ShortName", "Rob" + id)
				.set("Sector", "1001").set("Gender", "MALE").set("Title", "MR")
				.set("FamilyName", "Peterson" + id).entity().links()
				.byRel("http://temenostech.temenos.com/rels/input").url()
				.post();
		assertEquals(201, setupSession.result().code());

		setupSession.reuse().basicAuth("AUTHOR", "123456").links()
				.byRel("http://temenostech.temenos.com/rels/authorise").url()
				.put();
		assertEquals(200, setupSession.result().code());

		InteractionSession holdSession = DefaultInteractionSession.newSession();
		holdSession
				.url()
				.baseuri(
						"http://localhost:9089/t24interactiontests-iris/t24interactiontests.svc/GB0010001")
				.path("verCustomer_Inputs('" + id + "')").get();
		holdSession.reuse().header("If-Match", holdSession.header("ETag"))
				.header("Content-Type", "application/atom+xml")
				.set("FamilyName", "Peter" + id).entity().links()
				.byRel("http://temenostech.temenos.com/rels/hold").url().post();
		assertEquals(201, holdSession.result().code());

		InteractionSession inputSession = DefaultInteractionSession
				.newSession();
		inputSession
				.url()
				.baseuri(
						"http://localhost:9089/t24interactiontests-iris/t24interactiontests.svc/GB0010001")
				.path("verCustomer_Inputs('" + id + "')").get();

		inputSession.reuse().basicAuth("AUTHOR", "123456")
				.header("If-Match", inputSession.header("ETag"))
				.header("Content-Type", "application/atom+xml")
				.set("Gender", "FEMALE").set("Title", "MS").links()
				.byRel("http://temenostech.temenos.com/rels/input").url()
				.post();
		assertEquals(201, holdSession.result().code());
	}
}
