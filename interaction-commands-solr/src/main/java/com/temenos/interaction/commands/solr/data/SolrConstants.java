package com.temenos.interaction.commands.solr.data;

/*
 * #%L
 * interaction-commands-solr
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
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

/**
 * Constants used in Solr Commands
 *
 * @author sjunejo
 *
 */
public class SolrConstants {

    // Keys for the key/value pairs which can be passed in as part of the search
    // URL.
    public static final String SOLR_CORE_KEY = "core";
    public static final String SOLR_SHARDS_KEY = "shards";
    public static final String SOLR_SHARDS_TOLERANT_KEY = "shards.tolerant";
    public static final String SOLR_QUERY_KEY = "q";
    public static final String SOLR_COMPANY_NAME_KEY = "companyid";
}
