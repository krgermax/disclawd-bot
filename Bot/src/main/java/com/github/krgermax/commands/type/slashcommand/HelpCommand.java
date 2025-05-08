package com.github.krgermax.commands.type.slashcommand;

import com.github.krgermax.commands.CommandManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import com.github.krgermax.main.Main;
import com.github.krgermax.tokens.Constants;

import java.awt.*;
import java.io.File;

public class HelpCommand implements SlashCommand {
    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Helpcenter");
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setThumbnail("attachment://logo.png");

        embedBuilder.setDescription(Constants.HELP_COMMAND_DESC);
        embedBuilder.addBlankField(false);
        embedBuilder.addField("Disclawd", Constants.HELP_WHAT_IS, false);
        embedBuilder.addField("", Constants.HELP_END_TEXT, false);

        embedBuilder.setFooter("PS: The title is a link :D");
        embedBuilder.setUrl("https://krgermax.com/");

        File imgFile = new File(Constants.PATH_LOGO_IMG);

        event.replyEmbeds(embedBuilder.build())
                .addFiles(FileUpload.fromData(imgFile, "logo.png"))
                .setEphemeral(true)
                .complete();

        Main.LOGGER.info("Executed '" + CommandManager.HELP_COMMAND_ID + "' command");
    }
}
