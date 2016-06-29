/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.temenos.interaction.loader.detector;

/*
 * #%L
 * interaction-dynamic-loader
 * %%
 * Copyright (C) 2012 - 2015 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.loader.Action;

/**
 * Closes a ClassLoader marked to be closed and releases any system resources associated with it.
 * 
 * The ClassLoader needs to be an instance of Closeable.
 * 
 * @author andres
 * @author trojan
 */
public class ClassLoaderClosingAction implements Action<ClassLoader> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassLoaderClosingAction.class);

    ClassLoader previous = null;

    @Override
    public void execute(ClassLoader toClose) {
        try {
            if (previous != null) {
                ((Closeable) previous).close();
            }
        } catch (IOException ex) {
            LOGGER.error("Error closing the class loader.", ex);
        }
        if (!(toClose instanceof Closeable)) {
            previous = null;
        } else {
            previous = toClose;
        }
    }

}
