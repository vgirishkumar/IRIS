package com.temenos.interaction.commands.solr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.TermsResponse.Term;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;

public abstract class AbstractSolrCommand {

	public CollectionResource<Entity> buildCollectionResource(String entityName, SolrDocumentList docs) {
		List<EntityResource<Entity>> results = new ArrayList<EntityResource<Entity>>();
		long numFound = docs.getNumFound();
		for (int i = 0; i < 10 && i < numFound; i++) {
			EntityProperties properties = new EntityProperties();
			SolrDocument doc = docs.get(i);
			Collection<String> fields = doc.getFieldNames();
			for (String propName : fields) {
				properties.setProperty(new EntityProperty(propName, doc.getFirstValue(propName)));
			}
			Entity entity = new Entity(entityName, properties);
			results.add(new EntityResource<Entity>(entity));
		}
		return new CollectionResource<Entity>(results) {};
	}
	
	public CollectionResource<Entity> buildCollectionResource(String entityName, String termName, Map<String, List<Term>> termMap) {
		List<EntityResource<Entity>> results = new ArrayList<EntityResource<Entity>>();
		List<Term> terms = termMap.get(termName);
		for (Term t : terms) {
			EntityProperties properties = new EntityProperties();
			properties.setProperty(new EntityProperty(termName, t.getTerm()));
			Entity entity = new Entity(entityName, properties);
			results.add(new EntityResource<Entity>(entity));
		}
		return new CollectionResource<Entity>(results) {};
	}

}
