package com.temenos.messagingLayer.test;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

import org.junit.Test;

import com.temenos.messagingLayer.save.CalculateAddressDuration;

/**
 * JUnit test for {@link CalculateAddressDuration}
 * 
 * @author vionescu
 * 
 */

public class TestCalculateAddressDuration {
	
	CalculateAddressDuration calAddrDuration = new CalculateAddressDuration();

	@Test
	public void testGetAddressDuration() {
		Calendar c = Calendar.getInstance();
		// 20 January 2011
		c.set(2011, 0, 20);
		String startDate = CalculateAddressDuration.T24_DATE_FORMAT.format(c.getTime());
		try {
			assertEquals("20 Feb 2011", calAddrDuration.getAddressDuration(startDate, 1));
			// 20 Jun 2011
			c.set(2011, 5, 20);
			startDate = CalculateAddressDuration.T24_DATE_FORMAT.format(c.getTime());
			assertEquals("20 Jul 2011", calAddrDuration.getAddressDuration(startDate, 1));
		} catch (ParseException e) {
			throw new RuntimeException("Error in calculating duration ", e);
		}

	}

	@Test
	public void testGetNextStartDate() {
		Calendar c = Calendar.getInstance();
		// 20 January 2011
		c.set(2011, 0, 20);
		String currentStartDate = CalculateAddressDuration.T24_DATE_FORMAT.format(c.getTime());
		try {
			assertEquals("19 Jan 2011", calAddrDuration.getNextStartDate(currentStartDate));
		} catch (ParseException e) {
			throw new RuntimeException("Error in calculating duration ", e);
		}
	}
	
	@Test
	public void testDifferentLocale() {
		// Test with different environment: what if the server's locale is not English?
		Locale defaultLocale = Locale.getDefault();
		Locale.setDefault(Locale.CHINA);
		
		Calendar c = Calendar.getInstance();
		// 20 January 2011
		c.set(2011, 0, 20);
		String startDate = CalculateAddressDuration.T24_DATE_FORMAT.format(c.getTime());
		try {
			assertEquals("20 Feb 2011", calAddrDuration.getAddressDuration(startDate, 1));
			
			// back to default locale
			Locale.setDefault(defaultLocale);
			// 20 Jun 2011
			c.set(2011, 5, 20);
			startDate = CalculateAddressDuration.T24_DATE_FORMAT.format(c.getTime());
			assertEquals("20 Jul 2011", calAddrDuration.getAddressDuration(startDate, 1));
		} catch (ParseException e) {
			throw new RuntimeException("Error in calculating duration ", e);
		} finally {
			// make sure we set back the default locale
			Locale.setDefault(defaultLocale);
		}
		
	}
}
