/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.temenos.interaction.core.command;

import java.util.Map;

/**
 *
 * @author andres
 */
public interface RefreshableWithMapCommandController extends CommandController, Refreshable<Map<String,InteractionCommand>> {
    
}
