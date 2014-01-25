package com.temenos.ebank.wicket;

import java.util.MissingResourceException;

import org.apache.wicket.Localizer;
import org.apache.wicket.model.IModel;

import com.temenos.ebank.common.wicket.WicketUtils.SUFFIX;

/**
 * Localizer extension that provides different mechanisms for resource recovery. Specifically:
 * <ul>
 * <li>Hint text errors are passed on, to be treated by {@link com.temenos.ebank.common.wicket.components.HintPanel
 * HintPanel}</li>
 * <li>Any other {@link MissingResourceException} is caught, and a graceful recovery is supplied</li>
 * </ul>
 * <p>
 * 
 * @see EbankWicketApplication
 * @author gcristescu
 */
public class EbankLocalizer extends Localizer {
	@Override
	public String getString(String key, org.apache.wicket.Component component, IModel<?> model, String defaultValue)
			throws MissingResourceException {
		String value;
		try {
			value = super.getString(key, component, model, defaultValue);
		} catch (MissingResourceException me) {
			// TODO find a way to do this without enumerating (almost) all suffixes
			if (key.endsWith(SUFFIX.INFO) || key.endsWith(SUFFIX.HINT_IMG_ALT) || key.endsWith(SUFFIX.HINT_IMG_TITLE)
					|| key.endsWith(SUFFIX.MIN_LENGTH) || key.endsWith(SUFFIX.MAX_LENGTH)) {
				// for hints, the exception is passed on, and probably ignored
				throw me;
			} else {
				// this will be showed in the page instead of the property value
				value = "???" + key + "???";
			}
		}

		return value;
	}
}