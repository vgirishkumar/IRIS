package com.temenos.interaction.rimdsl.scoping;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.resource.impl.DefaultResourceServiceProvider;

/**
 * An IResourceServiceProvider which ignores resources in the target/ folder.
 * 
 * This is required because in at least one (proprietary, non open-source)
 * product using this, files frequently end up in the Maven target/ directory by
 * local builds, but those should not be picked up by the Xtext Builder Indexing
 * (in this product, the respective Eclipse projects are NOT Java JDT projects,
 * so they don't have a Build output directory).
 * 
 * @see http://rd.oams.com/browse/DS-7002
 * @see https://github.com/vorburger/efactory/commit/3aa7191248da17f15c55c804549eb601c51365f6
 * 
 * @author Michael Vorburger
 */
public class RIMDslResourceServiceProvider extends DefaultResourceServiceProvider {

	@Override
	public boolean canHandle(URI uri) {
		if (uri.isPlatform()) {
			if (uri.segmentCount() > 3) {
				if ("target".equals(uri.segment(2))) {
					return false;
				}
			}
		}
		return super.canHandle(uri);
	}

}