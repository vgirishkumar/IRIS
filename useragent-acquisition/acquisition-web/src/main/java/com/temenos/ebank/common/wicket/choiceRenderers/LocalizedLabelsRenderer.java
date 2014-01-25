package com.temenos.ebank.common.wicket.choiceRenderers;

import static org.apache.commons.lang.StringUtils.defaultString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.util.string.Strings;

/**
 * {@link IChoiceRenderer} implementation that makes it easy to work with java 5 enums. This
 * renderer will attempt to lookup strings used for the display value using a localizer of a given
 * component. If the component is not specified, the global instance of localizer will be used for
 * lookups.
 * <p>
 * display value resource key format: {@code <enum.getSimpleClassName()>.<enum.name()>}
 * </p>
 * <p>
 * id value format: {@code <enum.name()>}
 * </p>
 * 
 * @author igor.vaynberg
 * 
 * @param <T>
 */
public class LocalizedLabelsRenderer implements IGenericChoiceRenderer

{

	private static final long serialVersionUID = 1L;

	/**
	 * component used to resolve i18n resources for this renderer.
	 */
	private final Component resourceSource;

	private final String prefix;

	/**
	 * Constructor
	 * 
	 * @param resourceSource
	 */
	public LocalizedLabelsRenderer(String prefix, Component resourceSource) {
		InjectorHolder.getInjector().inject(this);
		this.prefix = defaultString(prefix);
		this.resourceSource = resourceSource;
	}

	/** {@inheritDoc} */
	public final Object getDisplayValue(String entry) {
		String value;

		String key = resourceKey(entry);

		value = resolveKey("CHOICE." + key);

		return postprocess(value);
	}

	private String resolveKey(String key) {
		final String value;
		if (resourceSource != null) {
			value = resourceSource.getString(key);
		} else {
			value = Application.get().getResourceSettings().getLocalizer().getString(key, null);
		}
		return value;
	}

	/**
	 * Translates the {@code object} into resource key that will be used to lookup the value shown
	 * to the user
	 * 
	 * @param object
	 * @return resource key
	 */
	protected String resourceKey(Object object) {
		// if object null suffix will be "null"
		String suffix = String.valueOf(object);
		return prefix + "." + suffix;
	}

	/**
	 * Postprocesses the {@code value} after it is retrieved from the localizer. Default
	 * implementation escapes any markup found in the {@code value}.
	 * 
	 * @param value
	 * @return postprocessed value
	 */
	protected CharSequence postprocess(String value) {
		return Strings.escapeMarkup(value);
	}

	/** {@inheritDoc} */
	public String getIdValue(String entry, int index) {

		return String.valueOf(entry);
	}

	public List<String> getChoices() {
		return getChoices("codes");
	}

	/**
	 * Use another suffix ( != .codes) for a subset of the .codes list to be displayed
	 * 
	 * @param choiceListSuffix
	 * @return
	 */
	public List<String> getChoices(String choiceListSuffix) {
		final String value;

		String key = "CHOICE." + prefix + "." + choiceListSuffix;

		value = resolveKey(key);

		if (value != null) {
			return Arrays.asList(StringUtils.split(value.trim(), ';'));
		}

		return new ArrayList<String>(0);
	}

}
