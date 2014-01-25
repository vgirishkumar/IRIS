package com.temenos.ebank.wicket;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.resource.loader.ComponentStringResourceLoader;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vbeuran
 *         The regular strategy for resolving the key for a property involves parsing the complete page relative path
 *         ("pageName.wizardName.formName.viewName.panelName.borderName.componentName.property=<locale specific text>").
 *         Let A be a component with direct or indirect subcomponents B1 through Bn. The present strategy allows
 *         resolving attributes "B1.propX" through "Bn.propX" with the value of "identifierOfA.propX" defined in A's
 *         properties file.
 *         Please note that the key contains <strong> component A's identifier </strong>.
 * 
 *         E.g. We want to resolve a "Required" property for multiple fields inside the CustomerDetailsPanel. As the
 *         required label should be different for the first customer panel from the one in the second panel, in
 *         CustomerDetailsPanel.properties we define :
 *         "customerDetails.Required = Please enter your '${label}' to proceed"
 *         "secondCustomerDetails.Required = Please enter second applicant's '${label}' to proceed"
 *         "customerDetails" and "secondCustomerDetails" are the ids of the two CustomerDetailsPanels defined
 *         in Step1
 */
public class ContextDependentPropertiesResourceLoader extends FlatBundleStringResourceLoader {
	private static final Logger log = LoggerFactory.getLogger(ComponentStringResourceLoader.class);

	@Override
	public String loadStringResource(Component component, String key) {
		if (component == null) {
			return null;
		}

		if (log.isDebugEnabled()) {
			log.debug("component: '" + component.toString(false) + "'; key: '" + key + "'");
		}

		String string = null;
		Locale locale = component.getLocale();
		String style = component.getStyle();

		// The key prefix is equal to the component path relative to the
		// current component on the top of the stack.
		String prefix = Strings.replaceAll(component.getPageRelativePath(), ":", ".").toString();
		String[] prefixBits = prefix.split("\\.");
		// The reason why we need to create that stack is because we need to
		// walk it upwards starting from Component
		List<Class<?>> searchStack = getComponentStack(component);
		// this matching requires same-size componentStack and pageRelativePath
		searchStack.remove(searchStack.size() - 1);
		if (searchStack.size() != prefixBits.length) {
		   return null;
		}

		String className = "";
		int lastFullStopSeparator = key.lastIndexOf(".");
		boolean compositeKey = lastFullStopSeparator >= 0;

		// go from closest to most distant parent in the page hierarchy and
		// try to resolve "parentName.<requested key>"
		for (int i = prefixBits.length - 1; i >= 0 && string == null; i--) {
			Class<?> clazz = searchStack.get(i);
			className = prefixBits[searchStack.size() - 1 - i];
			string = loadStringResource(clazz, className + "." + key, locale, style);
			if (compositeKey && StringUtils.isEmpty(string)) {
				string = loadStringResource(clazz, className + key.substring(lastFullStopSeparator), locale, style);
			}
		}

		return string;
	}

	/**
	 * Traverse the component hierarchy up to the Page and add each component class to the list
	 * (stack) returned
	 * 
	 * @param component
	 *            The component to evaluate
	 * @return The stack of classes
	 */
	private List<Class<?>> getComponentStack(final Component component) {
		// Build the search stack
		final List<Class<?>> searchStack = new ArrayList<Class<?>>();
		searchStack.add(component.getClass());

		if (!(component instanceof Page)) {
			// Add all the component on the way to the Page
			MarkupContainer container = component.getParent();
			while (container != null) {
				searchStack.add(container.getClass());
				if (container instanceof Page) {
					break;
				}

				container = container.getParent();
			}
		}
		return searchStack;
	}
}
