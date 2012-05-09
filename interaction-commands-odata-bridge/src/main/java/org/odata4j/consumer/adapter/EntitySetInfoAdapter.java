package org.odata4j.consumer.adapter;

import org.odata4j.core.EntitySetInfo;

public class EntitySetInfoAdapter implements EntitySetInfo {

  private String entitySetName;

  public EntitySetInfoAdapter(String entitySetName) {
    super();
    this.entitySetName = entitySetName;
  }

  @Override
  public String getTitle() {
    return entitySetName;
  }

  @Override
  public String getHref() {
    return entitySetName;
  }

}