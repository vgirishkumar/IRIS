/*
 * #%L
 * interaction-example-hateoas-dynamic
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.mockito.Matchers;

import DynamicModel.DynamicBehaviour;

import com.temenos.interaction.core.hypermedia.ResourceLocator;
import com.temenos.interaction.core.hypermedia.ResourceLocatorProvider;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.hypermedia.Transition;

/**
 * This test verifies the api that provides dynamic resolution of RIM target states 
 *
 * @author mlambert
 *
 */
public class DynamicTest {

	@Test
	public void testDynamicTransitionExists() {
        DynamicBehaviour behaviour = new DynamicBehaviour();
        
        ResourceLocatorProvider locatorProvider = mock(ResourceLocatorProvider.class);
        ResourceLocator resourceLocator = mock(ResourceLocator.class);        
        
        final String domainAndRim = "DynamicModel.Dynamic";
        final String newTarget = "dynamic";
        
        when(locatorProvider.get(anyString())).thenReturn(resourceLocator);
        when(resourceLocator.resolve(Matchers.<String>anyVararg())).thenReturn(domainAndRim + "." + newTarget);               
		
		behaviour.setResourceLocatorProvider(locatorProvider);
		
        ResourceStateMachine hypermediaEngine = new ResourceStateMachine(behaviour.getRIM(), behaviour.getExceptionResource());
        	
        List<Transition> transitions = hypermediaEngine.getInitial().getTransitions();
        
        assertEquals("Incorrect number of transitions", 3, transitions.size());
        
        int noTimesTargetFound = 0;
        
        for(Transition transition: transitions) {
        	System.out.println(transition.getSource().getName() + "-->" + transition.getTarget().getName());
        	
        	if(newTarget.equals(transition.getTarget().getName())) {
        		noTimesTargetFound++;
        	}
        }
        
        assertEquals("There should only be 1 transition for with the new target", 1, noTimesTargetFound);
        
        //Object x = hypermediaEngine.getInteractionByPath();
        
        System.out.println("XXXX");
	}
}
