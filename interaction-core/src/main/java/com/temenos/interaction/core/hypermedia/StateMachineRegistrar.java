package com.temenos.interaction.core.hypermedia;

public interface StateMachineRegistrar {
	public void registerResourceStateResult(ResourceState resourceState, String method);
	public void setResourceStateMachine(ResourceStateMachine resourceStateMachine);
}
