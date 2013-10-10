package org.odata4j.consumer;

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
import java.util.List;

import org.core4j.Enumerable;
import org.odata4j.consumer.adapter.CallFunctionRequestAdapter;
import org.odata4j.consumer.adapter.CreateEntityRequestAdapter;
import org.odata4j.consumer.adapter.CreateLinkRequestAdapter;
import org.odata4j.consumer.adapter.DeleteEntityRequestAdapter;
import org.odata4j.consumer.adapter.DeleteLinkRequestAdapter;
import org.odata4j.consumer.adapter.EntitySetInfoAdapter;
import org.odata4j.consumer.adapter.GetEntitiesRequestAdapter;
import org.odata4j.consumer.adapter.GetEntityRequestAdapter;
import org.odata4j.consumer.adapter.GetLinksRequestAdapter;
import org.odata4j.consumer.adapter.MergeEntityRequestAdapter;
import org.odata4j.consumer.adapter.UpdateEntityRequestAdapter;
import org.odata4j.consumer.adapter.UpdateLinkRequestAdapter;
import org.odata4j.core.EntitySetInfo;
import org.odata4j.core.OCreateRequest;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityDeleteRequest;
import org.odata4j.core.OEntityGetRequest;
import org.odata4j.core.OEntityId;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OEntityRequest;
import org.odata4j.core.OFunctionRequest;
import org.odata4j.core.OModifyRequest;
import org.odata4j.core.OObject;
import org.odata4j.core.OQueryRequest;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.producer.ODataProducer;

/**
 * The goal of the class is to implement an ODataConsumer adapter over an
 * ODataProducer. This is to prevent any network access. It can also be useful
 * in unit tests.
 * 
 * @author svt
 * 
 */
public class ODataConsumerAdapter extends AbstractODataConsumer implements
        ODataConsumer {

  private static final String SERVICE_URI = "LOCAL";
  private final ODataProducer producer;

  public ODataConsumerAdapter(ODataProducer producer) {
    super(SERVICE_URI);
    this.producer = producer;
  }

  @Override
  public Enumerable<EntitySetInfo> getEntitySets() {
    Iterable<EdmEntitySet> sets = producer.getMetadata().getEntitySets();
    List<EntitySetInfo> result = new ArrayList<EntitySetInfo>();
    for (EdmEntitySet edmEntitySet : sets) {
      EntitySetInfo setInfo = new EntitySetInfoAdapter(edmEntitySet.getName());
      result.add(setInfo);
    }
    return Enumerable.create(result);
  }

  @Override
  public EdmDataServices getMetadata() {
    return producer.getMetadata();
  }

  @Override
  public <T> OQueryRequest<T> getEntities(Class<T> entityType,
          String entitySetHref) {
    GetEntitiesRequestAdapter<T> request = new GetEntitiesRequestAdapter<T>(
            producer, getServiceRootUri(), entityType, entitySetHref);
    return request;
  }

  @Override
  public OQueryRequest<OEntityId> getLinks(OEntityId sourceEntity,
          String targetNavProp) {
    GetLinksRequestAdapter<OEntityId> request = new GetLinksRequestAdapter<OEntityId>(
            producer, getServiceRootUri(), sourceEntity, targetNavProp);
    return request;
  }

  @Override
  public <T> OEntityGetRequest<T> getEntity(Class<T> entityType,
          String entitySetName, OEntityKey key) {
    GetEntityRequestAdapter<T> request = new GetEntityRequestAdapter<T>(
            producer, entityType, entitySetName, key);
    return request;
  }

  @Override
  public OEntityRequest<Void> createLink(OEntityId sourceEntity,
          String targetNavProp, OEntityId targetEntity) {
    CreateLinkRequestAdapter<Void> request = new CreateLinkRequestAdapter<Void>(
            producer, sourceEntity, targetNavProp, targetEntity);
    return request;
  }

  @Override
  public OEntityRequest<Void> deleteLink(OEntityId sourceEntity,
          String targetNavProp, Object... targetKeyValues) {
    DeleteLinkRequestAdapter<Void> request = new DeleteLinkRequestAdapter<Void>(
            producer, sourceEntity, targetNavProp, targetKeyValues);
    return request;
  }

  @Override
  public OEntityRequest<Void> updateLink(OEntityId sourceEntity,
          OEntityId newTargetEntity, String targetNavProp,
          Object... oldTargetKeyValues) {
    UpdateLinkRequestAdapter<Void> request = new UpdateLinkRequestAdapter<Void>(
            producer, sourceEntity, newTargetEntity, targetNavProp,
            oldTargetKeyValues);
    return request;
  }

  @Override
  public OCreateRequest<OEntity> createEntity(String entitySetName) {
    CreateEntityRequestAdapter<OEntity> request = new CreateEntityRequestAdapter<OEntity>(
            producer, getServiceRootUri(), entitySetName);
    return request;
  }

  @Override
  public OModifyRequest<OEntity> updateEntity(OEntity entity) {
    UpdateEntityRequestAdapter<OEntity> request = new UpdateEntityRequestAdapter<OEntity>(
            producer, entity.getEntitySet().getName(), entity.getEntityKey());
    return request;
  }

  @Override
  public OModifyRequest<OEntity> mergeEntity(OEntity entity) {
    return mergeEntity(entity.getEntitySet().getName(), entity.getEntityKey());
  }

  @Override
  public OModifyRequest<OEntity> mergeEntity(String entitySetName,
          Object keyValue) {
    return mergeEntity(entitySetName, OEntityKey.create(keyValue));
  }

  @Override
  public OModifyRequest<OEntity> mergeEntity(String entitySetName,
          OEntityKey key) {
    MergeEntityRequestAdapter<OEntity> request = new MergeEntityRequestAdapter<OEntity>();
    return request;
  }

  @Override
  public OEntityDeleteRequest deleteEntity(OEntity entity) {
    DeleteEntityRequestAdapter<OEntityDeleteRequest> request = new DeleteEntityRequestAdapter<OEntityDeleteRequest>(
            producer, entity.getEntitySetName(), entity.getEntityKey());
    return request.execute();
   }

  @Override
  public OEntityDeleteRequest deleteEntity(String entitySetName, Object keyValue) {
	  DeleteEntityRequestAdapter<OEntityDeleteRequest> request = new DeleteEntityRequestAdapter<OEntityDeleteRequest>(			   
            producer, entitySetName, OEntityKey.create(keyValue));
	  return request.execute();
  }

  @Override
  public OEntityDeleteRequest deleteEntity(String entitySetName, OEntityKey key) {
	  DeleteEntityRequestAdapter<OEntityDeleteRequest> request = new DeleteEntityRequestAdapter<OEntityDeleteRequest>(
            producer, entitySetName, key);
	  return request.execute();
  }

  @Override
  public OFunctionRequest<OObject> callFunction(String functionName) {
    CallFunctionRequestAdapter<OObject> request = new CallFunctionRequestAdapter<OObject>(
            producer, getServiceRootUri(), functionName);
    return request;
  }

  @Override
  protected ODataClient getClient() {
	// TODO : We need to implement this
	  return null;
  }

}
