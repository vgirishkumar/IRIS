package com.temenos.interaction.rimdsl.generator.launcher;

/**
 * Implementations of this interface will receive notifications of validation events
 *  
 * @author mlambert
 */
public interface ValidatorEventListener {
	public void notify(String msg);
}
