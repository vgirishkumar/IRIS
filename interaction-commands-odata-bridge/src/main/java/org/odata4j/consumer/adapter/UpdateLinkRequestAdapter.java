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


import org.odata4j.core.OEntityId;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OEntityRequest;
import org.odata4j.exceptions.NotImplementedException;
import org.odata4j.producer.ODataProducer;

public class UpdateLinkRequestAdapter<T> implements OEntityRequest<T> {

  private final ODataProducer producer;
  private final OEntityId sourceEntity;
  private final OEntityId newTargetEntity;
  private final String targetNavProp;
  private final Object[] oldTargetKeyValues;

  public UpdateLinkRequestAdapter(ODataProducer producer,
          OEntityId sourceEntity, OEntityId newTargetEntity,
          String targetNavProp, Object[] oldTargetKeyValues) {
    super();
    this.producer = producer;
    this.sourceEntity = sourceEntity;
    this.newTargetEntity = newTargetEntity;
    this.targetNavProp = targetNavProp;
    this.oldTargetKeyValues = oldTargetKeyValues;
  }

  @Override
  public OEntityRequest<T> nav(String navProperty, OEntityKey key) {
    throw new NotImplementedException("Not supported yet.");
  }

  @Override
  public OEntityRequest<T> nav(String navProperty) {
    throw new NotImplementedException("Not supported yet.");
  }

  @Override
  public T execute() {
    producer.updateLink(sourceEntity, targetNavProp,
            OEntityKey.create(oldTargetKeyValues), newTargetEntity);
    return null;
  }

}
