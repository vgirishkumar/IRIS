package com.temenos.interaction.core.hypermedia;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementation of {@link LinkToFieldAssociation}
 */
public class LinkToFieldAssociationImpl implements LinkToFieldAssociation {

    private static final Pattern COLLECTION_PARAM_PATTERN = Pattern.compile("\\{*([a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)+)\\}*");
    private static final String PARAM_REPLACEMENT_REGEX = "\\((\\d+)\\)";
    private static final Logger logger = LoggerFactory.getLogger(LinkToFieldAssociationImpl.class);
    private Transition transition;
    private String targetFieldName;
    private List<String> transitionCollectionParams;
    private Boolean isCollectionDynamicResourceName;
    private Map<String, Object> transitionProperties;
    private Map<String, Object> normalisedTransitionProperties;

    public LinkToFieldAssociationImpl(Transition transition, Map<String, Object> properties) {
        this.transition = transition;
        targetFieldName = transition.getSourceField();
        transitionCollectionParams = getCollectionParams(transition.getCommand().getUriParameters());
        isCollectionDynamicResourceName = isCollectionDynamicResourceName(transition.getTarget());
        transitionProperties = properties;
        normalisedTransitionProperties = HypermediaTemplateHelper.normalizeProperties(properties);
    }

    @Override
    public boolean isTransitionSupported() {

        if (targetFieldName == null && (!transitionCollectionParams.isEmpty() || isCollectionDynamicResourceName)) {
            logger.error("Cannot generate links for transition " + transition + ". Target field name cannot be null if we have collection parameters or a collection dymamic resource.");
            return false;
        }

        if (isCollectionDynamicResourceName && !StringUtils.equals(getParentNameOfCollectionValue(targetFieldName), getParentNameOfDynamicResource())) {
            logger.error("Cannot generate links for transition " + transition + ". Parent of target field name and dynamic resource must be same.");
            return false;
        }

        if (!transitionCollectionParams.isEmpty() && !allParametersHaveSameParent()) {
            logger.error("Cannot generate links for transition " + transition + ". All collection parameters must have the same parent.");
            return false;
        }

        return true;
    }

    @Override
    public List<LinkTransitionProperties> getTransitionProperties() {

        List<LinkTransitionProperties> transitionPropertiesList = new ArrayList<LinkTransitionProperties>();

        List<String> childParamNames = new ArrayList<String>();
        for (String collectionParam : transitionCollectionParams) {
            childParamNames.add(getChildNameOfCollectionValue(collectionParam));
        }

        if (targetFieldName != null && targetFieldName.contains(".")) // Multivalue target
        {
            createLinkPropertiesForMultivalueTarget(transitionPropertiesList, childParamNames);
        } else // Non multivalue target
        {
            createLinkPropertiesForSingleTarget(transitionPropertiesList, childParamNames);
        }

        logger.debug("Created " + transitionPropertiesList.size() + " properties map(s) for transition " + transition);

        return transitionPropertiesList;
    }

    private void createLinkPropertiesForMultivalueTarget(List<LinkTransitionProperties> transitionPropertiesList, List<String> childParamNames) {
        List<String> targetFields = extractMatchingFieldsFromTransitionProperties(targetFieldName, true);
        String parentTargetFieldName = getParentNameOfCollectionValue(targetFieldName);

        boolean hasSameParent = false;
        if (transitionCollectionParams.size() > 0) {
            String firstCollectionParam = transitionCollectionParams.get(0);
            hasSameParent = StringUtils.equals(getParentNameOfCollectionValue(firstCollectionParam), parentTargetFieldName);
        }

        StringBuilder numOfChildren = new StringBuilder();
        StringBuilder childParentResolvedName = new StringBuilder();
        initChildParamProperties(numOfChildren, childParentResolvedName, hasSameParent);

        for (String targetField : targetFields) // Generate one or more map of properties for each target field
        {
            String resolvedDynamicResourceName = getDynamicResourceResolvedName(transition.getTarget(), targetField);
            if (transitionCollectionParams.isEmpty()) // If URI parameter map does have any multivalue param
            {
                LinkTransitionProperties linkTransitionProps = createLinkTransitionProperties(targetField, resolvedDynamicResourceName, childParamNames, null);
                transitionPropertiesList.add(linkTransitionProps);
            } else if (hasSameParent) // If parent of multivalue param in URI map is same as parent of target
            {
                String parentResolvedTargetFieldName = getParentNameOfCollectionValue(targetField);
                LinkTransitionProperties linkTransitionProps = createLinkTransitionProperties(targetField, resolvedDynamicResourceName, childParamNames, parentResolvedTargetFieldName);
                transitionPropertiesList.add(linkTransitionProps);
            } else {
                for (int i = 0; i <= Integer.parseInt(numOfChildren.toString()); i++) {
                    String childParentResolvedParamNewIndex = childParentResolvedName.substring(0, childParentResolvedName.lastIndexOf("(") + 1) + i + ")";
                    LinkTransitionProperties linkTransitionProps = createLinkTransitionProperties(targetField, resolvedDynamicResourceName, childParamNames, childParentResolvedParamNewIndex);
                    transitionPropertiesList.add(linkTransitionProps);
                }
            }
        }
    }

