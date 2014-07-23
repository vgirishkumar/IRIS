package com.temenos.interaction.springdsl.properties;

/*
 * #%L
 * interaction-springdsl
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
import java.util.Properties;
import java.util.Set;

import org.springframework.core.io.Resource;


/**
 * Useful base class for implementing {@link ReloadableProperties}. Credit to:
 * http://www.wuenschenswert.net/wunschdenken/archives/127
 */
@SuppressWarnings("unchecked")
public class ReloadablePropertiesBase extends DelegatingProperties implements ReloadableProperties {
	private static final long serialVersionUID = 1882584866192427533L;
	private List<ReloadablePropertiesListener> listeners = new ArrayList<ReloadablePropertiesListener>();
	private Properties internalProperties;

	public void setListeners(List<ReloadablePropertiesListener> listeners) {
		this.listeners = listeners;
	}

	protected Properties getDelegate() {
		synchronized (this) {
			return internalProperties;
		}
	}

	public Properties getProperties() {
		return getDelegate();
	}
	
	public Properties getProperties(Set<Object> keys) {
		Properties result = new Properties();
		for (Object key : keys) {
			Object value = null;
			if (internalProperties != null) {
				value = internalProperties.get(key);
			}
			result.put(key, value);
		}
		return result;
	}

	public void addReloadablePropertiesListener(ReloadablePropertiesListener l) {
		listeners.add(l);
	}

	public boolean removeReloadablePropertiesListener(ReloadablePropertiesListener l) {
		return listeners.remove(l);
	}

	protected void notifyPropertiesChanged(Properties newProperties) {
		PropertiesReloadedEvent event = new PropertiesReloadedEvent(this, newProperties);
		for (ReloadablePropertiesListener listener : listeners) {
			listener.propertiesReloaded(event);
		}
	}

	protected void notifyPropertiesLoaded(Resource resource, Properties newProperties) {
		PropertiesLoadedEvent event = new PropertiesLoadedEvent(this, resource, newProperties);
		for (ReloadablePropertiesListener listener : listeners) {
			listener.propertiesLoaded(event);
		}
	}
	
	protected void notifyPropertiesChanged(Resource resource, Properties newProperties) {
		PropertiesChangedEvent event = new PropertiesChangedEvent(this, resource, newProperties);
		for (ReloadablePropertiesListener listener : listeners) {
			listener.propertiesChanged(event);
		}
	}

	protected void setProperties(Properties properties) {
		synchronized (this) {
			internalProperties = properties;
		}
	}
}
