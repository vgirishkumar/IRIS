/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.temenos.interaction.loader.detector;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 *
 * @author andres
 */
public class LocalFirstURLClassLoader extends URLClassLoader {
    
    public LocalFirstURLClassLoader(URL[] urls, ClassLoader cl) {
        super(urls, cl);
    }
    
    public LocalFirstURLClassLoader(URL[] urls) {
        super(urls);
    }
    
    public LocalFirstURLClassLoader(URL[] urls, ClassLoader cl, URLStreamHandlerFactory shf) {
        super(urls, cl, shf);
    }
    
    
    
}
