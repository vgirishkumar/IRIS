package org.odata4j.consumer.adapter;

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
