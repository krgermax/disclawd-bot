package com.github.krgermax.commands.type.slashcommand;

import com.github.krgermax.data.inventory.UserStats;
import com.github.krgermax.main.Main;
import com.github.krgermax.tokens.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class RankCommand implements SlashCommand {

    private static final Map<String, String> optionStatQueryMap = Map.of(
            Constants.KILLS_OPTION_ID, Constants.MOB_KILLS_COLUMN_LABEL,
            Constants.XP_OPTION_ID, Constants.XP_COLUMN_LABEL,
            Constants.GOLD_OPTION_ID, Constants.GOLD_COLUMN_LABEL,
            Constants.MINED_OPTION_ID, Constants.MINED_COLUMN_LABEL
    );

    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {
        String userID = event.getUser().getId();
        if (!Main.sqlHandler.isUserRegistered(userID)) {
            Main.sqlHandler.registerUser(userID);
        }

        String option = event.getOption(Constants.RANK_COMMAND_OPTION_ID).getAsString();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.ORANGE);

        ArrayList<UserStats> userStatsList;
        String statQuery = optionStatQueryMap.get(option);

        if (statQuery == null) {
            embedBuilder.setDescription("Option **" + option + "** was not found, maybe you mistyped something :/");
            event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
            return;
        }

        userStatsList = Main.sqlHandler.getUsersRankedByCategory(statQuery);
        boolean isUserInTop10 = userStatsList.stream().anyMatch(stats -> stats.getUserID().equals(userID));

        UserStats userStats = null;
        if (!isUserInTop10) {
            userStats = Main.sqlHandler.getUserStatsRanked(statQuery, userID);
        }

        buildRankEmbed(embedBuilder, userStatsList, option, userID, userStats);
        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
        Main.LOGGER.info("Executed '" + Constants.RANK_COMMAND_ID + "' command");
    }

    private void buildRankEmbed(
            EmbedBuilder embedBuilder,
            ArrayList<UserStats> userStatsList,
            String option,
            String userID,
            UserStats execUserStats
    ) {
        embedBuilder.setTitle("** Ranked by " + option + "**");
        StringBuilder messageBody = new StringBuilder();
        for (UserStats userStats : userStatsList) {
            // TODO: It might be better to store usernames in the db and update them periodically
            String userName = Main.bot.getJda().retrieveUserById(userStats.getUserID()).complete().getName();
            messageBody.append(formatUserLine(userStats, option, userID, userName));
        }
        embedBuilder.addField("** Top 10 **", messageBody.toString(), false);
        if (execUserStats != null) {
            String userName = Main.bot.getJda().retrieveUserById(execUserStats.getUserID()).complete().getName();
            embedBuilder.addField(
                    "** Your position **",
                    getMedal(execUserStats.getRank()) + "— " + userName + " *(" + getStatByOption(execUserStats, option) + ")*",
                    false
            );
        }
    }

    private String getStatByOption(UserStats userStats, String option) {
        return switch (option) {
            case Constants.XP_OPTION_ID -> String.valueOf(userStats.getXpCount());
            case Constants.GOLD_OPTION_ID -> String.valueOf(userStats.getGoldCount());
            case Constants.KILLS_OPTION_ID -> String.valueOf(userStats.getMobKills());
            case Constants.MINED_OPTION_ID -> String.valueOf(userStats.getMinedCount());
            default -> "";
        };
    }

    private String formatUserLine(UserStats stats, String option, String currentUserID, String userName) {
        String stat = getStatByOption(stats, option);
        String prefix = getMedal(stats.getRank());
        String name = stats.getUserID().equals(currentUserID) ? "**" + userName + "**" : userName;
        return String.format("%s— %s *(%s)*\n", prefix, name, stat);
    }


    private String getMedal(int pos) {
        return switch (pos) {
            case 1 -> ":first_place_medal: ";
            case 2 -> ":second_place_medal: ";
            case 3 -> ":third_place_medal: ";
            default -> "`" + pos + ".` ";
        };
    }
}
