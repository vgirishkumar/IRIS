package com.temenos.interaction.media.odata.xml.atom;

/*
 * #%L
 * interaction-media-odata-xml
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


import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceListener;
import org.w3c.dom.Node;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * NB - credit to http://ayazanwar.wordpress.com/2012/09/23/xmlunit-ignoring-certain-pieces-of-xml-during-xml-comparison-using-regular-expression/
 * This is a custom listener class used to ignore differences between expected
 * and generated xmls based on regular expression.
 */
public class RegexBasedDifferenceListener implements DifferenceListener {
	/**
	 * list of regex expressions used to ignore differences found between
	 * expected and generated xmls.
	 */
	private final List<Pattern> ignorableRegexPatters;

	public RegexBasedDifferenceListener(final List<String> ignorableXPathsRegex) {
		this.ignorableRegexPatters = compileXpathExpressions(ignorableXPathsRegex);
	}

	/**
	 * compile all regular expressions once.
	 * 
	 * @param ignorableXPathsRegex
	 *            list of regular expressions for ignorable xpath locations.
	 * @return list of compiled regular expressions.
	 */
	private List<Pattern> compileXpathExpressions(
			final List<String> ignorableXPathsRegex) {
		final List<Pattern> compiledRegexList = new ArrayList<Pattern>();
		Iterator<String> it = ignorableXPathsRegex.iterator();
		while (it.hasNext()) {
			final Pattern pattern = Pattern.compile(it.next().toString());
			compiledRegexList.add(pattern);
		}
		return compiledRegexList;
	}

	/**
	 * On each difference this method is called by XMLUnit framework to
	 * determine whether we accept the difference or ignore it. If any of the
	 * provided regular expression match with xml xpath location at which
	 * difference found then ignore the difference.
	 * 
	 * @param difference
	 *            contains information about differences.
	 */
	public int differenceFound(Difference difference) {
		Iterator<Pattern> it = this.ignorableRegexPatters.iterator();
		final String xpathLocation = difference.getTestNodeDetail()
				.getXpathLocation();
		while (it.hasNext()) {
			final Pattern pattern = it.next();
			final Matcher m = pattern.matcher(xpathLocation);
			if (m.find()) {
				return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
				/** ignore it, not a valid difference */
			}
		}
		return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
		/** no objection, mark it as a valid difference */
	}

	/**
	 * This method is here just b/c it exist in DifferenceListener interface.
	 * So, needs dummy implementation. We actually do not need to implement it
	 * for current scenario.
	 */
	public void skippedComparison(Node node, Node node1) {
	}
}