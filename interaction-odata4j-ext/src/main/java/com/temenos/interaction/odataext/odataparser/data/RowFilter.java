package com.temenos.interaction.odataext.odataparser.data;

import org.odata4j.expression.BinaryCommonExpression;
import org.odata4j.expression.CommonExpression;
import org.odata4j.producer.resources.OptionsQueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.odataext.odataparser.ODataParser;
import com.temenos.interaction.odataext.odataparser.ODataParser.UnsupportedQueryOperationException;

/*
 * Class containing information about a single row filters.
 * 
 * This code supports backwards compatibility with the old oDataParser which stored a list of individual 'and' terms. New
 * code should use the 'RowFilters' class which can handle more complex filter expression.
 */

/*
 * #%L
 * interaction-commands-Authorization
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

public class RowFilter {
    
    // Since this class supports backwards comparability, with a class that did not throw, its callers may not hamdle
    // exceptions. So catch them and don't throw. When we replace this with RowFilters adjuxt the callers to do things
    // correctly
    private final static Logger logger = LoggerFactory.getLogger(RowFilter.class);
    
    // Wrapped OData4j object.
    private BinaryCommonExpression oData4jExpression;

    public RowFilter(FieldName name, Relation relation, String value) {
        this(name.getName(), relation, value);
    }

    // Constructor for callers that don't have a FieldName.
    public RowFilter(String name, Relation relation, String value) {
        // Only way to convert fields to an Expression is to print and then parse it.
        String filterStr = name + " " + relation.getoDataString() + " " + value;
        CommonExpression expr = OptionsQueryParser.parseFilter(filterStr);
        
        if (!(expr instanceof BinaryCommonExpression)) {
            // Too complex to fit in a RowFIlter
            logger.error("Expression too complex for row filter. Type=\"" + expr + "\"");
            
            // For backward comparability cannot throw UnsupportedQueryOperationException. So throw something that old
            // callers can handle.
            throw new NullPointerException("Expression too complex for row filter. Type=\"" + expr + "\"");
        }         
        oData4jExpression = (BinaryCommonExpression)expr;
    }
    
    /*
     * Get wrapped oData4J object
     */
    public BinaryCommonExpression getOData4jExpression() {
        return oData4jExpression;
    }
    
    public FieldName getFieldName() {
        FieldName name = null;
        
        try {
            name = new FieldName(oData4jExpression.getLHS());
        }
        catch (UnsupportedQueryOperationException e) {
            logger.error("LHS incompatible with FieldName.");
        }
        return name;
    }

    public Relation getRelation() {
        // Look for a matching relation
        for (Relation rel : Relation.values()) {
            if (rel.getOData4jClass().isInstance(oData4jExpression)) {
                return rel;
            }
        }
        return null;
    }

    public String getValue() {
        return (ODataParser.OData4jToFilters(oData4jExpression.getRHS()));
    }
}
