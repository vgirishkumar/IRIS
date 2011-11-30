package com.temenos.ebank.common.wicket.wizard;

import java.io.Serializable;

import org.apache.wicket.PageReference;

/**
 * Create a token that uniquely identifies an instance of a form. The token uses the page reference and the form path to
 * obtain a unique identifier.
 * 
 * @author gcristescu
 */
public class EbankFormToken implements Serializable {
	private final PageReference reference;
	private final String pathToForm;

	public EbankFormToken(PageReference reference, String pathToForm) {
		this.reference = reference;
		this.pathToForm = pathToForm;
	}

	@Override
	public int hashCode() {
		return 31 * reference.hashCode() * pathToForm.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj instanceof EbankFormToken)

		{
			EbankFormToken other = (EbankFormToken) obj;
			return other.reference.equals(reference) && other.pathToForm.equals(pathToForm);
		}
		return false;
	}
}