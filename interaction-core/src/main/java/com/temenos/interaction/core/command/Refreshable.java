package com.temenos.interaction.core.command;

/**
 *
 * @author andres
 */
public interface Refreshable<T> {
  public void refresh(T context);    
}
