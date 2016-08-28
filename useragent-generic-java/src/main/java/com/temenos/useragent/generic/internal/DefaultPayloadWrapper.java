package com.temenos.useragent.generic.internal;

/*
 * #%L
 * useragent-generic-java
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


import java.util.ArrayList;
import java.util.List;

import com.temenos.useragent.generic.Link;
import com.temenos.useragent.generic.PayloadHandler;

public class DefaultPayloadWrapper implements PayloadWrapper {

	private PayloadHandler transformer;
	private List<EntityWrapper> entities;

	public DefaultPayloadWrapper() {
	}

	@Override
	public List<Link> links() {
		return transformer.links();
	}

	@Override
	public EntityWrapper entity() {
		return transformer.entity();
	}

	@Override
	public List<EntityWrapper> entities() {
		checkAndBuildEntities();
		return entities;
	}

	private void checkAndBuildEntities() {
		if (entities == null) {
			entities = new ArrayList<EntityWrapper>();
			for (EntityWrapper entityWrapper : transformer.entities()) {
				entities.add(entityWrapper);
			}
		}
	}

	@Override
	public void setHandler(PayloadHandler transformer) {
		this.transformer = transformer;
	}

	@Override
	public boolean isCollection() {
		return transformer.isCollection();
	}
}
