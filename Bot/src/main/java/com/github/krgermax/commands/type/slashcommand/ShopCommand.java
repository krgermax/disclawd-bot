package com.github.krgermax.commands.type.slashcommand;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import com.github.krgermax.main.Main;
import com.github.krgermax.tokens.Constants;

public class ShopCommand implements SlashCommand {
    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {
        Main.shopHandler.replyWithShopFirstEmbedded(event);
        Main.LOGGER.info("Executed '" + Constants.SHOP_COMMAND_ID + "' command");
    }
}
