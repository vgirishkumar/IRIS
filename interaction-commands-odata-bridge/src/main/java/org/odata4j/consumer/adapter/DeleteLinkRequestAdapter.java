package org.odata4j.consumer.adapter;

import org.odata4j.core.OEntityId;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OEntityRequest;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.exceptions.NotImplementedException;

public class DeleteLinkRequestAdapter<T> implements OEntityRequest<T> {

  private ODataProducer producer;
  private OEntityId sourceEntity;
  private String navProperty;
  private Object[] targetKeyValues;

  public DeleteLinkRequestAdapter(ODataProducer producer,
          OEntityId sourceEntity, String navProperty, Object... targetKeyValues) {
    super();
    this.producer = producer;
    this.sourceEntity = sourceEntity;
    this.navProperty = navProperty;
    this.targetKeyValues = targetKeyValues;
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
    for (Object value : targetKeyValues) {
      OEntityKey keyValue = OEntityKey.create(value);
      producer.deleteLink(sourceEntity, navProperty, keyValue);
    }
    return null;
  }
}
