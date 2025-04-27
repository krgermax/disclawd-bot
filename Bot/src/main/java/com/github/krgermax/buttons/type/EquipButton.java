package com.github.krgermax.buttons.type;

import com.github.krgermax.data.items.Item;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import com.github.krgermax.buttons.CustomButton;
import com.github.krgermax.main.Main;
import com.github.krgermax.tokens.Constants;

import java.awt.*;

public class EquipButton implements CustomButton {
    @Override
    public void executeButton(ButtonInteractionEvent event) {
        String userID = event.getUser().getId();
        if (!Main.sqlHandler.isUserRegistered(userID)) {
            Main.sqlHandler.registerUser(userID);
            Main.sqlHandler.sqlEmbeddedHandler.replyToNewRegisteredUser(event);
        } else {
            String componentId = event.getComponentId();
            int itemID = Integer.parseInt(componentId.replace(Constants.EQUIP_BUTTON_ID, ""));
            Item item = Main.shopHandler.getItemByID(itemID);

            Main.sqlHandler.sqlInventoryHandler.equipItem(userID, item.getID());

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.ORANGE);
            embedBuilder.setDescription("You're now wielding **" + item.getEmoji() + item.getName() + item.getEmoji() + "**!");

            event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
            Main.LOGGER.info("Executed '" + Constants.EQUIP_BUTTON_ID + "' button");
        }
    }
}
