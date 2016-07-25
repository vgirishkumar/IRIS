package com.temenos.interaction.core.loader;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
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

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.temenos.interaction.core.hypermedia.ResourceState;

/**
 * Loads a list of ResourceState from a prd name, which should be a filename
 * without a path. This is for compatibility with the Spring class
 * ClassPathXmlApplicationContext.
 *
 * @author kwieconkowski
 * @author andres
 * @author dgroves
 */
public class SpringResourceStateLoadingStrategy implements ResourceStateLoadingStrategy<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringResourceStateLoadingStrategy.class);
    private static final String MSG_NAME_BLANK_OR_NULL = "Passed PRD file name is NULL or empty";
    private static final String MSG_PATH_IN_NAME = "Spring PRD file name must not contain the path";

    @Override
    public List<ResourceStateResult> load(String nameOfSpringFile) {
        validateSpringNameOtherwiseThrowException(nameOfSpringFile);
        ApplicationContext PrdAppCtx = loadSpringContext(nameOfSpringFile);
        
        if (PrdAppCtx == null) {
            LOGGER.warn("File not found while loading spring configuration in name: " + nameOfSpringFile);
            return null;
        }
        
        List<ResourceStateResult> resourceStates = new ArrayList<ResourceStateResult>();
        
        for (Map.Entry<String, ResourceState> springBean : PrdAppCtx.getBeansOfType(ResourceState.class).entrySet()) {
            resourceStates.add(new ResourceStateResult(springBean.getKey(), springBean.getValue()));
        }
        
        LOGGER.info("Resource states loaded from spring configuration xml: " + nameOfSpringFile);
        
        return resourceStates;
    }

    private ApplicationContext loadSpringContext(String nameOfSpringFile) {
        ApplicationContext PrdAppCtx = null;
        
        try {
            PrdAppCtx = new ClassPathXmlApplicationContext(nameOfSpringFile);
        } catch (Exception e) {
            LOGGER.error("Failed to create context from: " + nameOfSpringFile, e);
        }
        
        return PrdAppCtx;
    }

    private void validateSpringNameOtherwiseThrowException(String nameOfSpringFile) {
        if (nameOfSpringFile == null || nameOfSpringFile.isEmpty()) {
            LOGGER.error(MSG_NAME_BLANK_OR_NULL);
            
            throw new IllegalArgumentException(MSG_NAME_BLANK_OR_NULL);
        } else if (!Paths.get(nameOfSpringFile).getFileName().toString().equals(nameOfSpringFile)) {
            LOGGER.error(MSG_PATH_IN_NAME);
            
            throw new IllegalArgumentException(MSG_PATH_IN_NAME);
        }
    }
}