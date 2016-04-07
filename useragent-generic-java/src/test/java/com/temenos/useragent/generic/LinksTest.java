package com.temenos.useragent.generic;

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
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.temenos.useragent.generic.internal.ActionableLink;
import com.temenos.useragent.generic.internal.Payload;
import com.temenos.useragent.generic.internal.SessionContext;

public class LinksTest {

	private SessionContext mockSessionContext = mock(SessionContext.class);

	@Test
	public void testByRelForMultipleLinks() {
		List<Link> linksList = new ArrayList<Link>();
		linksList.add(new TestLink().rel("rel1"));
		linksList.add(new TestLink().rel("rel1"));
		linksList.add(new TestLink().rel("rel2"));
		Links links = Links.create(linksList, mockSessionContext);
		assertEquals("rel1", links.byRel("rel1").rel());
	}

	@Test
	public void testByRelForSingleLink() {
		List<Link> linksList = new ArrayList<Link>();
		linksList.add(new TestLink().rel("rel1"));
		Links links = Links.create(linksList, mockSessionContext);
		assertEquals("rel1", links.byRel("rel1").rel());
	}

	@Test(expected = IllegalStateException.class)
	public void testByRelForNoLink() {
		Links.create(new ArrayList<Link>(), mockSessionContext).byRel("foo");
	}

	@Test
	public void testAllByRelForMultipleLinks() {
		List<Link> linksList = new ArrayList<Link>();
		linksList.add(new TestLink().rel("rel1"));
		linksList.add(new TestLink().rel("rel1"));
		linksList.add(new TestLink().rel("rel2"));
		Links links = Links.create(linksList, mockSessionContext);
		List<ActionableLink> actionableLinks = links.allByRel("rel1");
		assertEquals(2, actionableLinks.size());
		assertEquals("rel1", actionableLinks.get(0).rel());
		assertEquals("rel1", actionableLinks.get(1).rel());
	}

	@Test
	public void testAllByRelForNoLinks() {
		Links links = Links.create(new ArrayList<Link>(), mockSessionContext);
		List<ActionableLink> actionableLinks = links.allByRel("foo");
		assertTrue(actionableLinks.isEmpty());
	}

	@Test
	public void testByHrefForMultipleLinks() {
		List<Link> linksList = new ArrayList<Link>();
		linksList.add(new TestLink().href("href1"));
		linksList.add(new TestLink().href("href1"));
		linksList.add(new TestLink().href("href2"));
		Links links = Links.create(linksList, mockSessionContext);
		assertEquals("href1", links.byHref("href1").href());
	}

	@Test
	public void testByHrefForSingleLink() {
		List<Link> linksList = new ArrayList<Link>();
		linksList.add(new TestLink().href("href1"));
		Links links = Links.create(linksList, mockSessionContext);
		assertEquals("href1", links.byHref("href1").href());
	}

	@Test(expected = IllegalStateException.class)
	public void testByHrefForNoLink() {
		Links.create(new ArrayList<Link>(), mockSessionContext).byHref("foo");
	}

	@Test
	public void testAllByHrefForMultipleLinks() {
		List<Link> linksList = new ArrayList<Link>();
		linksList.add(new TestLink().href("href1"));
		linksList.add(new TestLink().href("href1"));
		linksList.add(new TestLink().href("href2"));
		Links links = Links.create(linksList, mockSessionContext);
		List<ActionableLink> actionableLinks = links.allByHref("href1");
		assertEquals(2, actionableLinks.size());
		assertEquals("href1", actionableLinks.get(0).href());
		assertEquals("href1", actionableLinks.get(1).href());
	}

	@Test
	public void testAllByHrefForNoLinks() {
		Links links = Links.create(new ArrayList<Link>(), mockSessionContext);
		List<ActionableLink> actionableLinks = links.allByHref("foo");
		assertTrue(actionableLinks.isEmpty());
	}

	@Test
	public void testByIdForMultipleLinks() {
		List<Link> linksList = new ArrayList<Link>();
		linksList.add(new TestLink().id("id1"));
		linksList.add(new TestLink().id("id1"));
		linksList.add(new TestLink().id("id2"));
		Links links = Links.create(linksList, mockSessionContext);
		assertEquals("id1", links.byId("id1").id());
	}

	@Test
	public void testByIdForSingleLink() {
		List<Link> linksList = new ArrayList<Link>();
		linksList.add(new TestLink().id("id1"));
		Links links = Links.create(linksList, mockSessionContext);
		assertEquals("id1", links.byId("id1").id());
	}

