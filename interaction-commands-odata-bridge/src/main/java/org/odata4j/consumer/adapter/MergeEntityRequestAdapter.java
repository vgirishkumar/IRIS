package org.odata4j.consumer.adapter;

import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OModifyRequest;
import org.odata4j.core.OProperty;

public class MergeEntityRequestAdapter<T> implements OModifyRequest<T> {

  @Override
  public OModifyRequest<T> properties(OProperty<?>... props) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public OModifyRequest<T> properties(Iterable<OProperty<?>> props) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public OModifyRequest<T> link(String navProperty, OEntity target) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public OModifyRequest<T> link(String navProperty, OEntityKey targetKey) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void execute() {
    // TODO Auto-generated method stub
  }

  @Override
  public OModifyRequest<T> nav(String navProperty, OEntityKey key) {
    // TODO Auto-generated method stub
    return null;
  }

@Override
public OModifyRequest<T> ifMatch(String precondition) {
	// TODO Auto-generated method stub
	return null;
}

}
