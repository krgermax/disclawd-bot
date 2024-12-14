package org.clawd.parser;

import org.clawd.data.items.Item;
import org.clawd.data.items.enums.ItemType;
import org.clawd.data.items.UtilItem;
import org.clawd.data.items.WeaponItem;
import org.clawd.main.Main;
import org.clawd.parser.exceptions.FailedDataParseException;
import org.clawd.tokens.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemParser {

    private final Factory factory = new Factory();
    private final List<Integer> idList = new ArrayList<>();

    /**
     * Wrapper function to parse the Items out of a JSON file
     *
     * @return a list containing all items
     * @throws FailedDataParseException when parsing fails, either to invalid items or
     *                                  empty item list
     */
    public List<Item> parseItems() throws FailedDataParseException {
        List<Item> items = getItemsFromJSON();

        boolean isItemListValid = validateItems(items);
        // Storing if item list is empty is for logging purposes
        boolean isItemListEmpty = items.isEmpty();

        if (!isItemListValid || isItemListEmpty)
            throw new FailedDataParseException(
                    "Could not parse items correctly:\n" +
                            "- valid items = " + isItemListValid + "\n" +
                            "- is item list empty = " + isItemListEmpty);

        Main.LOG.info("Item parsing finished, item list size: " + items.size());
        return items;
    }

    /**
     * The actual JSON parser itself for the item JSON
     *
     * @return the list of parsed items
     */
    private List<Item> getItemsFromJSON() {

        List<Item> items = new ArrayList<>();

        try (FileReader fileReader = new FileReader(Constants.ITEMS_JSON_FILEPATH)) {

            JSONObject obj = new JSONObject(new JSONTokener(fileReader));
            JSONArray arr = obj.getJSONArray(Constants.ITEMS_JSON_ITEMS);

            for (Object o : arr) {

                JSONObject jsonItem = (JSONObject) o;

                int itemID = jsonItem.getInt("id");
                String itemName = jsonItem.getString("name");
                String itemEmoji = jsonItem.getString("emoji");
                String itemDesc = jsonItem.getString("description");
                String imgPath = jsonItem.getString("imgPath");
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
            Main.LOG.severe("Failed to parse JSON items file: " + ex.getMessage());
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
        Main.LOG.info("Item validation finished");
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
                || item.getDropChance() > Constants.ITEM_DROP_CHANCE_UPPER_B
                || item.getXpMultiplier() < Constants.XP_MULTIPLIER_LOWER_B
                || checkIsIDUnique(uniqueID)) {
            return false;
        }

        if (item.getItemType().equals(ItemType.UTILITY)) {
            UtilItem utilItem = (UtilItem) item;
            return utilItem.getGoldMultiplier() >= Constants.GOLD_MULTIPLIER_LOWER_B;

        } else if (item.getItemType().equals(ItemType.WEAPON)) {
            WeaponItem weaponItem = (WeaponItem) item;
            return weaponItem.getDmgMultiplier() >= Constants.DMG_MULTIPLIER_LOWER_B;
        }

        return true;
    }

    private boolean checkIsIDUnique(int id) {
        return idList.stream().filter(i -> i == id).count() >= 2;
    }
}
