package com.github.krgermax.commands.type.slashcommand;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import com.github.krgermax.main.Main;
import com.github.krgermax.tokens.Constants;

public class InvCommand implements SlashCommand {

    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {
        String userID = event.getUser().getId();
        if (!Main.sqlHandler.isUserRegistered(userID)) {
            Main.sqlHandler.registerUser(userID);
        }
        Main.inventoryHandler.replyWithInventoryFirstEmbedded(event);
        Main.LOGGER.info("Executed '" + Constants.INV_COMMAND_ID + "' button");
    }
}
