package com.temenos.interaction.odataext.odataparser.output;

/*
 * OData4j uses 'visitors' to convert it's expressions into Strings. However there does not appear to be a visitor that
 * produces output in the format required for re-parsing. Unlike OData4j, which parses once and then works 
 * with the QueryInfo structure, we need to repeatedly parse parameters and write them back into the parameter list. 
 * (Looks like PreOrderVisitor is moving towards this functionality but it is not present in oData4j 0.7.0)
 * 
 * This class extend OUR version of PrintExpressionVisitor. Not the, almost identical, OData4j version. This means we can
 * make use of most of the parents, unchanged, visit() methods. These will call back to our, changed, append() methods.
 * If the OData4j PrintExpressionVisitor is extended then these calls will go to it's, private, append() methods and the
 * desired results will not be achieved.
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
import org.odata4j.expression.BoolParenExpression;
import org.odata4j.expression.CeilingMethodCallExpression;
import org.odata4j.expression.ConcatMethodCallExpression;
import org.odata4j.expression.DateTimeLiteral;
import org.odata4j.expression.DateTimeOffsetLiteral;
import org.odata4j.expression.DayMethodCallExpression;
import org.odata4j.expression.DecimalLiteral;
import org.odata4j.expression.DoubleLiteral;
import org.odata4j.expression.EndsWithMethodCallExpression;
import org.odata4j.expression.FloorMethodCallExpression;
import org.odata4j.expression.HourMethodCallExpression;
import org.odata4j.expression.IndexOfMethodCallExpression;
import org.odata4j.expression.Int64Literal;
import org.odata4j.expression.IsofExpression;
import org.odata4j.expression.LengthMethodCallExpression;
import org.odata4j.expression.MethodCallExpression;
import org.odata4j.expression.MinuteMethodCallExpression;
import org.odata4j.expression.MonthMethodCallExpression;
import org.odata4j.expression.OrderByExpression;
import org.odata4j.expression.ParenExpression;
import org.odata4j.expression.ReplaceMethodCallExpression;
import org.odata4j.expression.RoundMethodCallExpression;
import org.odata4j.expression.SecondMethodCallExpression;
import org.odata4j.expression.StartsWithMethodCallExpression;
import org.odata4j.expression.StringLiteral;
import org.odata4j.expression.SubstringMethodCallExpression;
import org.odata4j.expression.SubstringOfMethodCallExpression;
import org.odata4j.expression.TimeLiteral;
import org.odata4j.expression.ToLowerMethodCallExpression;
import org.odata4j.expression.ToUpperMethodCallExpression;
import org.odata4j.expression.TrimMethodCallExpression;
import org.odata4j.expression.YearMethodCallExpression;
import org.odata4j.internal.InternalUtil;

import com.temenos.interaction.odataext.odataparser.odata4j.PrintExpressionVisitor;

public class OutputExpressionVisitor extends PrintExpressionVisitor {

    // Variables handling the ExpressionPrinter tree,
    private OutputExpressionNode rootNode;
    private OutputExpressionNode currentNode;
    
    public OutputExpressionVisitor() {
        reset();
    }
    
    // Reset tree to it's starting state. Put current tree up for garbage collection.
    public void reset() {
        super.reset();
        rootNode = new OutputExpressionNode();
        currentNode = rootNode;
    }

    @Override
    public String toString() {
        // Print out the expression tree
        return rootNode.toOdataParameter();
    }

    protected void append(String format, Object... args) {
        // For non overridden methods append the arg as a simple string. Don't
        // do the formatting.
        append(String.format("%s", args));
    }

    protected void appendFormatted(String format, Object... args) {
        // For overridden methods do the formatting.
        append(String.format(format, args));
    }

    protected void append(String str) {
        if (!currentNode.setOp(str)) {
            new RuntimeException("Internal error adding:" + str);
        }
    }

    @Override
    public void afterDescend() {
        // Move a level up the ExpressionPrinter tree
        OutputExpressionNode parentNode = currentNode.getParent();
        if (null == parentNode) {
            // Tried to go above the top of the tree.
            new RuntimeException("Tried to go above tree root.");
            return;
        }

        // Child is complete. Print it into parent arguments.
        parentNode.addArgument(currentNode.toOdataParameter());

        currentNode = parentNode;
    }

    @Override
    public void beforeDescend() {
        // Create a new child node and start building it.
        currentNode = new OutputExpressionNode(currentNode);
    }

    @Override
    public void betweenDescend() {
        OutputExpressionNode parentNode = currentNode.getParent();
        if (null == parentNode) {
            // Tried to go above the top of the tree.
            new RuntimeException("Tried to go above tree root.");
            return;
        }

        // Child is complete. Print it into parent arguments.
        parentNode.addArgument(currentNode.toOdataParameter());

        // Move back to parent
        currentNode = parentNode;

        // Create next child node and move to it.
        currentNode = new OutputExpressionNode(currentNode);
    }

    // Don't append the 'orderBy' tag.
    @Override
    public void visit(OrderByExpression expr) {
    }

    // The current term is bracketed. Make a note of the fact.
    @Override
    public void visit(BoolParenExpression expr) {
        currentNode.setIsBracketed();
    }

    @Override
    public void visit(ParenExpression expr) {
        currentNode.setIsBracketed();
    }

    // Literal strings may contain spaces or dots. So must be quoted
    @Override
    public void visit(StringLiteral expr) {
        append("'" + expr.getValue() + "'");
    }

    @Override
    public void visit(SubstringMethodCallExpression expr) {
        currentNode.setIsFunction();
        super.visit(expr);
    }

    public void visit(MethodCallExpression expr) {
        currentNode.setIsFunction();
    }

    @Override
    public void visit(SubstringOfMethodCallExpression expr) {
        currentNode.setIsFunction();
        super.visit(expr);
    }

    @Override
    public void visit(ToUpperMethodCallExpression expr) {
        currentNode.setIsFunction();
        super.visit(expr);
    }

    @Override
    public void visit(ToLowerMethodCallExpression expr) {
        currentNode.setIsFunction();
        super.visit(expr);
    }

    @Override
    public void visit(ReplaceMethodCallExpression expr) {
        currentNode.setIsFunction();
        super.visit(expr);
    }

    @Override
    public void visit(LengthMethodCallExpression expr) {
        currentNode.setIsFunction();
        super.visit(expr);
    }

    @Override
    public void visit(TrimMethodCallExpression expr) {
        currentNode.setIsFunction();
        super.visit(expr);
    }

    @Override
    public void visit(YearMethodCallExpression expr) {
        currentNode.setIsFunction();
        super.visit(expr);
    }

    @Override
    public void visit(MonthMethodCallExpression expr) {
        currentNode.setIsFunction();
        super.visit(expr);
    }

    @Override
    public void visit(DayMethodCallExpression expr) {
        currentNode.setIsFunction();
        super.visit(expr);
    }

    @Override
    public void visit(HourMethodCallExpression expr) {
        currentNode.setIsFunction();
        super.visit(expr);
    }

    @Override
    public void visit(MinuteMethodCallExpression expr) {
        currentNode.setIsFunction();
        super.visit(expr);
    }

    @Override
    public void visit(SecondMethodCallExpression expr) {
        currentNode.setIsFunction();
        super.visit(expr);
    }

    @Override
    public void visit(RoundMethodCallExpression expr) {
        currentNode.setIsFunction();
        super.visit(expr);
    }

    @Override
    public void visit(FloorMethodCallExpression expr) {
        currentNode.setIsFunction();
        super.visit(expr);
    }

    @Override
    public void visit(CeilingMethodCallExpression expr) {
        currentNode.setIsFunction();
        super.visit(expr);
    }

    @Override
    public void visit(IsofExpression expr) {
        currentNode.setIsFunction();

        // Isof has literal arguments.
        currentNode.setQuoteArguments();

        super.visit(expr);
    }

    @Override
    public void visit(EndsWithMethodCallExpression expr) {
        currentNode.setIsFunction();
        super.visit(expr);
    }

    @Override
    public void visit(StartsWithMethodCallExpression expr) {
        currentNode.setIsFunction();
        super.visit(expr);
    }

    @Override
    public void visit(IndexOfMethodCallExpression expr) {
        currentNode.setIsFunction();
        super.visit(expr);
    }

    @Override
    public void visit(ConcatMethodCallExpression expr) {
        currentNode.setIsFunction();
        super.visit(expr);
    }

    @Override
    public void visit(DecimalLiteral expr) {
        appendFormatted("%sM", expr.getValue());
    }

    @Override
    public void visit(Int64Literal expr) {
        appendFormatted("%sL", expr.getValue());

    }

    @Override
    public void visit(DoubleLiteral expr) {
        appendFormatted("%sd", expr.getValue());
    }

    @Override
    public void visit(DateTimeLiteral expr) {
        appendFormatted("datetime'%s'", InternalUtil.formatDateTimeForXml(expr.getValue()));
    }

    /*
     * TODO Currently not sure about the syntax for this.
     */
    @Override
    public void visit(DateTimeOffsetLiteral expr) {
        throw new UnsupportedOperationException("DateTimeOffsetLiteral not supported.");

        // Maybe should be something like.
        // appendFormatted("%sZ",
        // InternalUtil.formatDateTimeOffsetForXml(expr.getValue()));
    }

    /*
     * TODO Currently not sure about the syntax for this.
     */
    @Override
    public void visit(TimeLiteral expr) {
        throw new UnsupportedOperationException("TimeLiteral not supported.");

        // Maybe should be something like.
        // appendFormatted("%s",
        // expr.getValue().toString(ExpressionParser.TIME_FORMATTER));
    }
}
