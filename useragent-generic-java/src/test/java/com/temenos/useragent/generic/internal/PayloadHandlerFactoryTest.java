package com.temenos.useragent.generic.internal;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.temenos.useragent.generic.Link;
import com.temenos.useragent.generic.PayloadHandler;

public class PayloadHandlerFactoryTest {

	@Test
	public void testCreateFactoryForValidHandler() {
		PayloadHandlerFactory factory = PayloadHandlerFactory
				.createFactory(MockPayloadHandler.class);
		assertNotNull(factory);
	}

	@Test
	public void testCreateFactoryForInvalidHandler() {
		try {
			PayloadHandlerFactory factory = PayloadHandlerFactory
					.createFactory(null);
			fail("IllegalArgumentException should have thrown");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@Test
	public void testCreateHandler() {
		PayloadHandlerFactory factory = PayloadHandlerFactory
				.createFactory(MockPayloadHandler.class);
		MockPayloadHandler handler = (MockPayloadHandler) factory
				.createHandler("Test payload");
		assertEquals("Test payload", handler.getPayload());
	}

	public static class MockPayloadHandler implements PayloadHandler {

		private String payload;

		@Override
		public boolean isCollection() {
			return false;
		}

		@Override
		public List<Link> links() {
			return Collections.emptyList();
		}

		@Override
		public List<EntityWrapper> entities() {
			return Collections.emptyList();
		}

		@Override
		public EntityWrapper entity() {
			return null;
		}

		@Override
		public void setPayload(String payload) {
			this.payload = payload;
		}

		@Override
		public void setParameter(String parameter) {
		}

		public String getPayload() {
			return payload;
		}
	}
}
