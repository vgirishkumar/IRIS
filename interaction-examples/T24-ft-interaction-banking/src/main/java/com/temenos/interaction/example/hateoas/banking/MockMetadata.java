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
		
		EntityMetadata metadata = new EntityMetadata();
		
		// TransactionType
		Vocabulary voc_Id = new Vocabulary();
		voc_Id.setTerm(new TermValueType(TermValueType.TEXT));
		metadata.setPropertyVocabulary("Id", voc_Id);
				
		// TransactionType
		Vocabulary voc_TransactionType = new Vocabulary();
		voc_TransactionType.setTerm(new TermValueType(TermValueType.TEXT));
		metadata.setPropertyVocabulary("TransactionType", voc_TransactionType);
		
		// DebitAcctNo
		Vocabulary voc_DebitAcctNo = new Vocabulary();
		voc_DebitAcctNo.setTerm(new TermValueType(TermValueType.NUMBER));
		metadata.setPropertyVocabulary("DebitAcctNo", voc_DebitAcctNo);
		
		// DebitCurrency
		Vocabulary voc_DebitCurrency = new Vocabulary();
		voc_DebitCurrency.setTerm(new TermValueType(TermValueType.TEXT));
		metadata.setPropertyVocabulary("DebitCurrency", voc_DebitCurrency);
		
		// DebitAmount
		Vocabulary voc_DebitAmount = new Vocabulary();
		voc_DebitAmount.setTerm(new TermValueType(TermValueType.NUMBER));
		metadata.setPropertyVocabulary("DebitAmount", voc_DebitAmount);
		
		// CreditAcctNo
		Vocabulary voc_CreditAcctNo = new Vocabulary();
		voc_CreditAcctNo.setTerm(new TermValueType(TermValueType.NUMBER));
		metadata.setPropertyVocabulary("CreditAcctNo", voc_CreditAcctNo);
		
		// CreditAcctNo
		Vocabulary voc_RecordStatus = new Vocabulary();
		voc_RecordStatus.setTerm(new TermValueType(TermValueType.TEXT));
		metadata.setPropertyVocabulary("RecordStatus", voc_RecordStatus);
		
		super.setEntityMetadata("FundsTransfer", metadata);
	}
}
