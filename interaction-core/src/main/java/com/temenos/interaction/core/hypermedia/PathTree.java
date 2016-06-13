package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-springdsl
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


import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a tree representation of a set of OData path template / http method tuples including handling path wildcards like /{id} and provides a
 * means of resolving a path to a path template allowing navigation from a path to an object associated with a given path template / http method tuple. 
 *
 * @author mlambert
 */
public class PathTree {
    private static final Pattern PATH_PARAMETER_PATTERN = Pattern.compile("(.*)(?:\\()(.*)(?:\\))$");    
    private final Logger logger = LoggerFactory.getLogger(PathTree.class);
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    
    private class Node {
        String segment;
        Map<String, String> value = new HashMap<String,String>();
        Map<String, Node> literals = new HashMap<String, Node>();
        List<Node> variables = new LinkedList<Node>();
    }
    
    private Node root;
    
    /**
     * Returns true if there are no OData paths in the tree otherwise false.
     *  
     * @return true if there are no OData paths in the tree otherwise false.
     */
    public boolean isEmpty() { 
        Lock readLock = readWriteLock.readLock(); 
        readLock.lock();
        
        try {
            return root == null;   
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Puts an OData path template / http method tuple in the tree, if the tuple already exists then the state name associated with it will be replaced.
     *  
     * @param path
     *          The OData path template part of the new tuple that will be put in the tree
     *          
     * @param httpMethod
     *          The http method part of the new tuple that will be put in the tree
     *          
     * @param stateName
     *          The state name to associate with the new tuple
     */
    public void put(String path, String httpMethod, String stateName) {
    	if("dynamic".equals(stateName)){
    		// Skip placeholder dynamic state
    		return;
    	}
    	
        LinkedList<String> segments = new LinkedList<String>(Arrays.asList(path.split("/")));
        
        Lock writeLock = readWriteLock.writeLock(); 
        writeLock.lock();

        try {
            if(root == null) {
                // All paths will be relative to "/" so this we be the root
                root = new Node();
                String segment = "/";
                root.segment = segment;
            }
                    
            if(segments.isEmpty()) {
                root.value.put(httpMethod, stateName);
            } else {
                segments.remove(0);
                put(root, segments, httpMethod, stateName);    
            }
        } finally {
            writeLock.unlock();
        }
    }
    
    private void put(Node current, LinkedList<String> segments, String httpMethod, String stateName) {
        String segment = segments.remove(0);
        Node match = null;        
        boolean variableSegment = false;
                        
        Matcher m = PATH_PARAMETER_PATTERN.matcher(segment);
        
        if (m.find()) {
            // We are dealing with a path that contains (...) where ... may contain 0 or more characters
            String tmpSegment = m.group(2);
            
            if (!"".equals(tmpSegment)) {
                /*
                 * TODO The current implementation internally expands /myResource('{id}')/modify to something that can be thought of as equivalent to 
                 * /myResource/{id}/modify; in future we may want to modify it to be /myResource/(/{id}/)/modify to reduce the chance of a collision 
                 * with another path such as /myResource/{other}/modify
                 */
                if (tmpSegment.charAt(0) == '\'' && tmpSegment.charAt(tmpSegment.length() - 1) == '\'') {
                    // There are ' at the start and end of the segment - drop them
                    tmpSegment = tmpSegment.substring(1, tmpSegment.length() - 1);
                }
                segments.addFirst(tmpSegment);
            }

            // Reduce the segment to non (...) section
            segment = m.group(1) == null ? "" : m.group(1);
        }
        
        if(segment.charAt(0) == '{' && segment.charAt(segment.length() - 1) == '}') {
            // The current segment represents a variable
            variableSegment = true;
        }
        
        if(variableSegment) {
            // We are dealing with a variable segment - check if this node already has this variable child            
            for(Node variable: current.variables) {
                if(variable.segment.equals(segment)) {
                    match = variable;
                    break;
                }
            }
            
            if(match == null) {
                match = new Node();
                match.segment = segment;
                current.variables.add(match);
                
                if(logger.isDebugEnabled()) {
                    logger.debug("Adding " + match + " to " + segment);
                }
            }            
        }

        if(!variableSegment) {
            // We a dealing with a literal segment - check if this node already has this literal child
            if(current.literals.containsKey(segment)) {
                match = current.literals.get(segment);
            } else {
                match = new Node();
                match.segment = segment;
                current.literals.put(segment, match);
                
                if(logger.isDebugEnabled()) {
                    logger.debug("Adding " + match + " to " + segment);
                }
            }
        }
        
        if(segments.isEmpty()) {
            match.value.put(httpMethod, stateName);
        } else {
            put(match, segments, httpMethod, stateName);
        }        
    }
    
    /**
     * Gets the http method / state name pairs associated with the given OData path
     * 
     * @param path The OData path to look up
     * 
     * @return The http method / state name pairs associated with the given OData path
     */
    public Map<String,String> get(String path) {
        List<String> segments = new LinkedList<String>(Arrays.asList(path.split("/")));
        
        String segment = "/";
        
        Lock readLock = readWriteLock.readLock(); 
        readLock.lock();
        
        try {
            if(root != null && root.segment != null && root.segment.equals(segment)) {
                if(!segments.isEmpty()) {
                    segments.remove(0);
                }
                
                if(segments.isEmpty()) {
                    return root.value;
                } else {                
                    return get(root, segments);
                }
            } else {
                return null;
            }            
        } finally {
            readLock.unlock();
        }
    }

    private Map<String,String> get(Node current, List<String> segments) {
        LinkedList<String> tmpSegments = new LinkedList<String>();
        tmpSegments.addAll(segments);
        String segment = tmpSegments.get(0);
        tmpSegments.remove(0);
        Matcher matcher = PATH_PARAMETER_PATTERN.matcher(segment);
        
        if(matcher.find()) {
            // We are dealing with a path that contains (...) where ... may contain 0 or more characters
            String tmpSegment = matcher.group(2);
            
            if(!"".equals(tmpSegment)) {
                // Drop '(' and ')' characters and add the value as the next segment to process                                 
                tmpSegments.addFirst(tmpSegment);
            }
            
            // Reduce the segment to non (...) section
            segment = matcher.group(1) == null ? "" : matcher.group(1);            
        }
        
        if(current.literals.containsKey(segment)) {
            if(tmpSegments.isEmpty()) {
                return current.literals.get(segment).value;
            } else {
                return get(current.literals.get(segment), tmpSegments);
            }
        } else {
        	Map<String,String> result = null;
        	
        	if(tmpSegments.isEmpty()) {
                if(current.variables.size() == 1) {
                    return current.variables.get(0).value;
                } else {
                    return null;
                }
            } else {
                for(Node variable: current.variables) {
                    
                    result = get(variable, tmpSegments);
                    
                    if(result != null) {
                        break;
                    }
                }            
            }
            
            return result;
        }
    }

    /**
     * Removes an OData path template / http method tuple from the tree.
     *  
     * @param path
     *          The OData path template part of the tuple to remove from the tree
     *          
     * @param httpMethod
     *          The http method part of the tuple to remove from the tree          
     */    
    public void remove(String path, String httpMethod) {
        Lock writeLock = readWriteLock.writeLock(); 
        writeLock.lock();
        
        try {
            Map<String,String> httpMethodToState = get(path);
            
            if(httpMethodToState == null) {
                throw new IllegalArgumentException("Path not found (" + path + ")");
            } else {
                if(httpMethodToState.containsKey(httpMethod)) {
                    // Remove the http method given from the set of http methods associated with the url
                    httpMethodToState.remove(httpMethod);
                } else {
                    throw new IllegalArgumentException("Method (" + httpMethod + ") not found for path (" + path + ")");
                }
            }
        } finally {
            writeLock.unlock();
        }        
    }    
}