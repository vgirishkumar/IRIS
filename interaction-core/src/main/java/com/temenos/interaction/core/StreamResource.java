package com.temenos.interaction.core;

import java.io.OutputStream;

public interface StreamResource {

	public String getMIMEType();
	public OutputStream getStream();
}
