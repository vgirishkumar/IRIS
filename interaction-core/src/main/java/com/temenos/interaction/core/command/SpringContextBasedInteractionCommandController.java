package com.temenos.interaction.core.command;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextBasedInteractionCommandController
		implements CommandControllerInterface {

	private ApplicationContext applicationContext = null;

	@Override
	public InteractionCommand fetchCommand(String name) {
		if (applicationContext == null) {
			return null;
		}
		try {
			return applicationContext.getBean(name, InteractionCommand.class);
		} catch (BeansException ex) {
			return null;
		}
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}

}
