package com.github.krgermax.commands.type.slashcommand;

import com.github.krgermax.commands.CommandManager;
import com.github.krgermax.data.mineworld.Mineworld;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import com.github.krgermax.main.Main;

public class BiomeCommand implements SlashCommand {
    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {
        String guildID = event.getGuild().getId();
        Mineworld mineworld = Main.mineworldManager.getMineworld(guildID);
        mineworld.replyWithBiomeEmbedded(event);
        Main.LOGGER.info("Executed '" + CommandManager.BIOME_COMMAND_ID + "' command");
    }
}
