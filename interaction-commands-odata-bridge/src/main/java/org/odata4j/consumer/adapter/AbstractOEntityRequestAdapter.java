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

import org.odata4j.core.OEntityKey;
import org.odata4j.core.OEntityRequest;
import org.odata4j.internal.EntitySegment;
import org.odata4j.producer.ODataProducer;

public abstract class AbstractOEntityRequestAdapter<T> implements
        OEntityRequest<T> {

  protected final ODataProducer producer;
  protected final String entitySetName;
  protected final OEntityKey entityKey;

  protected final List<EntitySegment> segments = new ArrayList<EntitySegment>();

  public AbstractOEntityRequestAdapter(ODataProducer producer,
          String entitySetName, OEntityKey entityKey) {
    super();
    this.producer = producer;
    this.entitySetName = entitySetName;
    this.entityKey = entityKey;

    segments.add(new EntitySegment(entitySetName, entityKey));
  }

  @Override
  public OEntityRequest<T> nav(String navProperty, OEntityKey key) {
    segments.add(new EntitySegment(navProperty, key));
    return this;
  }

  @Override
  public OEntityRequest<T> nav(String navProperty) {
    segments.add(new EntitySegment(navProperty, null));
    return this;
  }

}
