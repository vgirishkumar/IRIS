package com.temenos.interaction.commands.mule;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;


public class TestEntityXMLReader {

	private final static String SIMPLE_ENTITY_XML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><Forecast><State>CA</State><ResponseText>City Found</ResponseText><City>Beverly Hills</City><WeatherStationCity>Burbank</WeatherStationCity><ForecastResult>Sunny</ForecastResult><PostCode>90210</PostCode></Forecast>";
	
	@Test
	public void testParseSimpleEntity() {
		EntityXMLReader reader = new EntityXMLReader();
		InputStream in = new ByteArrayInputStream(SIMPLE_ENTITY_XML.getBytes());
		Entity entity = reader.toEntity(in);
		assertEquals("Forecast", entity.getName());
		EntityProperties properties = entity.getProperties();
		assertEquals(6, properties.getProperties().keySet().size());
		assertEquals("CA", properties.getProperty("State").getValue());
		assertEquals("City Found", properties.getProperty("ResponseText").getValue());
		assertEquals("Beverly Hills", properties.getProperty("City").getValue());
		assertEquals("Burbank", properties.getProperty("WeatherStationCity").getValue());
		assertEquals("Sunny", properties.getProperty("ForecastResult").getValue());
		assertEquals("90210", properties.getProperty("PostCode").getValue());
	}
	
}
