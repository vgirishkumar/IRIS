package com.temenos.ebank.common.wicket;

import java.util.List;

/**
 * Marker interface for containers used to provide a list of IDs of the obligatory components of the container
 * 
 * @author ajurubita
 * 
 */
public interface IPartiallySavable {

	List<String> getObligatoryComponentsIds();
}
