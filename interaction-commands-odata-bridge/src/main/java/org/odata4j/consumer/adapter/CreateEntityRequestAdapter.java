package org.odata4j.consumer.adapter;

/*
 * #%L
 * interaction-commands-odata-bridge
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


import java.util.Arrays;

import org.odata4j.consumer.AbstractConsumerEntityPayloadRequest;
import org.odata4j.core.OCreateRequest;
import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OLinks;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmMultiplicity;
import org.odata4j.edm.EdmNavigationProperty;
import org.odata4j.format.xml.XmlFormatWriter;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;

public class CreateEntityRequestAdapter<T> extends
        AbstractConsumerEntityPayloadRequest implements OCreateRequest<T> {

  private final ODataProducer producer;

  private OEntity parent;
  private String navProperty;

  public CreateEntityRequestAdapter(ODataProducer producer,
          String serviceRootUri, String entitySetName) {
    super(entitySetName, serviceRootUri, producer.getMetadata());
    this.producer = producer;
  }

  @SuppressWarnings("unchecked")
  @Override
  public T execute() {
    EdmEntitySet entitySet = producer.getMetadata().findEdmEntitySet(
            entitySetName);
    OEntity createEntity = OEntities.createRequest(entitySet, props, links);

    EntityResponse response = null;
    if (parent != null) {
      response = producer.createEntity(entitySetName, parent.getEntityKey(),
              navProperty, createEntity);
    } else {
      response = producer.createEntity(entitySetName, createEntity);
    }
    return (T) response.getEntity();
  }

  @SuppressWarnings("unchecked")
  @Override
  public T get() {
    EdmEntitySet entitySet = metadata.getEdmEntitySet(entitySetName);
    return (T) OEntities.createRequest(entitySet, props, links);
  }

  @Override
  public OCreateRequest<T> properties(OProperty<?>... props) {
    return super.properties(this, props);
  }

  @Override
  public OCreateRequest<T> properties(Iterable<OProperty<?>> props) {
    return super.properties(this, props);
  }

  @Override
  public OCreateRequest<T> link(String navProperty, OEntity target) {
    return super.link(this, navProperty, target);
  }

  @Override
  public OCreateRequest<T> link(String navProperty, OEntityKey targetKey) {
    return super.link(this, navProperty, targetKey);
  }

  @Override
  public OCreateRequest<T> addToRelation(OEntity parent, String navProperty) {
    if (parent == null || navProperty == null) {
      throw new IllegalArgumentException(
              "please provide the parent and the navProperty");
    }

    this.parent = parent;
    this.navProperty = navProperty;
    return this;
  }

  @Override
  public OCreateRequest<T> inline(String navProperty, OEntity... entities) {
    EdmEntitySet entitySet = metadata.getEdmEntitySet(entitySetName);
    EdmNavigationProperty navProp = entitySet.getType().findNavigationProperty(
            navProperty);
    if (navProp == null)
      throw new IllegalArgumentException("unknown navigation property "
              + navProperty);

    // TODO get rid of XmlFormatWriter
    String rel = XmlFormatWriter.related + navProperty;
    String href = entitySetName + "/" + navProperty;
    if (navProp.getToRole().getMultiplicity() == EdmMultiplicity.MANY) {
      links.add(OLinks.relatedEntitiesInline(rel, navProperty, href,
              Arrays.asList(entities)));
    } else {
      if (entities.length > 1)
        throw new IllegalArgumentException(
                "only one entity is allowed for this navigation property "
                        + navProperty);

      links.add(OLinks.relatedEntityInline(rel, navProperty, href,
              entities.length > 0 ? entities[0] : null));
    }

    return this;
  }

@Override
public OCreateRequest<T> inline(String navProperty, Iterable<OEntity> entities) {
	// TODO Auto-generated method stub
	return null;
}

}
