package com.temenos.interaction.command;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;

public class HelloWorldCommand implements InteractionCommand {
    private static final Logger logger = LoggerFactory.getLogger(HelloWorldCommand.class);

    private InteractionCommand wrappedCommand = new InteractionCommand() {

        public Result execute(InteractionContext ctx) throws InteractionException {

     logger.warn("" + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%===\n"
                    + "=                                                                 =\n"
                    + "=    HELLO WORLD COMMAND UNCONFIGURED FOR WRAPPING, BUT USED      =\n"
                    + "=                                                                 =\n"
                    + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%===" + "");
            return Result.SUCCESS;
        }
    };

    public Result execute(InteractionContext ctx) throws InteractionException {

        logger.warn("\n"
                + StringUtils.repeat("=", 102)+"\n"
                + StringUtils.repeat(" ", 102)+"\n"
                + "|"+StringUtils.center("DEMO COMMAND WORKING AGAIN", 100)+"|\n"
                + StringUtils.repeat(" ", 102)+"\n"
                + StringUtils.repeat("=", 102)+"\n"
                );


        return wrappedCommand.execute(ctx);
    }

    public InteractionCommand getWrappedCommand() {
        return wrappedCommand;
    }

    public void setWrappedCommand(InteractionCommand wrappedCommand) {
        this.wrappedCommand = wrappedCommand;
    }

}
