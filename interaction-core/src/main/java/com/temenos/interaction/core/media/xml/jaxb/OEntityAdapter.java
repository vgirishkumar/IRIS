package com.temenos.interaction.core.media.xml.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.odata4j.core.OEntity;
import org.odata4j.core.OProperty;

public class OEntityAdapter extends XmlAdapter<XMLOEntity, OEntity>{

	@Override
	public XMLOEntity marshal(OEntity oEntity) throws Exception {
		XMLOEntity oe = new XMLOEntity();
		oe.entitySetName = oEntity.getEntitySetName();
		for (OProperty<?> property : oEntity.getProperties()) {
			XMLOProperty op = new XMLOProperty();
			op.key = property.getName();
			op.value = property.getValue().toString();
		}
		return oe;
	}

	@Override
	public OEntity unmarshal(XMLOEntity xEntity) throws Exception {
		// nah, not going to work.  No way to lookup the EdmEntitySet
		return null;
	}

}