	@Test(expected = IllegalStateException.class)
	public void testByIdForNoLink() {
		Links.create(new ArrayList<Link>(), mockSessionContext).byId("foo");
	}

	@Test
	public void testAllByIdForMultipleLinks() {
		List<Link> linksList = new ArrayList<Link>();
		linksList.add(new TestLink().id("id1"));
		linksList.add(new TestLink().id("id1"));
		linksList.add(new TestLink().id("id2"));
		Links links = Links.create(linksList, mockSessionContext);
		List<ActionableLink> actionableLinks = links.allById("id1");
		assertEquals(2, actionableLinks.size());
		assertEquals("id1", actionableLinks.get(0).id());
		assertEquals("id1", actionableLinks.get(1).id());
	}

	@Test
	public void testAllByIdForNoLinks() {
		Links links = Links.create(new ArrayList<Link>(), mockSessionContext);
		List<ActionableLink> actionableLinks = links.allById("foo");
		assertTrue(actionableLinks.isEmpty());
	}

	@Test
	public void testByTitleForMultipleLinks() {
		List<Link> linksList = new ArrayList<Link>();
		linksList.add(new TestLink().title("title1"));
		linksList.add(new TestLink().title("title1"));
		linksList.add(new TestLink().title("title2"));
		Links links = Links.create(linksList, mockSessionContext);
		assertEquals("title1", links.byTitle("title1").title());
	}

	@Test
	public void testByTitleForSingleLink() {
		List<Link> linksList = new ArrayList<Link>();
		linksList.add(new TestLink().title("title1"));
		Links links = Links.create(linksList, mockSessionContext);
		assertEquals("title1", links.byTitle("title1").title());
	}

	@Test(expected = IllegalStateException.class)
	public void testByTitleForNoLink() {
		Links.create(new ArrayList<Link>(), mockSessionContext).byTitle("foo");
	}

	@Test
	public void testAllByTitleForMultipleLinks() {
		List<Link> linksList = new ArrayList<Link>();
		linksList.add(new TestLink().title("title1"));
		linksList.add(new TestLink().title("title1"));
		linksList.add(new TestLink().title("title2"));
		Links links = Links.create(linksList, mockSessionContext);
		List<ActionableLink> actionableLinks = links.allByTitle("title1");
		assertEquals(2, actionableLinks.size());
		assertEquals("title1", actionableLinks.get(0).title());
		assertEquals("title1", actionableLinks.get(1).title());
	}

	@Test
	public void testAllByTitleForNoLinks() {
		Links links = Links.create(new ArrayList<Link>(), mockSessionContext);
		List<ActionableLink> actionableLinks = links.allByTitle("foo");
		assertTrue(actionableLinks.isEmpty());
	}

	@Test
	public void testAllForMultipleLinks() {
		List<Link> linksList = new ArrayList<Link>();
		linksList.add(new TestLink().rel("rel1"));
		linksList.add(new TestLink().rel("rel1"));
		linksList.add(new TestLink().rel("rel2"));
		Links links = Links.create(linksList, mockSessionContext);
		assertEquals(3, links.all().size());
		assertEquals("rel1", links.all().get(0).rel());
		assertEquals("rel1", links.all().get(1).rel());
		assertEquals("rel2", links.all().get(2).rel());
	}

	@Test
	public void testAllForNoLinks() {
		List<Link> linksList = new ArrayList<Link>();
		Links links = Links.create(linksList, mockSessionContext);
		assertTrue(links.all().isEmpty());
	}

	private static class TestLink implements Link {

		private String title = "";
		private String href = "";
		private String rel = "";
		private String id = "";

		@Override
		public String title() {
			return title;
		}

		@Override
		public String href() {
			return href;
		}

		@Override
		public String rel() {
			return rel;
		}

		@Override
		public String baseUrl() {
			return null;
		}

		@Override
		public String id() {
			return id;
		}

		public TestLink title(String title) {
			this.title = title;
			return this;
		}

		public TestLink href(String href) {
			this.href = href;
			return this;
		}

		public TestLink rel(String rel) {
			this.rel = rel;
			return this;
		}

		public TestLink id(String id) {
			this.id = id;
			return this;
		}

		public boolean hasEmbeddedPayload() {
			return false;
		}

		@Override
		public Payload embedded() {
			return null;
		}
	}
}
