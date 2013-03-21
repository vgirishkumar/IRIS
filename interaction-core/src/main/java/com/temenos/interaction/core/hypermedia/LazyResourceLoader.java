package com.temenos.interaction.core.hypermedia;

/**
 * This interface signals the ResourceFactory to call initialise after the
 * resource object has been instantiated.
 * @author aphethean
 */
public interface LazyResourceLoader {

	public boolean initialise();
	
}
