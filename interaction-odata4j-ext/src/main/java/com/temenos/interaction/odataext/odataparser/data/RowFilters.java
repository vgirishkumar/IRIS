package com.temenos.interaction.odataext.odataparser.data;

import java.util.ArrayList;
import java.util.List;

import org.odata4j.expression.AndExpression;
import org.odata4j.expression.BinaryCommonExpression;
import org.odata4j.expression.BoolCommonExpression;
import org.odata4j.expression.CommonExpression;
import org.odata4j.expression.EntitySimpleProperty;
import org.odata4j.expression.Expression;
import org.odata4j.expression.IntegralLiteral;
import org.odata4j.expression.StringLiteral;
import org.odata4j.producer.resources.OptionsQueryParser;

import com.temenos.interaction.odataext.odataparser.ODataParser;
import com.temenos.interaction.odataext.odataparser.ODataParser.UnsupportedQueryOperationException;

/*
 * Classes containing information about a set of row filters.
 */

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

public class RowFilters {
    // Wrapped OData4j object. Null means an empty filter list.
    private BoolCommonExpression oData4jExpression;
    
    public RowFilters() {
        // Empty list
        oData4jExpression = null;
    }
    
    public RowFilters(String filterStr) {
        if (filterStr.isEmpty()) {
            // OData4j parser appears to throw on empty strings. Mark this as an
            // empty filter.
            oData4jExpression = null;
        } else {
            oData4jExpression = OptionsQueryParser.parseFilter(filterStr);
        }
    }

    @Deprecated
    public RowFilters(List<RowFilter> filterList) {
        if (filterList.isEmpty()) {
            // OData4j parser appears to throw on empty strings. Mark this as an
            // empty filter.
            oData4jExpression = null;
        } else {
            // Add all the filters
            for (RowFilter filter : filterList) {
                addFilters((BoolCommonExpression) filter.getOData4jExpression());
            }
        }
    }

    public RowFilters(BoolCommonExpression expression) {
        oData4jExpression = expression;
    }

    public BoolCommonExpression getOData4jExpression() {
        return oData4jExpression;
    }

    // Add (and) a filter to the current filter.
    public void addFilters(String filterStr) {
        BoolCommonExpression newExpression = OptionsQueryParser.parseFilter(filterStr);
        addFilters(newExpression);
    }
    
    // Add (and) extra filters to the current filter.
    public void addFilters(RowFilters addFilter) {
        addFilters(addFilter.getOData4jExpression());
    }

    // Add (and) a filter with the list. Where possible use the addFilter(RowFilters...) instead of this.
    public void addFilters(BoolCommonExpression expr) {
        if (null == expr) {
            // Nothing to add
            return;
        }
        
        if (null == oData4jExpression) {
            // This is the first expression. Just use it.
            oData4jExpression = expr;
        } else {
            // We become a new 'and' expression with the old expression and the added
            // expression as leafs.
            oData4jExpression = Expression.and(oData4jExpression, expr);
        }
    }

    /*
     * Convert to a list of, old style, RowFilters. Used for backwards
     * compatibility with code that has not been converted to use 'RowFilters'.
     * 
     * Can fail if the old style RowFilter syntax cannot express the contents of
     * the BoolCommonExpression. In this case we may cause a security issue. So
     * must throw.
     */
    @Deprecated
    public List<RowFilter> asRowFilters() throws UnsupportedQueryOperationException {
        // Start wih top level expression
        return asRowFilters(oData4jExpression);
    }

    @Deprecated
    private List<RowFilter> asRowFilters(BoolCommonExpression expression) throws UnsupportedQueryOperationException {
        List<RowFilter> filters = new ArrayList<RowFilter>();

        if (null == expression) {
            // This is the empty row filter case. Return the empty list.
            return filters;
        }

        // Split BoolCommonExpression up across AndExpressions.
        if (expression instanceof AndExpression) {
            filters.addAll(asRowFilters(((AndExpression) expression).getLHS()));
            filters.addAll(asRowFilters(((AndExpression) expression).getRHS()));
        } else {

            // Only handle the known relationships.
            Relation rel = null;
            for (Relation relation : Relation.values()) {
                if (relation.getOData4jClass().isInstance(expression)) {
                    // Found it
                    rel = relation;
                    break;
                }
            }

            if (null == rel) {
                throw new UnsupportedQueryOperationException("Unrecognised relationship type " + expression);
            }

            if (!BinaryCommonExpression.class.isAssignableFrom(expression.getClass())) {
                throw new UnsupportedQueryOperationException("Expression \"" + expression
                        + "\" cannot be converted to a BinaryCommonExpression.");
            }

            CommonExpression lhsExpression = ((BinaryCommonExpression) expression).getLHS();
            String lhsStr = toRowFilterCompatible(lhsExpression);

            CommonExpression rhsExpression = ((BinaryCommonExpression) expression).getRHS();
            String rhsStr = toRowFilterCompatible(rhsExpression);

            filters.add(new RowFilter(lhsStr, rel, rhsStr));
        }
        return filters;
    }

    // Convert expression to a RowFilter name/value. Throw if it's too complex.
    private String toRowFilterCompatible(CommonExpression expr) throws UnsupportedQueryOperationException {

        // Convert expression to a string.
        String str = ODataParser.OData4jToFilters(expr);

        if (!(expr instanceof IntegralLiteral) && !(expr instanceof EntitySimpleProperty)
                && !(expr instanceof StringLiteral)) {
            throw new UnsupportedQueryOperationException("Expression too complex for row filter. Type=\"" + expr
                    + "\" value=\"" + str + "\"");
        }

        return str;
    }

    public boolean isEmpty() {
        return (null == getOData4jExpression());
    }
}
