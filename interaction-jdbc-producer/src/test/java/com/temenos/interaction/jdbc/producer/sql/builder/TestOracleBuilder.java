package com.temenos.interaction.jdbc.producer.sql.builder;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Ignore;
import org.junit.Test;
import org.odata4j.expression.OrderByExpression;

import com.temenos.interaction.jdbc.ServerMode;
import com.temenos.interaction.jdbc.SqlRelation;
import com.temenos.interaction.jdbc.producer.sql.ColumnTypesMap;
import com.temenos.interaction.jdbc.producer.sql.SqlBuilder;
import com.temenos.interaction.jdbc.producer.sql.SqlBuilderFactory;
import com.temenos.interaction.odataext.odataparser.data.AccessProfile;
import com.temenos.interaction.odataext.odataparser.data.FieldName;
import com.temenos.interaction.odataext.odataparser.data.OrderBy;
import com.temenos.interaction.odataext.odataparser.data.Relation;
import com.temenos.interaction.odataext.odataparser.data.RowFilters;

/**
 * Test SqlCommandBuilder class.
 */
public class TestOracleBuilder {

    private static final String TEST_TABLE_NAME = "testTable";

    /**
     * Test a command with empty lists
     */
    @Test
    public void testGetCommandEmpty() {

        // Build up an access profile
        RowFilters filters = new RowFilters();
        Set<FieldName> selects = new HashSet<FieldName>();
        AccessProfile accessProfile = new AccessProfile(filters, selects);

        // Build up some column metadata.
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("col1", java.sql.Types.VARCHAR);
        ColumnTypesMap columnTypesMap = new ColumnTypesMap(map, "col1");

        // Create the builder
        SqlBuilder builder = null;
        try {
            builder = SqlBuilderFactory.getSqlBuilder(TEST_TABLE_NAME, null, accessProfile, columnTypesMap, null, null,
                    null, null);
        } catch (Exception e) {
            fail();
        }

        // Get the command.
        String actualCommand = builder.getCommand();

        assertEquals("SELECT * FROM \"" + TEST_TABLE_NAME + "\" ORDER BY \"col1\"", actualCommand);
    }

    /**
     * Test a command with empty lists and no primary key
     */
    @Test
    public void testGetCommandEmptyNoKey() {

        // Build up an access profile
        RowFilters filters = new RowFilters();
        Set<FieldName> selects = new HashSet<FieldName>();
        AccessProfile accessProfile = new AccessProfile(filters, selects);

        // Build up some column metadata.
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("col1", java.sql.Types.VARCHAR);
        ColumnTypesMap columnTypesMap = new ColumnTypesMap(map, null);

        // Create the builder
        SqlBuilder builder = null;
        try {
            builder = SqlBuilderFactory.getSqlBuilder(TEST_TABLE_NAME, null, accessProfile, columnTypesMap, null, null,
                    null, null);
        } catch (Exception e) {
            fail();
        }

        // Get the command.
        String actualCommand = builder.getCommand();

        assertEquals("SELECT * FROM \"" + TEST_TABLE_NAME + "\"", actualCommand);
    }

    static class ComparableFieldName extends FieldName implements Comparable<FieldName> {

        public ComparableFieldName(String name) {
            super(name);
        }

        @Override
        public int compareTo(FieldName t) {
            return getName().compareTo(t.getName());
        }

    }

    /**
     * Test a command with a key.
     */
    @Test
    public void testGetCommandKey() {

        // Build up an access profile
        RowFilters filters = new RowFilters();
        Set<FieldName> selects = new HashSet<FieldName>();
        AccessProfile accessProfile = new AccessProfile(filters, selects);

        // Build up some column metadata with a primary key
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("col1", java.sql.Types.VARCHAR);
        map.put("col2", java.sql.Types.INTEGER);
        ColumnTypesMap columnTypesMap = new ColumnTypesMap(map, "col1");

        String keyValue = "aKeyValue";

        // Create the builder
        SqlBuilder builder = null;
        try {
            builder = SqlBuilderFactory.getSqlBuilder(TEST_TABLE_NAME, keyValue, accessProfile, columnTypesMap, null,
                    null, null, null);
        } catch (Exception e) {
            fail();
        }

        // Get the command.
        String actualCommand = builder.getCommand();

        assertEquals("SELECT * FROM \"" + TEST_TABLE_NAME + "\" WHERE \"col1\"" + "='" + keyValue + "'"
                + " ORDER BY \"col1\"", actualCommand);
    }

