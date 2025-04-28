package com.github.krgermax.commands.type.slashcommand;

import com.github.krgermax.data.inventory.UserStats;
import com.github.krgermax.main.Main;
import com.github.krgermax.tokens.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.ArrayList;

public class RankCommand implements SlashCommand {
    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {

        String option = event.getOption(Constants.RANK_COMMAND_OPTION_ID).getAsString();
        Main.LOGGER.info(option);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.ORANGE);

        ArrayList<UserStats> userStatsList;
        switch (option) {
            case Constants.XP_OPTION_ID:
                userStatsList = getUserStatsList(Constants.XP_COLUMN_LABEL);
                break;
            case Constants.GOLD_OPTION_ID:
                userStatsList = getUserStatsList(Constants.GOLD_COLUMN_LABEL);
                break;
            case Constants.KILLS_OPTION_ID:
                userStatsList = getUserStatsList(Constants.MOB_KILLS_COLUMN_LABEL);
                break;
            case Constants.MINED_OPTION_ID:
                userStatsList = getUserStatsList(Constants.MINED_COLUMN_LABEL);
                break;
            default:
                embedBuilder.setDescription("Option **" + option + "** was not found, maybe you mistyped something :/");
                event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
                return;
        }
        buildRankEmbed(event, embedBuilder, userStatsList, option);
        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
    }

    // TODO: make embedded message prettier
    private void buildRankEmbed(SlashCommandInteractionEvent event, EmbedBuilder embedBuilder, ArrayList<UserStats> userStatsList, String option) {
        embedBuilder.setTitle("** Ranked by" + option + "**");
        StringBuilder messageBody = new StringBuilder();
        for (int i = 0; i < userStatsList.size(); i++) {
            UserStats userStats = userStatsList.get(i);
            String userName = Main.bot.getJda().retrieveUserById(userStats.getUserID()).complete().getName();
            String stat = switch (option) {
                case Constants.XP_OPTION_ID -> String.valueOf(userStats.getXpCount());
                case Constants.GOLD_OPTION_ID -> String.valueOf(userStats.getGoldCount());
                case Constants.KILLS_OPTION_ID -> String.valueOf(userStats.getMobKills());
                case Constants.MINED_OPTION_ID -> String.valueOf(userStats.getMinedCount());
                default -> "";
            };

            if (event.getUser().getId().equals(userStats.getUserID())) {
                messageBody.append(getMedal(i)).append("**").append(userName).append("**").append(" *(").append(stat).append("*)\n");
            } else {
                messageBody.append(getMedal(i)).append(userName).append(" *(").append(stat).append("*)\n");
            }
        }
        embedBuilder.addField("** Top 10 players by " + option + "**", messageBody.toString(), true);
    }

    private String getMedal(int pos) {
        return switch (pos) {
            case 0 -> ":first_place_medal: ";
            case 1 -> ":second_place_medal: ";
            case 2 -> ":third_place_medal: ";
            default -> pos + ". ";
        };
    }

    private ArrayList<UserStats> getUserStatsList(String statQuery) {
        return Main.sqlHandler.getUsersRankedByCategory(statQuery);
    }
}
