/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.temenos.interaction.loader.detector;

import com.temenos.interaction.core.loader.Action;
import java.io.Closeable;
import java.io.IOException;

/**
 *
 * @author andres
 */
public class ClassLoaderClosingAction implements Action<ClassLoader> {

    @Override
    public void execute(ClassLoader toClose) {
        if (!(toClose instanceof Closeable)) {
            return;
        }
        
        try {
            ((Closeable) toClose).close();
        } catch (IOException ex) {
            // TODO: log properly
        }
    }

}
