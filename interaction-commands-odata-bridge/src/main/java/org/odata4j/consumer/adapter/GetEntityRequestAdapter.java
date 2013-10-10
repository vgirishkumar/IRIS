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


import java.util.List;
import java.util.Map;

import org.odata4j.core.OEntityGetRequest;
import org.odata4j.core.OEntityKey;
import org.odata4j.expression.BoolCommonExpression;
import org.odata4j.expression.EntitySimpleProperty;
import org.odata4j.expression.ExpressionParser;
import org.odata4j.internal.InternalUtil;
import org.odata4j.producer.EntityQueryInfo;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;

public class GetEntityRequestAdapter<T> extends
        AbstractOEntityRequestAdapter<T> implements OEntityGetRequest<T> {

  private final Class<T> entityType;

  private BoolCommonExpression filter;
  private Map<String, String> customOptions;
  private List<EntitySimpleProperty> expand;
  private List<EntitySimpleProperty> select;

  public GetEntityRequestAdapter(ODataProducer producer, Class<T> entityType,
          String entitySetName, OEntityKey entityKey) {
    super(producer, entitySetName, entityKey);
    this.entityType = entityType;
  }

  @Override
  public T execute() {
    EntityQueryInfo queryInfo = new EntityQueryInfo(filter, customOptions,
            expand, select);
    EntityResponse response = producer.getEntity(entitySetName, entityKey,
            queryInfo);
    return InternalUtil.toEntity(entityType, response.getEntity());
  }

  @Override
  public OEntityGetRequest<T> select(String select) {
    this.select = ExpressionParser.parseExpand(select);
    return this;
  }

  @Override
  public OEntityGetRequest<T> expand(String expand) {
    this.expand = ExpressionParser.parseExpand(expand);
    return this;
  }
}