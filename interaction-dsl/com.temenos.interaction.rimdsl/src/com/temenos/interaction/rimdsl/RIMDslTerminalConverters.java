package com.temenos.interaction.rimdsl;

import org.eclipse.xtext.common.services.DefaultTerminalConverters;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverter;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class RIMDslTerminalConverters extends DefaultTerminalConverters {

	@Inject
	private UriStringConverter uriStringConverter;
	
	@ValueConverter(rule = "URISTRING")
	public IValueConverter<String> URISTRING() {
		return uriStringConverter;
	}

	@ValueConverter(rule = "URIPARAM")
	public IValueConverter<String> URIPARAM() {
		return uriStringConverter;
	}

}
