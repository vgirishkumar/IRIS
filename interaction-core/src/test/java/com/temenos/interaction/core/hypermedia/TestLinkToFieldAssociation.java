package com.temenos.interaction.core.hypermedia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;


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
 * 
 * Unit test case for {@link LinkToFieldAssociation}
 *
 */
public class TestLinkToFieldAssociation {

    @Test
    public void testNotSupportedNullTargetEmptyCollectionParams() {
        LinkToFieldAssociation linkToFieldAssociation = new LinkToFieldAssociationImpl(createTransition(null, false, new String[] { "filter", "Id={AB.CD}" }), new HashMap<String, Object>());
        assertFalse(linkToFieldAssociation.isTransitionSupported());
    }

    @Test
    public void testNotSupportedNullTargetCollectionDynamicResource() {
        LinkToFieldAssociation linkToFieldAssociation = new LinkToFieldAssociationImpl(createTransition(null, true), new HashMap<String, Object>());
        assertFalse(linkToFieldAssociation.isTransitionSupported());
    }

    @Test
    public void testNotSupportedAllCollectionParamsDifferentParent() {
        LinkToFieldAssociation linkToFieldAssociation = new LinkToFieldAssociationImpl(createTransition("targetFieldName", false, new String[] { "filter", "value is {AB.CD} or {XX.MN}" }), new HashMap<String, Object>());
        assertFalse(linkToFieldAssociation.isTransitionSupported());
    }

    @Test
    public void testIsSupported() {
        LinkToFieldAssociation linkToFieldAssociation = new LinkToFieldAssociationImpl(createTransition("targetFieldName", false, new String[] { "filter", "value is {AB.CD} or {AB.MN}" }), new HashMap<String, Object>());
        assertTrue(linkToFieldAssociation.isTransitionSupported());
    }

    @Test
    public void testSingleTargetFieldNoCollectionParam() {
        LinkToFieldAssociation linkToFieldAssociation = new LinkToFieldAssociationImpl(createTransition("targetFieldName", false), new HashMap<String, Object>());
        List<LinkTransitionProperties> transitionPropsList = linkToFieldAssociation.getTransitionProperties();
        assertEquals(1, transitionPropsList.size());
    }

    @Test
    public void testSingleTargetFieldWithCollectionParam() {
        Map<String, Object> transitionPropertiesMap = createTransitionPropertiesMap(new String[] { "AB(0).CD", "value1", "AB(1).CD", "value2" });
        LinkToFieldAssociation linkToFieldAssociation = new LinkToFieldAssociationImpl(createTransition("targetFieldName", false, new String[] { "filter", "id={AB.CD}" }), transitionPropertiesMap);
        List<LinkTransitionProperties> transitionPropsList = linkToFieldAssociation.getTransitionProperties();
        assertEquals(2, transitionPropsList.size());
        for (LinkTransitionProperties linkTransitionProps : transitionPropsList) {
            assertEquals("targetFieldName", linkTransitionProps.getTargetFieldFullyQualifiedName());

            Map<String, Object> transitionMap = linkTransitionProps.getTransitionProperties();
            assertTrue(transitionMap.containsKey("AB.CD"));
            String value = (String) transitionMap.get("AB.CD");
            assertTrue(StringUtils.equals("value1", value) || StringUtils.equals("value2", value));
        }
    }

    @Test
    public void testCollectionTargetFieldNoCollectionParameter() {
        Map<String, Object> transitionPropertiesMap = createTransitionPropertiesMap(new String[] { "AB(0).CD", "value1", "AB(1).CD", "value2" });
        LinkToFieldAssociation linkToFieldAssociation = new LinkToFieldAssociationImpl(createTransition("AB.CD", false), transitionPropertiesMap);
        List<LinkTransitionProperties> transitionPropsList = linkToFieldAssociation.getTransitionProperties();
        assertEquals(2, transitionPropsList.size());
        for (LinkTransitionProperties linkTransitionProps : transitionPropsList) {
            String fullyQualifiedTargetField = linkTransitionProps.getTargetFieldFullyQualifiedName();
            assertTrue(StringUtils.equals("AB(0).CD", fullyQualifiedTargetField) || StringUtils.equals("AB(1).CD", fullyQualifiedTargetField));
        }
    }

