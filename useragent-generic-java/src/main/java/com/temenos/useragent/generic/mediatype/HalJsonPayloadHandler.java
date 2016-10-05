package com.temenos.useragent.generic.mediatype;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.temenos.useragent.generic.Link;
import com.temenos.useragent.generic.PayloadHandler;
import com.temenos.useragent.generic.internal.DefaultEntityWrapper;
import com.temenos.useragent.generic.internal.EntityWrapper;
import com.temenos.useragent.generic.internal.NullEntityWrapper;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;

/**
 * A payload handler for <i>application/hal+json</i> media type.
 * 
 * @author ssethupathi
 *
 */
public class HalJsonPayloadHandler implements PayloadHandler {

	private ReadableRepresentation representation;
	private RepresentationFactory representationFactory = HalJsonUtil
			.initRepresentationFactory();
	private String parameter; // not used yet

	@Override
	public boolean isCollection() {
		if (representation.getResourcesByRel("item").isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public List<Link> links() {
		return HalJsonUtil.extractLinks(representation);
	}

	@Override
	public List<EntityWrapper> entities() {
		List<EntityWrapper> entityWrappers = new ArrayList<EntityWrapper>();
		for (ReadableRepresentation itemRepresentation : representation
				.getResourcesByRel("item")) {
			EntityWrapper entityWrapper = new DefaultEntityWrapper();
			HalJsonEntityHandler entityHandler = new HalJsonEntityHandler();
			entityHandler.setContent(IOUtils.toInputStream(itemRepresentation
					.toString(RepresentationFactory.HAL_JSON)));
			entityWrapper.setHandler(entityHandler);
			entityWrappers.add(entityWrapper);
		}
		return entityWrappers;
	}

	@Override
	public EntityWrapper entity() {
		if (!isCollection()) {
			EntityWrapper wrapper = new DefaultEntityWrapper();
			HalJsonEntityHandler entityHandler = new HalJsonEntityHandler();
			entityHandler.setContent(IOUtils.toInputStream(representation
					.toString(RepresentationFactory.HAL_JSON)));
			wrapper.setHandler(entityHandler);
			return wrapper;
		}
		return new NullEntityWrapper();
	}

	@Override
	public void setPayload(String payload) {
		if (payload == null) {
			throw new IllegalArgumentException("Payload is null");
		}
		ReadableRepresentation jsonRepresentation = representationFactory
				.readRepresentation(RepresentationFactory.HAL_JSON,
						new InputStreamReader(IOUtils.toInputStream(payload)));
		representation = jsonRepresentation;
	}

	@Override
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
}
