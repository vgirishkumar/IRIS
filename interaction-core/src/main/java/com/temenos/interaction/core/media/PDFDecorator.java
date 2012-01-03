package com.temenos.interaction.core.media;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import com.temenos.interaction.core.RESTResponse;

public class PDFDecorator implements Decorator<StreamingOutput> {

	public PDFDecorator() {}
	
	public StreamingOutput decorateRESTResponse(RESTResponse r) {
        /* read the requestBodyStream like a normal input stream */
        return new StreamingOutput() {

            public void write(OutputStream output) throws IOException, WebApplicationException {
            	/* get some bytes to write */
            	byte[] out = null;
                output.write(out);
            }
        };

	}
}
