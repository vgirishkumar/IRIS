package com.temenos.interaction.loader.classloader;

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


import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

public class ParentLastURLClassloader extends URLClassLoader {

    public ParentLastURLClassloader(URL[] urls, ClassLoader cl, URLStreamHandlerFactory urlshf) {
        super(urls, cl, urlshf);
    }

    public ParentLastURLClassloader(URL[] urls, ClassLoader cl) {
        super(urls, cl);
    }

    public ParentLastURLClassloader(URL[] urls) {
        super(urls);
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // check if loaded locally
        Class<?> result = findLoadedClass(name);
        if (result == null) {
            try {
                // search locally 
                result = findClass(name);
            } catch (ClassNotFoundException ex) {
                // Next, delegate to the parent, if not found locally.
                try {
                result = getParent().loadClass(name);
                } catch (Throwable t) {
                    System.out.println(t);
                }
            }
        }

        if (resolve) {
            resolveClass(result);
        }
        return result;
    }
}
