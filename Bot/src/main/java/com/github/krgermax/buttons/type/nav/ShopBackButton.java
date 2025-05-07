package com.github.krgermax.buttons.type.nav;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import com.github.krgermax.buttons.CustomButton;
import com.github.krgermax.main.Main;
import com.github.krgermax.tokens.Constants;

public class ShopBackButton implements CustomButton {
    @Override
    public void executeButton(ButtonInteractionEvent event) {
        Main.shopManager.replyToNextShopPage(event, true);
        Main.LOGGER.info("Executed '"+ Constants.BACK_SHOP_BUTTON_ID +"' button");
    }
}
