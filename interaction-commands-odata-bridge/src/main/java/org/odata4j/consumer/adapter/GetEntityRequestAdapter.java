package org.odata4j.consumer.adapter;

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