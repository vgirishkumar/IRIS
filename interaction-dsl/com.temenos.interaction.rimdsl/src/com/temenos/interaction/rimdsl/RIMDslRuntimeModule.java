package com.temenos.interaction.rimdsl;

import org.eclipse.xtext.conversion.IValueConverterService;


public class RIMDslRuntimeModule extends com.temenos.interaction.rimdsl.AbstractRIMDslRuntimeModule {
	
	@Override
	public Class<? extends IValueConverterService> bindIValueConverterService() {
		return RIMDslTerminalConverters.class;
	}
	
}
