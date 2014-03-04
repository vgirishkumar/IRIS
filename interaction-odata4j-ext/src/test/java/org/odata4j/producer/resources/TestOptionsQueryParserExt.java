package org.odata4j.producer.resources;

/*
 * #%L
 * interaction-odata4j-ext
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
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


import junit.framework.Assert;

import org.junit.Test;

public class TestOptionsQueryParserExt {

	@Test
	public void testParseFilter1() {
		String filter = "departureTime+ge+time'PT11H'+and+departureTime+lt+time'PT12H'";
		Assert.assertEquals("AndExpression", OptionsQueryParserExt.parseFilter(filter).toString());
	}
	
	@Test
	public void testParseFilter2() {
		String filter = "departureTime ge time'PT11H' and departureTime lt time'PT12H'";
		Assert.assertEquals("AndExpression", OptionsQueryParserExt.parseFilter(filter).toString());
	}
	
	@Test
	public void testParseFilter3() {
		String filter = "Id eq 'F.EB.ERROR>LC-CAN'T.BE.GT.THAN.MAT.REVIEW.DATE'";
		Assert.assertEquals("EqExpression", OptionsQueryParserExt.parseFilter(filter).toString());
	}
	
	@Test
	public void testParseFilter4() {
		String filter = "Id eq 'F.EB.ERROR>LC-CAN%27T.BE.GT.THAN.MAT.REVIEW.DATE'";
		Assert.assertEquals("EqExpression", OptionsQueryParserExt.parseFilter(filter).toString());
	}
	
	@Test
	public void testParseFilter5() {
		String filter = "Id eq 'F.EB.ERROR>LC-CAN%2527T.BE.GT.THAN.MAT.REVIEW.DATE'";
		Assert.assertEquals("EqExpression", OptionsQueryParserExt.parseFilter(filter).toString());
	}
	
	@Test
	public void testParseFilter6() {
		String filter = "Id eq 'F.EB.ERROR>LC-CAN%2527T.BE.GT.THAN.MAT.REVIEW.DATE'";
		Assert.assertEquals("EqExpression", OptionsQueryParserExt.parseFilter(filter).toString());
	}
}
