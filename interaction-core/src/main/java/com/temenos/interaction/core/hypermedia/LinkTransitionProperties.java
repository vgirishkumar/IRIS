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

import java.util.HashMap;
import java.util.Map;


/**
 * This class holds the properties required to resolve link multivalue fields.
 */
public class LinkTransitionProperties {

    private String targetFieldFullyQualifiedName;

    private Map<String, Object> transitionProperties = new HashMap<String, Object>();

    public LinkTransitionProperties(String targetFieldName, Map<String, Object> transitionProperties) {
        this.targetFieldFullyQualifiedName = targetFieldName;
        for (String key : transitionProperties.keySet()) {
            this.transitionProperties.put(key, transitionProperties.get(key));
        }
    }

    /**
     * @return the targetFieldFullyQualifiedName
     */
    public String getTargetFieldFullyQualifiedName() {
        return targetFieldFullyQualifiedName;
    }

    /**
     * @param targetFieldFullyQualifiedName the targetFieldFullyQualifiedName to set
     */
    public void setTargetFieldFullyQualifiedName(String targetFieldFullyQualifiedName) {
        this.targetFieldFullyQualifiedName = targetFieldFullyQualifiedName;
    }

    /**
     * @return the transitionProperties
     */
    public Map<String, Object> getTransitionProperties() {
        return transitionProperties;
    }

    /**
     * @param transitionProperties the transitionProperties to set
     */
    public void setTransitionProperties(Map<String, Object> transitionProperties) {
        this.transitionProperties = transitionProperties;
    }

}
