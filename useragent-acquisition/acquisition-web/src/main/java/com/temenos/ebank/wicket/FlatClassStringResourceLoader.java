package com.temenos.ebank.wicket;

import java.lang.ref.WeakReference;
import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.Session;


/**
 * This class was created by copying the ClassStringResourceLoader in Wicket
 * The difference from the copied class is that it inherits the @EbankBundleStringResourceLoader
 * which looks for properties files in a flat location as opposed to #ComponentStringResourceLoader
 * which looks for properties files in the package hierarchy. Below - original documentation of the class.
 * 
 * This string resource loader attempts to find a single resource bundle that has the same name and
 * location as the clazz provided in the constructor. If the bundle is found than strings are
 * obtained from here. This implementation is fully aware of both locale and style values when
 * trying to obtain the appropriate bundle.
 * <p>
 * An instance of this loader is registered with the Application by default.
 * 
 * @author Chris Turner
 * @author Juergen Donnerstag
 */
public class FlatClassStringResourceLoader extends FlatBundleStringResourceLoader {
	/** The application we are loading for. */
	private final WeakReference<Class<?>> clazzRef;

	/**
	 * Create and initialize the resource loader.
	 * 
	 * @param clazz
	 *            The class that this resource loader is associated with
	 */
	public FlatClassStringResourceLoader(final Class<?> clazz)
	{
		if (clazz == null)
		{
			throw new IllegalArgumentException("Parameter 'clazz' must not be null");
		}
		clazzRef = new WeakReference<Class<?>>(clazz);
	}

	/**
	 * @see org.apache.wicket.resource.loader.ComponentStringResourceLoader#loadStringResource(java.lang.Class,
	 *      java.lang.String, java.util.Locale, java.lang.String)
	 */
	@Override
	public String loadStringResource(final Class<?> clazz, final String key, final Locale locale,
		final String style)
	{
		return super.loadStringResource(clazzRef.get(), key, locale, style);
	}

	/**
	 * @see org.apache.wicket.resource.loader.ComponentStringResourceLoader#loadStringResource(org.apache.wicket.Component,
	 *      java.lang.String)
	 */
	@Override
	public String loadStringResource(Component component, String key)
	{
		if (component == null)
		{
			return loadStringResource(null, key, Session.get().getLocale(), Session.get()
				.getStyle());
		}
		return super.loadStringResource(component, key);
	}
}