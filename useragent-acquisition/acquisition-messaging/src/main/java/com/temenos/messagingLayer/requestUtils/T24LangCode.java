package com.temenos.messagingLayer.requestUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.temenos.messagingLayer.languagemappingpojo.Language;
import com.temenos.messagingLayer.languagemappingpojo.T24LanguageMapping;
import com.temenos.messagingLayer.util.MappingFactory;

/**
 * Gets the language code to be passed to T24
 * @author anitha
 *
 */
public class T24LangCode {
	
	private MappingFactory mappingFactory;
	/**
	 * In-memory 'cache' of the web-t24 language mapping
	 */
	private Map<String, String> languages = new HashMap<String, String>(3); // initialize this here so that we do not waste time checking for null

	/**
	 * Constructor.
	 */
	public T24LangCode(MappingFactory mappingFactory) {
		this.mappingFactory = mappingFactory;
	}

	public String getT24LangCode(String webLangCode) {
		if (languages.containsKey(webLangCode)) {
			return languages.get(webLangCode);
		}
		T24LanguageMapping t24LangMapping = (T24LanguageMapping) mappingFactory.getMapping("com.temenos.messagingLayer.languagemappingpojo", "T24LanguageMapping.xml");
		List<Language> t24Languages = t24LangMapping.getLanguage();
		Iterator<Language> iterLanguages = t24Languages.iterator();
		while (iterLanguages.hasNext()) {
			Language t24Language = iterLanguages.next();
			String webLang = t24Language.getWebLangCode();
			if (webLang.equals(webLangCode)) {
				return putAndReturn(webLangCode, t24Language.getT24LangCode().toString());
			}
		}
		// language not mapped, store a null for it to avoid future lookups
		return putAndReturn(webLangCode, null);
	}

	/**
	 * Update the cache with the new pair; return the t24 language code.
	 */
	private String putAndReturn(String webCode, String t24Code) {
		languages.put(webCode, t24Code);
		return t24Code;
	}
}
