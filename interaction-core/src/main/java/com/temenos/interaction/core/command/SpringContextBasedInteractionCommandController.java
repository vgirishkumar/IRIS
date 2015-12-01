package com.temenos.interaction.core.command;

/*
 * #%L
 * interaction-core
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


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public class SpringContextBasedInteractionCommandController
        implements CommandControllerInterface {

    private ApplicationContext applicationContext = null;

    @Override
    public InteractionCommand fetchCommand(String name) {
        if (applicationContext == null) {
            return null;
        }
        try {
            return applicationContext.getBean(name, InteractionCommand.class);
        } catch (BeansException ex) {
            return null;
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public boolean isValidCommand(String name) {
        if (applicationContext == null) {
            return false;
        }
        try {
            return (applicationContext.getBean(name, InteractionCommand.class) != null);
        } catch (BeansException ex) {
            return false;
        }
    }

}