    /**
     * Test a command with $top, $skip and $orderby 
     */
    @Test
    public void testGetCommandComplex() {

        // Build up an access profile
        RowFilters filters = new RowFilters();
        Set<FieldName> selects = new HashSet<FieldName>();
        AccessProfile accessProfile = new AccessProfile(filters, selects);

        // Build up some column metadata with a primary key
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("col1", java.sql.Types.VARCHAR);
        map.put("col2", java.sql.Types.INTEGER);
        ColumnTypesMap columnTypesMap = new ColumnTypesMap(map, "col1");

        String keyValue = "aKeyValue";

        // Create order by list
        List<OrderBy> orderBy = new ArrayList<OrderBy>();
        OrderBy expected1 = new OrderBy("col1", OrderByExpression.Direction.ASCENDING);
        orderBy.add(expected1);
        OrderBy expected2 = new OrderBy("col2", OrderByExpression.Direction.DESCENDING);
        orderBy.add(expected2);

        // Create the builder
        OracleBuilder builder = null;
        try {
            builder = new OracleBuilder(TEST_TABLE_NAME, keyValue, accessProfile, columnTypesMap, "2", "3", orderBy);
        } catch (Exception e) {
            fail();
        }

        // Get the command.
        String actualCommand = builder.getCommand();

        assertEquals("SELECT * FROM ( SELECT inner_tab.*, ROWNUM \"rn\" FROM (" + " SELECT * FROM \"" + TEST_TABLE_NAME
                + "\" WHERE \"col1\"='aKeyValue' ORDER BY \"col1\", \"col2\" DESC ) inner_tab )"
                + " WHERE \"rn\" > 3 AND \"rn\" <= 5", actualCommand);
    }
    
    /**
     * Test a command with bracketed $filter
     */
    @Test
    public void testGetCommandBracketed() {

        // Build up an access profile
        RowFilters filters = new RowFilters("(a " + Relation.EQ.getoDataString() + " b) "
                + Relation.AND.getoDataString() + " (c " + Relation.NE.getoDataString() + " d)");
        Set<FieldName> selects = new HashSet<FieldName>();
        AccessProfile accessProfile = new AccessProfile(filters, selects);

        // Build up some column metadata with a primary key
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("col1", java.sql.Types.VARCHAR);
        map.put("col2", java.sql.Types.INTEGER);
        ColumnTypesMap columnTypesMap = new ColumnTypesMap(map, "col1");

        // Create the builder
        SqlBuilder builder = null;
        try {
            builder = SqlBuilderFactory.getSqlBuilder(TEST_TABLE_NAME, null, accessProfile, columnTypesMap, null, null,
                    null, ServerMode.ORACLE);
        } catch (Exception e) {
            fail();
        }

        // Get the command.
        String actualCommand = builder.getCommand();

        assertEquals("SELECT * FROM \"" + TEST_TABLE_NAME
                + "\" WHERE (\"a\"=\"b\") AND (\"c\"<>\"d\") ORDER BY \"col1\"", actualCommand);
    }

    /**
     * Test failure if a key value is given but no primary key column is
     * present.
     */
    @Test(expected = SecurityException.class)
    public void testGetCommandNullKey() throws Exception {

        // Build up an access profile
        RowFilters filters = new RowFilters();
        Set<FieldName> selects = new HashSet<FieldName>();
        AccessProfile accessProfile = new AccessProfile(filters, selects);

        // Build up some column metadata with a primary key
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("col1", java.sql.Types.VARCHAR);
        map.put("col2", java.sql.Types.INTEGER);
        ColumnTypesMap columnTypesMap = new ColumnTypesMap(map, null);

        String keyValue = "aKeyValue";

        // Create the builder
        SqlBuilder builder = null;
        try {
            builder = SqlBuilderFactory.getSqlBuilder(TEST_TABLE_NAME, keyValue, accessProfile, columnTypesMap, null,
                    null, null, null);
        } catch (Exception e) {
            fail();
        }

        // Get the command. Should throw.
        builder.getCommand();
    }

