package com.github.krgermax.buttons.type.nav;

import com.github.krgermax.data.inventory.InventoryManager;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import com.github.krgermax.buttons.CustomButton;
import com.github.krgermax.main.Main;
import com.github.krgermax.tokens.Constants;

public class InvBackButton implements CustomButton {
    @Override
    public void executeButton(ButtonInteractionEvent event) {
        String userID = event.getUser().getId();
        if (!Main.sqlHandler.isUserRegistered(userID)) {
            Main.sqlHandler.registerUser(userID);
            Main.sqlHandler.sqlEmbeddedHandler.replyToNewRegisteredUser(event);
        } else {
            InventoryManager inventoryManager = Main.inventoryManager;
            inventoryManager.replyToNextInvPage(event, true);
            Main.LOGGER.info("Executed '"+ Constants.BACK_INV_BUTTON_ID +"' button");
        }
    }
}
