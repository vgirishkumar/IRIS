package com.temenos.interaction.media.odata.xml.atom;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import com.temenos.interaction.core.entity.EntityProperty;

public class AtomXMLProviderHelperTest {

	@Test
	public void checkAndConvertDateTimeToUTCFromNonUTCDate() throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("GMT-8:00"));
		EntityProperty property = new EntityProperty("dateOfBirth",
				format.parse("2015-12-31T16:10:00"));
		assertEquals("2016-01-01T00:10:00",
				AtomXMLProviderHelper.checkAndConvertDateTimeToUTC(property));
	}

	@Test
	public void checkAndConvertDateTimeToUTCFromUTCDate() throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		EntityProperty property = new EntityProperty("dateOfBirth",
				format.parse("2015-12-31T16:10:00"));
		assertEquals("2015-12-31T16:10:00",
				AtomXMLProviderHelper.checkAndConvertDateTimeToUTC(property));
	}

	@Test
	public void checkAndConvertDateTimeToUTCFromLocalJodaDate()
			throws Exception {
		LocalDateTime ldt = new LocalDateTime(2015, 12, 31, 16, 10, 0);
		EntityProperty property = new EntityProperty("dateOfBirth", ldt);
		assertEquals("2015-12-31T16:10:00",
				AtomXMLProviderHelper.checkAndConvertDateTimeToUTC(property));
	}

	@Test
	public void checkAndConvertDateTimeToUTCFromInvalidType()
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
