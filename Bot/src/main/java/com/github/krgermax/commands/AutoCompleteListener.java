package com.github.krgermax.commands;

import com.github.krgermax.tokens.Constants;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;
import java.util.stream.Stream;

public class AutoCompleteListener extends ListenerAdapter {

    private final String[] rankOptions = new String[]{
            Constants.XP_OPTION_ID,
            Constants.GOLD_OPTION_ID,
            Constants.KILLS_OPTION_ID,
            Constants.MINED_OPTION_ID
    };

    /**
     * Handles auto complete for slash commands
     *
     * @param event CommandAutoCompleteInteractionEvent
     */
    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        /*
            Handles auto complete for the '/rank' command
         */
        if (event.getName().equals(Constants.RANK_COMMAND_ID) && event.getFocusedOption().getName().equals(Constants.RANK_COMMAND_OPTION_ID)) {
            List<Command.Choice> options = Stream.of(rankOptions)
                    .filter(word -> word.toLowerCase().startsWith(event.getFocusedOption().getValue().toLowerCase()))
                    .map(word -> new Command.Choice(word, word))
                    .toList();
            event.replyChoices(options).queue();
        }
    }
}
