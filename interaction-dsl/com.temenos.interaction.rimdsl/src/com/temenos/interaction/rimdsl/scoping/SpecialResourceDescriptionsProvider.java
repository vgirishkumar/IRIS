package com.temenos.interaction.rimdsl.scoping;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider;

/**
 * Special ResourceDescriptionsProvider.
 * 
 * @see http://rd.oams.com/browse/DS-7312
 * @see http://www.eclipse.org/forums/index.php/m/1289834/
 * @see https://github.com/vorburger/xtext-sandbox/pull/1
 * 
 * @author Michael Vorburger
 */
public class SpecialResourceDescriptionsProvider extends ResourceDescriptionsProvider {

	public static final String INDEX = "index";

	@Override
	public IResourceDescriptions getResourceDescriptions(ResourceSet resourceSet) {
		Object index = resourceSet.getLoadOptions().get(INDEX);
		if (index instanceof IResourceDescriptions) {
			return (IResourceDescriptions) index;
		} else {
			return super.getResourceDescriptions(resourceSet);
		}
	}
}
