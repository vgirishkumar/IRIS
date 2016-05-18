package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-core
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

import java.util.List;
import java.util.Map;

/**
 * LinkToFieldAssociation determines 
 * <br> 1. which field is associated with a particular link 
 * <br> 2. how many links to associate with a field
 */
public interface LinkToFieldAssociation {
    
    /**
     * Return true if only one link is required per field. 
     * @param fieldLabel
     * @param paramName
     * @return
     */
    boolean generateOneLinkPerField();
    
    /**
     * Extract list of property keys that match the field.
     * @param fieldLabel
     * @param properties
     * @return
     */
    List<String> getFullyQualifiedFieldNames(Map<String, Object> properties);
    
    /**
     * Determine which target field name or rel value to use.
     * @param transitionFieldLabel
     * @param paramName
     * @param fullyQualifiedFieldLabel
     * @param fullyQualifiedParamName
     * @param properties
     * @return
     */
    String determineTargetFieldName(String fullyQualifiedFieldLabel, String fullyQualifiedParamName, Map<String, Object> properties);

}