    private void createLinkPropertiesForSingleTarget(List<LinkTransitionProperties> transitionPropertiesList, List<String> childParamNames) {
        if (transitionCollectionParams.isEmpty()) // If URI parameter map does have any multivalue param
        {
            LinkTransitionProperties linkTransitionProps = new LinkTransitionProperties(targetFieldName, transitionProperties);
            transitionPropertiesList.add(linkTransitionProps);
        } else {
            StringBuilder numOfChildren = new StringBuilder();
            StringBuilder childParentResolvedName = new StringBuilder();
            initChildParamProperties(numOfChildren, childParentResolvedName, false);

            for (int i = 0; i <= Integer.parseInt(numOfChildren.toString()); i++) {
                String childParentResolvedParamNewIndex = childParentResolvedName.substring(0, childParentResolvedName.lastIndexOf("(") + 1) + i + ")";
                LinkTransitionProperties linkTransitionProps = createLinkTransitionProperties(targetFieldName, null, childParamNames, childParentResolvedParamNewIndex);
                transitionPropertiesList.add(linkTransitionProps);
            }
        }
    }

    private LinkTransitionProperties createLinkTransitionProperties(String targetField, String resolvedDynamicResourceName, List<String> childParamNames, String resolvedParentName) {
        List<String> paramPropertyKeys = new ArrayList<String>();
        if (StringUtils.isNotBlank(resolvedParentName)) {
            paramPropertyKeys = getListOfParamPropertyKeys(childParamNames, resolvedParentName);
        }

        if (StringUtils.isNotBlank(resolvedDynamicResourceName)) {
            paramPropertyKeys.add(resolvedDynamicResourceName);
        }
        LinkTransitionProperties linkTransitionProps = new LinkTransitionProperties(targetField, transitionProperties);
        addEntriesToLinkProperties(linkTransitionProps, paramPropertyKeys);
        return linkTransitionProps;
    }

    private List<String> getListOfParamPropertyKeys(List<String> childParamNames, String fullyQualifiedParentName) {
        List<String> propertyKeys = new ArrayList<String>();

        for (String childParam : childParamNames) {
            propertyKeys.add(fullyQualifiedParentName + "." + childParam);
        }

        return propertyKeys;
    }

    private String getDynamicResourceResolvedName(ResourceState targetResourceState, String targetField) {
        if (isCollectionDynamicResourceName) {
            String targetFieldParentName = getParentNameOfCollectionValue(targetField);
            DynamicResourceState dynamicResourceState = (DynamicResourceState) targetResourceState;
            String[] resourceLocatorArgs = dynamicResourceState.getResourceLocatorArgs();

            String resolvedDynamicResourceName = targetFieldParentName + "." + getChildNameOfCollectionValue(resourceLocatorArgs[0]).replaceAll("\\}", "");
            return resolvedDynamicResourceName;
        }
        return null;
    }

    private void initChildParamProperties(StringBuilder numberOfChildren, StringBuilder childResolvedParentName, boolean hasSameParent) {
        if (transitionCollectionParams.size() > 0 && !hasSameParent) {
            boolean setResolvedParentName = true;
            int maxNumberOfchildren = 0;
            for (String collectionParam : transitionCollectionParams) {
                List<String> entityParamFields = extractMatchingFieldsFromTransitionProperties(collectionParam, false);
                for (String entityParam : entityParamFields) {
                    if (setResolvedParentName) {
                        childResolvedParentName.append(getParentNameOfCollectionValue(entityParam));
                        setResolvedParentName = false;
                    }

                    int index = Integer.parseInt(entityParam.substring(entityParam.lastIndexOf("(") + 1, entityParam.lastIndexOf(")")));
                    if (index > maxNumberOfchildren) {
                        maxNumberOfchildren = index;
                    }
                }
            }
            numberOfChildren.append(maxNumberOfchildren);
        }
    }

