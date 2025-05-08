package com.github.krgermax.buttons.type.nav;

import com.github.krgermax.buttons.ButtonManager;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import com.github.krgermax.buttons.CustomButton;
import com.github.krgermax.main.Main;

public class ShopBackButton implements CustomButton {
    @Override
    public void executeButton(ButtonInteractionEvent event) {
        Main.shopManager.replyToNextShopPage(event, true);
        Main.LOGGER.info("Executed '"+ ButtonManager.BACK_SHOP_BUTTON_ID +"' button");
    }
}
