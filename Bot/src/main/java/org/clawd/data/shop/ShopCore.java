package org.clawd.data.shop;

import net.dv8tion.jda.api.EmbedBuilder;
import org.clawd.data.items.Item;
import org.clawd.main.Main;
import org.clawd.tokens.Constants;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ShopCore {
    private final List<Item> itemList;
    private final List<EmbedBuilder> pages;
    private final int shopPagesCount;

    public ShopCore(List<Item> itemList) {
        // this has to be done, without, createPages() wont work
        this.itemList = itemList.stream().filter(i -> i.getDropChance() == 0).toList();
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

            EmbedBuilder embedBuilder = createShopBase(i);

            int startIndex = i * Constants.ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + Constants.ITEMS_PER_PAGE, this.itemList.size());

            for (int j = startIndex; j < endIndex; j++) {
                Item item = itemList.get(j);
                embedBuilder.addField(item.createShopField());
            }
            embedBuilders.add(embedBuilder);
        }
        return embedBuilders;
    }

    @NotNull
    private EmbedBuilder createShopBase(int i) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(":label: Shop");
        embedBuilder.setDescription(">>> Remember, use '/item' to inspect and buy!");
        embedBuilder.setColor(Color.ORANGE);
        embedBuilder.setFooter("Page: " + (i + 1) + "/" + shopPagesCount);
        return embedBuilder;
    }

    public List<EmbedBuilder> getPages() {
        return pages;
    }

    public int getShopPagesCount() {
        return shopPagesCount;
    }
}
