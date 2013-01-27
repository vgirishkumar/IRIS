package com.temenos.interaction.commands.webhook;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.odata4j.core.OEntity;
import org.odata4j.core.OProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.resource.EntityResource;

/**
 * The Webhook command receives an Entity from the InteractionContext and POSTs
 * that entity to the configured URL with application/x-www-form-urlencoded Content-Type.
 * @author aphethean
 *
 */
public class WebhookCommand implements InteractionCommand {
	private final static Logger logger = LoggerFactory.getLogger(WebhookCommand.class);

	private String url = null;
	
	// TODO we should be able to pass the url from the RIM
	public WebhookCommand(String url) {
		this.url = url;
	}
	
	/**
	 * @precondition url has been supplied
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Result execute(InteractionContext ctx) {
		if (url != null && url.length() > 0) {
			Map<String,Object> properties = null;
			try {
				OEntity oentity = ((EntityResource<OEntity>) ctx.getResource()).getEntity();
				properties = transform(oentity);
			} catch (ClassCastException cce) {
				Entity entity = ((EntityResource<Entity>) ctx.getResource()).getEntity();
				properties = transform(entity);
			}
			String formData = getFormData(properties);
			try {
				logger.info("POST " + url + " [" + formData + "]");
				HttpClient client = new HttpClient();
				PostMethod postMethod = new PostMethod(url);
				postMethod.setRequestEntity(new StringRequestEntity(formData,
				                                                    "application/x-www-form-urlencoded",
				                                                    "UTF-8"));
				client.executeMethod(postMethod);
				logger.info("Status [" + postMethod.getStatusCode() + "]");
			} catch (Exception e) {
				e.printStackTrace();
				return Result.FAILURE;
			}
		}
		return Result.SUCCESS;
	}

	protected Map<String, Object> transform(OEntity entity) {
		assert(entity != null);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			OEntity oentity = (OEntity) entity;
			for (OProperty<?> property : oentity.getProperties()) {
				map.put(property.getName(), property.getValue());				
			}
			map.put("id", oentity.getEntityKey().toKeyStringWithoutParentheses());
		} catch (RuntimeException e) {
			logger.error("Error transforming OEntity to map", e);
			throw e;
		}
		return map;
	}

	protected Map<String, Object> transform(Entity entity) {
		assert(entity != null);
		Map<String, Object> map = new HashMap<String, Object>();
    	EntityProperties props = entity.getProperties();
    	for (String propKey : props.getProperties().keySet()) {
      		map.put(propKey, props.getProperty(propKey).getValue());
    	}
		return map;
	}

    /**
     * Get this Entity as form data to send to our webhook.
     * @return
     */
    protected String getFormData(Map<String,Object> props) {
    	StringBuilder b = new StringBuilder();
    	// guarantee the order of the fields (makes testing easier too)
    	Set<String> keySet = new TreeSet<String>(props.keySet());
    	
    	boolean first = true;
    	for (String propKey : keySet) {
    		if (!first) {
        		b.append("&");    		
    		} else {
    			first = false;
    		}
    		b.append(propKey + "=");
    		Object value = props.get(propKey);
    		if (value != null) {
            	b.append(value.toString()).append("");
    		}
    	}
    	return b.toString();
    }
	
}
