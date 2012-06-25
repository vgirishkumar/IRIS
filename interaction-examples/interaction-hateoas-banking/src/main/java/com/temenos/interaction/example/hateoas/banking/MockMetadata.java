package com.temenos.interaction.example.hateoas.banking;

import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.entity.vocabulary.Vocabulary;
import com.temenos.interaction.core.entity.vocabulary.terms.TermValueType;

/**
 * This class holds metadata information about resource entities.
 */
public class MockMetadata extends Metadata {
	public MockMetadata() {
		super();
		
		EntityMetadata vocs = new EntityMetadata();
		Vocabulary vocId = new Vocabulary();
		vocId.setTerm(new TermValueType(TermValueType.TEXT));
		vocs.setPropertyVocabulary("id", vocId);
		Vocabulary vocBody = new Vocabulary();
		vocBody.setTerm(new TermValueType(TermValueType.TEXT));
		vocs.setPropertyVocabulary("body", vocBody);
		super.setEntityMetadata("FundTransfer", vocs);
	}
}
