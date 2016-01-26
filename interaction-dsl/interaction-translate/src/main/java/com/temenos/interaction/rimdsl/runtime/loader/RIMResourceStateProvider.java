package com.temenos.interaction.rimdsl.runtime.loader;

/*
 * #%L
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import com.temenos.interaction.core.hypermedia.Event;
import com.temenos.interaction.core.hypermedia.ResourceState;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
