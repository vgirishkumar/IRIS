package com.temenos.interaction.example.hateoas.dynamic;

/*
 * #%L
 * interaction-example-hateoas-dynamic
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
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


import com.temenos.interaction.core.hypermedia.ParameterAndValue;
import com.temenos.interaction.core.hypermedia.ResourceParameterResolver;
import com.temenos.interaction.core.hypermedia.ResourceParameterResolverContext;

public class AuthorResourceParameterResolver implements ResourceParameterResolver {

	@Override
	public ParameterAndValue[] resolve(Object[] aliases, ResourceParameterResolverContext context) {
		return new ParameterAndValue[]{new ParameterAndValue("id", "AU002") };
	}
}