    private List<String> getCollectionParams(Map<String, String> transitionUriMap) {
        List<String> collectionParams = new ArrayList<String>();
        if (transitionUriMap == null) {
            return collectionParams;
        }

        for (Map.Entry<String, String> entry : transitionUriMap.entrySet()) {
            String parameter = entry.getValue();
            Matcher matcher = COLLECTION_PARAM_PATTERN.matcher(parameter);
            while (matcher.find()) {
                collectionParams.add(matcher.group(1));
            }
        }
        return collectionParams;
    }

    private boolean isCollectionDynamicResourceName(ResourceState target) {
        if (target instanceof DynamicResourceState) {
            String[] resourceLocatorArgs = ((DynamicResourceState) target).getResourceLocatorArgs();
            if (resourceLocatorArgs != null && resourceLocatorArgs.length > 0 && resourceLocatorArgs[0].contains(".")) {
                return true;
            }
            return false;
        }
        return false;
    }

    private String getParentNameOfDynamicResource() {
        String[] resourceLocatorArgs = ((DynamicResourceState) transition.getTarget()).getResourceLocatorArgs();
        String parent = getParentNameOfCollectionValue(resourceLocatorArgs[0]);
        if (parent != null) {
            parent = parent.replaceAll("\\{", "").replaceAll("\\([0-9]+\\)", "");
        }
        return parent;
    }

    private List<String> extractMatchingFieldsFromTransitionProperties(String fieldName, boolean forTargetField) {
        List<String> matchingFieldList = new ArrayList<String>();
        for (String key : normalisedTransitionProperties.keySet()) {
            if (StringUtils.equals(fieldName, key.replaceAll(PARAM_REPLACEMENT_REGEX, ""))) {
                matchingFieldList.add(key);
            }
        }

        // For non multivalue field
        if (forTargetField && matchingFieldList.isEmpty()) {
            matchingFieldList.add(fieldName);
        }
        return matchingFieldList;
    }

    private String getParentNameOfCollectionValue(String value) {
        if (StringUtils.isNotBlank(value) && value.contains(".")) {
            return value.substring(0, value.lastIndexOf("."));
        }
        return null;
    }

    private String getChildNameOfCollectionValue(String value) {
        if (StringUtils.isNotBlank(value) && value.contains(".")) {
            return value.substring(value.lastIndexOf(".") + 1);
        }
        return null;
    }

    private boolean allParametersHaveSameParent() {
        boolean haveSameParent = true;
        List<String> parents = new ArrayList<String>();
        for (String transitionParam : transitionCollectionParams) {
            String parent = getParentNameOfCollectionValue(transitionParam);
            if (parent == null) {
                continue;
            } else if (parents.isEmpty()) {
                parents.add(parent);
                continue;
            } else if (!parents.contains(parent)) {
                haveSameParent = false;
                break;
            }
        }

        return haveSameParent;
    }

    private void addEntriesToLinkProperties(LinkTransitionProperties linkTransitionProps, List<String> keys) {
        Map<String, Object> linkPropertiesMap = linkTransitionProps.getTransitionProperties();
        Set<String> unindexedKeys = new HashSet<String>();
        for (String key : keys) {
            Object value = this.normalisedTransitionProperties.get(key);
            String unindexedKey = key.replaceAll(PARAM_REPLACEMENT_REGEX, "");
            unindexedKeys.add(unindexedKey);
            if (value != null && value instanceof String) {
                linkPropertiesMap.put(unindexedKey, value);
            } else {
                linkPropertiesMap.put(unindexedKey, "");
            }
        }

        // Replace Id={A.B.C} with Id={VAL} if A.B.C=VAL is in the linkPropertiesMap
        for (String key : linkPropertiesMap.keySet()) {
            Object value = linkPropertiesMap.get(key);
            if (value instanceof String) {

                String replacementKey = ((String) value).replaceAll("\\{", "").replaceAll("\\}", "");

                if (unindexedKeys.contains(replacementKey)) {
                    String replacementValue = ((String) value).replaceAll("\\{" + Pattern.quote(replacementKey) + "\\}", linkPropertiesMap.get(replacementKey).toString());
                    linkPropertiesMap.put(key, replacementValue);
                }
            }
        }
    }
}
