package org.odata4j.consumer.adapter;

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

  @Override
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
