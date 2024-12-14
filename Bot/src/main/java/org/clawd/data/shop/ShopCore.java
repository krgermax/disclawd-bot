package org.clawd.data.shop;

import net.dv8tion.jda.api.EmbedBuilder;
import org.clawd.data.items.Item;
import org.clawd.data.items.UtilItem;
import org.clawd.data.items.WeaponItem;
import org.clawd.data.items.enums.ItemType;
import org.clawd.main.Main;
import org.clawd.tokens.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ShopCore {
    private final List<Item> itemList;
    private final List<EmbedBuilder> pages;
    private final int shopPagesCount;

    public ShopCore(List<Item> itemList) {
        this.itemList = itemList.stream().filter(i -> i.getDropChance() == 0).toList();
        // this has to be done, without createPages() wont work
        this.shopPagesCount = (int) Math.ceil((double) itemList.size() / Constants.ITEMS_PER_PAGE);
        this.pages = createPages();
        Main.LOG.info("Initialized shop core. Pages: " + this.shopPagesCount);
    }

    /**
     * Creates a list of embedded builders which do represent the single shop pages
     *
     * @return A list of all "pages"
     */
    private List<EmbedBuilder> createPages() {
        List<EmbedBuilder> embedBuilders = new ArrayList<>();
        for (int i = 0; i < this.shopPagesCount; i++) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(":label: Shop");
            embedBuilder.setDescription(">>> Remember, use '/item' to inspect and buy!");
            embedBuilder.setColor(Color.ORANGE);
            embedBuilder.setFooter("Page: " + (i + 1) + "/" + shopPagesCount);

            int startIndex = i * Constants.ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + Constants.ITEMS_PER_PAGE, this.itemList.size());

            for (int j = startIndex; j < endIndex; j++) {
                // TODO: a lot of this can be done in the respective item class
                Item item = itemList.get(j);

                String itemName = item.getName();
                int itemPrice = item.getPrice();
                double itemXPMult = item.getXpMultiplier();
                String alternativeTxt;
                double alternativePerk;

                if (item.getItemType() == ItemType.UTILITY) {
                    UtilItem utilItem = (UtilItem) item;
                    alternativeTxt = ":black_small_square: Gold boost: ";
                    alternativePerk = utilItem.getGoldMultiplier();
                } else {
                    WeaponItem weaponItem = (WeaponItem) item;
                    alternativeTxt = ":black_small_square: Damage boost: ";
                    alternativePerk = weaponItem.getDmgMultiplier();
                }

                embedBuilder.addField(
                        item.getEmoji() + itemName + item.getEmoji(),
                        ":black_small_square: XP boost: " + itemXPMult + "\n"
                                + alternativeTxt + alternativePerk + "\n"
                                + ":black_small_square: Price: " + itemPrice + " Coins" + "\n"
                                + ":black_small_square: Required lvl. " + item.getReqLvl(),
                        true);
            }
            embedBuilders.add(embedBuilder);
        }
        return embedBuilders;
    }

    public List<EmbedBuilder> getPages() {
        return pages;
    }

    public int getShopPagesCount() {
        return shopPagesCount;
    }
}
