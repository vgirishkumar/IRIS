package com.temenos.interaction.jdbc;

/*
 * Wrapper around the oDataParser 'Relation' class. Adds fields required for SQL support to the classe's oData 
 * 'Relations' ('operators' and 'functions') metadata.
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

import java.util.Arrays;
import java.util.List;

import com.temenos.interaction.odataext.odataparser.data.Relation;

public enum SqlRelation {
    // Note : Double line spacing here is ugly but necessary to prevent auto
    // formatting from putting it all on one line.

    // Operators
    EQ(Relation.EQ, "="),

    NE(Relation.NE, "<>"),

    LT(Relation.LT, "<"),

    GT(Relation.GT, ">"),

    LE(Relation.LE, "<="),

    GE(Relation.GE, ">="),

    AND(Relation.AND, "AND"),

    OR(Relation.OR, "OR"),

    NOT(Relation.NOT, "NOT"),

    ADD(Relation.ADD, "+"),

    SUB(Relation.SUB, "-"),

    MUL(Relation.MUL, "*"),

    DIV(Relation.DIV, "/"),

    MOD(Relation.MOD, "%"),

    // Unary functions.
    TOUPPER(Relation.TOUPPER, "UPPER"),

    TOLOWER(Relation.TOLOWER, "LOWER"),

    LENGTH(Relation.LENGTH, "LEN"),

    TRIM(Relation.TRIM, "RTRIM(LTRIM(%s))"),

    ISOF(Relation.ISOF, null),

    // Unary time related functions
    YEAR(Relation.YEAR, "DATEPART(YEAR, %s)"),

    MONTH(Relation.MONTH, "DATEPART(MONTH, %s)"),

    DAY(Relation.DAY, "DATEPART(DAY, %s)"),

    HOUR(Relation.HOUR, "DATEPART(HOUR, %s)"),

    MINUTE(Relation.MINUTE, "DATEPART(MINUTE, %s)"),

    SECOND(Relation.SECOND, "DATEPART(SECOND, %s)"),

    // Unary math functions
    ROUND(Relation.ROUND, "ROUND(%s, 0)"),

    FLOOR(Relation.FLOOR, "FLOOR"),

    CEILING(Relation.CEILING, "CEILING"),

    // Binary functions.
    SUBSTROF(Relation.SUBSTROF, "%s LIKE '%%' + %s + '%%'"),

    ENDSWITH(Relation.ENDSWITH, "%s LIKE '%%' + %s"),

    STARTSWITH(Relation.STARTSWITH, "%s LIKE %s + '%%'"),

    INDEXOF(Relation.INDEXOF, "CHARINDEX", Arrays.asList(1, 0)),

    CONCAT(Relation.CONCAT, "CONCAT"),

    // Ternary functions.
    SUBSTR(Relation.SUBSTR, "SUBSTRING"),

    REPLACE(Relation.REPLACE, "REPLACE");

    // Equivalent oDataParser Relation.
    Relation relation;

    // Equivalent SQL symbol, May also contain a standard format string
    // indicating how the arguments should be arranged.
    //
    // TODO Where formating differs between SQL dialects multiple fields may be
    // required.
    private String sqlSymbol;

    // Argument sequence. In most cases oData and SQL have the same argument
    // sequence. If the sequence differs this contains a list defining the
    // argument
    // order.
    //
    // Null if SQL argument sequence the same as oData.
    //
    // Arguments are numbered from 0.
    private List<Integer> argumentSequence;

    private SqlRelation(Relation relation, String sqlSymbol) {
        this(relation, sqlSymbol, null);
    }

    private SqlRelation(Relation relation, String sqlSymbol, List<Integer> argumentSequence) {
        this.relation = relation;
        this.sqlSymbol = sqlSymbol;
        this.argumentSequence = argumentSequence;
    }

    public String getoDataString() {
        return relation.getoDataString();
    }

    // Return equivalent SQL symbol. Null if not supported.
    public String getSqlSymbol() {
        return sqlSymbol;
    }

    public Class<?> getOData4jClass() {
        return relation.getOData4jClass();
    }

    public boolean isBoolean() {
        return relation.isBoolean();
    }

    public boolean isNumeric() {
        return relation.isNumeric();
    }

    // Get expected number of arguments
    public int getExpectedArgumentCount() {
        return relation.getExpectedArgumentCount();
    }

    // Get argument sequence
    public List<Integer> getArgumentSequence() {
        return argumentSequence;
    }

    // Returns true if a function, e.g. 'fn(a, b...)' rather then an operator,
    // e.g. 'a op b'.
    public boolean isFunctionCall() {
        return relation.isFunctionCall();
    }
}
