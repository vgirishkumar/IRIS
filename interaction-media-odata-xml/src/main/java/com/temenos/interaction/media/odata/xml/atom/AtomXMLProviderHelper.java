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


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.entity.EntityProperty;

/**
 * Utility class for Atom XML provider.
 * 
 * @author ssethupathi
 *
 */
public class AtomXMLProviderHelper {

	private final static Logger logger = LoggerFactory
			.getLogger(AtomXMLProviderHelper.class);

	/**
	 * Checks the property value type and does format the date time value to UTC
	 * accordingly.
	 * 
	 * @param property
	 *            entity property
	 * @return date time in UTC
	 */
	public static String checkAndConvertDateTimeToUTC(EntityProperty property) {
		Object propertyValue = property.getValue();
		SimpleDateFormat formatUTC = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");
		formatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
		if (propertyValue instanceof Date) {
			return formatUTC.format((Date) propertyValue);
		} else if (propertyValue instanceof LocalDateTime) {
			return formatUTC.format(((LocalDateTime) propertyValue)
					.toDateTime().toDate());
		} else {
			String errorMessage = "Unsupported value type '"
					+ propertyValue.getClass().getName() + "' for property '"
					+ property.getFullyQualifiedName() + "'";
			logger.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}
	}
}
