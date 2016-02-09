package com.temenos.interaction.odataext.odataparser.data;

/* 
 * #%L
 * interaction-odata4j-ext
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
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.odata4j.expression.BoolCommonExpression;
import org.odata4j.producer.resources.OptionsQueryParser;

public class AccessProfileTest {

    @Test
    public void testConstruct() {
        BoolCommonExpression filterStr = OptionsQueryParser.parseFilter("aname eq avalue");
        RowFilters expectedFilters = new RowFilters(filterStr);
        
        Set<FieldName> expectedSelects = new HashSet<FieldName>();
        
        AccessProfile profile = new AccessProfile(expectedFilters, expectedSelects);

        assertEquals(expectedFilters, profile.getNewRowFilters());
        assertEquals(expectedSelects, profile.getFieldNames());
    }
}
