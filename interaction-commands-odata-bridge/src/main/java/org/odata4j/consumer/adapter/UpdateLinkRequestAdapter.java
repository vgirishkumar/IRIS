package org.odata4j.consumer.adapter;

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
