package com.temenos.interaction.odataext.odataparser.output;


/* 
 * #%L
 * interaction-odata4j-ext
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OutputExpressionNodeTest {
    
    @Test
    public void testGetParent() {
        OutputExpressionNode parent = new OutputExpressionNode(null);
        OutputExpressionNode child = new OutputExpressionNode(parent);
        
        assertEquals(parent, child.getParent());
    }
    
    @Test
    public void testTooManyArgumentFailure() {
        OutputExpressionNode parent = new OutputExpressionNode(null);
        
        assertTrue(parent.addArgument("a"));
        assertTrue(parent.addArgument("b"));
        assertTrue(parent.addArgument("c"));
        assertFalse(parent.addArgument("d"));
    } 
    
    @Test
    public void testMultipleSetOpFailure() {
        String op = "anOp";
        OutputExpressionNode node  = new OutputExpressionNode(null);
        
        assertTrue(node.setOp(op));
        assertFalse(node.setOp(op));
    } 
    
    @Test
    public void testToString() {
        OutputExpressionNode parent = new OutputExpressionNode(null);
        
        String op = "parentOp";
        String lhValue = "value1"; 
        String rhValue = "value2"; 
        
        // Build a small tree
        parent.addArgument(lhValue);
        parent.setOp(op);
        parent.addArgument(rhValue);
        
        assertEquals(lhValue + " " + op + " " + rhValue, parent.toOdataParameter());
    }
    
    @Test
    public void testIsBracketed() {
        OutputExpressionNode node = new OutputExpressionNode(null);
        assertFalse(node.isBracketed());
        node.setIsBracketed();
        assertTrue(node.isBracketed());
    }
    
    @Test
    public void testIsFunction() {
        OutputExpressionNode node = new OutputExpressionNode(null);
        assertFalse(node.isFunction());
        node.setIsFunction();
        assertTrue(node.isFunction());
    }
}
