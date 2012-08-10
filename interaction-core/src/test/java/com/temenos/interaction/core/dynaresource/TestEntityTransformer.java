package com.temenos.interaction.core.dynaresource;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OLink;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSimpleType;

import com.temenos.interaction.core.hypermedia.EntityTransformer;
import com.temenos.interaction.core.hypermedia.Transformer;

public class TestEntityTransformer {

	@Test
	public void testMapTransform() {
		EdmEntitySet ees = createMockEdmEntitySet();
		OEntity entity = createMockOEntity(ees);
		
		Transformer t = new EntityTransformer();
		Map<String, Object> map = t.transform(entity);
		
		assertEquals(2, map.keySet().size());
		assertEquals("123", map.get("id"));
		assertEquals("EI218", map.get("flight"));
	}

	private EdmEntitySet createMockEdmEntitySet() {
		// Create an entity set
		List<EdmProperty.Builder> eprops = new ArrayList<EdmProperty.Builder>();
		EdmProperty.Builder ep = EdmProperty.newBuilder("id").setType(EdmSimpleType.STRING);
		eprops.add(ep);
		EdmEntityType.Builder eet = EdmEntityType.newBuilder().setNamespace("InteractionTest").setName("Flight").addKeys(Arrays.asList("id")).addProperties(eprops);
		EdmEntitySet.Builder eesb = EdmEntitySet.newBuilder().setName("Flight").setEntityType(eet);
		return eesb.build();
	}
	
	private OEntity createMockOEntity(EdmEntitySet ees) {
		OEntityKey entityKey = OEntityKey.create("123");
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		properties.add(OProperties.string("id", "123"));
		properties.add(OProperties.string("flight", "EI218"));
		return OEntities.create(ees, entityKey, properties, new ArrayList<OLink>());
	}	
}
