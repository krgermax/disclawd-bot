package com.github.krgermax.parser;

import com.github.krgermax.data.items.Item;
import com.github.krgermax.data.items.UtilItem;
import com.github.krgermax.data.items.WeaponItem;
import com.github.krgermax.data.items.enums.ItemType;
import com.github.krgermax.main.Main;
import com.github.krgermax.parser.exceptions.FailedDataParseException;
import com.github.krgermax.tokens.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemParser {

    private final Factory factory = new Factory();
    private final List<Integer> idList = new ArrayList<>();

    /*
     * THRESHOLDS for item params
     */
    public static final double ITEM_DROP_CHANCE_UPPER_B = 1;
    public static final double XP_MULTIPLIER_LOWER_B = 1;
    public static final double GOLD_MULTIPLIER_LOWER_B = 0;
    public static final double DMG_MULTIPLIER_LOWER_B = 0;

    public static final String ITEM_IMAGE_BASE_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "images" + File.separator + "items";
    public static final String ITEMS_JSON_FILEPATH = File.separator + "items.json";
    public static final String ITEMS_JSON_ITEMS = "items";

    /**
     * Wrapper function to parse the Items out of a JSON file
     *
     * @return a list containing all items
     * @throws FailedDataParseException when parsing fails, either to invalid items or
     *                                  empty item list
     */
    public List<Item> parseItems() throws FailedDataParseException {
        List<Item> items = getItemsFromJSON();

        if (!validateItems(items) || items.isEmpty())
            throw new FailedDataParseException(
                    "Could not parse items correctly:\n" +
                            "- valid items = " + validateItems(items) + "\n" +
                            "- is item list empty = " + items.isEmpty());

        Main.LOGGER.info("Item parsing finished, item list size: " + items.size());
        return items;
    }

    /**
     * The actual JSON parser itself for the item JSON
     *
     * @return the list of parsed items
     */
    private List<Item> getItemsFromJSON() {

        List<Item> items = new ArrayList<>();

        try (FileReader fileReader = new FileReader(Constants.JSON_BASE_PATH + ITEMS_JSON_FILEPATH)) {

            JSONObject obj = new JSONObject(new JSONTokener(fileReader));
            JSONArray arr = obj.getJSONArray(ITEMS_JSON_ITEMS);

            for (Object o : arr) {

                JSONObject jsonItem = (JSONObject) o;

                int itemID = jsonItem.getInt("id");
                String itemName = jsonItem.getString("name");
                String itemEmoji = jsonItem.getString("emoji");
                String itemDesc = jsonItem.getString("description");
                String fileName = jsonItem.getString("fileName");
                String imgPath = ITEM_IMAGE_BASE_PATH + File.separator + fileName;
                int reqLvl = jsonItem.getInt("reqLvl");
                ItemType itemType = ItemType.valueOf(jsonItem.getString("item_type"));

                double dropChance = jsonItem.getDouble("drop_chance");
                double xpMultiplier = jsonItem.getDouble("xp_multiplier");

                if (itemType.equals(ItemType.UTILITY)) {

                    double goldMultiplier = jsonItem.getDouble("gold_multiplier");

                    Item item = factory.createUtilityItem(
                            itemID,
                            itemName,
                            itemDesc,
                            itemEmoji,
                            imgPath,
                            reqLvl,
                            itemType,
                            dropChance,
                            xpMultiplier,
                            goldMultiplier
                    );

                    idList.add(itemID);
                    items.add(item);
                } else {
                    double dmgMultiplier = jsonItem.getDouble("dmg_multiplier");

                    Item item = factory.createWeaponItem(
                            itemID,
                            itemName,
                            itemDesc,
                            itemEmoji,
                            imgPath,
                            reqLvl,
                            itemType,
                            dropChance,
                            xpMultiplier,
                            dmgMultiplier
                    );

                    idList.add(itemID);
                    items.add(item);
                }
            }

        } catch (JSONException | IOException ex) {
            Main.LOGGER.severe("Failed to parse JSON items file: " + ex.getMessage());
        }
        return items;
    }

    /**
     * Validates all items for some restrictions make help of the
     * isValidItem() method
     *
     * @param items The list of Items that needs to be validated
     * @return True or false, depending on validation
     */
    private boolean validateItems(List<Item> items) {
        for (Item item : items) {
            if (!isValidItem(item))
                return false;
        }
        Main.LOGGER.info("Item validation finished");
        return true;
    }

    /**
     * Checks the validity of exactly one item
     *
     * @param item The item to be checked
     * @return True of false, depending on the items validity
     */
    private boolean isValidItem(Item item) {
        int uniqueID = item.getID();
        if (uniqueID < 0
                || item.getDropChance() > ITEM_DROP_CHANCE_UPPER_B
                || item.getXpMultiplier() < XP_MULTIPLIER_LOWER_B
                || checkIsIDUnique(uniqueID)) {
            return false;
        }

        if (item.getItemType().equals(ItemType.UTILITY)) {
            UtilItem utilItem = (UtilItem) item;
            return utilItem.getGoldMultiplier() >= GOLD_MULTIPLIER_LOWER_B;

        } else if (item.getItemType().equals(ItemType.WEAPON)) {
            WeaponItem weaponItem = (WeaponItem) item;
            return weaponItem.getDmgMultiplier() >= DMG_MULTIPLIER_LOWER_B;
        }

        File imageFile = new File(item.getImgPath());
        if (!imageFile.exists()) {
            Main.LOGGER.severe("Item image file not found: " + item.getImgPath());
            return false;
        }

        return true;
    }

    private boolean checkIsIDUnique(int id) {
        return idList.stream().filter(i -> i == id).count() >= 2;
    }
}
