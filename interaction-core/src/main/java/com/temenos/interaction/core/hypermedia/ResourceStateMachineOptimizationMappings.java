package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-core
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


import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author kwieconkowski
 */
public interface ResourceStateMachineOptimizationMappings<E extends Enum> {
    List<ResourceState> buildAllMappingsAndInitializeLazyResourceAndReturnAllStates(ResourceStateProvider resourceStateProvider, ResourceState initial);
    boolean updateMapsWithNewState(ResourceStateProvider resourceStateProvider, ResourceState state, String method);
    void removeResourceStateByName(String stateName);
    Map<String, Set<ResourceState>> getResourceStatesByPath(ResourceState begin);
    Map getInformationFrom(E target);
    Object getInformationFrom(E target, String key);
}
