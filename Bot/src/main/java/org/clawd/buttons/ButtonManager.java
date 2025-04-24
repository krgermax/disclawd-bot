package org.clawd.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.clawd.buttons.type.*;
import org.clawd.buttons.type.nav.*;
import org.clawd.main.Main;
import org.clawd.tokens.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ButtonManager extends ListenerAdapter {

    private final HashMap<String, CustomButton> buttons;
    private final HashMap<String, String> buttonsWithID;

    public ButtonManager() {
        this.buttons = new HashMap<>();

        this.buttons.put(Constants.MINE_BUTTON_ID, new MineButton());
        this.buttons.put(Constants.NEXT_SHOP_BUTTON_ID, new ShopNextButton());
        this.buttons.put(Constants.BACK_SHOP_BUTTON_ID, new ShopBackButton());
        this.buttons.put(Constants.HOME_SHOP_BUTTON_ID, new ShopHomeButton());
        this.buttons.put(Constants.BUY_BUTTON_ID, new BuyButton());
        this.buttons.put(Constants.EQUIP_BUTTON_ID, new EquipButton());
        this.buttons.put(Constants.NEXT_INV_BUTTON_ID, new InvNextButton());
        this.buttons.put(Constants.BACK_INV_BUTTON_ID, new InvBackButton());
        this.buttons.put(Constants.HOME_INV_BUTTON_ID, new InvHomeButton());
        this.buttons.put(Constants.HIT_BUTTON_ID, new HitButton());

        this.buttonsWithID = new HashMap<>();

        this.buttonsWithID.put(Constants.HIT_BUTTON_ID, Constants.HIT_BUTTON_ID);
        this.buttonsWithID.put(Constants.EQUIP_BUTTON_ID, Constants.EQUIP_BUTTON_ID);
        this.buttonsWithID.put(Constants.BUY_BUTTON_ID, Constants.BUY_BUTTON_ID);
    }

    /**
     * Receives a button interaction event and calls on the corresponding
     * button class executeButton() method
     *
     * @param event Received event
     */
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonID = event.getComponentId();
        Main.LOGGER.info("Received button interaction: " + buttonID);

        String key = buttonsWithID.entrySet().stream()
                .filter(entry -> buttonID.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(buttonID);

        CustomButton button = buttons.get(key);

        if (button != null) {
            button.executeButton(event);
        } else {
            Main.LOGGER.severe("Button ID not found: " + buttonID);
        }
    }
}
