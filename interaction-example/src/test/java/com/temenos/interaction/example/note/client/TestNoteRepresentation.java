package com.temenos.interaction.example.note.client;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestNoteRepresentation {

	@Test
	public void testDeserialiseFromString() {
		String xmlString = "<resource><note><noteID>10</noteID><body>test note</body></note></resource>";
        NoteRepresentation nr = NoteRepresentation.fromXmlString(xmlString);
        assertEquals("test note", nr.getBody());
	}

}
