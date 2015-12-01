package com.temenos.interaction.core.command;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of CommandController interface, bootstrapped by proving a map of InteractionCommands 
 * (keyed with names of the commands to be assigned). Replaces NewCommandController.
 * The class instances are thread safe, and individual commands can be added to the map returned by getCommandMap().
 * 
 * @author ktrojan
 */
public class MapBasedCommandController implements CommandController {

    private static final Logger logger = LoggerFactory.getLogger(MapBasedCommandController.class);

    private Map<String, InteractionCommand> commandMap = new ConcurrentHashMap<String, InteractionCommand>();

    public MapBasedCommandController() {
        logger.debug("Empty MapBasedCommandController created: " + this);
    }

    public MapBasedCommandController(Map<String, InteractionCommand> commandMap) {
        logger.debug("MapBasedCommandController created: " + this);
        setCommandMap(commandMap);
    }

    @Override
    public InteractionCommand fetchCommand(String name) {
        if (logger.isDebugEnabled()) {
            logger.debug("Retrieving command for name " + name + " from MapBasedCommandController " + this);
        }
        return commandMap.get(name);
    }

    @Override
    public boolean isValidCommand(String name) {
        return commandMap.containsKey(name);
    }

    /**
     * @return the commandMap
     */
    public Map<String, InteractionCommand> getCommandMap() {
        return commandMap;
    }

    /**
     * @param commandMap the commandMap to set
     */
    public final void setCommandMap(Map<String, InteractionCommand> commandMap) {
        logger.debug("New command map set on MapBasedCommandController" + this.toString());
        this.commandMap = new ConcurrentHashMap(commandMap);
    }

}
