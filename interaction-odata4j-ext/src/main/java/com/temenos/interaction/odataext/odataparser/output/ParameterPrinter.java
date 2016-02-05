package com.temenos.interaction.odataext.odataparser.output;

/*
 * Class used to print one or more oData parameter. Based on code from org.odata4j.producer.QueryInfo
 * but has been significantly modified.
 */

/*
 * #%L
 * interaction-authorization
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
import java.util.List;

import org.odata4j.expression.CommonExpression;

import com.temenos.interaction.odataext.odataparser.odata4j.PrintExpressionVisitor;

public class ParameterPrinter {

    // private final static Logger logger =
    // LoggerFactory.getLogger(ParameterPrinter.class);

    // Expression visitor used by this printer.
    private PrintExpressionVisitor visitor;

    public ParameterPrinter() {
        // Use the default visitor
        this(new OutputExpressionVisitor());
    }

    public ParameterPrinter(PrintExpressionVisitor visitor) {
        this.visitor = visitor;
    }

    /*
     * Method appending a single OData parameter. If a parameter contains more
     * than one field these are handled by sub methods.
     */
    public boolean appendParameter(StringBuffer sb, String name, Object parameter, boolean first) {
        if (parameter == null)
            return first;
        if (parameter instanceof List && ((List<?>) parameter).isEmpty())
            return first;
        if (first)
            first = false;
        else
            sb.append("&");
        if (null != name) {
            sb.append(name + "=");
        }
        appendSingleParameterOrList(sb, parameter);
        return first;
    }

    /*
     * Overload not adding the "Name=" term.
     */
    public boolean appendParameter(StringBuffer sb, Object parameter, boolean first) {
        // Do not accept nulls.
        if (null == parameter) {
            throw new NullPointerException();
        }
        return appendParameter(sb, null, parameter, first);
    }

    /*
     * Method appending a single OData field or list of OData fields.
     */
    @SuppressWarnings("unchecked")
    private void appendSingleParameterOrList(StringBuffer sb, Object parameter) {
        if (parameter instanceof List) {
            appendList(sb, (List<Object>) parameter);
        } else {
            appendSingleParameter(sb, (CommonExpression) parameter);
        }
    }

    /*
     * Method appending a comma separated list of OData fields.
     */
    private void appendList(StringBuffer sb, List<Object> list) {
        boolean first = true;
        for (Object field : list) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            appendSingleParameterOrList(sb, field);
        }
    }

    /*
     * Method appending a single OData expression.
     * 
     * The expression may be complex. However oData4j knows how to print complex
     * expressions. So use it's 'visitor' mechanism.
     */
    private void appendSingleParameter(StringBuffer sb, CommonExpression field) {
        // Clear current visitor content
        visitor.reset();

        field.visit(visitor);
        sb.append(visitor.toString());
    }
}
