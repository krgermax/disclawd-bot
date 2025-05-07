package com.github.krgermax.buttons.type.nav;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import com.github.krgermax.buttons.CustomButton;
import com.github.krgermax.main.Main;
import com.github.krgermax.tokens.Constants;

public class ShopHomeButton implements CustomButton {
    @Override
    public void executeButton(ButtonInteractionEvent event) {
        Main.shopManager.updateToFirstEmbedded(event);
        Main.LOGGER.info("Executed '"+ Constants.HOME_SHOP_BUTTON_ID +"' button");
    }
}
