package com.temenos.interaction.odataext.odataparser.data;

/*
 * Classes containing metadata about oData 'relations' (i.e. 'operators' and 'functions') between groups of operands.
 * 
 * Also contains information to support equivalent relation in other query languages (e.g. SQL/SOLR).
 * 
 * Each Relation corresponds to an oData4j expression. Where possibly the metadata is recovered from the equivalent
 * expression. However sometimes the required information is not present, or private, and has to be duplicated here.
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

import org.odata4j.expression.AddExpression;
import org.odata4j.expression.AndExpression;
import org.odata4j.expression.BinaryBoolCommonExpression;
import org.odata4j.expression.BinaryCommonExpression;
import org.odata4j.expression.BoolCommonExpression;
import org.odata4j.expression.CeilingMethodCallExpression;
import org.odata4j.expression.ConcatMethodCallExpression;
import org.odata4j.expression.DayMethodCallExpression;
import org.odata4j.expression.DivExpression;
import org.odata4j.expression.EndsWithMethodCallExpression;
import org.odata4j.expression.EqExpression;
import org.odata4j.expression.FloorMethodCallExpression;
import org.odata4j.expression.GeExpression;
import org.odata4j.expression.GtExpression;
import org.odata4j.expression.HourMethodCallExpression;
import org.odata4j.expression.IndexOfMethodCallExpression;
import org.odata4j.expression.LeExpression;
import org.odata4j.expression.LengthMethodCallExpression;
import org.odata4j.expression.LtExpression;
import org.odata4j.expression.MethodCallExpression;
import org.odata4j.expression.MinuteMethodCallExpression;
import org.odata4j.expression.ModExpression;
import org.odata4j.expression.MonthMethodCallExpression;
import org.odata4j.expression.MulExpression;
import org.odata4j.expression.NeExpression;
import org.odata4j.expression.NotExpression;
import org.odata4j.expression.OrExpression;
import org.odata4j.expression.ReplaceMethodCallExpression;
import org.odata4j.expression.RoundMethodCallExpression;
import org.odata4j.expression.SecondMethodCallExpression;
import org.odata4j.expression.StartsWithMethodCallExpression;
import org.odata4j.expression.SubExpression;
import org.odata4j.expression.SubstringMethodCallExpression;
import org.odata4j.expression.SubstringOfMethodCallExpression;
import org.odata4j.expression.ToLowerMethodCallExpression;
import org.odata4j.expression.ToUpperMethodCallExpression;
import org.odata4j.expression.TrimMethodCallExpression;
import org.odata4j.expression.YearMethodCallExpression;

public enum Relation {
    // Note : The double line spacing here is ugly but necessary to prevent auto
    // formatting from putting it all on one line.

    // Operators
    EQ("eq", "=", EqExpression.class, false),

    NE("ne", "<>", NeExpression.class, false),

    LT("lt", "<", LtExpression.class, false),

    GT("gt", ">", GtExpression.class, false),

    LE("le", "<=", LeExpression.class, false),

    GE("ge", ">=", GeExpression.class, false),

    AND("and", "TODO", AndExpression.class, false),

    OR("or", "TODO", OrExpression.class, false),

    NOT("not", "TODO", NotExpression.class, false),

    ADD("add", "TODO", AddExpression.class, true),

    SUB("sub", "TODO", SubExpression.class, true),

    MUL("mul", "TODO", MulExpression.class, true),

    DIV("div", "TODO", DivExpression.class, true),

    MOD("mod", "TODO", ModExpression.class, true),

    // Unary functions.
    TOUPPER("toupper", "TODO", ToUpperMethodCallExpression.class, false),

    TOLOWER("tolower", "TODO", ToLowerMethodCallExpression.class, false),

    LENGTH("length", "TODO", LengthMethodCallExpression.class, false),

    TRIM("trim", "TODO", TrimMethodCallExpression.class, false),

    // TODO this one has syntax like a function but is not derived from a
    // MethodCallExpression.
    // Also it has 1 or 2 arguments. For the moment can't handle it.
    // ISOF("isof", "TODO", IsofExpression.class, false),

    // Unary time related functions

    YEAR("year", "TODO", YearMethodCallExpression.class, false),

    MONTH("month", "TODO", MonthMethodCallExpression.class, false),

    DAY("day", "TODO", DayMethodCallExpression.class, false),

    HOUR("hour", "TODO", HourMethodCallExpression.class, false),

    MINUTE("minute", "TODO", MinuteMethodCallExpression.class, false),

    SECOND("second", "TODO", SecondMethodCallExpression.class, false),

    // Unary maths functions

    ROUND("round", "TODO", RoundMethodCallExpression.class, false),

    FLOOR("floor", "TODO", FloorMethodCallExpression.class, false),

    CEILING("ceiling", "TODO", CeilingMethodCallExpression.class, false),

    // Binary functions.
    SUBSTROF("substringof", "TODO", SubstringOfMethodCallExpression.class, false),

    ENDSWITH("endswith", "TODO", EndsWithMethodCallExpression.class, false),

    STARTSWITH("startswith", "TODO", StartsWithMethodCallExpression.class, false),

    INDEXOF("indexof", "TODO", IndexOfMethodCallExpression.class, false),

    CONCAT("concat", "TODO", ConcatMethodCallExpression.class, false),

    // Ternary functions.
    SUBSTR("substring", "TODO", SubstringMethodCallExpression.class, false),

    REPLACE("replace", "TODO", ReplaceMethodCallExpression.class, false);

    // OData equivalent. Should really be string constants from oData4j. But
    // those are scoped as private so have to redefine here.
    private final String oDataString;

    // Equivalent SQL symbol
    private String sqlSymbol;

    // Equivalent oData4j expression class.
    private Class<?> oData4jClass;

    // Flag indicated only numeric arguments are supported.
    private boolean isNumeric;

    private Relation(String oDataString, String sqlSymbol, Class<?> oDataClass, boolean isNumeric) {
        this.oDataString = oDataString;
        this.sqlSymbol = sqlSymbol;
        this.oData4jClass = oDataClass;
        this.isNumeric = isNumeric;
    }

    public String getoDataString() {
        return (oDataString);
    }

    public String getSqlSymbol() {
        return (sqlSymbol);
    }

    public Class<?> getOData4jClass() {
        return (oData4jClass);
    }

    public boolean isBoolean() {
        // return type == Type.BOOLEAN;
        return BoolCommonExpression.class.isAssignableFrom(getOData4jClass());
    }

    public boolean isNumeric() {
        return isNumeric;
    }

    // Get expected number of arguments
    public int getExpectedArgumentCount() {
        if (!isFunctionCall()) {
            // For operators we can work out the argument count from the
            // expression type.
            if (BinaryCommonExpression.class.isAssignableFrom(getOData4jClass())
                    || BinaryBoolCommonExpression.class.isAssignableFrom(getOData4jClass())) {
                // It's binary
                return 2;
            }

            if (SubstringMethodCallExpression.class.equals(getOData4jClass())) {
                // This is the only expression with three arguments.
                return 3;
            }

            // If we get here it's a unary operator.
            return 1;
        } else {
            // For functions there does not appear to be a way to work out
            // the argument count ... hard code it dependant on the relation
            // type.
            switch (this) {

            case REPLACE:
                return 3;

            case SUBSTR:
                // This one is special. Can be called with 2 or 2 arguments.
                return 2;

            case SUBSTROF:
            case ENDSWITH:
            case STARTSWITH:
            case INDEXOF:
            case CONCAT:
                // Binary functions
                return 2;

            case TOUPPER:
                // Unary functions
                return 1;

            default:
                // Should never get here
                return 1;
            }
        }
    }

    // Returns true if a function, e.g. 'fn(a, b...)' rather then an
    // operator, e.g. 'a op b'.
    public boolean isFunctionCall() {
        return MethodCallExpression.class.isAssignableFrom(getOData4jClass());
    }
}
