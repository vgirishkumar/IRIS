package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StrategyBasedTransformer implements Transformer {
	private final Logger logger = LoggerFactory.getLogger(StrategyBasedTransformer.class);

	private final List<Transformer> transformers;
	
	/**
	 * Constructs this transformer strategy with our default transformers.
	 * <li>{@link BeanTransformer}</li>
	 * <li>{@link EntityTransformer}</li>
	 */
	public StrategyBasedTransformer() {
		transformers = new ArrayList<Transformer>();
		transformers.add(new EntityTransformer());
		transformers.add(new BeanTransformer());
	}
	
	/**
	 * Constructs this transformer strategy using supplied {@link Transformer}s.
	 */
	public StrategyBasedTransformer(Collection<Transformer> newTransformers) {
		if (newTransformers == null)
			throw new IllegalArgumentException("Must supply a collection of transformers to this constructor");
		transformers = new ArrayList<Transformer>();
		transformers.addAll(newTransformers);
	}

	@Override
	public Map<String, Object> transform(Object entity) {
		Map<String, Object> properties = new HashMap<String, Object>();
		for (Transformer t : transformers) {
			logger.debug("Checking transformer [" + t.getClass().getName() + "]");
			if (t.canTransform(entity)) {
				logger.debug("Using transformer [" + t.getClass().getName() + "] for entity [" + entity.getClass().getName() + "]");
				properties = t.transform(entity);
				break;
			}
		}
		
		return properties;
	}

	/**
	 * Accepts all types of objects and delegates the work to all supplied transformers
	 * @see 
	 */
	@Override
	public boolean canTransform(Object entity) {
		return true;
	}
}
