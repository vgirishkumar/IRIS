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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author aburgos
 */
public class TestDefaultResourceStateProvider {

    static ResourceStateMachine hypermediaEngine;
    static PathTree paths;

    static String ENTITY_NAME = "TEST";

    private boolean simpleResourceStatesComparison(ResourceState rs1, ResourceState rs2) {
        if(!rs1.getEntityName().equals(rs2.getEntityName())) return false;
        if(!rs1.getName().equals(rs2.getName())) return false;
        if(!rs1.getPath().equals(rs2.getPath())) return false;
        return true;
    }
    
    @BeforeClass
    public static void setUpClass() {
        ResourceState initial = new ResourceState(ENTITY_NAME, "initial", new ArrayList<Action>(), "/entity");
        ResourceState published = new ResourceState(ENTITY_NAME, "published", new ArrayList<Action>(), "/published");
        ResourceState publishedDeleted = new ResourceState(published, "publishedDeleted", new ArrayList<Action>());
        ResourceState draft = new ResourceState(ENTITY_NAME, "draft", new ArrayList<Action>(), "/draft");
        ResourceState draftDeleted = new ResourceState(draft, "draftDeleted", new ArrayList<Action>());
    
        // create draft
        initial.addTransition(new Transition.Builder().method("PUT").target(draft).build());
        // updated draft
        draft.addTransition(new Transition.Builder().method("PUT").target(draft).build());
        // publish
        draft.addTransition(new Transition.Builder().method("PUT").target(published).build());
        // delete draft
        draft.addTransition(new Transition.Builder().method("DELETE").target(draftDeleted).build());
        // delete published
        published.addTransition(new Transition.Builder().method("DELETE").target(publishedDeleted).build());
        
        hypermediaEngine = new ResourceStateMachine(initial);
        
        // fill out paths
        paths = new PathTree();
        paths.put(initial.getPath(), "GET", initial.getName());
        paths.put(draft.getPath(), "PUT", draft.getName());
        paths.put(published.getPath(), "PUT", published.getName());
        paths.put(draftDeleted.getPath(), "DELETE", draftDeleted.getName());
        paths.put(publishedDeleted.getPath(), "DELETE", publishedDeleted.getName());
    }
    
    @Test
    public void testIsLoaded() throws Exception {
        DefaultResourceStateProvider rsp = new DefaultResourceStateProvider(hypermediaEngine);
        assertTrue(rsp.isLoaded("initial"));
        assertTrue(rsp.isLoaded("published"));
        assertTrue(rsp.isLoaded("publishedDeleted"));
        assertTrue(rsp.isLoaded("draft"));
        assertTrue(rsp.isLoaded("draftDeleted"));
    }

    @Test
    public void testGetResourceStateString() throws Exception {
        DefaultResourceStateProvider rsp = new DefaultResourceStateProvider(hypermediaEngine);
        ResourceState initial = new ResourceState(ENTITY_NAME, "initial", new ArrayList<Action>(), "/entity");
        assertTrue(simpleResourceStatesComparison(rsp.getResourceState("initial"), initial));
        ResourceState published = new ResourceState(ENTITY_NAME, "published", new ArrayList<Action>(), "/published");
        assertTrue(simpleResourceStatesComparison(rsp.getResourceState("published"), published));
        ResourceState draft = new ResourceState(ENTITY_NAME, "draft", new ArrayList<Action>(), "/draft");
        assertTrue(simpleResourceStatesComparison(rsp.getResourceState("draft"), draft));
        ResourceState publishedDeleted = new ResourceState("TEST", "publishedDeleted", new ArrayList<Action>(), "/published");
        assertTrue(simpleResourceStatesComparison(rsp.getResourceState("publishedDeleted"), publishedDeleted));
        ResourceState draftDeleted = new ResourceState("TEST", "draftDeleted", new ArrayList<Action>(), "/draft");
        assertTrue(simpleResourceStatesComparison(rsp.getResourceState("draftDeleted"), draftDeleted));
    }

    @Test
    public void testDetermineState() throws Exception {
        DefaultResourceStateProvider rsp = new DefaultResourceStateProvider(hypermediaEngine);
        ResourceState initial = new ResourceState(ENTITY_NAME, "initial", new ArrayList<Action>(), "/entity");
        assertTrue(simpleResourceStatesComparison(rsp.determineState(new Event("", "GET"), "/entity"), initial));
        ResourceState published = new ResourceState(ENTITY_NAME, "published", new ArrayList<Action>(), "/published");
        assertTrue(simpleResourceStatesComparison(rsp.determineState(new Event("", "PUT"), "/published"), published));
        ResourceState draft = new ResourceState(ENTITY_NAME, "draft", new ArrayList<Action>(), "/draft");
        assertTrue(simpleResourceStatesComparison(rsp.determineState(new Event("", "PUT"), "/draft"), draft));
        ResourceState publishedDeleted = new ResourceState(published, "publishedDeleted", new ArrayList<Action>());
        assertTrue(simpleResourceStatesComparison(rsp.determineState(new Event("", "DELETE"), "/published"), publishedDeleted));
        ResourceState draftDeleted = new ResourceState(draft, "draftDeleted", new ArrayList<Action>());
        assertTrue(simpleResourceStatesComparison(rsp.determineState(new Event("", "DELETE"), "/draft"), draftDeleted));
    }

