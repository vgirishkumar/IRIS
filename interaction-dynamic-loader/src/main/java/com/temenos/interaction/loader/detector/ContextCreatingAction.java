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

import com.temenos.interaction.core.loader.Action;
import com.temenos.interaction.core.loader.FileEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 *
 * @author andres
 */
public class ContextCreatingAction implements Action<ClassLoader>, ApplicationContextAware, InitializingBean {

    List<String> configPatterns = new ArrayList();
    ApplicationContext currentContext = null;
    ApplicationContext parentContext = null;
    boolean useCurrentContextAsParent = false;
    List<String> configLocationsPatterns = new ArrayList();
    private Collection<? extends Action<ApplicationContext>> listeners = new ArrayList();


    @Override
    public void execute(ClassLoader fe) {
        ClassPathXmlApplicationContext context;
        if (parentContext != null) {
            context = new ClassPathXmlApplicationContext(parentContext);
        } else {
            context = new ClassPathXmlApplicationContext();
        }
        context.setConfigLocations(configLocationsPatterns.toArray(new String[]{}));
        context.setClassLoader(fe);
        context.refresh();
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        currentContext = ac;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (parentContext==null && currentContext!=null && useCurrentContextAsParent) {
            parentContext = currentContext;
        }
    }

    /**
     * @return the listeners
     */
    public Collection<? extends Action<ApplicationContext>> getListeners() {
        return listeners;
    }

    /**
     * @param listeners the listeners to set
     */
    public void setListeners(Collection<? extends Action<ApplicationContext>> listeners) {
        this.listeners = new ArrayList(listeners);
    }

}
