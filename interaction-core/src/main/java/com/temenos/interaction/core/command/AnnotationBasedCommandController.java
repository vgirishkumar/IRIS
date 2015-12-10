package com.temenos.interaction.core.command;

import com.temenos.interaction.core.command.annotation.InteractionCommandImpl;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.BeanUtils;

/**
 * Implementation of {@link CommandController} based on Annotation. Using
 * Reflection will scan the classes in JAR's & Packages for required annotation.
 * Resolution is based on the name attribute of the annotation.
 *
 * @author hmanchala
 */
public class AnnotationBasedCommandController implements CommandController {

    protected Map<String, InteractionCommand> cache = new HashMap();
    private ClassLoader classloader = null;
    private Collection<String> packagesToScan = null;
    private Collection<URL> jarsToScan = null;
    private Reflections reflectionsHelper = null;
    private boolean initialized = false;

    public AnnotationBasedCommandController() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public AnnotationBasedCommandController(ClassLoader classloader) {
        this.classloader = classloader;
    }

    /**
     * @param name
     * @return If the command is already in the cache it is returned else using
     * reflection scan the JARs/Packages and get the required annotated classes
     * which have implemented the interface, and return the object by calling
     * BeanUtils.instantiate() method
     */
    @Override
    public InteractionCommand fetchCommand(String name) {
        if (!initialized) {
            reinitialize();
        }
        return cache.get(name);
    }

    protected synchronized void reinitialize() {

        ConfigurationBuilder config = new ConfigurationBuilder();
        if (getJarsToScan() != null && (!jarsToScan.isEmpty())) {
            config.setUrls(getJarsToScan());
        }
        if (getClassloader() != null) {
            config.addClassLoader(getClassloader());
        }
        if (getPackagesToScan() != null && (!packagesToScan.isEmpty())) {
            config.forPackages(getPackagesToScan().toArray(new String[]{}));
        }
        reflectionsHelper = new Reflections(config);

        Set<Class<?>> annotatedClasses = reflectionsHelper.getTypesAnnotatedWith(InteractionCommandImpl.class);

        for (Class<?> annotatedClass : annotatedClasses) {
            String nameFound = annotatedClass.getAnnotation(InteractionCommandImpl.class).name();
            Object newCommandAsObject = BeanUtils.instantiate(annotatedClass);
            if (newCommandAsObject instanceof InteractionCommand) {
                InteractionCommand newCommand = (InteractionCommand) newCommandAsObject;
                cache.put(nameFound, newCommand);
            }
        }
        initialized = true;
    }

    @Override
    public boolean isValidCommand(String name) {
        return (fetchCommand(name) != null);
    }

    public ClassLoader getClassloader() {
        return classloader;
    }

    public void setClassloader(ClassLoader classloader) {
        this.classloader = classloader;
        initialized = false;
    }

    public Collection<String> getPackagesToScan() {
        return packagesToScan;
    }

    public void setPackagesToScan(Collection<String> packagesToScan) {
        this.packagesToScan = packagesToScan;
        initialized = false;
    }

    public Collection<URL> getJarsToScan() {
        return jarsToScan;
    }

    public void setJarsToScan(Collection<URL> jarsToScan) {
        this.jarsToScan = jarsToScan;
        initialized = false;
    }
}
