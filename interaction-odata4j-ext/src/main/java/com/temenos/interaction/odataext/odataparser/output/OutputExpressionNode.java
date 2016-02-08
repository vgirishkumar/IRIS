package com.temenos.interaction.odataext.odataparser.output;

import java.util.ArrayList;
import java.util.List;

/*
 * Class containing an Odata4j Expression constructed as a tree . This is required because OData4j Expression calls it's
 * visitor in a different sequence to that required printing OData parameters.
 * 
 * Handles errors by return true/false values (like java.util.Set).
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

public class OutputExpressionNode {

    private List<String> arguments = new ArrayList<String>();

    // String representation of operator, function or value.
    private String op;

    // Flag indicating term is bracketed.
    private boolean isBracketed;

    // Flag indicating arguments should be quoted.
    private boolean quoteArguments;

    // Flag indicating therm is a 'function', e.g. func(args), rather than an
    // 'operator', e.g. arg op arg.
    //
    // TODO: We would like to set this based on the visiting oData4j expression
    // type e.g. something like:
    //
    // 'MethodCallExpression.class.isAssignableFrom(expr)'.
    //
    // However by the time our visitors append() function is called the type is
    // no longer available. So have to set this with a messy additional call.
    //
    private boolean isFunction;

    // Parent node of this node.
    private OutputExpressionNode parent;

    // Maximum number of sub nodes per node.
    private static int MAX_SUB_NODES = 3;

    // Constructor for the root node.
    public OutputExpressionNode() {
        this(null);
    }

    // Constructor for child nodes.
    public OutputExpressionNode(OutputExpressionNode parent) {
        this.parent = parent;
        isBracketed = false;
        isFunction = false;
        quoteArguments = false;
    }

    /*
     * Method to print a complete node as an oData parameter.
     */
    public String toOdataParameter() {
        StringBuffer sb = new StringBuffer();

        if (isBracketed) {
            appendOpenBracket(sb);
        }

        if (isFunction()) {
            appendFunction(sb);
        } else {
            appendOperator(sb);
        }

        if (isBracketed) {
            appendCloseBracket(sb);
        }

        // Remove any trailing spaces
        String str = sb.toString().trim();

        return str;
    }

    private void appendFunction(StringBuffer sb) {
        sb.append(op);
        appendOpenBracket(sb);
        appendFunctionArguments(sb);
        appendCloseBracket(sb);
    }

    private void appendFunctionArguments(StringBuffer sb) {
        boolean first = true;

        for (String argument : arguments) {
            if (first) {
                first = false;
            } else {
                appendCommaSpace(sb);
            }
            appendArgument(sb, argument);
        }
    }

    private void appendArgument(StringBuffer sb, String argument) {
        if (isQuoteArgumnets()) {
            appendQuote(sb);
        }

        sb.append(argument);

        if (isQuoteArgumnets()) {
            appendQuote(sb);
        }
    }

    private void appendQuote(StringBuffer sb) {
        sb.append("'");
    }

    private void appendOperator(StringBuffer sb) {
        switch (arguments.size()) {
        case 0:
            // Just append self
            appendValue(sb);
            break;

        case 1:
            appendUnaryOperator(sb);
            break;

        case 2:
            appendBinaryOperator(sb);
            break;

        default:
            new RuntimeException("Too many arguments for an operator \"" + op + "\". Trailing args ignored.");
        }
    }

    private void appendBinaryOperator(StringBuffer sb) {
        sb.append(arguments.get(0));

        appendSpace(sb);
        if (null != op) {
            appendValue(sb);
            appendSpace(sb);
        }
        sb.append(arguments.get(1));
    }

    private void appendUnaryOperator(StringBuffer sb) {
        // It's a unary op. Since it was written first the single operand
        // will be on the LHS
        if (null != op) {
            appendValue(sb);
            appendSpace(sb);
        }
        sb.append(arguments.get(0));
    }

    private void appendValue(StringBuffer sb) {
        sb.append(op);
    }

    private void appendOpenBracket(StringBuffer sb) {
        sb.append("(");
    }

    private void appendCloseBracket(StringBuffer sb) {
        sb.append(")");
    }

    private void appendSpace(StringBuffer sb) {
        sb.append(" ");
    }

    private void appendComma(StringBuffer sb) {
        sb.append(",");
    }

    private void appendCommaSpace(StringBuffer sb) {
        appendComma(sb);
        appendSpace(sb);
    }

    /*
     * Get parent node. If already at the parent returns null.
     */
    public OutputExpressionNode getParent() {
        return parent;
    }

    public boolean addArgument(String argument) {
        if (MAX_SUB_NODES <= arguments.size()) {
            new RuntimeException("Too many argumentents for any function or operator. Trailing args ignored.");
            return false;
        }
        arguments.add(argument);
        return true;
    }

    public boolean setOp(String op) {
        if (null != this.op) {
            new RuntimeException("Operator already set. Subsequent set ignored.");
            return false;
        }
        this.op = op;
        return true;
    }

    public void setIsBracketed() {
        isBracketed = true;
    }

    public boolean isBracketed() {
        return isBracketed;
    }

    public void setQuoteArguments() {
        quoteArguments = true;
    }

    public boolean isQuoteArgumnets() {
        return quoteArguments;
    }

    public void setIsFunction() {
        isFunction = true;
    }

    public boolean isFunction() {
        return isFunction;
    }
}
