package com.temenos.interaction.core.hypermedia;

import java.util.List;

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


/**
 * Determine if links can be generated from a particular transition.
 * If so, return a collection of transition properties used to resolve multivalue fields.
 */
public interface LinkToFieldAssociation {
    
    /**
     * Return a list of transition properties that is used to resolve multivalue fields.
     * The size of the list determines the number of links to generate for each underlying transition.
     * @return
     */
    List<LinkTransitionProperties> getTransitionProperties();
    
    /**
     * Determine whether the transition is supported by the implementation.
     * @return
     */
    boolean isTransitionSupported();
    
}
