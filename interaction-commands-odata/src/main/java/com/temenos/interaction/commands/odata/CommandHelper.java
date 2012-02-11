package com.temenos.interaction.commands.odata;

import java.text.SimpleDateFormat;
import java.util.List;

import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;

public class CommandHelper {

	/**
	 * Create an OEntityKey instance for the specified entity id
	 * @param entityTypes List of entity types
	 * @param entity Entity name
	 * @param id Id
	 * @return An OEntityKey instance
	 * @throws Exception Error creating key 
	 */
	public static OEntityKey createEntityKey(Iterable<EdmEntityType> entityTypes, String entity, String id) throws Exception {
		//Lookup type of entity key (simple keys only)
		String keyType = null;
		for (EdmEntityType entityType : entityTypes) {
			if (entityType.getName().equals(entity)) {
				List<String> keys = entityType.getKeys();
				if(keys.size() == 1) {
					EdmProperty prop = entityType.findDeclaredProperty(keys.get(0));
					if(prop != null && prop.getType() != null) {
						keyType = prop.getType().getFullyQualifiedTypeName();
					}
					break;
				}
			}
		}		
		
		//Create an entity key
		OEntityKey key;
		if(keyType.equals("Edm.Int64")) {
			key = OEntityKey.create(Long.parseLong(id));
		}
		else if(keyType.equals("Edm.Int32")) {
			key = OEntityKey.create(Integer.parseInt(id));
		}
		else if(keyType.equals("Edm.DateTime")) {
		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			key = OEntityKey.create(dateFormat.parse(id));
		}
		else if(keyType.equals("Edm.Time")) {
		    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			key = OEntityKey.create(dateFormat.parse(id));
		}
		else if(keyType.equals("Edm.String")) {
			key = OEntityKey.create(id);
		}
		else {
			throw new Exception("Entity key type " + id + " is not supported.");
		}
		return key;
	}
}
