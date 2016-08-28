package com.temenos.interaction.core.loader;

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

import com.temenos.interaction.core.hypermedia.ResourceState;

import static com.temenos.interaction.core.loader.ResourceStateLoadingStrategy.ResourceStateResult;

/**
 * Interface for loading a list of ResourceStateResult from a source. Ideally,
 * we would return a list of ResourceState, but we also need to store the ids
 * of each ResourceState for legacy code compatibility (such as storing the
 * bean ids from a PRD file). For this reason we use ResourceStateResult.
 * 
 * @author kwieconkowski
 * @author andres
 * @author dgroves
 */
public interface ResourceStateLoadingStrategy<S> extends LoadingStrategy<ResourceStateResult, S> {
    class ResourceStateResult {
        public final String resourceStateId;
        public final ResourceState resourceState;

        public ResourceStateResult(String resourceStateId, ResourceState resourceState) {
            this.resourceStateId = resourceStateId;
            this.resourceState = resourceState;
        }
    }
}
