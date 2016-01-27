package com.temenos.interaction.rimdsl.generator.launcher;

import com.temenos.interaction.core.loader.ResourceStateLoader;
import org.junit.Test;

import java.util.List;

import static com.temenos.interaction.core.loader.ResourceStateLoader.ResourceStateResult;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * @author kwieconkowski
 */
public class RIMResourceStateLoaderTest {

    @Test
    public void testLoad() {
        ResourceStateLoader<String> loader = new ThroughPrdRIMResourceStateLoader();
        List<ResourceStateResult> resourceStateList = loader.load("Airline.rim");
        assertNotNull(resourceStateList);
        assertFalse(resourceStateList.isEmpty());
    }

    @Test
    public void testLoad_Pure() {
        ResourceStateLoader<String> loader = new RIMResourceStateLoader();
        List<ResourceStateResult> resourceStateList = loader.load("Airline.rim");
        assertNotNull(resourceStateList);
        assertFalse(resourceStateList.isEmpty());
    }
}