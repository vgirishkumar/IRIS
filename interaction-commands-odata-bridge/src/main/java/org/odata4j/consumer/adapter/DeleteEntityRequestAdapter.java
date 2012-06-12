package org.odata4j.consumer.adapter;

import org.odata4j.core.OEntityKey;
import org.odata4j.core.OEntityRequest;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.exceptions.NotImplementedException;

public class DeleteEntityRequestAdapter<T> extends
        AbstractOEntityRequestAdapter<T> {

  public DeleteEntityRequestAdapter(ODataProducer producer,
          String entitySetName, OEntityKey entityKey) {
    super(producer, entitySetName, entityKey);
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
    producer.deleteEntity(entitySetName, entityKey);
    return null;
  }

}