    /**
     * Test failure with null filter.
     */
    @Test(expected = Exception.class)
    public void testGetCommandNullFilter() throws Exception {

        // Build up an access profile
        Set<FieldName> selects = new HashSet<FieldName>();
        RowFilters nullFilters = null;
        AccessProfile accessProfile = new AccessProfile(nullFilters, selects);

        // Create the builder
        SqlBuilder builder = null;
        try {
            builder = SqlBuilderFactory.getSqlBuilder(TEST_TABLE_NAME, null, accessProfile, mock(ColumnTypesMap.class),
                    null, null, null, null);
        } catch (Exception e) {
            fail();
        }

        // Get the command. Should throw.
        builder.getCommand();
    }

    /**
     * Test failure with null select.
     */
    @Test(expected = Exception.class)
    public void testGetCommandNullSelect() throws Exception {
        // Build up an access profile
        RowFilters filters = new RowFilters();

        AccessProfile accessProfile = new AccessProfile(filters, null);

        // Create the builder
        SqlBuilder builder = null;
        try {
            builder = SqlBuilderFactory.getSqlBuilder(TEST_TABLE_NAME, null, accessProfile, mock(ColumnTypesMap.class),
                    null, null, null, null);
        } catch (Exception e) {
            fail();
        }

        // Get the command. Should throw.
        builder.getCommand();
    }

    /**
     * Test failure with bad filter column name.
     */
    @Test(expected = SecurityException.class)
    /*
     * Since we no longer check the column name list at command build time this
     * will no longer work. However the command will fail when it is executed.
     */
    @Ignore
    public void testBadColumnName() {

        // Build up an access profile.
        RowFilters filters = new RowFilters("badName " + SqlRelation.EQ.getoDataString() + " 'value1'");

        // Build up some column metadata which does not match.
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("goodName", java.sql.Types.VARCHAR);
        ColumnTypesMap columnTypesMap = new ColumnTypesMap(map, null);

        Set<FieldName> selects = new HashSet<FieldName>();
        selects.add(new FieldName("goodName"));
        AccessProfile accessProfile = new AccessProfile(filters, selects);

        // Create the builder
        SqlBuilder builder = null;
        try {
            builder = SqlBuilderFactory.getSqlBuilder(TEST_TABLE_NAME, null, accessProfile, columnTypesMap, null, null,
                    null, null);
        } catch (Exception e) {
            fail();
        }

        // Get the command. Should throw
        builder.getCommand();
    }

    /**
     * Test a simple command where the reserved row number column is present.
     */
    @Test
    public void testReservedRnCommand() {
        // Build column metadata containing the reserved column..
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put(SqlBuilder.getRnName(), java.sql.Types.INTEGER);
        ColumnTypesMap columnTypesMap = new ColumnTypesMap(map, "col1");

        // Create the builder
        try {
            SqlBuilderFactory.getSqlBuilder(TEST_TABLE_NAME, null, null, columnTypesMap, null, null, null, null);
        } catch (Exception e) {
            fail();
        }

        // For now this should work. Maybe one day it will be changed to throw.
    }
    
    /**
     * Test operators
     */
    @Test
    public void testGetCommandOperands() {
        for (SqlRelation rel : SqlRelation.values()) {
            if (!rel.isFunctionCall()) {
                testOperator(rel);
            }
        }
    }

