package com.temenos.interaction.media.xhtml;

/*
 * #%L
 * interaction-media-xhtml
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


import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.LocalTime;
import org.junit.Test;

import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.entity.vocabulary.Vocabulary;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexType;
import com.temenos.interaction.core.entity.vocabulary.terms.TermIdField;
import com.temenos.interaction.core.resource.EntityResource;

public class TestEntityResourceWrapperXHTML {

	@Test
	public void testLocalTime() {
		EntityMetadata vocs = new EntityMetadata("Test");
		Vocabulary vocName = new Vocabulary();
		vocName.setTerm(new TermComplexType(false));
		vocName.setTerm(new TermIdField(true));
		vocs.setPropertyVocabulary("time", vocName);
		
		Set<String> propertyNames = new HashSet<String>();
		propertyNames.add("time");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("time", new LocalTime(1));
		EntityResource<Map<String, Object>> entityResource = new EntityResource<Map<String, Object>>(map);
		
		EntityResourceWrapperXHTML er = new EntityResourceWrapperXHTML(
				vocs, propertyNames, entityResource);
		List<String> properties = er.getEntityProperties();
		assertEquals(1, properties.size());
		assertEquals("01:00:00.001", properties.get(0));
	}

}
