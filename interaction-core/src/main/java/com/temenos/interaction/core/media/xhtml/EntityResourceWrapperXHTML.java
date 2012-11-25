package com.temenos.interaction.core.media.xhtml;

import java.util.Map;

import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.media.EntityResourceWrapper;
import com.temenos.interaction.core.resource.EntityResource;

/**
 * XHTML entity resource wrapper classes which exposes a link to itself. 
 */
public class EntityResourceWrapperXHTML extends EntityResourceWrapper {

	public EntityResourceWrapperXHTML(EntityResource<Map<String, Object>> entityResource) {
		super(entityResource);
	}	

	/**
	 * Obtains an xhtml string describing an entity resource as as a map <dl>...</dl>
	 * @return xhtml string
	 */
	public String getEntityResourceString() {
		//Get entity data
		String s = "<dl>";
		Map<String, Object> data = getResource().getEntity();
		for(String key : data.keySet()) {
			s += getEntityResourceMapString(new EntityProperty(key, data.get(key)));
		}
		s += "</dl>";
		
		//Get entity links
		s += "<ul>";
		for(Link link : getResource().getLinks()) {
			s += "<li><a href=\"" + link.getHref() + "\" rel=\"" + link.getRel() + "\">" + link.getTitle() + "</a></li>";
		}
		s += "</ul>";
		
		return s;
	}
	
	private String getEntityResourceMapString(EntityProperty entityProperty) {
		String s = "<dt>" + entityProperty.getName() + "</dt>";
		Object value = entityProperty.getValue();
		if(value instanceof EntityProperties) {
			s += "<dl>";
			for(EntityProperty prop : ((EntityProperties) value).getProperties().values()) {
				s += getEntityResourceMapString(prop);
			}
			s += "</dl>";
		}
		else {
			s += "<dd>" + value + "</dd>";
		}
		return s;
	}
}
