package com.temenos.interaction.springdsl;

/*
 * #%L
 * interaction-springdsl
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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.temenos.interaction.core.hypermedia.ResourceFactory;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.Transition;

// TODO: Auto-generated Javadoc
/**
 * The Class TestTransitionFactoryBean.
 */
public class TestTransitionFactoryBean {

	/**
	 * Test construct.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testConstruct() throws Exception {
		ResourceState mockTargetResoruce = new ResourceState("entity", "name", null, "/test");

		Transition fromBuilder = new Transition.Builder().target(mockTargetResoruce).build();
		TransitionFactoryBean factoryBean = new TransitionFactoryBean();
		factoryBean.setTarget(mockTargetResoruce);
		Transition fromFactory = factoryBean.getObject();
		assertEquals(fromBuilder, fromFactory);
	}

	/**
	 * Test construct.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testConstructWithExpressionOK() throws Exception {
		ResourceState mockTargetResoruce = new ResourceState("entity", "name", null, "/test");

		List<String> functionList = new ArrayList<String>();	
		StringBuilder function1 = new StringBuilder("entityName= Order ;");
		function1.append("name= OrderCreated;");
		function1.append("actions= ");
		function1.append("CreateEntity, Action.TYPE.ENTRY, actionViewProperties; ");
		function1.append("path= /{franchise}/Orders(); ");
		function1.append("linkRelations= ");
		function1.append("http://relations.restbucks.com/order;");
    	function1.append("function=RestbucksModel.Restbucks.order , ResourceGETExpression.Function.OK;");
    	functionList.add(function1.toString());
    	
		ResourceFactory resourceFactory = new ResourceFactory(); 
		Transition fromBuilder = new Transition.Builder().target(mockTargetResoruce).build();
		TransitionFactoryBean factoryBean = new TransitionFactoryBean();
		factoryBean.setTarget(mockTargetResoruce);
		factoryBean.setFunctionList(functionList);
		Transition fromFactory = factoryBean.getObject();
		assertEquals(fromBuilder, fromFactory);
	}

}
