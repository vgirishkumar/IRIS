package com.temenos.interaction.core.state;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.jayway.jaxrs.hateoas.HateoasContext;
import com.jayway.jaxrs.hateoas.core.HateoasResponse;
import com.jayway.jaxrs.hateoas.core.HateoasResponse.HateoasResponseBuilder;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.NoopGETCommand;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HateoasResponse.class})
public class TestPowerMockAbstractHTTPResourceInteractionModel {

	/* Test a GET with no HateoasContext will not try to build links */
	@Test
	public void testGETWithNoHateoasContext() {
		String resourcePath = "/test";
		AbstractHTTPResourceInteractionModel r = new AbstractHTTPResourceInteractionModel("TEST_ENTITY", resourcePath) {
		};
		CommandController cc = r.getCommandController();
		cc.setGetCommand("/test", new NoopGETCommand());

		// mock the HateoasResponse to do nothing
		mockStatic(HateoasResponse.class);
		HateoasResponseBuilder mockBuilder = mock(HateoasResponseBuilder.class);
		when(mockBuilder.selfLink(any(HateoasContext.class), anyString(), anyString())).thenReturn(mockBuilder);
		when(mockBuilder.entity(any(EntityResource.class))).thenReturn(mockBuilder);
		when(mockBuilder.header(anyString(), anyString())).thenReturn(mockBuilder);
		when(HateoasResponse.ok()).thenReturn(mockBuilder);
		
		r.get(null, "123", null);
		
		// verify that the selfLink method was not call as we had a null HateoasContext
		verify(mockBuilder, times(0)).selfLink(any(HateoasContext.class), anyString(), anyString());
	}

	/* Test a GET with no HateoasContext will not try to build links */
	@Test
	public void testGETWithHateoasContext() {
		String resourcePath = "/test";
		AbstractHTTPResourceInteractionModel r = new AbstractHTTPResourceInteractionModel("TEST_ENTITY", resourcePath) {
			public HateoasContext getHateoasContext() {
				return mock(HateoasContext.class);
			}
		};
		CommandController cc = r.getCommandController();
		cc.setGetCommand("/test", new NoopGETCommand());

		// mock the HateoasResponse to do nothing
		mockStatic(HateoasResponse.class);
		HateoasResponseBuilder mockBuilder = mock(HateoasResponseBuilder.class);
		when(mockBuilder.selfLink(any(HateoasContext.class), anyString(), anyString())).thenReturn(mockBuilder);
		when(mockBuilder.entity(any(EntityResource.class))).thenReturn(mockBuilder);
		when(mockBuilder.header(anyString(), anyString())).thenReturn(mockBuilder);
		when(HateoasResponse.ok()).thenReturn(mockBuilder);
		
		r.get(null, "123", null);
		
		// verify that the selfLink method was not call as we had a null HateoasContext
		verify(mockBuilder, times(1)).selfLink(any(HateoasContext.class), anyString(), anyString());
	}
}
