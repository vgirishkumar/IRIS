package com.temenos.interaction.core.command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.startsWith;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestChainingCommandController {

	@InjectMocks
	private ChainingCommandController controller;
	
	@Mock
	private CommandController commandController;
	
	@Before
	public void setUp() throws Exception {
		when(this.commandController.isValidCommand(startsWith("testCommand"))).thenReturn(true);
		when(this.commandController.fetchCommand(startsWith("testCommand"))).thenReturn(new TestCommand());
		this.controller.setCommandControllers(Arrays.asList(new CommandController[]{
			this.commandController
		}));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFetchCommand() {
		assertThat(this.controller.fetchCommand("testCommand1"), notNullValue());
	}
	
	@Test
	public void testFetchCommandWithInvalidCommandName(){
		assertThat(this.controller.fetchCommand("notAValidCommand"), nullValue());
	}

	@Test
	public void testIsValidCommand() {
		assertThat(this.controller.isValidCommand("testCommand1"), equalTo(true));
	}
	
	@Test
	public void testIsValidCommandWithInvalidCommandName() {
		assertThat(this.controller.isValidCommand("notAValidCommand"), equalTo(false));
	}
	
}
