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
 * Parser for the http header AcceptLanguage.
 * 
 */
public class AcceptLanguageHeaderParser {

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
	 * value in the order of their preference defined through the qvalue.
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
		for (String languageString : acceptLanguageValue.split(",")) {
			if (languageString.isEmpty()) {
				continue;
			}
			String[] langParts = languageString.trim().split(";");
			if (langParts.length == 2) {
				languages.add(new Language(langParts[0], langParts[1]));
			} else if (langParts.length == 1) {
				languages.add(new Language(langParts[0], "q=1.0"));
			}
		}

		Collections.sort(languages, Collections.reverseOrder(new QualityValueComparator()));

		languageCodes = new ArrayList<String>();
		for (Language language : languages) {
			languageCodes.add(language.range);
		}
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
			return Float.parseFloat(qValueParts[1]);
		}
	}

	private  class QualityValueComparator implements Comparator<Language> {
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
