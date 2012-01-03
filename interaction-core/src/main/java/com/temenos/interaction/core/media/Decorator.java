package com.temenos.interaction.core.media;

import com.temenos.interaction.core.RESTResponse;

public interface Decorator<RESPONSE> {

	public RESPONSE decorateRESTResponse(RESTResponse c);
	
}
