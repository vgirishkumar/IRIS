package com.temenos.interaction.rimdsl;

import org.eclipse.xtext.conversion.impl.AbstractLexerBasedConverter;
import org.eclipse.xtext.nodemodel.INode;

/**
 * Handler the escaping of any URISTRING types within RIMDsl
 *
 * @author aphethean
 */
public class UriStringConverter extends AbstractLexerBasedConverter<String> {

	@Override
	protected String toEscapedString(String value) {
		return value;
	}

	public String toValue(String string, INode node) {
		if (string == null)
			return null;
		return string.substring(1, string.length() - 1);
	}
	
}
