package com.temenos.interaction.commands.mule;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.temenos.interaction.core.MultivaluedMapImpl;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;

public class TestXMLWriter {

	private final static String EXPECTED_VIEW_COMMAND_XML = "<?xml version='1.0' encoding='UTF-8'?><viewcommand><pathparameters><key>value</key></pathparameters><queryparameters><key>value</key></queryparameters></viewcommand>";

	@Test
	public void testViewCommandToXml() throws Exception {
		XMLWriter writer = new XMLWriter();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.putSingle("key", "value");
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.putSingle("key", "value");
		ViewCommandWrapper commandWrapper = new ViewCommandWrapper(pathParams, queryParams);

		
		writer.toXml(commandWrapper, bos);
		String result = new String(bos.toByteArray());
		assertEquals(EXPECTED_VIEW_COMMAND_XML, result);
	}
	
	private final static String EXPECTED_ACTION_COMMAND_XML = "<?xml version='1.0' encoding='UTF-8'?><actioncommand><pathparameters><key>value</key></pathparameters><queryparameters><key>value</key></queryparameters><entity><name>Customer</name><b>2</b><a>1</a></entity></actioncommand>";

	@Test
	public void testActionCommandToXml() throws Exception {
		XMLWriter writer = new XMLWriter();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.putSingle("key", "value");
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.putSingle("key", "value");
		EntityProperties entityFields = new EntityProperties();
		entityFields.setProperty(new EntityProperty("a", "1"));
		entityFields.setProperty(new EntityProperty("b", "2"));
		Entity entity = new Entity("Customer", entityFields);
		ActionCommandWrapper commandWrapper = new ActionCommandWrapper(pathParams, queryParams, entity);

		writer.toXml(commandWrapper, bos);
		String result = new String(bos.toByteArray());
		assertEquals(EXPECTED_ACTION_COMMAND_XML, result);
	}

}
