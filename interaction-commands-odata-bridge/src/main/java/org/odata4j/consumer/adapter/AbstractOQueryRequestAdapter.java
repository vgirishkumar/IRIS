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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.odata4j.core.OEntityKey;
import org.odata4j.core.OQueryRequest;
import org.odata4j.internal.EntitySegment;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.QueryInfo;
import org.odata4j.producer.resources.OptionsQueryParser;

public abstract class AbstractOQueryRequestAdapter<T> implements
        OQueryRequest<T> {

  private final ODataProducer producer;
  private final String serviceRootUri;

  private Integer top;
  private Integer skip;
  private String orderBy;
  private String filter;
  private String select;
  private String lastSegment;
  private String expand;

  protected final List<EntitySegment> segments = new ArrayList<EntitySegment>();
  private final Map<String, String> customs = new HashMap<String, String>();

  public AbstractOQueryRequestAdapter(ODataProducer producer,
          String serviceRootUri) {
    super();
    this.producer = producer;
    this.serviceRootUri = serviceRootUri;
  }

  protected QueryInfo buildQueryInfo() {

    QueryInfo query = new QueryInfo(
    /* OptionsQueryParser.parseInlineCount(inlineCount) */null, top, skip,
            OptionsQueryParser.parseFilter(filter),
            OptionsQueryParser.parseOrderBy(orderBy),
            /* OptionsQueryParser.parseSkipToken(skipToken) */null, customs,
            OptionsQueryParser.parseExpand(expand),
            OptionsQueryParser.parseSelect(select));
    return query;
  }

  public ODataProducer getProducer() {
    return producer;
  }

  public String getServiceRootUri() {
    return serviceRootUri;
  }

  public Iterator<T> iterator() {
    return execute().iterator();
  }

  @Override
  public OQueryRequest<T> top(int top) {
    this.top = top;
    return this;
  }

  @Override
  public OQueryRequest<T> skip(int skip) {
    this.skip = skip;
    return this;
  }

  @Override
  public OQueryRequest<T> orderBy(String orderBy) {
    this.orderBy = orderBy;
    return this;
  }

  @Override
  public OQueryRequest<T> filter(String filter) {
    this.filter = filter;
    return this;
  }

  @Override
  public OQueryRequest<T> select(String select) {
    this.select = select;
    return this;
  }

  @Override
  public OQueryRequest<T> expand(String expand) {
    this.expand = expand;
    return this;
  }

  @Override
  public OQueryRequest<T> nav(Object keyValue, String navProperty) {
    return nav(OEntityKey.create(keyValue), navProperty);
  }

  @Override
  public OQueryRequest<T> nav(OEntityKey key, String navProperty) {
    segments.add(new EntitySegment(lastSegment, key));
    lastSegment = navProperty;
    return this;
  }

  @Override
  public OQueryRequest<T> custom(String name, String value) {
    customs.put(name, value);
    return this;
  }

}
