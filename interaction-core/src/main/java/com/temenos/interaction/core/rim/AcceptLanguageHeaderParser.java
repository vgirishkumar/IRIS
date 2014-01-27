package com.temenos.interaction.core.rim;

/*
 * #%L
 * interaction-core
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Parser for the http header AcceptLanguage as defined at <a
 * href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.4">14.4
 * Accept-Language</a>
 * 
 */
public class AcceptLanguageHeaderParser {

	private final static String REGEX_ACCEPT_LANGUAGE = "([a-zA-Z]{1,8}(-[a-zA-Z]{1,8})?)\\s*(;\\s*q\\s*=\\s*((1|0)\\.[0-9]+))?";

	private String acceptLanguageValue;
	private List<String> languageCodes;

	public AcceptLanguageHeaderParser(String acceptLanguageValue) {
		this.acceptLanguageValue = acceptLanguageValue;
		if (this.acceptLanguageValue == null || this.acceptLanguageValue.isEmpty()) {
			languageCodes = new ArrayList<String>();
		}
	}

	/**
	 * Returns the language codes extracted from the AcceptLanguage http header
	 * value in the descending order of their preference defined through the
	 * qvalue.
	 * 
	 * @return language codes list
	 */
	public List<String> getLanguageCodes() {
		if (languageCodes == null) {
			buildLanguageCodes();
		}
		return languageCodes;
	}

	// builds the language codes from the AcceptLanguage value by applying the
	// qvalue.
	private void buildLanguageCodes() {
		List<Language> languages = new ArrayList<Language>();
		for (String languageStr : acceptLanguageValue.split(",")) {
			if (isValidLanguage(languageStr.trim())) {
				String[] languageParts = languageStr.trim().split(";");
				Language language = null;
				if (languageParts.length == 2) {
					language = new Language(languageParts[0].trim(), languageParts[1].trim());
				} else if (languageParts.length == 1) {
					language = (new Language(languageParts[0].trim(), "q=1.0"));
				}
				
				if (language.quality > 0f) { // languages with q=0 not preferred
					languages.add(language);
				}
			} else {
				// invalid languages are ignored
			}
		}

		// sort based on quality value in descending order
		Collections.sort(languages, Collections.reverseOrder(new QualityValueComparator()));

		languageCodes = new ArrayList<String>();
		for (Language language : languages) {
			languageCodes.add(language.range);
		}
	}

	private static boolean isValidLanguage(String language) {
		return language.matches(REGEX_ACCEPT_LANGUAGE);
	}

	// Represents a Language in the AcceptLanguage value
	private static class Language {
		private String range;
		private float quality;

		private Language(String range, String qValue) {
			this.range = range;
			this.quality = determineQuality(qValue);
		}

		private float determineQuality(String qValue) {
			String[] qValueParts = qValue.split("=");
			return Float.parseFloat(qValueParts[1].trim());
		}
	}

	private class QualityValueComparator implements Comparator<Language> {
		@Override
		public int compare(Language lang1, Language lang2) {
			if (lang1.quality > lang2.quality) {
				return 1;
			} else if (lang1.quality < lang2.quality) {
				return -1;
			}
			return 0;
		}
	}
}
