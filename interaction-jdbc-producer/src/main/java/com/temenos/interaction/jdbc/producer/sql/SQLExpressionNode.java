package com.temenos.interaction.jdbc.producer.sql;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import com.temenos.interaction.jdbc.SqlRelation;
import com.temenos.interaction.jdbc.exceptions.JdbcException;

/*
 * Class containing a SQL command stored as a tree. This is required because OData4j Expression calls it's
 * visitor in a different sequence to that needed for construction of the SQL command.
 */

/*
 * #%L
 * interaction-jdbc-producer
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

public class SQLExpressionNode {

    // Node will either contain a relation or a list of arguments.
    private List<String> arguments = new ArrayList<String>();
    private SqlRelation rel;

    // Flag indicating term is bracketed.
    private boolean isBracketed;

    // Flag indicating SQL term should be surrounded by spaces.
    private boolean isSpaced;

    // Parent node of this node.
    private SQLExpressionNode parent;

    // Maximum number of arguments for a node
    private static int MAX_ARGUMENTS = 3;

    // Constructor for the root node.
    public SQLExpressionNode() {
        this(null);
    }

    // Constructor for child nodes.
    public SQLExpressionNode(SQLExpressionNode parent) {
        this.parent = parent;
        isBracketed = false;
    }

    /*
     * Method to print a complete node as an SQL command.
     */
    public String toSqlParameter() {
        StringBuilder sb = new StringBuilder();

        if (isBracketed) {
            appendOpenBracket(sb);
        }

        if ((null != rel) && (arguments.isEmpty())) {
            throw new JdbcException(Status.INTERNAL_SERVER_ERROR, "Node has no content.");
        }

        if (null != rel) {
            // First try to add a relation
            if (!rel.getSqlSymbol().contains("%s")) {
                // It's a regular function or operator
                if (isFunction()) {
                    appendFunction(sb);
                } else {
                    appendOperator(sb);
                }
            } else {
                // It's an SQL operation that cannot be represented as a
                // simple function or operator.
                appendFormatString(sb);
            }
        } else {
            // Add the arguments.
            boolean first = true;
            for (String argument : arguments) {
                first = appendFunctionArgument(sb, argument, first);
            }
        }

        if (isBracketed) {
            appendCloseBracket(sb);
        }

        // Remove any trailing spaces
        String str = sb.toString().trim();

        return str;
    }

    private void appendFunction(StringBuilder sb) {
        appendSymbol(sb);
        appendOpenBracket(sb);
        appendFunctionArguments(sb);
        appendCloseBracket(sb);
    }

    private void appendFunctionArguments(StringBuilder sb) {
        boolean first = true;

        if (null == rel.getArgumentSequence()) {
            // Add SQL arguments in same order as oData.
            for (String argument : arguments) {
                first = appendFunctionArgument(sb, argument, first);
            }
        } else {
            // Add SQL arguments in specified order.
            appendOrderedFunctionArguments(sb);
        }
    }

    private void appendOrderedFunctionArguments(StringBuilder sb) {
        boolean first = true;

        for (Integer i : rel.getArgumentSequence()) {
            if (i >= arguments.size()) {
                throw new JdbcException(Status.INTERNAL_SERVER_ERROR, "Argument index \"" + i + "\" too big.");
            } else {
                first = appendFunctionArgument(sb, arguments.get(i), first);
            }
        }
    }

    private boolean appendFunctionArgument(StringBuilder sb, String argument, boolean first) {
        boolean firstReturn = first;
        if (firstReturn) {
            firstReturn = false;
        } else {
            appendCommaSpace(sb);
        }
        sb.append(argument);

        return firstReturn;
    }

    /*
     * There are some SQL operations that cannot be represented as an operator
     * (e.g. 'a eq b') or as a function (e.g. 'func(a, b...')). In this case we
     * have a formatted sting into which the arguments must be inserted.
     */
    private void appendFormatString(StringBuilder sb) {
        sb.append(String.format(rel.getSqlSymbol(), arguments.toArray()));
    }

    private void appendOperator(StringBuilder sb) {
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
            throw new JdbcException(Status.INTERNAL_SERVER_ERROR, "Too many arguments for an operator \""
                    + rel.getSqlSymbol() + "\".");
        }
    }

    private void appendBinaryOperator(StringBuilder sb) {
        sb.append(arguments.get(0));

        if (isSpaced()) {
            appendSingleSpace(sb);
        }

        if (null != rel) {
            appendSymbol(sb);

            if (isSpaced()) {
                appendSingleSpace(sb);
            }
        }
        sb.append(arguments.get(1));
    }

    private void appendSymbol(StringBuilder sb) {
        sb.append(rel.getSqlSymbol());
    }

    private void appendUnaryOperator(StringBuilder sb) {
        if (null != rel) {
            appendSymbol(sb);

            if (isSpaced()) {
                appendSingleSpace(sb);
            }
        }
        sb.append(arguments.get(0));
    }

    private void appendValue(StringBuilder sb) {
        // Add the first argument.
        sb.append(arguments.get(0));
    }

    /*
     * Add space if not already present.
     */
    private void appendSingleSpace(StringBuilder sb) {
        if ((0 < sb.length()) && !(' ' == sb.charAt(sb.length() - 1))) {
            appendSpace(sb);
        }
    }

    private void appendCommaSpace(StringBuilder sb) {
        appendComma(sb);
        appendSpace(sb);
    }

    private void appendOpenBracket(StringBuilder sb) {
        sb.append("(");
    }

    private void appendCloseBracket(StringBuilder sb) {
        sb.append(")");
    }

    private void appendSpace(StringBuilder sb) {
        sb.append(" ");
    }

    private void appendComma(StringBuilder sb) {
        sb.append(",");
    }

    /*
     * Get parent node. The root returns null.
     */
    public SQLExpressionNode getParent() {
        return parent;
    }

    public boolean addArgument(String argument) {
        if (MAX_ARGUMENTS <= arguments.size()) {
            throw new JdbcException(Status.INTERNAL_SERVER_ERROR, "Too many argumentents for function or operator.");
        }
        arguments.add(argument);
        return true;
    }

    public boolean setRelation(SqlRelation rel) {
        if (null != this.rel) {
            throw new JdbcException(Status.INTERNAL_SERVER_ERROR, "Relation already set.");
        }
        this.rel = rel;
        return true;
    }

    public void setIsBracketed() {
        isBracketed = true;
    }

    public boolean isBracketed() {
        return isBracketed;
    }

    public void setIsSpaced(boolean spaced) {
        this.isSpaced = spaced;
    }

    public boolean isFunction() {
        if (null == rel) {
            // If there is no relation it's not a functions.
            return false;
        }
        return rel.isFunctionCall();
    }

    public boolean isSpaced() {
        return isSpaced;
    }
}
