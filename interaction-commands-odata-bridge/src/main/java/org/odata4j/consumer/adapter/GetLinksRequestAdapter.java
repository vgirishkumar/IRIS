package org.odata4j.consumer.adapter;

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