    private void testOperator(SqlRelation rel) {
        RowFilters filters = null;
        String expectedCommand = null;
        if (rel.isBoolean()) {
            if (2 == rel.getExpectedArgumentCount()) {
                filters = new RowFilters("true " + rel.getoDataString() + " true");
                expectedCommand = "SELECT \"col1\" FROM \"" + TEST_TABLE_NAME + "\"" + " WHERE true "
                        + rel.getSqlSymbol() + " true ORDER BY \"col1\"";
            } else {
                filters = new RowFilters(rel.getoDataString() + " true");
                expectedCommand = "SELECT \"col1\" FROM \"" + TEST_TABLE_NAME + "\"" + " WHERE " + rel.getSqlSymbol()
                        + " true ORDER BY \"col1\"";

            }
        } else {
            if (rel.isNumeric()) {
                filters = new RowFilters("col1 eq 1 " + rel.getoDataString() + " 2");
                expectedCommand = "SELECT \"col1\" FROM \"" + TEST_TABLE_NAME + "\"" + " WHERE \"col1\""
                        + SqlRelation.EQ.getSqlSymbol() + "1" + rel.getSqlSymbol() + "2 ORDER BY \"col1\"";
            } else {
                filters = new RowFilters("col1 " + rel.getoDataString() + " 'value1'");
                expectedCommand = "SELECT \"col1\" FROM \"" + TEST_TABLE_NAME + "\"" + " WHERE \"col1\""
                        + rel.getSqlSymbol() + "'value1' ORDER BY \"col1\"";
            }
        }

        // Build up some column metadata matching the above columns. The correct
        // fields must be numeric of textual.
        Map<String, Integer> map = new TreeMap<String, Integer>();
        map.put("col1", java.sql.Types.VARCHAR);
        ColumnTypesMap columnTypesMap = new ColumnTypesMap(map, "col1") {
            public Map<String, Integer> readColumnTypes(DatabaseMetaData dsMetaData, String tableName) throws SQLException {
                return new TreeMap<String, Integer>(super.readColumnTypes(dsMetaData, tableName));
            }
        };

        // adding as TreeSet to ensure iteration order matches on all JVMs
        // however FieldName does not implement comparable - thus helper class
        // created inhering from Field name and implementing Comparable usign
        // names
        Set<FieldName> selects = new TreeSet<FieldName>();
        selects.add(new ComparableFieldName("col1"));
        AccessProfile accessProfile = new AccessProfile(filters, selects);

        // Create the builder
        SqlBuilder builder = null;
        try {
            builder = SqlBuilderFactory.getSqlBuilder(TEST_TABLE_NAME, null, accessProfile, columnTypesMap, null, null,
                    null, ServerMode.ORACLE);
        } catch (Exception e) {
            fail();
        }

        // Get the command.
        String actualCommand = null;
        boolean threw = false;
        try {
            actualCommand = builder.getCommand();
        } catch (UnsupportedOperationException e) {
            threw = true;
        }

        if (null == rel.getSqlSymbol()) {
            // Unsupported operand ... should throw.
            assertTrue(threw);
        } else {
            // Supported operand ... should return correct output.
            assertEquals(expectedCommand, actualCommand);
        }
    }

    /**
     * Test functions
     */
    @Test
    public void testGetCommandFunctions() {
        for (SqlRelation rel : SqlRelation.values()) {
            // ISOF requires quoted args ... skip it.
            if (rel.isFunctionCall() && (SqlRelation.ISOF != rel)) {
                testFunction(rel);
            }
        }
    }

