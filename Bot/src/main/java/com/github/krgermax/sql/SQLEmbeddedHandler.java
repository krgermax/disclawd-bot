package com.github.krgermax.sql;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.awt.*;

public class SQLEmbeddedHandler {

    /**
     * Creates and replies to the button interaction event, if a user was not registered
     * in the SQL database before
     *
     * @param event The button interaction event
     */
    public void replyToNewRegisteredUser(ButtonInteractionEvent event) {
        String userName = event.getUser().getName();

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("**New user registered!**");
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.addField(
                "",
                "Hi, **" + userName + "** welcome to 'Disclawd'. You're first interaction " +
                "registered you're user ID in the database ( with some other default values c: ). "+
                "Continue with the /help command to learn more! :sloth:", false);

        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
    }

    /**
     * Creates and replies to the slash command interaction event, if a user was not registered
     * in the SQL database before
     *
     * @param event The button interaction event
     */
    public void replyToNewRegisteredUser(SlashCommandInteractionEvent event) {
        String userName = event.getUser().getName();

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("**New user registered!**");
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.addField(
                "",
                "Hi, **" + userName + "** welcome to 'Disclawd'. You're first interaction " +
                        "registered you're user ID in the database ( with some other default values c: ). "+
                        "Continue with the /help command to learn more! :sloth:", false);

        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
    }
}
