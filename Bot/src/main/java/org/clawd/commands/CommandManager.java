package org.clawd.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.clawd.commands.type.slashcommand.*;
import org.clawd.main.Main;
import org.clawd.tokens.Constants;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;

public class CommandManager extends ListenerAdapter {

    private final HashMap<String, SlashCommand> commands;

    public CommandManager() {
        this.commands = new HashMap<>();

        this.commands.put(Constants.HELP_COMMAND_ID, new HelpCommand());
        this.commands.put(Constants.BIOME_COMMAND_ID, new BiomeCommand());
        this.commands.put(Constants.SHOP_COMMAND_ID, new ShopCommand());
        this.commands.put(Constants.ITEM_COMMAND_ID, new ItemCommand());
        this.commands.put(Constants.INV_COMMAND_ID, new InvCommand());
    }

    /**
     * Receives slash command interaction event and calls on the
     * corresponding command class executeCommand() method
     *
     * @param event Received event
     */
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        Main.LOGGER.info("Received slash command: " + command);

        SlashCommand slashCommand = commands.get(command);
        if (slashCommand != null) {
            slashCommand.executeCommand(event);
        } else {
            Main.LOGGER.info("Command: " + command + ", does not exist.");
            replyToNonExistingCommand(event);
        }
    }

    /**
     * This function is called in the case of a non-existing slash command
     *
     * @param event Received event
     */
    private void replyToNonExistingCommand(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Color.RED);
        embedBuilder.setTitle("Command does not exist!");
        embedBuilder.setDescription("The command you are trying to use doesnt exist or was eaten" +
                " by the cookie monster :confused:");

        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
    }
}
