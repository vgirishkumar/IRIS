package com.temenos.interaction.sdk.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds information about an entity field
 */
public class EMProperty {
	private String name;
	private List<EMTerm> terms = new ArrayList<EMTerm>();

	public EMProperty(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public List<EMTerm> getVocabularyTerms() {
		return terms;
	}

	public void addVocabularyTerm(EMTerm term) {
		terms.add(term);
	}
}
