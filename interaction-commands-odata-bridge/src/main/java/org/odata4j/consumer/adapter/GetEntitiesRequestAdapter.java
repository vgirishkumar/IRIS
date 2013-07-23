package org.odata4j.consumer.adapter;

import java.util.ArrayList;
import java.util.List;

import org.core4j.Enumerable;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.exceptions.NotImplementedException;
import org.odata4j.internal.EntitySegment;
import org.odata4j.internal.InternalUtil;
import org.odata4j.producer.BaseResponse;
import org.odata4j.producer.EntitiesResponse;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.QueryInfo;

public class GetEntitiesRequestAdapter<T> extends
        AbstractOQueryRequestAdapter<T> {

  private Class<T> entityType;
  private String entitySetHref;

  public GetEntitiesRequestAdapter(ODataProducer producer,
          String serviceRootUri, Class<T> entityType, String entitySetHref) {
    super(producer, serviceRootUri);
    this.entityType = entityType;
    this.entitySetHref = entitySetHref;
  }

  @SuppressWarnings("unchecked")
@Override
  public Enumerable<T> execute() {
    QueryInfo queryInfo = buildQueryInfo();

    if (segments.isEmpty()) {
      EntitiesResponse entityResponse = getProducer().getEntities(
              entitySetHref, queryInfo);
      return convertList(entityResponse.getEntities());

    } else {

      for (EntitySegment segment : segments) {
        OEntityKey entityKey = segment.key;
        String navProp = segment.segment;
        BaseResponse response = getProducer().getNavProperty(entitySetHref,
                entityKey, navProp, queryInfo);
        if (response == null) {
          return null;
        }
        if (response instanceof EntitiesResponse) {
          return convertList(((EntitiesResponse) response).getEntities());
        } else if (response instanceof EntityResponse) {
          return Enumerable
                  .create((T) (((EntityResponse) response).getEntity()));
        } else {
          throw new NotImplementedException("Response class "
                  + response.getClass().getName() + " not supported yet.");
        }
      }
      throw new NotImplementedException("Not supported yet.");
    }

  }

  private Enumerable<T> convertList(List<OEntity> entities) {
    List<T> result = new ArrayList<T>();
    for (OEntity entity : entities) {
      result.add(InternalUtil.toEntity(entityType, entity));
    }
    return Enumerable.create(result);
  }

}
