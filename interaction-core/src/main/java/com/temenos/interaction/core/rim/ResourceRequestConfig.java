package com.temenos.interaction.core.rim;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
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

import java.util.ArrayList;
import java.util.List;

import com.temenos.interaction.core.hypermedia.Transition;

public class ResourceRequestConfig {

	/* the resources we want to get */
	private List<Transition> transitions;
	private boolean injectLinks;
	private boolean embedResources;
	private Transition selfTransition;

	public List<Transition> getTransitions() {
		return transitions;
	}
	
	public boolean isInjectLinks() {
		return injectLinks;
	}

	public boolean isEmbedResources() {
		return embedResources;
	}

	public Transition getSelfTransition() {
		return selfTransition;
	}

	/* The Builder can be regenerated with the fast code eclipse plugin */

	public static class Builder {
		private List<Transition> transitions = new ArrayList<Transition>();
		private boolean injectLinks = true;
		private boolean embedResources = true;
		private Transition selfTransition;

		public Builder transition(Transition transition) {
			this.transitions.add(transition);
			return this;
		}

		public Builder injectLinks(boolean injectLinks) {
			this.injectLinks = injectLinks;
			return this;
		}

		public Builder embedResources(boolean embedResources) {
			this.embedResources = embedResources;
			return this;
		}

		public Builder selfTransition(Transition selfTransition) {
			this.selfTransition = selfTransition;
			return this;
		}

		public ResourceRequestConfig build() {
			return new ResourceRequestConfig(this);
		}
	}

	private ResourceRequestConfig(Builder builder) {
		this.transitions = builder.transitions;
		this.injectLinks = builder.injectLinks;
		this.embedResources = builder.embedResources;
		this.selfTransition = builder.selfTransition;
	}
}
