package com.temenos.useragent.generic.mediatype;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.temenos.useragent.generic.Link;
import com.temenos.useragent.generic.internal.EntityHandler;

public class PlainTextEntityHandler implements EntityHandler {

	private String plainText;

	public PlainTextEntityHandler(String plainText) {
		this.plainText = plainText;
	}

	@Override
	public String getId() {
		return "";
	}

	@Override
	public List<Link> getLinks() {
		return Collections.emptyList();
	}

	@Override
	public String getValue(String fqPropertyName) {
		return "";
	}

	@Override
	public void setValue(String fqPropertyName, String value) {
		// do nothing
	}

	@Override
	public int getCount(String fqPropertyName) {
		return 0;
	}

	@Override
	public void setContent(InputStream stream) {
		// do nothing
	}

	@Override
	public InputStream getContent() {
		return IOUtils.toInputStream(plainText);
	}
}
