package com.temenos.interaction.odataext.odataparser.data;

import java.util.ArrayList;
import java.util.List;

import org.odata4j.expression.AndExpression;
import org.odata4j.expression.BinaryCommonExpression;
import org.odata4j.expression.BoolCommonExpression;
import org.odata4j.expression.CommonExpression;
import org.odata4j.expression.EntitySimpleProperty;
import org.odata4j.expression.Expression;
import org.odata4j.producer.resources.OptionsQueryParser;

import com.temenos.interaction.odataext.odataparser.ODataParser;

/*
 * Classes containing information about a set of row filters.
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

public class RowFilters {
    // Wrapped OData4j object. Null means an empty filter list.
    private BoolCommonExpression oData4jExpression;

    public RowFilters(String filterStr) {
        if (filterStr.isEmpty()) {
            // OData4j parser appears to throw on empty strings. Mark this as an
            // empty filter.
            oData4jExpression = null;
        } else {
            oData4jExpression = OptionsQueryParser.parseFilter(filterStr);
        }
    }

    public RowFilters(BoolCommonExpression expression) {
        oData4jExpression = expression;
    }

    public BoolCommonExpression getBoolCommonExpression() {
        return oData4jExpression;
    }

    // Add (and) a filter with the list
    public void addFilter(String filterStr) {
        BoolCommonExpression newExpression = OptionsQueryParser.parseFilter(filterStr);

        // We become a new 'and' expression with the old expression and our new
        // expression as leafs.
        oData4jExpression = Expression.and(oData4jExpression, newExpression);
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
    public List<RowFilter> asRowFilters() {
        // Start wih top level expression
        return asRowFilters(oData4jExpression);
    }

    @Deprecated
    private List<RowFilter> asRowFilters(BoolCommonExpression expression) {
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
                throw new SecurityException("Unrecognised relationship type " + expression);
            }

            if (!BinaryCommonExpression.class.isAssignableFrom(expression.getClass())) {
                throw new SecurityException("Expression \"" + expression
                        + "\" cannot be converted to a BinaryCommonExpression.");
            }

            CommonExpression lhsExpression = ((BinaryCommonExpression) expression).getLHS();
            if (!(lhsExpression instanceof EntitySimpleProperty)) {
                throw new SecurityException("LHS expression too complex " + lhsExpression);
            }
            String lhsStr = ODataParser.OData4jToFilters(lhsExpression);

            CommonExpression rhsExpression = ((BinaryCommonExpression) expression).getRHS();
            if (!(rhsExpression instanceof EntitySimpleProperty)) {
                throw new SecurityException("RHS expression too complex" + rhsExpression);
            }
            String rhsStr = ODataParser.OData4jToFilters(rhsExpression);

            filters.add(new RowFilter(lhsStr, rel, rhsStr));
        }
        return filters;
    }
}
