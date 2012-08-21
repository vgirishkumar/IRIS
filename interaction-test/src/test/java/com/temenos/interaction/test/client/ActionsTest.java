package com.temenos.interaction.test.client;

import static org.junit.Assert.*;

import org.junit.Test;

import com.temenos.interaction.test.client.Actions;

public class ActionsTest {
    @Test
    public void shouldBeAbleToAskWhichActionsAreAvailable() {
       Actions actions = fillActions(); 
       
       assertTrue(actions.has(MockActivity1.class));
       assertTrue(actions.has(MockActivity2.class));
       assertFalse(actions.has(MockActivity3.class));
    }
    
    @Test
    public void shouldBeAbleToRetreiveAnAction() {
        final Actions actions = fillActions();
        
        assertNotNull(actions.get(MockActivity1.class));
        assertNull(actions.get(MockActivity3.class));
    }

    private Actions fillActions() {
        Actions actions = new Actions();
        actions.add(new MockActivity1());
        actions.add(new MockActivity2());
        return actions;
    }
}
