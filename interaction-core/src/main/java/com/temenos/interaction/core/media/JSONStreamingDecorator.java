package com.temenos.interaction.core.media;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.odata4j.core.OProperty;

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.RESTResponse;

public class JSONStreamingDecorator implements Decorator<StreamingOutput> {

	public StreamingOutput decorateRESTResponse(final RESTResponse r) {
        return new StreamingOutput() {
            public void write(OutputStream output) throws IOException, WebApplicationException {
        		JsonFactory jsonFactory = new JsonFactory();
        		JsonGenerator g = jsonFactory.createJsonGenerator(output, JsonEncoding.UTF8);
        		g.writeStartObject();
        		if (r.getResource() != null && r.getResource() instanceof EntityResource) {
            		EntityResource resource = (EntityResource) r.getResource();
            		assert(resource.getOEntity() != null);
            		for (OProperty<?> property : resource.getOEntity().getProperties()) {
                   		g.writeFieldName(property.getName());
                   		g.writeString(property.getValue().toString());
            		}
        		}
        		g.writeEndObject();
        		g.flush();
        	}
        };
		
	}

}
