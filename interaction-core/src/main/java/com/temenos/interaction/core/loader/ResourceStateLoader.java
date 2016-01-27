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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.temenos.interaction.core.loader.ResourceStateLoader.ResourceStateResult;

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
public interface ResourceStateLoader<S> extends Loader<List<ResourceStateResult>, S> {
    class ResourceStateResult {
        public final String resourceStateId;
        public final ResourceState resourceState;
        public final String[] methods;
        public final String path;

        public ResourceStateResult(String resourceStateId, ResourceState resourceState) {
            this(resourceStateId, resourceState, null, null);
        }

        public ResourceStateResult(String resourceStateId, ResourceState resourceState, String[] methods, String path) {
            this.resourceStateId = resourceStateId;
            this.resourceState = resourceState;
            this.methods = methods;
            this.path = path;
        }
        
        public String getResourceStateId(){
        	return resourceStateId;
        }
        
        public ResourceState getResourceState(){
        	return resourceState;
        }
        
        public String[] getMethods(){
        	return methods;
        }
        
        public String getPath(){
        	return path;
        }

		@Override
		public int hashCode() {
			return Objects.hash(resourceStateId, resourceState, methods, path);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ResourceStateResult other = (ResourceStateResult) obj;
			return Objects.equals(resourceStateId, other.getResourceStateId()) &&
					Objects.equals(resourceState, other.getResourceState()) &&
					Objects.equals(methods, other.getMethods()) &&
					Objects.equals(path, other.getPath());
		}
    }
}
