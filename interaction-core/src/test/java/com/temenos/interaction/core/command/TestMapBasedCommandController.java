package com.temenos.interaction.core.command;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestMapBasedCommandController {

	@InjectMocks
	private MapBasedCommandController controller;
	
	@Before
	public void setUp() throws Exception {
		this.controller = new MapBasedCommandController(new HashMap<String, InteractionCommand>() {
			private static final long serialVersionUID = 1L;
			{
				this.put("testCommand1", new TestCommand());
				this.put("testCommand2", new TestCommand());
			}
		});
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFetchCommand() {
		assertNotNull(this.controller.fetchCommand("testCommand1"));
		assertNull(this.controller.fetchCommand("testCommand123214321"));
	}

	@Test
	public void testIsValidCommand() {
		assertTrue(this.controller.isValidCommand("testCommand1"));
		assertFalse(this.controller.isValidCommand("testCommand12325415"));
	}
	
	@Test
	public void testMapIsNotUsedAfterSetting() {
		Map<String,InteractionCommand> commandMap = new HashMap<String, InteractionCommand>() {
			private static final long serialVersionUID = 1L;
			{
				this.put("testCommand1", new TestCommand());
				this.put("testCommand2", new TestCommand());
			}
		};
		MapBasedCommandController contrller = new MapBasedCommandController(commandMap);
		commandMap.clear();
		assertNotNull(contrller.fetchCommand("testCommand1"));
	}
}
