package org.odata4j.consumer.adapter;

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
