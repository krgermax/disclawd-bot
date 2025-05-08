package com.github.krgermax.buttons.type;

import com.github.krgermax.buttons.ButtonManager;
import com.github.krgermax.data.items.Item;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import com.github.krgermax.buttons.CustomButton;
import com.github.krgermax.main.Main;

import java.awt.*;

public class BuyButton implements CustomButton {
    @Override
    public void executeButton(ButtonInteractionEvent event) {
        String userID = event.getUser().getId();
        if (!Main.sqlHandler.isUserRegistered(userID)) {
            Main.sqlHandler.registerUser(userID);
            Main.sqlHandler.sqlEmbeddedHandler.replyToNewRegisteredUser(event);
        } else {
            String componentId = event.getComponentId();
            int itemID = Integer.parseInt(componentId.replace(ButtonManager.BUY_BUTTON_ID, ""));
            Item item = Main.shopManager.getItemByID(itemID);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.ORANGE);

            if (item == null) {
                embedBuilder.setColor(Color.RED);
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
                            Button.success(ButtonManager.BUY_BUTTON_ID, "Buy").asDisabled(),
                            Button.success(ButtonManager.EQUIP_BUTTON_ID + itemID, "Equip").asEnabled()
                    )
            ).queue();
            InteractionHook hook = event.getHook();
            hook.sendMessageEmbeds(embedBuilder.build()).setEphemeral(true).queue();
            Main.LOGGER.info("Executed '" + ButtonManager.BUY_BUTTON_ID + "' button");
        }
    }
}
