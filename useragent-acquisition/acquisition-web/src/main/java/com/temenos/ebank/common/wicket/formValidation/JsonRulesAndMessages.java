package com.temenos.ebank.common.wicket.formValidation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

/**
 * @author raduf
 *         This class hides Json implementation details and translates string to Json objects under the form
 *         of rules and messages for the rules. Expected structure is "{rule:value, messages:{rule:'message for rule'}}"
 */
public class JsonRulesAndMessages {

	private static final String MESSAGES = "messages";

	public static enum Rules {
		MAXLENGTH("maxlength"), REQUIRED("required"), MINLENGTH("minlength");
		private String rule;

		private Rules(String rule) {
			this.rule = rule;
		}

		public String toString() {
			return rule;
		}
	}

	private static ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
	}

	public JsonRulesAndMessages(String jSonString) {
		if (jSonString != null && jSonString.length() > 0) {
			InputStream inputStream = new ByteArrayInputStream(jSonString.getBytes());
			try {
				rulesAndMessages = (ObjectNode) mapper.readValue(inputStream, JsonNode.class);
				messages = (ObjectNode) rulesAndMessages.get(MESSAGES);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			rulesAndMessages = new ObjectNode(mapper.getNodeFactory());
			messages = new ObjectNode(mapper.getNodeFactory());
		}
	}

	private ObjectNode rulesAndMessages;

	public ObjectNode getRulesAndMessages() {
		return rulesAndMessages;
	}

	private ObjectNode messages;

	public ObjectNode getMessages() {
		// never returning a null ObjectNode
		if ((ObjectNode) rulesAndMessages.get(MESSAGES) != null)
			return (ObjectNode) rulesAndMessages.get(MESSAGES);
		else
			return messages;
	}

	public String getRulesAndMessagesString() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			mapper.writeValue(outputStream, rulesAndMessages);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String output = new String(outputStream.toByteArray());

		// escape single quotes for js
		output = output.replace("'", "\\'");
		// convert double to single quotes
		output = output.replace("\"", "'");
		return output;
	}

	public void replaceMessage(Rules rule, String newMessage) {
		messages.put(rule.toString(), newMessage);
	}

	public void addOrReplaceRuleValue(Rules rule, String value) {
		rulesAndMessages.put(rule.toString(), value);
	}

	public void addOrReplaceRuleValue(Rules rule, Boolean value) {
		rulesAndMessages.put(rule.toString(), value);
	}

	public void addOrReplaceRuleValue(Rules rule, Integer value) {
		rulesAndMessages.put(rule.toString(), value);
	}

	public JsonRulesAndMessages appendJSon(JsonRulesAndMessages toAppend) {
		ObjectNode messagesCopy = getMessages();
		rulesAndMessages.putAll(toAppend.getRulesAndMessages());
		getMessages().putAll(messagesCopy);
		return this;
	}

}
