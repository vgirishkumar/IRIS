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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Unit tests for LinkGeneratorHelper
 */
public class TestLinkGeneratorHelper {

    @Test
    public void testValueBlankMap() {        
        String input = "Id = {ABCD}";
        String output = LinkGeneratorHelper.replaceParamValue(input, createPropertiesMap(new String[]{"",""}));
        assertEquals("Id = {ABCD}", output);
    }
    
    @Test
    public void testValueContainedInMap()
    {
        String input = "Id = {ABCD}";
        String output = LinkGeneratorHelper.replaceParamValue(input, createPropertiesMap(new String[]{"ABCD","value"}));
        assertEquals("Id = value", output);
    }
    
    @Test
    public void testCriteriaContainedInMap()
    {
        String input = "{ABCD}";
        String output = LinkGeneratorHelper.replaceParamValue(input, createPropertiesMap(new String[]{"ABCD","REFERENCE.NUMBER GT 123456.31*"}));
        assertEquals("ReferenceNumber gt %27123456.31*%27", output);
    }
    
    @Test
    public void testMultipleCriteriaContainedInMap()
    {
        String input = "{ABCD} and {EFGH}";
        String output = LinkGeneratorHelper.replaceParamValue(input, createPropertiesMap(new String[]{"ABCD","REFERENCE.NUMBER GT 123456.31*", "EFGH", "BOOKING.DATE LE 20160823"}));
        assertEquals("ReferenceNumber gt %27123456.31*%27 and BookingDate le %2720160823%27", output);
    }
    
    @Test
    public void testMultipleCriteriaFirstMissingInMap()
    {
        String input = "{ABCD} and {EFGH}";
        String output = LinkGeneratorHelper.replaceParamValue(input, createPropertiesMap(new String[]{"ABCD", "", "EFGH", "BOOKING.DATE LE 20160823"}));
        assertEquals("BookingDate le %2720160823%27", output);
    }
    
    @Test
    public void testMultipleCriteriaSecondMissingInMap()
    {
        String input = "{ABCD} and {EFGH}";
        String output = LinkGeneratorHelper.replaceParamValue(input, createPropertiesMap(new String[]{"ABCD","REFERENCE.NUMBER GT 123456.31*", "EFGH", ""}));
        assertEquals("ReferenceNumber gt %27123456.31*%27", output);
    }
    
    @Test
    public void testMultipleCriteriaMiddleMissingInMap()
    {
        String input = "{ABCD} or {EFGH} or {IJKL}";
        String output = LinkGeneratorHelper.replaceParamValue(input, createPropertiesMap(new String[]{"ABCD", "", "EFGH", "BOOKING.DATE LE 20160823", "IJKL", ""}));
        assertEquals("BookingDate le %2720160823%27", output);
    }
    
    private Map<String, Object> createPropertiesMap(String... values)
    {
        Map<String, Object> propertiesMap = new HashMap<String, Object>();
        for(int i=0; i<values.length; i+=2)
        {
            propertiesMap.put(values[i], values[i+1]);
        }
        
        return propertiesMap;
    }

}
