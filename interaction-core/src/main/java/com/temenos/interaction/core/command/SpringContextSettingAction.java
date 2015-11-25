/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.temenos.interaction.core.command;

import com.temenos.interaction.core.loader.Action;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author andres
 */
public class SpringContextSettingAction implements Action<ApplicationContext> {
    
    private SpringContextBasedInteractionCommandController commandController = null;

    @Override
    public void execute(ApplicationContext ctx) {
        if (commandController!=null) {
            commandController.setApplicationContext(ctx);
        }
    }

    public SpringContextBasedInteractionCommandController getCommandController() {
        return commandController;
    }

    public void setCommandController(SpringContextBasedInteractionCommandController commandController) {
        this.commandController = commandController;
    }
    
}
