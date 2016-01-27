package com.temenos.interaction.core.Performance;

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
