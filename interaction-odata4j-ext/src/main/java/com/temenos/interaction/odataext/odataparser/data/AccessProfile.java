package com.temenos.interaction.odataext.odataparser.data;

/*
 * #%L
 * interaction-commands-authorization
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.odataext.odataparser.ODataParser.UnsupportedQueryOperationException;

/**
 * Class to contain full user access profile information for consumers.
 * 
 * @author sjunejo
 *
 */
public class AccessProfile {
    private final static Logger logger = LoggerFactory.getLogger(AccessProfile.class);

    // Filter conditions
    private RowFilters rowFilters;

    // Set of field/column to select
    private Set<FieldName> fieldNames;

    public AccessProfile(List<RowFilter> rowFilters, Set<FieldName> fieldNames) {
        setRowFilters(rowFilters);
        this.fieldNames = fieldNames;
    }

    @Deprecated
    public List<RowFilter> getRowFilters() {
        try {
            return rowFilters.asRowFilters();
        } catch (UnsupportedQueryOperationException e) {
            // For backward comparability cannot throw
            // UnsupportedQueryOperationException. So throw something that old
            // callers can handle.
            logger.error("Could not convert to row filters");
            return null;
        }
    }
    
    public void setRowFilters(RowFilters rowFilters) {
        this.rowFilters = rowFilters;
    }
    
    @Deprecated
    public void setRowFilters(List<RowFilter> rowFilters) {
        setRowFilters(new RowFilters(rowFilters));
    }
    
    public Set<FieldName> getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(Set<FieldName> fieldNames) {
        this.fieldNames = fieldNames;
    }
}
