package com.temenos.interaction.core.hypermedia;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class StrategyBasedTransformer implements Transformer {
	private final Logger logger = LoggerFactory.getLogger(StrategyBasedTransformer.class);

	private final List<Transformer> transformers;
	
	/**
	 * Constructs this transformer strategy with our default transformers.
	 * <li>{@link BeanTransformer}</li>
	 * <li>{@link EntityTransformer}</li>
	 */
	public StrategyBasedTransformer() {
		transformers = Lists.newArrayList();
		transformers.add(new EntityTransformer());
		transformers.add(new BeanTransformer());
	}
	
	/**
	 * Constructs this transformer strategy using supplied {@link Transformer}s.
	 */
	public StrategyBasedTransformer(Collection<Transformer> newTransformers) {
		if (newTransformers == null)
			throw new IllegalArgumentException("Must supply a collection of transformers to this constructor");
		transformers = Lists.newArrayList();
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
