package com.temenos.ebank.common.wicket.choiceRenderers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class MapChoiceRenderer implements IGenericChoiceRenderer {

	private Map<String, String> choicesMap;

	public MapChoiceRenderer(Map<String, String> choicesMap) {
		super();
		this.choicesMap = choicesMap;
	}

	public Object getDisplayValue(String entry) {
		return choicesMap.get(entry);
	}

	public String getIdValue(String entry, int index) {
		return entry;
	}

	public List<String> getChoices() {
		return new ArrayList<String>(choicesMap.keySet());
	}

}