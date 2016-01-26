package com.temenos.interaction.rimdsl.runtime.loader;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.temenos.interaction.core.hypermedia.Event;
import com.temenos.interaction.core.hypermedia.ResourceState;

/**
 * TODO: Document me!
 *
 *
 */
public class RIMResourceStateProvider implements TranslatorDrivenResourceStateProvider {

    @Override
    public boolean isLoaded(String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ResourceState getResourceState(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResourceState determineState(Event event, String resourcePath) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Set<String>> getResourceStatesByPath() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Set<String>> getResourceMethodsByState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> getResourcePathsByState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ResourceState> getAllResourceStates(URI location) {
        // TODO Auto-generated method stub
        return null;
    }

}
