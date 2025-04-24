package org.clawd.buttons.type.nav;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.clawd.buttons.CustomButton;
import org.clawd.data.inventory.InventoryHandler;
import org.clawd.main.Main;
import org.clawd.tokens.Constants;

public class InvBackButton implements CustomButton {
    @Override
    public void executeButton(ButtonInteractionEvent event) {
        String userID = event.getUser().getId();
        if (!Main.sqlHandler.isUserRegistered(userID)) {
            Main.sqlHandler.registerUser(userID);
            Main.sqlHandler.sqlEmbeddedHandler.replyToNewRegisteredUser(event);
        } else {
            InventoryHandler inventoryHandler = Main.inventoryHandler;
            inventoryHandler.replyToNextInvPage(event, true);
            Main.LOGGER.info("Executed '"+ Constants.BACK_INV_BUTTON_ID +"' button");
        }
    }
}
