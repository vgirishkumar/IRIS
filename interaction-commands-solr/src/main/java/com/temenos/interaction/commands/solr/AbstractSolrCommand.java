package com.temenos.interaction.commands.solr;

/*
 * #%L
 * interaction-commands-solr
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


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
import com.temenos.interaction.odataext.odataparser.data.RowFilter;

public abstract class AbstractSolrCommand {
	
    // Root of the Solr URL
    protected String solrRootURL;
    
	static final int MAX_ENTITIES_RETURNED = 50;

	public CollectionResource<Entity> buildCollectionResource(String entityName, SolrDocumentList docs) {
		List<EntityResource<Entity>> results = new ArrayList<EntityResource<Entity>>();
		long numFound = docs.getNumFound();
		for (int i = 0; i < MAX_ENTITIES_RETURNED && i < numFound; i++) {
			EntityProperties properties = new EntityProperties();
			SolrDocument doc = docs.get(i);
			Collection<String> fields = doc.getFieldNames();
			for (String propName : fields) {
			    properties.setProperty(new EntityProperty(propName, doc.getFirstValue(propName)));
			}
			// Give some control to user if they have something in mind
			customizeEntityProperties(doc, properties);
			
			// Build the entity as is
			Entity entity = new Entity(entityName, properties);
			results.add(new EntityResource<Entity>(entityName, entity));
		}
		return new CollectionResource<Entity>(results) {};
	}
	
	/**
	 * TODO : Remove this method as I am not sure why we have this?
	 * @param entityName
	 * @param termName
	 * @param termMap
	 * @return
	 */
	public CollectionResource<Entity> buildCollectionResource(String entityName, String termName, Map<String, List<Term>> termMap) {
		List<EntityResource<Entity>> results = new ArrayList<EntityResource<Entity>>();
		List<Term> terms = termMap.get(termName);
		for (Term t : terms) {
			EntityProperties properties = new EntityProperties();
			properties.setProperty(new EntityProperty(termName, t.getTerm()));
			Entity entity = new Entity(entityName, properties);
			results.add(new EntityResource<Entity>(entityName, entity));
		}
		return new CollectionResource<Entity>(results) {};
	}
	
	/**
	 * This method will update all filter fieldName to 'text'
	 * @param rowFilters
	 * @return
	 */
	protected List<RowFilter> toSolrTextFieldFilter(List<RowFilter> rowFilters) {
	    return toSolrFieldFilters(rowFilters, "text");
	}
	
	 /*
     * Sometime we would like to search everything agains one field, so convert here, simply pass the
     * taregt field name as text
     */
    protected List<RowFilter> toSolrFieldFilters(List<RowFilter> rowFilters, String solrFieldName) {
        if (rowFilters != null && solrFieldName != null && !solrFieldName.isEmpty()) {
            List<RowFilter> solrFieldFilters = new ArrayList<RowFilter>();
            for (RowFilter filter : rowFilters) {
                if (filter != null) // Filter all null objects if there is any
                   solrFieldFilters.add(toSolrFieldFilter(filter, solrFieldName));
            }
            return solrFieldFilters;
        }
        return new ArrayList<RowFilter>();
    }
    
    protected RowFilter toSolrFieldFilter(RowFilter filter, String solrFieldName) {
        if (filter != null && solrFieldName != null && !solrFieldName.isEmpty()) {
            return new RowFilter(solrFieldName, filter.getRelation(), filter.getValue());
        }
        return null;
    }
	
	
	/**
	 * This method will be called before constructing a final entity so that 
	 * caller can customize the results if required, for example apply select
	 * or filter on result set which was not possible at query time
	 * @param doc
	 * @param properties
	 */
	protected abstract void customizeEntityProperties(SolrDocument doc, EntityProperties properties);
}
