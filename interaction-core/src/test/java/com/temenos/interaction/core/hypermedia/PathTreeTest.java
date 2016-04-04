package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-winkext
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

public class PathTreeTest {
	@Test
	public void testRequestWhenNoRegisteredPaths() {
		PathTree pathTree = new PathTree();
		
		assertNull(pathTree.get("/"));
	}
	
    @Test
    public void testUrlWithLiteralsOnly(){
        PathTree pathTree = new PathTree();
        pathTree.put("/{fridge}/magnet/{size}", "GET", "Fridge Magnet Size Selector"); 
        pathTree.put("/{plasticDuck}/colours", "GET", "Duck Colour Selector");
        pathTree.put("/europe/uk/london", "GET", "London");        
        pathTree.put("/{id}/profile", "GET", "Profile Resource");
        pathTree.put("/", "GET", "Root resource");
        pathTree.put("/{id}/myResource()/new", "GET", "New My Resource");
        pathTree.put("/asia/japan", "GET", "Japan");        
        
        assertEquals("Failed to get root resource", "Root resource", pathTree.get("/").get("GET"));
        assertEquals("Failed to get resource", "London", pathTree.get("/europe/uk/london").get("GET"));
        assertEquals("Failed to get resource", "Japan", pathTree.get("/asia/japan").get("GET"));
        assertNull("Found non existant resource", pathTree.get("/europe/germany"));
    }

    @Test
    public void testUrlWithVariables() {
        PathTree pathTree = new PathTree();
        pathTree.put("/{fridge}/magnet/{size}", "GET", "Fridge Magnet Size Selector"); 
        pathTree.put("/{plasticDuck}/colours", "GET", "Duck Colour Selector");
        pathTree.put("/europe/uk/london", "GET", "London");        
        pathTree.put("/{id}/profile", "GET", "Profile Resource");
        pathTree.put("/middle_east/country('Bahrain')", "GET", "Bahrain");
        pathTree.put("/{id}/myResource()/new", "GET", "New My Resource");
        pathTree.put("/{country}/myResource('{id}')", "GET", "Resource by id");               
        pathTree.put("/asia/japan", "GET", "Japan");
        
        assertEquals("Failed to get resource", "Profile Resource", pathTree.get("/123/profile").get("GET"));
        assertEquals("Failed to get resource", "Duck Colour Selector", pathTree.get("/malard/colours").get("GET"));
        assertEquals("Failed to get resource", "New My Resource", pathTree.get("/123/myResource()/new").get("GET"));        
        assertEquals("Failed to get resource", "Fridge Magnet Size Selector", pathTree.get("/hotpoint/magnet/medium").get("GET"));
        assertNull("Found non existant resource", pathTree.get("/hotpoint/door/medium"));
        assertEquals("Failed to get resource", "Resource by id", pathTree.get("/france/myResource(123)").get("GET"));
    }
    
    @Test
    public void testIsEmpty() {
        PathTree pathTree = new PathTree();        
        assertTrue(pathTree.isEmpty());
        
        pathTree.put("/{id}/profile", "GET", "Profile Resource");
        
        assertFalse(pathTree.isEmpty());                
    }

    @Test
    public void testRemoveValidArgs() {
        PathTree pathTree = new PathTree();        
        pathTree.put("/{id}/profile", "GET", "Profile Resource");
        pathTree.put("/{fridge}/magnet/{size}", "GET", "Fridge Magnet Size Selector");        
        pathTree.put("/{id}/profile", "PUT", "Upload Profile Resource");
        
        
        Map<String,String> httpMethodToState = pathTree.get("/{id}/profile");
        assertEquals(2, httpMethodToState.size());
        
        pathTree.remove("/{id}/profile", "GET");
        
        httpMethodToState = pathTree.get("/{id}/profile");
        assertEquals(1, httpMethodToState.size());        
        
        assertTrue(httpMethodToState.containsKey("PUT"));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testRemoveInvalidPath() {
        PathTree pathTree = new PathTree();        
        pathTree.put("/{id}/profile", "GET", "Profile Resource");
        pathTree.put("/{fridge}/magnet/{size}", "GET", "Fridge Magnet Size Selector");        
        pathTree.put("/{id}/profile", "PUT", "Upload Profile Resource");
        
        pathTree.remove("/random", "GET");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testRemoveInvalidMethod() {
        PathTree pathTree = new PathTree();        
        pathTree.put("/{id}/profile", "GET", "Profile Resource");
        pathTree.put("/{fridge}/magnet/{size}", "GET", "Fridge Magnet Size Selector");        
        pathTree.put("/{id}/profile", "PUT", "Upload Profile Resource");
        
        pathTree.remove("/{id}/profile", "POST");
    }        
}