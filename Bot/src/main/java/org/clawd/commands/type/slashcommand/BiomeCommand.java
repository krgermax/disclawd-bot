package org.clawd.commands.type.slashcommand;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.clawd.data.biomes.Biome;
import org.clawd.main.Main;
import org.clawd.tokens.Constants;

public class BiomeCommand implements SlashCommand {
    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {
        Main.mineworld.replyWithBiomeEmbedded(event);
        Main.LOGGER.info("Executed '" + Constants.BIOME_COMMAND_ID + "' command");
    }
}
