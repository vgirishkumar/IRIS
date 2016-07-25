package com.temenos.interaction.jdbc.producer.sql;

/*
 * OData4j uses 'visitors' to convert expressions into Strings. This version converts an expression into a SQL command.
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
import java.sql.Timestamp;

import javax.ws.rs.core.Response.Status;

import org.joda.time.LocalDateTime;
import org.odata4j.expression.AddExpression;
import org.odata4j.expression.AndExpression;
import org.odata4j.expression.BoolParenExpression;
import org.odata4j.expression.CeilingMethodCallExpression;
import org.odata4j.expression.ConcatMethodCallExpression;
import org.odata4j.expression.DateTimeLiteral;
import org.odata4j.expression.DateTimeOffsetLiteral;
import org.odata4j.expression.DayMethodCallExpression;
import org.odata4j.expression.DivExpression;
import org.odata4j.expression.EndsWithMethodCallExpression;
import org.odata4j.expression.EntitySimpleProperty;
import org.odata4j.expression.EqExpression;
import org.odata4j.expression.FloorMethodCallExpression;
import org.odata4j.expression.GeExpression;
import org.odata4j.expression.GtExpression;
import org.odata4j.expression.HourMethodCallExpression;
import org.odata4j.expression.IndexOfMethodCallExpression;
import org.odata4j.expression.IsofExpression;
import org.odata4j.expression.LeExpression;
import org.odata4j.expression.LengthMethodCallExpression;
import org.odata4j.expression.LtExpression;
import org.odata4j.expression.MinuteMethodCallExpression;
import org.odata4j.expression.ModExpression;
import org.odata4j.expression.MonthMethodCallExpression;
import org.odata4j.expression.MulExpression;
import org.odata4j.expression.NeExpression;
import org.odata4j.expression.NotExpression;
import org.odata4j.expression.OrExpression;
import org.odata4j.expression.OrderByExpression;
import org.odata4j.expression.ParenExpression;
import org.odata4j.expression.ReplaceMethodCallExpression;
import org.odata4j.expression.RoundMethodCallExpression;
import org.odata4j.expression.SecondMethodCallExpression;
import org.odata4j.expression.StartsWithMethodCallExpression;
import org.odata4j.expression.StringLiteral;
import org.odata4j.expression.SubExpression;
import org.odata4j.expression.SubstringMethodCallExpression;
import org.odata4j.expression.SubstringOfMethodCallExpression;
import org.odata4j.expression.TimeLiteral;
import org.odata4j.expression.ToLowerMethodCallExpression;
import org.odata4j.expression.ToUpperMethodCallExpression;
import org.odata4j.expression.TrimMethodCallExpression;
import org.odata4j.expression.YearMethodCallExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.jdbc.SqlRelation;
import com.temenos.interaction.jdbc.exceptions.JdbcException;
import com.temenos.interaction.odataext.odataparser.odata4j.PrintExpressionVisitor;

public class SQLExpressionVisitor extends PrintExpressionVisitor {

    private final static Logger logger = LoggerFactory.getLogger(SQLExpressionVisitor.class);

    // Variables handling the SQLExpression tree,
    private SQLExpressionNode rootNode = new SQLExpressionNode();
    private SQLExpressionNode currentNode = rootNode;

    @Override
    public String toString() {
        // Print out the expression tree
        return rootNode.toSqlParameter();
    }

    /*
     * Support for formatted calls from the super class. These append the
     * argument as a simple string without formatting. Where local code requires
     * formatting use appendFormatted().
     */
    protected void append(String format, Object... args) {
        append(String.format("%s", args));
    }

    /*
     * Append with format string
     */
    protected void appendFormatted(String format, Object... args) {
        append(String.format(format, args));
    }

    protected void append(String str) {
        if (!currentNode.addArgument(str)) {
            throw new JdbcException(Status.INTERNAL_SERVER_ERROR, "Internal error adding:" + str);
        }
    }

    /*
     * Append an operator or function.
     */
    private void append(SqlRelation rel) {
        append(rel, false);
    }

    /*
     * Append an operator of function optionally surrounded by spaces.
     */
    private void append(SqlRelation rel, boolean spaced) {
        // If there is no SQL equivalent throw.
        if (null == rel.getSqlSymbol()) {
            String msg = "Unsupported SQL relation \"" + rel + "\".";
            logger.error(msg);
            throw new UnsupportedOperationException(msg);
        }

        // Remember if symbol should be surrounded by spaces.
        currentNode.setIsSpaced(spaced);

        currentNode.setRelation(rel);
    }

    @Override
    public void afterDescend() {
        toParent();
    }

    /*
     * Move a level up the expression tree.
     */
    private void toParent() {
        SQLExpressionNode parentNode = currentNode.getParent();
        if (null == parentNode) {
            throw new JdbcException(Status.INTERNAL_SERVER_ERROR, "Tried to go above expression tree root.");
        }

        // Child is complete. Print it as a parent argument.
        parentNode.addArgument(currentNode.toSqlParameter());

        currentNode = parentNode;
    }

    @Override
    public void beforeDescend() {
        // Create a new child node and move to it.
        currentNode = new SQLExpressionNode(currentNode);
    }

    @Override
    public void betweenDescend() {
        toParent();

        // Create next child node and move to it.
        currentNode = new SQLExpressionNode(currentNode);
    }

    @Override
    public void visit(OrderByExpression expr) {
        // Don't append the 'orderBy' tag.
    }

    // Double quote column names.
    @Override
    public void visit(EntitySimpleProperty expr) {
        appendFormatted("\"%s\"", expr.getPropertyName());
    }

    @Override
    public void visit(EqExpression expr) {
        append(SqlRelation.EQ);
    }

    @Override
    public void visit(NeExpression expr) {
        append(SqlRelation.NE);
    }

    @Override
    public void visit(GtExpression expr) {
        append(SqlRelation.GT);
    }

    @Override
    public void visit(LtExpression expr) {
        append(SqlRelation.LT);
    }

    @Override
    public void visit(GeExpression expr) {
        append(SqlRelation.GE);
    }

    @Override
    public void visit(LeExpression expr) {
        append(SqlRelation.LE);
    }

    @Override
    public void visit(AndExpression expr) {
        append(SqlRelation.AND, true);
    }

    @Override
    public void visit(OrExpression expr) {
        append(SqlRelation.OR, true);
    }

    @Override
    public void visit(AddExpression expr) {
        append(SqlRelation.ADD);
    }

    @Override
    public void visit(SubExpression expr) {
        append(SqlRelation.SUB);
    }

    @Override
    public void visit(MulExpression expr) {
        append(SqlRelation.MUL);
    }

    @Override
    public void visit(DivExpression expr) {
        append(SqlRelation.DIV);
    }

    @Override
    public void visit(ModExpression expr) {
        append(SqlRelation.MOD);
    }

    @Override
    public void visit(NotExpression expr) {
        append(SqlRelation.NOT, true);
    }

    @Override
    public void visit(BoolParenExpression expr) {
        // Make a note that The current term is bracketed.
        currentNode.setIsBracketed();
    }

    @Override
    public void visit(ParenExpression expr) {
        // Make a note that The current term is bracketed.
        currentNode.setIsBracketed();
    }

    // Literal strings may contain spaces or dots. So single quote.
    @Override
    public void visit(StringLiteral expr) {
        appendFormatted("'%s'", expr.getValue());
    }

    @Override
    public void visit(SubstringMethodCallExpression expr) {
        append(SqlRelation.SUBSTR);
    }

    @Override
    public void visit(SubstringOfMethodCallExpression expr) {
        append(SqlRelation.SUBSTROF);
    }

    @Override
    public void visit(ToUpperMethodCallExpression expr) {
        append(SqlRelation.TOUPPER);
    }

    @Override
    public void visit(ToLowerMethodCallExpression expr) {
        append(SqlRelation.TOLOWER);
    }

    @Override
    public void visit(ReplaceMethodCallExpression expr) {
        append(SqlRelation.REPLACE);
    }

    @Override
    public void visit(LengthMethodCallExpression expr) {
        append(SqlRelation.LENGTH);
    }

    @Override
    public void visit(TrimMethodCallExpression expr) {
        append(SqlRelation.TRIM);
    }

    @Override
    public void visit(YearMethodCallExpression expr) {
        append(SqlRelation.YEAR);
    }

    @Override
    public void visit(MonthMethodCallExpression expr) {
        append(SqlRelation.MONTH);
    }

    @Override
    public void visit(DayMethodCallExpression expr) {
        append(SqlRelation.DAY);
    }

    @Override
    public void visit(HourMethodCallExpression expr) {
        append(SqlRelation.HOUR);
    }

    @Override
    public void visit(MinuteMethodCallExpression expr) {
        append(SqlRelation.MINUTE);
    }

    @Override
    public void visit(SecondMethodCallExpression expr) {
        append(SqlRelation.SECOND);
    }

    @Override
    public void visit(RoundMethodCallExpression expr) {
        append(SqlRelation.ROUND);
    }

    @Override
    public void visit(FloorMethodCallExpression expr) {
        append(SqlRelation.FLOOR);
    }

    @Override
    public void visit(CeilingMethodCallExpression expr) {
        append(SqlRelation.CEILING);
    }

    @Override
    public void visit(IsofExpression expr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(EndsWithMethodCallExpression expr) {
        append(SqlRelation.ENDSWITH);
    }

    @Override
    public void visit(StartsWithMethodCallExpression expr) {
        append(SqlRelation.STARTSWITH);
    }

    @Override
    public void visit(IndexOfMethodCallExpression expr) {
        append(SqlRelation.INDEXOF);
    }

    @Override
    public void visit(ConcatMethodCallExpression expr) {
        append(SqlRelation.CONCAT);
    }

    @Override
    public void visit(DateTimeLiteral expr) {
        // Get the joda representation
        LocalDateTime jodaTime = expr.getValue();

        // Convert it to SQL representation
        Timestamp timeStamp = new Timestamp(jodaTime.toDateTime().getMillis());

        // Print it out.
        String timeStampStr = timeStamp.toString();
        appendFormatted("'%s'", timeStampStr);
    }

    @Override
    public void visit(DateTimeOffsetLiteral expr) {
        throw new UnsupportedOperationException("DateTimeOffsetLiteral not supported.");
    }

    @Override
    public void visit(TimeLiteral expr) {
        throw new UnsupportedOperationException("TimeLiteral not supported.");
    }
}
