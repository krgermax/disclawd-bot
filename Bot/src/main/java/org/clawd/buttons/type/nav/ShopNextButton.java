package org.clawd.buttons.type.nav;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.clawd.buttons.CustomButton;
import org.clawd.main.Main;
import org.clawd.tokens.Constants;

public class ShopNextButton implements CustomButton {
    @Override
    public void executeButton(ButtonInteractionEvent event) {
        String userID = event.getUser().getId();
        if (!Main.sqlHandler.isUserRegistered(userID)) {
            Main.sqlHandler.registerUser(userID);
            Main.sqlHandler.sqlEmbeddedHandler.replyToNewRegisteredUser(event);
        } else {
            Main.shopHandler.replyToNextShopPage(event, false);
            Main.LOGGER.info("Executed '"+ Constants.NEXT_SHOP_BUTTON_ID +"' button");
        }
    }
}
