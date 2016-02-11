package com.temenos.interaction.authorization;

/*
 * Interface for authentication beans.
 */

/*
 * #%L
 * interaction-authorization
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
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

import java.util.List;
import java.util.Set;

import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.odataext.odataparser.data.AccessProfile;
import com.temenos.interaction.odataext.odataparser.data.FieldName;
import com.temenos.interaction.odataext.odataparser.data.RowFilter;
import com.temenos.interaction.odataext.odataparser.data.RowFilters;

public interface IAuthorizationProvider {

    /**
     * This method will return the authorized AccessProfile for a current logged
     * in user.
     * 
     * @param ctx
     */
    public AccessProfile getAccessProfile(InteractionContext ctx) throws InteractionException;

    /*
     * Get the row filter, for the current principle, in new filter format.
     * 
     * An empty list means do no filtering i.e. return all rows. This is
     * represented by a missing $filter term.
     */
    public RowFilters getNewFilters(InteractionContext ctx) throws InteractionException;

    /*
     * Get the row filter for the current principle
     * 
     * An empty list means do no filtering i.e. return all rows. This is
     * represented by a missing $filter term.
     */
    @Deprecated
    public List<RowFilter> getFilters(InteractionContext ctx) throws InteractionException;

    /*
     * Get the select for the current principle.
     * 
     * An empty list means do no selecting i.e. return all columns. This is
     * represented by a missing $select term.
     */
    public Set<FieldName> getSelect(InteractionContext ctx) throws InteractionException;

}