package com.temenos.interaction.commands.webhook;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
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
		assert(url != null);
		Entity entity = ((EntityResource<Entity>) ctx.getResource()).getEntity();
		try {
			String formData = getFormData(entity);
			logger.info("POST [" + formData + "]");
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
		return Result.SUCCESS;
	}

    /**
     * Get this Entity as form data to send to our webhook.
     * @return
     */
    protected String getFormData(Entity entity) {
    	StringBuilder b = new StringBuilder();
    	EntityProperties props = entity.getProperties();
    	
    	// guarantee the order of the fields (makes testing easier too)
    	Set<String> keySet = new TreeSet<String>(props.getProperties().keySet());
    	
    	boolean first = true;
    	for (String propKey : keySet) {
    		if (!first) {
        		b.append("&");    		
    		} else {
    			first = false;
    		}
    		b.append(propKey + "=");
    		Object value = props.getProperty(propKey).getValue();
    		if (value != null) {
            	b.append(value.toString()).append("");
    		}
    	}
    	return b.toString();
    }
	
}