    @Test
    public void testGetResourceStatesByPath() throws Exception {
        DefaultResourceStateProvider rsp = new DefaultResourceStateProvider(hypermediaEngine);
        Map<String, Set<String>> resourceStatesByPath = rsp.getResourceStatesByPath();
        assertEquals(3, resourceStatesByPath.size());
        assertEquals(new HashSet<String>(Arrays.asList("initial")), resourceStatesByPath.get("/entity"));
        assertEquals(new HashSet<String>(Arrays.asList("publishedDeleted", "published")), resourceStatesByPath.get("/published"));
        assertEquals(new HashSet<String>(Arrays.asList("draftDeleted", "draft")), resourceStatesByPath.get("/draft"));
    }

    @Test
    public void testGetResourceMethodsByState() throws Exception {
        DefaultResourceStateProvider rsp = new DefaultResourceStateProvider(hypermediaEngine);
        Map<String, Set<String>> resourceMethodsByState = rsp.getResourceMethodsByState();
        assertEquals(5, resourceMethodsByState.size());
        assertEquals(new HashSet<String>(Arrays.asList("GET")), resourceMethodsByState.get("initial"));
        assertEquals(new HashSet<String>(Arrays.asList("PUT")), resourceMethodsByState.get("published"));
        assertEquals(new HashSet<String>(Arrays.asList("PUT")), resourceMethodsByState.get("draft"));
        assertEquals(new HashSet<String>(Arrays.asList("DELETE")), resourceMethodsByState.get("publishedDeleted"));
        assertEquals(new HashSet<String>(Arrays.asList("DELETE")), resourceMethodsByState.get("draftDeleted"));
    }

    @Test
    public void testGetResourcePathsByState() throws Exception {
        DefaultResourceStateProvider rsp = new DefaultResourceStateProvider(hypermediaEngine);
        Map<String, String> resourcePathsByState = rsp.getResourcePathsByState();
        assertEquals(5, resourcePathsByState.size());
        assertEquals("/entity", resourcePathsByState.get("initial"));
        assertEquals("/published", resourcePathsByState.get("published"));
        assertEquals("/draft", resourcePathsByState.get("draft"));
        assertEquals("/published", resourcePathsByState.get("publishedDeleted"));
        assertEquals("/draft", resourcePathsByState.get("draftDeleted"));
    }

    @Test
    public void testGetResourceStateStringString() throws Exception {
        DefaultResourceStateProvider rsp = new DefaultResourceStateProvider(hypermediaEngine);
        ResourceState initial = new ResourceState(ENTITY_NAME, "initial", new ArrayList<Action>(), "/entity");
        assertTrue(simpleResourceStatesComparison(rsp.getResourceState("GET", "/entity"), initial));
        ResourceState published = new ResourceState(ENTITY_NAME, "published", new ArrayList<Action>(), "/published");
        assertTrue(simpleResourceStatesComparison(rsp.getResourceState("PUT", "/published"), published));
        ResourceState draft = new ResourceState(ENTITY_NAME, "draft", new ArrayList<Action>(), "/draft");
        assertTrue(simpleResourceStatesComparison(rsp.getResourceState("PUT", "/draft"), draft));
        ResourceState publishedDeleted = new ResourceState(published, "publishedDeleted", new ArrayList<Action>());
        assertTrue(simpleResourceStatesComparison(rsp.getResourceState("DELETE", "/published"), publishedDeleted));
        ResourceState draftDeleted = new ResourceState(draft, "draftDeleted", new ArrayList<Action>());
        assertTrue(simpleResourceStatesComparison(rsp.getResourceState("DELETE", "/draft"), draftDeleted));
    }

    @Test
    public void testGetResourceStateId() throws Exception {
        DefaultResourceStateProvider rsp = new DefaultResourceStateProvider(hypermediaEngine);
        assertEquals(rsp.getResourceStateId("GET", "/entity"), "initial");
        assertEquals(rsp.getResourceStateId("PUT", "/draft"), "draft");
        assertEquals(rsp.getResourceStateId("DELETE", "/draft"), "draftDeleted");
        assertEquals(rsp.getResourceStateId("PUT", "/published"), "published");
        assertEquals(rsp.getResourceStateId("DELETE", "/published"), "publishedDeleted");
    }

    @Test(expected=MethodNotAllowedException.class)
    public void testMethodNotAllowedExceptionforGetResourceStateId() throws Exception {
        DefaultResourceStateProvider rsp = new DefaultResourceStateProvider(hypermediaEngine);

        // allowed method
        String resourceId = rsp.getResourceStateId("GET", "/entity");
        assertEquals("initial", resourceId);

        // not allowed method
        rsp.getResourceStateId("DELETE", "/entity");
    }
    
    @Test(expected=MethodNotAllowedException.class)
    public void testMethodNotAllowedExceptionforGetResourceState() throws Exception {
        DefaultResourceStateProvider rsp = new DefaultResourceStateProvider(hypermediaEngine);
        
        // allowed method
        ResourceState resource = rsp.getResourceState("GET", "/entity");
        assertEquals("initial", resource.getName());

        // not allowed method
        rsp.getResourceState("DELETE", "/entity");
    }
}
