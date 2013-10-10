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


import java.util.ArrayList;
import java.util.List;

import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OLink;
import org.odata4j.core.OLinks;
import org.odata4j.core.OModifyRequest;
import org.odata4j.core.OProperty;
import org.odata4j.producer.ODataProducer;

public class UpdateEntityRequestAdapter<T> implements OModifyRequest<T> {

  private final ODataProducer producer;
  private final String entitySetName;
  private final OEntityKey entityKey;

  private List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
  private List<OLink> links = new ArrayList<OLink>();

  public UpdateEntityRequestAdapter(ODataProducer producer,
          String entitySetName, OEntityKey entityKey) {
    super();
    this.producer = producer;
    this.entitySetName = entitySetName;
    this.entityKey = entityKey;
  }

  @Override
  public OModifyRequest<T> properties(OProperty<?>... props) {
    for (OProperty<?> prop : props) {
      properties.add(prop);
    }
    return this;
  }

  @Override
  public OModifyRequest<T> properties(Iterable<OProperty<?>> props) {
    for (OProperty<?> prop : props) {
      properties.add(prop);
    }
    return this;
  }

  @Override
  public OModifyRequest<T> link(String navProperty, OEntity target) {
    // TODO : check
    OLink link = OLinks.relatedEntityInline(navProperty, null, null, target);
    links.add(link);
    return this;
  }

  @Override
  public OModifyRequest<T> link(String navProperty, OEntityKey targetKey) {
    // TODO : check
    OLink link = OLinks.relatedEntity(navProperty, navProperty, null);
    links.add(link);
    return this;
  }

  @Override
  public void execute() {
    OEntity entity = OEntities.create(
            producer.getMetadata().getEdmEntitySet(entitySetName), entityKey,
            properties, links);
    try {
      producer.updateEntity(entitySetName, entity);
    } catch (Exception e) {
      // TODO : Add logs
    }
  }

  @Override
  public OModifyRequest<T> nav(String navProperty, OEntityKey key) {
    // TODO Auto-generated method stub
    return this;
  }

@Override
public OModifyRequest<T> ifMatch(String precondition) {
	// TODO Auto-generated method stub
	return null;
}

}
