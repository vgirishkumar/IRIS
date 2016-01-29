package com.temenos.interaction.core.Performance;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
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


import org.junit.Ignore;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author kwieconkowski
 */

public class TestPerformanceMeasurement {

    @Test
    @Ignore
    public void test_getExecutionTimeSummary() {
        Runnable method = new Runnable() {
            @Override
            // String concatenation test
            public void run() {
                String tmp;
                for (int i = 0; i < 1_000_000; i++) {
                    tmp = "variable1[" + i + "], variable2[" + i + "]";
                }
            }
        };
        String summaryString = PerformanceMeasurement.getExecutionTimeSummary(method, true);
        assertNotNull(summaryString);
        assertTrue(Pattern.matches("Time of execution: \\d+ min, \\d+ sec", summaryString));
    }

    @Test
    @Ignore
    public void test_getExecutionTimeInSeconds() {
        Runnable method = new Runnable() {
            @Override
            // String concatenation test
            public void run() {
                String tmp;
                for (int i = 0; i < 1_000_000; i++) {
                    tmp = "variable1[" + i + "], variable2[" + i + "]";
                }
            }
        };
        long seconds = PerformanceMeasurement.getExecutionTimeInSeconds(method, true);
        assertTrue(seconds >= 0);
    }

    @Test
    @Ignore
    public void test_comparePerformance() {
        Runnable method_1 = new Runnable() {
            @Override
            // String concatenation test
            public void run() {
                String tmp;
                for (int i = 0; i < 10_000_000; i++) {
                    tmp = "variable1[" + i + "], variable2[" + i + "]";
                }
            }
        };

        Runnable method_2 = new Runnable() {
            @Override
            // String concatenation using String.format test
            public void run() {
                String tmp;
                for (int i = 0; i < 10_000_000; i++) {
                    tmp = String.format("variable1[%d], variable2[%d]", i, i);
                }
            }
        };

        long comparison = PerformanceMeasurement.comparePerformance(method_1, method_2, true);
        assertTrue(comparison == -1 || comparison == 0 || comparison == 1);
    }
}
