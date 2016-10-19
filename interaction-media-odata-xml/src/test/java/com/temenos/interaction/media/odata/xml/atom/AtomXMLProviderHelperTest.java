package com.temenos.interaction.media.odata.xml.atom;

/*
 * #%L
 * interaction-media-odata-xml
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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import com.temenos.interaction.core.entity.EntityProperty;

public class AtomXMLProviderHelperTest {

	@Test
	public void testCheckAndConvertDateTimeToUTCFromNonUTCDate() throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("GMT-8:00"));
		EntityProperty property = new EntityProperty("dateOfBirth",
				format.parse("2015-12-31T16:10:00"));
		assertEquals("2016-01-01T00:10:00",
				AtomXMLProviderHelper.checkAndConvertDateTimeToUTC(property));
	}

	@Test
	public void testCheckAndConvertDateTimeToUTCFromUTCDate() throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		EntityProperty property = new EntityProperty("dateOfBirth",
				format.parse("2015-12-31T16:10:00"));
		assertEquals("2015-12-31T16:10:00",
				AtomXMLProviderHelper.checkAndConvertDateTimeToUTC(property));
	}

	@Test
	public void testCheckAndConvertDateTimeToUTCFromLocalJodaDate()
			throws Exception {
		DateTimeZone defaultZone = DateTimeZone.getDefault();
		LocalDateTime ldt = new LocalDateTime(2015, 12, 31, 16, 10, 0);
		DateTimeZone.setDefault(DateTimeZone.UTC);
		EntityProperty property = new EntityProperty("dateOfBirth", ldt);
		DateTime utcTime = ldt.toDateTime();
		String expected = "2015-12-31T" + utcTime.hourOfDay().get() + ":"
				+ utcTime.minuteOfHour().get() + ":"
				+ utcTime.secondOfMinute().get() + "0";
		assertEquals(expected,
				AtomXMLProviderHelper.checkAndConvertDateTimeToUTC(property));
		DateTimeZone.setDefault(defaultZone);
	}

	@Test
	public void testCheckAndConvertDateTimeToUTCFromInvalidType()
			throws Exception {
		try {
			AtomXMLProviderHelper
					.checkAndConvertDateTimeToUTC(new EntityProperty(
							"dateOfBirth", "some string object"));
			fail("RuntimeException should be thrown");
		} catch (Exception e) {
			assertTrue(e instanceof RuntimeException);
		}
	}
}
