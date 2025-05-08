package com.github.krgermax.commands.type.slashcommand;

import com.github.krgermax.commands.CommandManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import com.github.krgermax.main.Main;

public class ShopCommand implements SlashCommand {
    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {
        Main.shopManager.replyWithShopFirstEmbedded(event);
        Main.LOGGER.info("Executed '" + CommandManager.SHOP_COMMAND_ID + "' command");
    }
}
