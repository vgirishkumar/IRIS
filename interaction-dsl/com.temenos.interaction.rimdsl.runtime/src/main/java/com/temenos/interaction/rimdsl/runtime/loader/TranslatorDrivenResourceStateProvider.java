package com.temenos.interaction.rimdsl.runtime.loader;

import java.net.URI;
import java.util.List;

import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateProvider;

/**
 * TODO: Document me!
 *
 * @author hmanchala
 *
 */
public interface TranslatorDrivenResourceStateProvider extends ResourceStateProvider {
    public List<ResourceState> getAllResourceStates(URI location);
}