    @Test
    public void testCollectionTargetFieldCollectionParameterSameParent() {
        Map<String, Object> transitionPropertiesMap = createTransitionPropertiesMap(new String[] { "AB(0).CD", "value1", "AB(1).CD", "value2", "AB(0).XX", "value1", "AB(1).XX", "value2" });
        LinkToFieldAssociation linkToFieldAssociation = new LinkToFieldAssociationImpl(createTransition("AB.CD", false, new String[] { "filter", "id={AB.XX}" }), transitionPropertiesMap);
        List<LinkTransitionProperties> transitionPropsList = linkToFieldAssociation.getTransitionProperties();
        assertEquals(2, transitionPropsList.size());
        for (LinkTransitionProperties linkTransitionProps : transitionPropsList) {
            String fullyQualifiedTargetField = linkTransitionProps.getTargetFieldFullyQualifiedName();
            assertTrue(StringUtils.equals("AB(0).CD", fullyQualifiedTargetField) || StringUtils.equals("AB(1).CD", fullyQualifiedTargetField));

            Map<String, Object> transitionMap = linkTransitionProps.getTransitionProperties();
            assertTrue(transitionMap.containsKey("AB.XX"));
            String value = (String) transitionMap.get("AB.XX");
            assertTrue(StringUtils.equals("value1", value) || StringUtils.equals("value2", value));
        }
    }

    @Test
    public void testCollectionTargetFieldCollectionParameterDifferentParent() {
        Map<String, Object> transitionPropertiesMap = createTransitionPropertiesMap(new String[] { "AB(0).CD", "value1", "AB(1).CD", "value2", "ZZ(0).XX", "value1", "ZZ(1).XX", "value2" });
        LinkToFieldAssociation linkToFieldAssociation = new LinkToFieldAssociationImpl(createTransition("AB.CD", false, new String[] { "filter", "id={ZZ.XX}" }), transitionPropertiesMap);
        List<LinkTransitionProperties> transitionPropsList = linkToFieldAssociation.getTransitionProperties();
        assertEquals(4, transitionPropsList.size());
        for (LinkTransitionProperties linkTransitionProps : transitionPropsList) {
            String fullyQualifiedTargetField = linkTransitionProps.getTargetFieldFullyQualifiedName();
            assertTrue(StringUtils.equals("AB(0).CD", fullyQualifiedTargetField) || StringUtils.equals("AB(1).CD", fullyQualifiedTargetField));

            Map<String, Object> transitionMap = linkTransitionProps.getTransitionProperties();
            assertTrue(transitionMap.containsKey("ZZ.XX"));
            String value = (String) transitionMap.get("ZZ.XX");
            assertTrue(StringUtils.equals("value1", value) || StringUtils.equals("value2", value));
        }
    }

    @Test
    public void testCollectionDynamicResource() {
        Map<String, Object> transitionPropertiesMap = createTransitionPropertiesMap(new String[] { "AB(0).CD", "value1", "AB(1).CD", "value2", "AB(0).XX", "value1", "AB(1).XX", "value2" });
        LinkToFieldAssociation linkToFieldAssociation = new LinkToFieldAssociationImpl(createTransition("AB.XX", true), transitionPropertiesMap);
        List<LinkTransitionProperties> transitionPropsList = linkToFieldAssociation.getTransitionProperties();
        assertEquals(2, transitionPropsList.size());
        for (LinkTransitionProperties linkTransitionProps : transitionPropsList) {
            String fullyQualifiedTargetField = linkTransitionProps.getTargetFieldFullyQualifiedName();
            assertTrue(StringUtils.equals("AB(0).XX", fullyQualifiedTargetField) || StringUtils.equals("AB(1).XX", fullyQualifiedTargetField));

            Map<String, Object> transitionMap = linkTransitionProps.getTransitionProperties();
            assertTrue(transitionMap.containsKey("AB.CD"));
            String value = (String) transitionMap.get("AB.CD");
            assertTrue(StringUtils.equals("value1", value) || StringUtils.equals("value2", value));
        }
    }

    private Transition createTransition(String targetField, boolean dynamicResource, String... inputValues) {
        ResourceState resourceState = null;
        if (dynamicResource) {
            String[] resourceArgs = new String[] { "AB.CD" };
            resourceState = new DynamicResourceState("entityName", "name", "resourceLocatorName", resourceArgs);
        } else {
            resourceState = new CollectionResourceState("entityName", "name", new ArrayList<Action>(), "/customer()", null, null);
        }

        Map<String, String> uriParameters = new HashMap<String, String>();
        for (int i = 0; i < inputValues.length; i += 2) {
            uriParameters.put(inputValues[i], inputValues[i + 1]);
        }

        return new Transition.Builder().method("GET").target(resourceState).uriParameters(uriParameters).flags(Transition.FOR_EACH).sourceField(targetField).build();
    }

    private Map<String, Object> createTransitionPropertiesMap(String... inputValues) {
        Map<String, Object> transitionMap = new HashMap<String, Object>();
        for (int i = 0; i < inputValues.length; i += 2) {
            transitionMap.put(inputValues[i], inputValues[i + 1]);
        }
        return transitionMap;

    }
}