    private void testFunction(SqlRelation rel) {
        StringBuffer filterStr = new StringBuffer();
        StringBuffer expectedCommand = new StringBuffer();

        filterStr.append("col1 " + SqlRelation.EQ.getoDataString() + " ");
        expectedCommand.append("SELECT \"col1\" FROM \"" + TEST_TABLE_NAME + "\"" + " WHERE \"col1\""
                + SqlRelation.EQ.getSqlSymbol());

        filterStr.append(rel.getoDataString() + "(");

        if ((null != rel.getSqlSymbol()) && (!rel.getSqlSymbol().contains("%s"))) {
            // A regular bracket function
            expectedCommand.append(rel.getSqlSymbol() + "(");
            addFunctionArgumentList(filterStr, expectedCommand, rel);
            expectedCommand.append(")");
        } else {
            // A formated string function.

            // Build up the arg list
            boolean first = true;
            List<String> arguments = new ArrayList<String>();
            for (int i = 0; i < rel.getExpectedArgumentCount(); i++) {
                if (first) {
                    first = false;
                } else {
                    filterStr.append(", ");
                }

                filterStr.append(i);
                arguments.add(Integer.toString(i));
            }

            if (null != rel.getSqlSymbol()) {
                String fnString = String.format(rel.getSqlSymbol(), arguments.toArray());
                expectedCommand.append(fnString);
            }
        }

        filterStr.append(")");

        expectedCommand.append(" ORDER BY \"col1\"");

        RowFilters filters = new RowFilters(filterStr.toString());

        // Build up some column metadata matching the above columns. The correct
        // fields must be numeric of textual.
        Map<String, Integer> map = new TreeMap<String, Integer>();
        map.put("col1", java.sql.Types.VARCHAR);
        ColumnTypesMap columnTypesMap = new ColumnTypesMap(map, "col1") {
            public Map<String, Integer> readColumnTypes(DatabaseMetaData dsMetaData, String tableName) throws SQLException {
                return new TreeMap<String, Integer>(super.readColumnTypes(dsMetaData, tableName));
            }
        };

        // adding as TreeSet to ensure iteration order matches on all JVMs
        // however FieldName does not implement comparable - thus helper class
        // created inhering from Field name and implementing Comparable usign
        // names
        Set<FieldName> selects = new TreeSet<FieldName>();
        selects.add(new ComparableFieldName("col1"));
        AccessProfile accessProfile = new AccessProfile(filters, selects);

        // Create the builder
        SqlBuilder builder = null;
        try {
            builder = SqlBuilderFactory.getSqlBuilder(TEST_TABLE_NAME, null, accessProfile, columnTypesMap, null, null,
                    null, ServerMode.ORACLE);
        } catch (Exception e) {
            fail();
        }

        // Get the command.
        String actualCommand = null;
        boolean threw = false;
        try {
            actualCommand = builder.getCommand();
        } catch (UnsupportedOperationException e) {
            threw = true;
        }

        if (null == rel.getSqlSymbol()) {
            // Unsupported operand ... should throw.
            assertTrue(threw);
        } else {
            // Supported operand ... should return correct output.
            assertEquals(expectedCommand.toString(), actualCommand);
        }
    }

    /*
     * Utility to add a functions arg list
     */
    private void addFunctionArgumentList(StringBuffer filterStr, StringBuffer expectedCommand, SqlRelation rel) {
        boolean first = true;
        if (null == rel.getArgumentSequence()) {
            // Add arguments in order.
            for (int i = 0; i < rel.getExpectedArgumentCount(); i++) {
                if (first) {
                    first = false;
                } else {
                    filterStr.append(",");
                    expectedCommand.append(", ");
                }

                filterStr.append(i);
                expectedCommand.append(i);
            }
        } else {
            // Add arguments in specified order.
            int count = 0;
            for (Integer i : rel.getArgumentSequence()) {
                if (first) {
                    first = false;
                } else {
                    filterStr.append(",");
                    expectedCommand.append(", ");
                }

                filterStr.append(count);
                expectedCommand.append(i);
                count ++;
            }
        }
    }

