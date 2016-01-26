package com.temenos.interaction.rimdsl.generator.launcher;

import com.temenos.interaction.core.hypermedia.ResourceState;
import org.junit.Test;

import java.util.List;

/**
 * @author kwieconkowski
 */
public class RIMResourceStateLoaderTest {

    @Test
    public void testLoad() throws Exception {
        RIMResourceStateLoader loader = new RIMResourceStateLoader();
        List<ResourceState> resourceStateList = loader.load("Airline.rim");
        int i = 4;
    }
}