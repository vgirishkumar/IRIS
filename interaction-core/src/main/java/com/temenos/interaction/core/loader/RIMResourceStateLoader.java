/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.temenos.interaction.core.loader;

import java.util.List;

/**
 *
 * @author andres
 */
public class RIMResourceStateLoader implements ResourceStateLoader<String> {

    @Override
    public List<ResourceStateResult> load(String rimFileName) {
        if(rimFileName == null || rimFileName.isEmpty()) {
            throw new IllegalArgumentException("RIM file name empty");
        }

    }
    
}
