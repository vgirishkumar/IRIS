/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.temenos.interaction.core.command;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author andres
 */
public class DefaultRefreshableCommandController         
        extends NewCommandController
implements RefreshableWithMapCommandController {

    @Override
    public void refresh(Map<String, InteractionCommand> context) {
        commands = new HashMap(context);
    }
    
}
