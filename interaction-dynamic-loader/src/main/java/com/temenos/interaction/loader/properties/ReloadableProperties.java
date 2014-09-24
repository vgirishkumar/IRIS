package com.temenos.interaction.loader.properties;

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

import java.util.Properties;


/**
 * For Properties maps that notify about changes. Would extend interface
 * java.util.Properties if it were an interface. Classes implementing this
 * interface should consider extending {@link DelegatingProperties}. Credit to:
 * http://www.wuenschenswert.net/wunschdenken/archives/127
 */
public interface ReloadableProperties {
	public Properties getProperties();

	void addReloadablePropertiesListener(ReloadablePropertiesListener l);

	boolean removeReloadablePropertiesListener(ReloadablePropertiesListener l);
}
