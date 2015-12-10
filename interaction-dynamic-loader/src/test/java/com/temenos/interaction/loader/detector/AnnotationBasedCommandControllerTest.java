package com.temenos.interaction.loader.detector;

import com.temenos.interaction.core.command.AnnotationBasedCommandController;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.loader.classloader.ParentLastURLClassloader;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author hmanchala
 */
public class AnnotationBasedCommandControllerTest {

    @Test
    public void testExistenceOfAnnotatedCommandInTheController() throws MalformedURLException {

        File jarFile = new File("src/test/jars/AnnotatedTestInteractionCommandClasses.jar");
        ClassLoader classloader = new ParentLastURLClassloader(new URL[]{jarFile.toURI().toURL()}, Thread.currentThread().getContextClassLoader());

        AnnotationBasedCommandController controller = new AnnotationBasedCommandController();
        controller.setClassloader(classloader);
        controller.setJarsToScan(Collections.singleton(jarFile.toURI().toURL()));
        Assert.assertNotNull("Not found a commad that shopuld be there",controller.fetchCommand("testName1"));  // this one was annotated, and is an InteractionCommand - should be there
        Assert.assertNull("Found a class that is annotated, but should be excluded / not an InteractionCommand",controller.fetchCommand("testName3")); // this one was annotated, but is not InteractionCommand - filtered out silently
    }

    @Test
    public void testNumberOfCommandsDetectedByAnnotations() throws MalformedURLException {

        File jarFile = new File("src/test/jars/AnnotatedTestInteractionCommandClasses.jar");
        ClassLoader classloader = new ParentLastURLClassloader(new URL[]{jarFile.toURI().toURL()}, Thread.currentThread().getContextClassLoader());

        CacheExposingAnnotationBasedCommandController controller = new CacheExposingAnnotationBasedCommandController();
        controller.setClassloader(classloader);
        controller.setJarsToScan(Collections.singleton(jarFile.toURI().toURL()));

        Assert.assertNotNull(controller.fetchCommand("testName1")); // ensure cache is initialized

        Assert.assertEquals("The JAR was prepared with 2 annotated InteractionCommands, however different number was detected",2, controller.getCachedCommands().entrySet().size());
    }

    public static class CacheExposingAnnotationBasedCommandController extends AnnotationBasedCommandController {

        public Map<String, InteractionCommand> getCachedCommands() {
            return cache;
        }
    }
}
