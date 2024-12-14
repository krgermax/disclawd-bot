package org.clawd.buttons.type;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.clawd.buttons.CustomButton;
import org.clawd.data.items.Item;
import org.clawd.main.Main;
import org.clawd.tokens.Constants;

import java.awt.*;
import java.util.List;

public class BuyButton implements CustomButton {
    @Override
    public void executeButton(ButtonInteractionEvent event) {
        String userID = event.getUser().getId();
        if (!Main.sqlHandler.isUserRegistered(userID)) {
            Main.sqlHandler.registerUser(userID);
            Main.sqlHandler.sqlEmbeddedHandler.replyToNewRegisteredUser(event);
        } else {
            String componentId = event.getComponentId();
            int itemID = Integer.parseInt(componentId.replace(Constants.BUY_BUTTON_ID, ""));
            Item item = Main.mineworld.getItemByID(itemID);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Couldn't buy item");
            embedBuilder.setColor(Color.ORANGE);

            if (item == null) {
                embedBuilder.setDescription("Something went wrong, the cake is a lie!");
                event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
                return;
            }

            int itemPrice = item.getPrice();

            Main.sqlHandler.sqlStatsHandler.changeGoldCount(userID, -itemPrice);
            Main.sqlHandler.sqlInventoryHandler.addItemToPlayer(userID, itemID);
            embedBuilder.setDescription("You successfully acquired " + item.getEmoji() + "**" + item.getName() + "**" + item.getEmoji() + " !");

            event.editComponents(
                    ActionRow.of(
                            Button.success(Constants.BUY_BUTTON_ID, "Buy").asDisabled(),
                            Button.success(Constants.EQUIP_BUTTON_ID, "Equip").asEnabled()
                    )
            ).queue();
            InteractionHook hook = event.getHook();
            hook.sendMessageEmbeds(embedBuilder.build()).setEphemeral(true).queue();
            Main.LOG.info("Executed '"+ Constants.BUY_BUTTON_ID +"' button");
        }
    }
}
