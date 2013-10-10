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

import org.core4j.Enumerable;
import org.odata4j.core.OEntityId;
import org.odata4j.core.OQueryRequest;
import org.odata4j.producer.EntityIdResponse;
import org.odata4j.producer.ODataProducer;

public class GetLinksRequestAdapter<T> extends AbstractOQueryRequestAdapter<T>
        implements OQueryRequest<T> {

  private OEntityId sourceEntityId;
  private String navProperty;

  public GetLinksRequestAdapter(ODataProducer producer, String serviceRootUri,
          OEntityId sourceEntityId, String navProperty) {
    super(producer, serviceRootUri);
    this.sourceEntityId = sourceEntityId;
    this.navProperty = navProperty;
  }

  @SuppressWarnings("unchecked")
@Override
  public Enumerable<T> execute() {
    EntityIdResponse response = getProducer().getLinks(sourceEntityId,
            navProperty);
    List<T> result = new ArrayList<T>();
    for (OEntityId entityId : response.getEntities()) {
      result.add((T) entityId);
    }
    return Enumerable.create(result);
  }

}
