package com.temenos.interaction.core.command;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

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

    @Override
    public boolean isValidCommand(String name) {
        if (applicationContext == null) {
            return false;
        }
        try {
            return (applicationContext.getBean(name, InteractionCommand.class) != null);
        } catch (BeansException ex) {
            return false;
        }
    }

}
