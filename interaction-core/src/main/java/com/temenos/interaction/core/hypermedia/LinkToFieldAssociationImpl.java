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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link LinkToFieldAssociation}
 */
public class LinkToFieldAssociationImpl implements LinkToFieldAssociation {

    private final Logger logger = LoggerFactory.getLogger(LinkToFieldAssociationImpl.class);
    private String fieldLabel;
    private String paramName;
    
    public LinkToFieldAssociationImpl(String fieldLabel, String paramName)
    {
        this.fieldLabel = fieldLabel;
        this.paramName = paramName;
    }
    
    @Override
    public boolean generateOneLinkPerField() {
        return paramName!=null && (paramName.equals(fieldLabel) || haveSameParent());
    }

    @Override
    public List<String> getFullyQualifiedFieldNames(Map<String, Object> properties) {
        List<String> fieldLabelList = new ArrayList<String>();
        for(String key : properties.keySet()) {
            if(StringUtils.equals(fieldLabel, key.replaceAll(LinkGeneratorImpl.PARAM_REPLACEMENT_REGEX, ""))) {
                fieldLabelList.add(key);
            }
        }
        //For self links
        if(fieldLabelList.isEmpty()) {
            fieldLabelList.add(fieldLabel);
        }        
        return fieldLabelList;
    }

    @Override
    public String determineTargetFieldName(String fullyQualifiedFieldLabel,
            String fullyQualifiedParamName, Map<String, Object> properties) {
        String resolvedFieldLabel = null;
        if(haveSameParent()) {
            resolvedFieldLabel = fullyQualifiedParamName.substring(0, fullyQualifiedParamName.lastIndexOf(".")) + fieldLabel.substring(fieldLabel.lastIndexOf("."));
        }
        else if(paramName.equals(fieldLabel)) {
            resolvedFieldLabel = fullyQualifiedParamName;
        }
        else {
            resolvedFieldLabel = fullyQualifiedFieldLabel;
        }
        
        if(!properties.containsKey(resolvedFieldLabel)) {
            logger.debug("Field label " + resolvedFieldLabel + " does not have any corresponding value in the properties map.");
            resolvedFieldLabel = null;
        }
        
        return resolvedFieldLabel;
    }
    
    private boolean haveSameParent() {
        boolean isSame = false;
        if(fieldLabel!=null && fieldLabel.contains("."))
        {
           String fieldLabelParent = fieldLabel.substring(0, fieldLabel.lastIndexOf("."));
           String collectionNameParent = paramName.substring(0, paramName.lastIndexOf("."));
           if(StringUtils.equals(fieldLabelParent, collectionNameParent)) {
               isSame = true;
           }
        }
        return isSame;
    }

}