    /**
     * Test the differing SQL data types.
     */
    @Test
    public void testGetDataTypes() {

        // Build up an access profile. Use a mixture of relations and
        // numeric/text fields.
        RowFilters filters = new RowFilters("col1 " + SqlRelation.EQ.getoDataString() + " 'value1'");
        filters.addFilters("col2 " + SqlRelation.NE.getoDataString() + " 2");
        filters.addFilters("col3 " + SqlRelation.LT.getoDataString() + " 'value3'");
        filters.addFilters("col4 " + SqlRelation.GT.getoDataString() + " 4");
        filters.addFilters("col5 " + SqlRelation.LE.getoDataString() + " 'value5'");
        filters.addFilters("col6 " + SqlRelation.GE.getoDataString() + " 'value6'");

        // Build up some column metadata matching the above columns. The correct
        // fields must be numeric of textual.
        Map<String, Integer> map = new TreeMap<String, Integer>();
        map.put("col1", java.sql.Types.VARCHAR);
        map.put("col2", java.sql.Types.INTEGER);
        map.put("col3", java.sql.Types.NVARCHAR);
        map.put("col4", java.sql.Types.DOUBLE);
        map.put("col5", java.sql.Types.TIMESTAMP);
        map.put("col6", java.sql.Types.VARCHAR);
        ColumnTypesMap columnTypesMap = new ColumnTypesMap(map, "col1") {
            public Map<String, Integer> readColumnTypes(DatabaseMetaData dsMetaData, String tableName) throws SQLException {
                return new TreeMap<String, Integer>(super.readColumnTypes(dsMetaData, tableName));
            }
        };

        // adding as TreeSet to ensure iteration order matches on all JVMs
        // however FieldName does not implement comparable - thus helper class
        // created inhering from Field name and implementing Comparable usign
        // names
        Set<FieldName> selects = new TreeSet<FieldName>();
        selects.add(new ComparableFieldName("col1"));
        selects.add(new ComparableFieldName("col2"));
        AccessProfile accessProfile = new AccessProfile(filters, selects);

        // Create the builder
        SqlBuilder builder = null;
        try {
            builder = SqlBuilderFactory.getSqlBuilder(TEST_TABLE_NAME, null, accessProfile, columnTypesMap, null, null,
                    null, ServerMode.ORACLE);
        } catch (Exception e) {
            fail();
        }

        // Get the command.
        String actualCommand = builder.getCommand();
        assertEquals("SELECT \"col1\", \"col2\" FROM \"" + TEST_TABLE_NAME + "\""
                + " WHERE \"col1\"='value1' AND \"col2\"<>2 AND \"col3\"<'value3' AND \"col4\">4 AND "
                + "\"col5\"<='value5' AND \"col6\">='value6' ORDER BY \"col1\"", actualCommand);
    }
    
    /**
     * Test the time types.
     */
    @Test
    public void testTimeTypes() {

        // Build up an access profile. Use a mixture of relations and
        // numeric/text fields.
        RowFilters filters = new RowFilters("col1 " + SqlRelation.EQ.getoDataString() + " '13:20:00'");
        filters.addFilters("col2 " + SqlRelation.NE.getoDataString() + " datetime'2000-12-12T12:00'");

        // Build up some column metadata matching the above columns. The correct
        // fields must be numeric of textual.
        Map<String, Integer> map = new TreeMap<String, Integer>();
        map.put("col1", java.sql.Types.TIMESTAMP);
        map.put("col2", java.sql.Types.TIMESTAMP);
        ColumnTypesMap columnTypesMap = new ColumnTypesMap(map, "col1") {
            public Map<String, Integer> readColumnTypes(DatabaseMetaData dsMetaData, String tableName) throws SQLException {
                return new TreeMap<String, Integer>(super.readColumnTypes(dsMetaData, tableName));
            }
        };

        // adding as TreeSet to ensure iteration order matches on all JVMs
        // however FieldName does not implement comparable - thus helper class
        // created inhering from Field name and implementing Comparable usign
        // names
        Set<FieldName> selects = new TreeSet<FieldName>();
        selects.add(new ComparableFieldName("col1"));
        selects.add(new ComparableFieldName("col2"));
        AccessProfile accessProfile = new AccessProfile(filters, selects);

        // Create the builder
        SqlBuilder builder = null;
        try {
            builder = SqlBuilderFactory.getSqlBuilder(TEST_TABLE_NAME, null, accessProfile, columnTypesMap, null, null,
                    null, ServerMode.ORACLE);
        } catch (Exception e) {
            fail();
        }

        // Get the command.
        String actualCommand = builder.getCommand();
        assertEquals("SELECT \"col1\", \"col2\" FROM \"" + TEST_TABLE_NAME + "\""
                + " WHERE \"col1\"='13:20:00' AND \"col2\"<>'2000-12-12 12:00:00.0' ORDER BY \"col1\"", actualCommand);
    }
}
