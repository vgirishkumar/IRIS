package com.temenos.interaction.rimdsl.runtime.loader;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.loader.Action;
import com.temenos.interaction.core.loader.FileEvent;

/**
 * TODO: Document me!
 *
 *
 */
public class ResourceSetGeneratorAction implements Action<FileEvent<File>>, ApplicationContextAware, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ResourceSetGeneratorAction.class);
    
    private ResourceStateMachine resourceStateMachine;
    private TranslatorDrivenResourceStateProvider resourceStateProvider;
    private ApplicationContext currentContext = null;
    private ApplicationContext parentContext = null;
    private boolean useCurrentContextAsParent = false;
    private Collection<? extends Action<ApplicationContext>> listeners = new ArrayList<Action<ApplicationContext>>();

    @Override
    public void execute(FileEvent<File> dirEvent) {
        logger.debug("Creation of new ResourceSetGeneratorAction triggerred by change in", dirEvent.getResource().getAbsolutePath());
        Collection<File> rims = FileUtils.listFiles(dirEvent.getResource(), new String[]{"rim"}, true);
        Collection<ResourceState> resourceStates = this.generateResourceStatesFromRIMFiles(rims);
        for(ResourceState resourceState : resourceStates){
            this.registerResourceStatesWithResourceStateMachine(resourceState);
        }
    }

    private void registerResourceStatesWithResourceStateMachine(ResourceState resourceState){
        for(String resourceStateMethod : this.resourceStateProvider.getResourceMethodsByState().get(resourceState.getName())){
            this.resourceStateMachine.register(resourceState, resourceStateMethod);
        }
    }
    
    /**
     * @param rims
     * @param resourceStates
     */
    private Collection<ResourceState> generateResourceStatesFromRIMFiles(Collection<File> rims) {
        Collection<ResourceState> resourceStates = new HashSet<ResourceState>();
        for (File f : rims) {
            URI uri = f.toURI(); 
            logger.debug("Adding {} to list of URIs to create ApplicationContext from", uri.getPath());
            resourceStates.addAll(this.resourceStateProvider.getAllResourceStates(uri));
        }
        return resourceStates;
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        currentContext = ac;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (parentContext == null && currentContext != null && useCurrentContextAsParent) {
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
        this.listeners = new ArrayList<Action<ApplicationContext>>(listeners);
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
    
    public void setResourceStateMachine(ResourceStateMachine resourceStateMachine){
        this.resourceStateMachine = resourceStateMachine;
    }
    
    public void setResourceStateProvider(TranslatorDrivenResourceStateProvider resourceStateProvider){
        this.resourceStateProvider = resourceStateProvider;
    }
}
