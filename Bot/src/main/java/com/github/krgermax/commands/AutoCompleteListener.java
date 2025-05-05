package com.github.krgermax.commands;

import com.github.krgermax.data.items.Item;
import com.github.krgermax.main.Main;
import com.github.krgermax.tokens.Constants;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;
import java.util.stream.Stream;

public class AutoCompleteListener extends ListenerAdapter {

    private final String[] rankOptions = new String[]{
            Constants.MINED_OPTION_ID,
            Constants.XP_OPTION_ID,
            Constants.GOLD_OPTION_ID,
            Constants.KILLS_OPTION_ID,
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
            List<Command.Choice> options = generateStringChoiceList(event, Stream.of(rankOptions).toList());
            event.replyChoices(options).queue();
        }
        /*
            Handles auto complete for the '/item' command
         */
        if (event.getName().equals(Constants.ITEM_COMMAND_ID) && event.getFocusedOption().getName().equals(Constants.ITEM_COMMAND_OPTION_ID)) {
            List<Command.Choice> options = generateItemChoiceList(event, Main.shopManager.getItemList());
            event.replyChoices(options).queue();
        }
    }

    private List<Command.Choice> generateStringChoiceList(CommandAutoCompleteInteractionEvent event, List<String> strings) {
        String userInput = event.getFocusedOption().getValue().toLowerCase();
        return strings.stream()
                .filter(s -> s.toLowerCase().startsWith(userInput))
                .map(s -> new Command.Choice(s, s))
                .limit(25)
                .toList();
    }

    private List<Command.Choice> generateItemChoiceList(CommandAutoCompleteInteractionEvent event, List<Item> dataObjects) {
        List<String> names = dataObjects.stream().map(Item::getName).toList();
        return generateStringChoiceList(event, names);
    }
}
