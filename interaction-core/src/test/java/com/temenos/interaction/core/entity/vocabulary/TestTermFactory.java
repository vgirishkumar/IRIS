package com.temenos.interaction.core.entity.vocabulary;

/*
 * #%L
 * interaction-core
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


import org.junit.Assert;
import org.junit.Test;

import com.temenos.interaction.core.entity.vocabulary.terms.TermMandatory;
import com.temenos.interaction.core.entity.vocabulary.terms.TermRange;

public class TestTermFactory {

	@Test
	public void testTermNotExist() {
		try {
			new TermFactory().createTerm("AAA", null);
			Assert.fail();
		}
		catch(Exception e) {
		}
	}

	@Test
	public void testTermNullValue() {
		try {
			new TermFactory().createTerm(TermRange.TERM_NAME, null);
			Assert.fail();
		}
		catch(Exception e) {
		}
	}
	
	@Test
	public void testTermRange() {
		try {
			Term term = new TermFactory().createTerm(TermRange.TERM_NAME, "10, 30");
			Assert.assertTrue(term instanceof TermRange);
			Assert.assertEquals(10, ((TermRange) term).getMin());
			Assert.assertEquals(30, ((TermRange) term).getMax());
		}
		catch(Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testTermRangeWrongValue() {
		try {
			new TermFactory().createTerm(TermRange.TERM_NAME, "10; 30");
			Assert.fail();
		}
		catch(Exception e) {
		}
	}
	
	@Test
	public void testTermMandatory() {
		try {
			Term term = new TermFactory().createTerm(TermMandatory.TERM_NAME, "true");
			Assert.assertTrue(term instanceof TermMandatory);
			Assert.assertTrue(((TermMandatory) term).isMandatory());
		}
		catch(Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testTermMandatoryWrongValue() {
		try {
			Term term = new TermFactory().createTerm(TermMandatory.TERM_NAME, "werwer");
			Assert.assertTrue(term instanceof TermMandatory);
			Assert.assertFalse(((TermMandatory) term).isMandatory());
		}
		catch(Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}
