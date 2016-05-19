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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
/**
 * 
 * Unit test case for {@link LinkToFieldAssociation}
 *
 */
public class TestLinkToFieldAssociation {
    
    @Test
    public void testGenerationOneLinkPerField()
    {
        assertEquals(true, new LinkToFieldAssociationImpl("field.name", "field.name").generateOneLinkPerField());
    }
    
    @Test
    public void testGenerationOneLinkPerFieldNullParam()
    {
        assertEquals(false, new LinkToFieldAssociationImpl("field", null).generateOneLinkPerField());
    }
    
    @Test
    public void testGenerationOneLinkPerFieldSameFieldParam()
    {
        assertEquals(true, new LinkToFieldAssociationImpl("parent.field", "parent.param").generateOneLinkPerField());
    }
    
    @Test
    public void testGetFieldNames()
    {
        List<String> expectedFieldNames = new ArrayList<String>();
        expectedFieldNames.add("field(0).name");
        expectedFieldNames.add("field(1).name");
        
        Map<String, Object> properties = buildPropertiesMap("field(0).name","bla","abc.def","bla","field(1).name","bla");
        List<String> actualFieldNames = new LinkToFieldAssociationImpl("field.name", "param.name").getFullyQualifiedFieldNames(properties);
        assertEquals(2, actualFieldNames.size());
        assertTrue(expectedFieldNames.contains(actualFieldNames.get(0)));
        assertTrue(expectedFieldNames.contains(actualFieldNames.get(1)));        
    }
    
    @Test
    public void testGetFieldNamesNoMatchingProperty()
    {
        Map<String, Object> properties = buildPropertiesMap("abc","bla","def","bla","xyz","bla");
        List<String> actualFieldNames = new LinkToFieldAssociationImpl("field.name", "param.name").getFullyQualifiedFieldNames(properties);
        assertEquals(1, actualFieldNames.size());
        assertEquals("field.name", actualFieldNames.get(0));        
    }
    
    @Test
    public void testDetermineTargetFieldNameSameParent()
    {
        Map<String, Object> properties = buildPropertiesMap("parent(0).name","bla");
        String resolvedTarget = new LinkToFieldAssociationImpl("parent.name", "parent.value").determineTargetFieldName(null, "parent(0).value", properties);
        assertEquals("parent(0).name", resolvedTarget);
    }
    
    @Test
    public void testDetermineTargetFieldNameEqualFieldParam()
    {
        Map<String, Object> properties = buildPropertiesMap("field(0).name","bla");
        String resolvedTarget = new LinkToFieldAssociationImpl("field.name", "field.name").determineTargetFieldName(null, "field(0).name", properties);
        assertEquals("field(0).name", resolvedTarget);
    }
    
    @Test
    public void testDetermineTargetFieldNameDifferentFieldParam()
    {
        Map<String, Object> properties = buildPropertiesMap("field(0).name","bla");
        String resolvedTarget = new LinkToFieldAssociationImpl("field.name", "param.name").determineTargetFieldName("field(0).name", "param(0).name", properties);
        assertEquals("field(0).name", resolvedTarget);
    }
    
    @Test
    public void testDetermineTargetFieldNameNotInProperties()
    {
        Map<String, Object> properties = buildPropertiesMap("abc","bla");
        String resolvedTarget = new LinkToFieldAssociationImpl("field.name", "param.name").determineTargetFieldName("field(0).name", "param(0).name", properties);
        assertNull(resolvedTarget);
    }
    
    private Map<String, Object> buildPropertiesMap(String... properties)
    {
        Map<String, Object> propertiesMap = new HashMap<String, Object>();
        for(int i=0; i<properties.length; i+=2)
        {
            propertiesMap.put(properties[i], properties[i+1]);   
        }
        return propertiesMap;
    }
}
