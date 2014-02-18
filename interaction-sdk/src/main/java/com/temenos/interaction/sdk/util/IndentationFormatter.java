package com.temenos.interaction.sdk.util;

/*
 * #%L
 * interaction-sdk
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

/**
 * This class helps in managing the indentation when building pretty printing
 * XML content.
 * 
 */
public class IndentationFormatter {
	private static final IndentationFormatter INSTANCE = new IndentationFormatter();
	private static final String INDENT_SIZE = "    ";

	private int currentIndent;

	/**
	 * Returns the singleton instance of the {@link IndentationFormatter}.
	 * 
	 * @return instance
	 */
	public static IndentationFormatter getInstance() {
		return INSTANCE;
	}

	private IndentationFormatter() {
		currentIndent = 0;
	}

	/**
	 * Increases indent.
	 */
	public void indent() {
		currentIndent++;
	}

	/**
	 * Decreases indent.
	 */
	public void outdent() {
		if (currentIndent > 0) {
			currentIndent--;
		}
	}

	/**
	 * Returns a string of spaces for the current indent.
	 * @return current indent
	 */
	public String currentIndent() {
		StringBuilder indentSpaces = new StringBuilder();
		for (int size = 0; size < currentIndent; size++) {
			indentSpaces.append(INDENT_SIZE);
		}
		return indentSpaces.toString();
	}
}
