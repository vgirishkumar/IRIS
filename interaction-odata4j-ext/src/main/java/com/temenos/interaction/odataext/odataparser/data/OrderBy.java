package com.temenos.interaction.odataext.odataparser.data;

import java.util.Objects;

import org.odata4j.expression.Expression;
import org.odata4j.expression.OrderByExpression;
import org.odata4j.expression.OrderByExpression.Direction;

import com.temenos.interaction.odataext.odataparser.output.ParameterPrinter;

/*
 * Thin wrapper round the odat4j OrderByExpression.
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

public class OrderBy {
    // Wrapped OData4j object.
    private OrderByExpression oData4jExpression;

    public OrderBy(String orderBy, Direction direction) {
        this(Expression.orderBy(Expression.parse(orderBy), direction));
    }

    public OrderBy(OrderByExpression orderBy) {
        oData4jExpression = orderBy;
    }

    public FieldName getFieldName() {
        StringBuffer sb = new StringBuffer();
        ParameterPrinter printer = new ParameterPrinter();
        printer.appendParameter(sb, oData4jExpression.getExpression(), true);
        return new FieldName(sb.toString());
    }

    public OrderByExpression getOData4jExpression() {
        return oData4jExpression;
    }

    public boolean isAcsending() {
        return oData4jExpression.getDirection() == Direction.ASCENDING;
    }

    public Direction getDirection() {
        return oData4jExpression.getDirection();
    }

    public String getDirectionString() {
        if (getDirection() == Direction.ASCENDING) {
            return "asc";
        } else {
            return "desc";
        }
    }

    /**
     * Define equality of state. To enable comparison.
     */
    @Override
    public boolean equals(Object aThat) {
        if (this == aThat)
            return true;
        if (!(aThat instanceof OrderBy))
            return false;

        OrderBy that = (OrderBy) aThat;
        return (this.getFieldName().getName().equals(that.getFieldName().getName()) && (this.getDirection() == that
                .getDirection()));
    }

    /**
     * Return same hash code for identical objects. So contains() works.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getFieldName().getName(), this.getDirection());
    }
}
