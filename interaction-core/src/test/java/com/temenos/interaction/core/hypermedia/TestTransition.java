package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-core
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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestTransition {

	@Test
	public void testEquality() {
		ResourceState begin = new ResourceState("entity", "", new ArrayList<Action>(), "/");
		ResourceState begin2 = new ResourceState("entity", "", new ArrayList<Action>(), "/");

		Transition t = new Transition(begin, new TransitionCommandSpec("PUT", "stuff", Transition.FOR_EACH), begin2);
		Transition t2 = new Transition(begin, new TransitionCommandSpec("PUT", "stuff", Transition.FOR_EACH), begin2);
		assertEquals(t, t2);
		assertEquals(t.hashCode(), t2.hashCode());
	}
	
	@Test
	public void testEqualityNullSource() {
		ResourceState begin2 = new ResourceState("entity", "", new ArrayList<Action>(), "/");
		Transition t = new Transition(null, new TransitionCommandSpec("PUT", "stuff", Transition.FOR_EACH), begin2);
		Transition t2 = new Transition(null, new TransitionCommandSpec("PUT", "stuff", Transition.FOR_EACH), begin2);
		assertEquals(t, t2);
		assertEquals(t.hashCode(), t2.hashCode());
	}

	@Test 
	public void testInequality() {
		ResourceState begin = new ResourceState("entity", "begin", new ArrayList<Action>(), "/");
		ResourceState exists = new ResourceState("entity", "exists", new ArrayList<Action>(), "{id}");
		ResourceState end = new ResourceState("entity", "end", new ArrayList<Action>(), "/");

		Transition t = new Transition(begin, new TransitionCommandSpec("PUT", "stuff", Transition.FOR_EACH), end);
		Transition t2 = new Transition(begin, new TransitionCommandSpec("PUT", "stuff", Transition.FOR_EACH), exists);
		assertFalse(t.equals(t2));
		assertFalse(t.hashCode() == t2.hashCode());
		
		Transition t3 = new Transition(begin, new TransitionCommandSpec("PUT", "stuff", Transition.FOR_EACH), end);
		Transition t4 = new Transition(begin, new TransitionCommandSpec(null, "stuffed", Transition.AUTO), end);
		assertFalse(t3.equals(t4));
		assertFalse(t3.hashCode() == t4.hashCode());

	}

	@Test 
	public void testInequalityUriParameters() {
		ResourceState begin = new ResourceState("entity", "collection", new ArrayList<Action>(), "/");
		ResourceState exists = new ResourceState("entity", "onetype", new ArrayList<Action>(), "{id}");

		Map<String, String> uriParameters = new HashMap<String, String>();
		uriParameters.put("id", "abc");
		Transition t = new Transition(begin, new TransitionCommandSpec("PUT", "stuff", Transition.FOR_EACH, null, uriParameters, null), exists);
		
		uriParameters.clear();
		uriParameters.put("id", "xyz");
		Transition t2 = new Transition(begin, new TransitionCommandSpec("PUT", "stuff", Transition.FOR_EACH, null, uriParameters, null), exists);
		assertFalse(t.equals(t2));
		assertFalse(t.hashCode() == t2.hashCode());
	}

	@Test 
	public void testInequalityLabel() {
		ResourceState begin = new ResourceState("entity", "collection", new ArrayList<Action>(), "/");
		ResourceState exists = new ResourceState("entity", "onetype", new ArrayList<Action>(), "{id}");

		Transition t = new Transition(begin, new TransitionCommandSpec("PUT", "stuff", Transition.FOR_EACH, null, null, null), exists, "label1");
		Transition t2 = new Transition(begin, new TransitionCommandSpec("PUT", "stuff", Transition.FOR_EACH, null, null, null), exists, "differentlabel");
		assertFalse(t.equals(t2));
		assertFalse(t.hashCode() == t2.hashCode());
	}
	
	@Test
	public void testGetId() {
		ResourceState begin = new ResourceState("entity", "begin", new ArrayList<Action>(), "{id}");
		ResourceState end = new ResourceState("entity", "end", new ArrayList<Action>(), "{id}");

		Transition t = new Transition(begin, new TransitionCommandSpec("PUT", "stuff", 0), end);
		assertEquals("entity.begin>PUT>entity.end", t.getId());
	}

	@Test
	public void testToString() {
		ResourceState begin = new ResourceState("entity", "begin", new ArrayList<Action>(), "/begin");
		ResourceState end = new ResourceState("entity", "end", new ArrayList<Action>(), "/end");

		Transition t = new Transition(begin, new TransitionCommandSpec("PUT", "stuff", 0), end);
		assertEquals("entity.begin>PUT>entity.end", t.toString());
	}

	@Test
	public void testGetLabel() {
		ResourceState begin = new ResourceState("entity", "begin", new ArrayList<Action>(), "{id}");
		ResourceState end = new ResourceState("entity", "end", new ArrayList<Action>(), "{id}");

		Transition ta = new Transition(begin, new TransitionCommandSpec("GET", "stuff", 0), end, "A");
		assertEquals("A", ta.getLabel());
		Transition tb = new Transition(begin, new TransitionCommandSpec("GET", "stuff", 0), end, "B");
		assertEquals("B", tb.getLabel());
	}

	@Test
	public void testIdMultiTransitions() {
		ResourceState begin = new ResourceState("entity", "begin", new ArrayList<Action>(), "{id}");
		ResourceState end = new ResourceState("entity", "end", new ArrayList<Action>(), "{id}");

		Transition ta = new Transition(begin, new TransitionCommandSpec("GET", "stuff", 0), end, "A");
		Transition taPut = new Transition(begin, new TransitionCommandSpec("PUT", "stuff", 0), end, "A");
		assertEquals("entity.begin>GET(A)>entity.end", ta.getId());
		assertEquals("entity.begin>PUT(A)>entity.end", taPut.getId());
		Transition tb = new Transition(begin, new TransitionCommandSpec("GET", "stuff", 0), end, "B");
		assertEquals("entity.begin>GET(B)>entity.end", tb.getId());
	}

	@Test
	public void testCheckTransitionFromCollectionToEntityResource() {
		ResourceState begin = new ResourceState("entity", "begin", new ArrayList<Action>(), "{id}");
		ResourceState end = new ResourceState("entity", "end", new ArrayList<Action>(), "{id}");
		Transition t = new Transition(begin, new TransitionCommandSpec("GET", "stuff", 0), end);
		assertFalse(t.isGetFromCollectionToEntityResource());

		begin = new ResourceState("entity", "begin", new ArrayList<Action>(), "{id}");
		end = new CollectionResourceState("entity", "end", new ArrayList<Action>(), "{id}");
		t = new Transition(begin, new TransitionCommandSpec("GET", "stuff", 0), end);
		assertFalse(t.isGetFromCollectionToEntityResource());

		begin = new CollectionResourceState("entity", "begin", new ArrayList<Action>(), "{id}");
		end = new ResourceState("entity", "end", new ArrayList<Action>(), "{id}");
		t = new Transition(begin, new TransitionCommandSpec("GET", "stuff", 0), end);
		assertTrue(t.isGetFromCollectionToEntityResource());

		begin = new CollectionResourceState("entity", "begin", new ArrayList<Action>(), "{id}");
		end = new ResourceState("otherEntity", "end", new ArrayList<Action>(), "{id}");
		t = new Transition(begin, new TransitionCommandSpec("GET", "stuff", 0), end);
		assertFalse(t.isGetFromCollectionToEntityResource());

		begin = new CollectionResourceState("otherEntity", "begin", new ArrayList<Action>(), "{id}");
		end = new ResourceState("entity", "end", new ArrayList<Action>(), "{id}");
		t = new Transition(begin, new TransitionCommandSpec("GET", "stuff", 0), end);
		assertFalse(t.isGetFromCollectionToEntityResource());
	}

	@Test
	public void testIdMultiTransitionsWithParametersNoLabel() {
		ResourceState begin = new ResourceState("entity", "begin", new ArrayList<Action>(), "{id}");
		ResourceState end = new ResourceState("entity", "end", new ArrayList<Action>(), "{id}");

		Map<String, String> params = new HashMap<String, String>();
		params.put("paramA", "hello A");
		Transition ta = new Transition(begin, new TransitionCommandSpec("GET", "stuff", 0, null, null, params), end);
		Transition taPut = new Transition(begin, new TransitionCommandSpec("PUT", "stuff", 0), end);
		assertEquals("entity.begin>GET(hello A)>entity.end", ta.getId());
		assertEquals("entity.begin>PUT>entity.end", taPut.getId());
		params = new HashMap<String, String>();
		params.put("paramB", "hello B");
		Transition tb = new Transition(begin, new TransitionCommandSpec("GET", "stuff", 0, null, null, params), end);
		assertEquals("entity.begin>GET(hello B)>entity.end", tb.getId());
	}
}
