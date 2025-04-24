package org.clawd.buttons.type;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.clawd.buttons.CustomButton;
import org.clawd.data.items.Item;
import org.clawd.main.Main;
import org.clawd.tokens.Constants;

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
