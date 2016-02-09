package com.temenos.interaction.odataext.odataparser.data;

/*
 * #%L
 * interaction-odata4j-ext
 * %%
 * Copyright (C) 2012 - 2015 Temenos Holdings N.V.
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

import java.util.List;
import java.util.Set;

import com.temenos.interaction.odataext.odataparser.ODataParser.UnsupportedQueryOperationException;

/**
 * Class to contain full user access profile information for consumers.
 * 
 * @author sjunejo
 *
 */
public class AccessProfile {

    // Filter conditions
    private RowFilters rowFilters;

    // Set of field/column to select
    private Set<FieldName> fieldNames;
    
    public AccessProfile(RowFilters rowFilters, Set<FieldName> fieldNames) {
        this.rowFilters = rowFilters;
        this.fieldNames = fieldNames;
    }
    
    @Deprecated
    public AccessProfile(List<RowFilter> rowFilters, Set<FieldName> fieldNames) {

        this.rowFilters = new RowFilters(rowFilters);
        this.fieldNames = fieldNames;
    }
    
    // TODO once old getRowFilters() has been removed rename this.
    public RowFilters getNewRowFilters() {
        return rowFilters;
    }

    @Deprecated
    public List<RowFilter> getRowFilters() {
        try {
            return rowFilters.asRowFilters();
        } catch (UnsupportedQueryOperationException e) {
            new RuntimeException("Could not convert to row filters");
            return null;
        }
    }
    
    public Set<FieldName> getFieldNames() {
        return fieldNames;
    }
}
