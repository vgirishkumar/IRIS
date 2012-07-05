package com.temenos.interaction.core.link;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestLink {

	@Test
	public void testGetHrefTransition() throws Exception {
		Link link = new Link("NoteToPersonLink", "Person", 
				"http://localhost:8080/example/interaction-odata-notes.svc/Notes(1)/Person",
				null, null, "GET", "NoteToPersonLink", "", null);
		String hrefTransition = link.getHrefTransition("example/interaction-odata-notes.svc");
		assertEquals("Notes(1)/Person", hrefTransition);
	}
}
