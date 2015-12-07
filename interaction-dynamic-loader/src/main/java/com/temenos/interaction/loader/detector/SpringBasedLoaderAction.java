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

import com.temenos.interaction.core.command.ChainingCommandController;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.SpringContextBasedInteractionCommandController;
import com.temenos.interaction.core.loader.Action;
import com.temenos.interaction.core.loader.FileEvent;
import com.temenos.interaction.loader.objectcreation.ParameterizedFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author andres
 */
public class SpringBasedLoaderAction implements Action<FileEvent<File>>, ApplicationContextAware, InitializingBean {

    public final static String DEFAULT_COMMAND_CONTROLLER_BEAN_NAME = "commandController";
        
    List<String> configPatterns = new ArrayList();
    private ApplicationContext currentContext = null;
    private ApplicationContext parentContext = null;
    private boolean useCurrentContextAsParent = false;
    private List<String> configLocationsPatterns = new ArrayList(Collections.singletonList("/spring/*-interaction-context.xml"));
    private Collection<? extends Action<ApplicationContext>> listeners = new ArrayList();
    private ParameterizedFactory<FileEvent<File>, ClassLoader> classloaderFactory = new CurrentThreadClassLoaderFactory();
    private String commandControllerBeanName = DEFAULT_COMMAND_CONTROLLER_BEAN_NAME;
    private ChainingCommandController parentChainingCommandController = null;


    @Override
    public void execute(FileEvent<File> dirEvent) {
        ClassPathXmlApplicationContext context;
        if (useCurrentContextAsParent && (parentContext != null)) {
            context = new ClassPathXmlApplicationContext(parentContext);
        } else {
            context = new ClassPathXmlApplicationContext();
        }
        context.setConfigLocations(configLocationsPatterns.toArray(new String[]{}));
        context.setClassLoader(classloaderFactory.getForObject(dirEvent));
        context.refresh();
        
        CommandController cc = null;

        try {
            cc = context.getBean(commandControllerBeanName, CommandController.class);
        } catch (BeansException ex) {
            Map<String,InteractionCommand> commands = context.getBeansOfType(InteractionCommand.class);
            if (!commands.isEmpty()) {
                SpringContextBasedInteractionCommandController scbcc = new SpringContextBasedInteractionCommandController();
                scbcc.setApplicationContext(context);
                cc = scbcc;
            }
        }
        
        if (cc!=null && parentChainingCommandController!=null) {
            List<CommandController> newCommandControllers = new ArrayList<CommandController>(parentChainingCommandController.getCommandControllers());
            newCommandControllers.add(0,cc);
            parentChainingCommandController.setCommandControllers(newCommandControllers);
        }
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

    /**
     * @return the classloaderFactory
     */
    public ParameterizedFactory<FileEvent<File>, ClassLoader> getClassloaderFactory() {
        return classloaderFactory;
    }

    /**
     * @param classloaderFactory the classloaderFactory to set
     */
    public void setClassloaderFactory(ParameterizedFactory<FileEvent<File>, ClassLoader> classloaderFactory) {
        this.classloaderFactory = classloaderFactory;
    }

    /**
     * @return the currentContext
     */
    public ApplicationContext getCurrentContext() {
        return currentContext;
    }

    /**
     * @param currentContext the currentContext to set
     */
    public void setCurrentContext(ApplicationContext currentContext) {
        this.currentContext = currentContext;
    }

    /**
     * @return the parentContext
     */
    public ApplicationContext getParentContext() {
        return parentContext;
    }

    /**
     * @param parentContext the parentContext to set
     */
    public void setParentContext(ApplicationContext parentContext) {
        this.parentContext = parentContext;
    }

    /**
     * @return the useCurrentContextAsParent
     */
    public boolean isUseCurrentContextAsParent() {
        return useCurrentContextAsParent;
    }

    /**
     * @param useCurrentContextAsParent the useCurrentContextAsParent to set
     */
    public void setUseCurrentContextAsParent(boolean useCurrentContextAsParent) {
        this.useCurrentContextAsParent = useCurrentContextAsParent;
    }

    /**
     * @return the configLocationsPatterns
     */
    public List<String> getConfigLocationsPatterns() {
        return configLocationsPatterns;
    }

    /**
     * @param configLocationsPatterns the configLocationsPatterns to set
     */
    public void setConfigLocationsPatterns(List<String> configLocationsPatterns) {
        this.configLocationsPatterns = configLocationsPatterns;
    }

    /**
     * @return the commandControllerBeanName
     */
    public String getCommandControllerBeanName() {
        return commandControllerBeanName;
    }

    /**
     * @param commandControllerBeanName the commandControllerBeanName to set
     */
    public void setCommandControllerBeanName(String commandControllerBeanName) {
        this.commandControllerBeanName = commandControllerBeanName;
    }

    /**
     * @return the parentChainingCommandController
     */
    public ChainingCommandController getParentChainingCommandController() {
        return parentChainingCommandController;
    }

    /**
     * @param parentChainingCommandController the parentChainingCommandController to set
     */
    public void setParentChainingCommandController(ChainingCommandController parentChainingCommandController) {
        this.parentChainingCommandController = parentChainingCommandController;
    }

    public static class CurrentThreadClassLoaderFactory implements ParameterizedFactory<FileEvent<File>, ClassLoader> {

        public CurrentThreadClassLoaderFactory() {
        }

        @Override
        public ClassLoader getForObject(FileEvent<File> param) {
            return Thread.currentThread().getContextClassLoader();
        }
    }

}
