package com.temenos.interaction.core.entity.vocabulary;

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
