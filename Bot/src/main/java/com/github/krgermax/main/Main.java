package com.github.krgermax.main;

import com.github.krgermax.data.Generator;
import com.github.krgermax.data.biomes.Biome;
import com.github.krgermax.data.inventory.InventoryManager;
import com.github.krgermax.data.items.Item;
import com.github.krgermax.data.mineworld.MineworldManager;
import com.github.krgermax.data.mobs.Mob;
import com.github.krgermax.data.shop.ShopManager;
import com.github.krgermax.parser.exceptions.FailedDataParseException;
import com.github.krgermax.sql.SQLManager;
import net.dv8tion.jda.api.OnlineStatus;
import com.github.krgermax.parser.BiomeParser;
import com.github.krgermax.parser.ItemParser;
import com.github.krgermax.parser.MobParser;
import com.github.krgermax.tokens.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static Bot bot;
    public static Logger LOGGER;

    public static SQLManager sqlHandler;

    public static MineworldManager mineworldManager;

    public static InventoryManager inventoryManager;
    public static ShopManager shopManager;

    public static Generator generator;

    public static void main(String[] args) {
        LOGGER = Logger.getLogger(Constants.LOGGER_NAME);
        try {
            initialize();
            scheduleMineworldManagerCleanUp();
            scheduleInventoryCacheCleanUp();
            run(bot);
        } catch (FailedDataParseException ex) {
            LOGGER.severe(ex.getMessage());
        }
    }

    /**
     * Initializes everything needed to run the bot
     *
     * @throws FailedDataParseException If one of the parsers fails
     */
    private static void initialize() throws FailedDataParseException {
        sqlHandler = SQLManager.getInstance();

        ItemParser itemParser = new ItemParser();
        List<Item> itemList = itemParser.parseItems();

        MobParser mobParser = new MobParser();
        List<Mob> mobList = mobParser.parseMobs();

        BiomeParser biomeParser = new BiomeParser();
        List<Biome> biomeList = biomeParser.parseBiomes();

        shopManager = ShopManager.getInstance(itemList);
        inventoryManager = InventoryManager.getInstance();
        mineworldManager = MineworldManager.getInstance(biomeList, mobList);
        generator = Generator.getInstance();

        bot = Bot.getInstance();
    }

    /**
     * This method schedules the timing for inventory cache cleanups periodically
     */
    private static void scheduleInventoryCacheCleanUp() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> inventoryManager.inventoryCache.cleanUpCache(), 0, Constants.INV_CACHE_PERIOD_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * This method schedules the timing for mineworld cache cleanups periodically
     */
    private static void scheduleMineworldManagerCleanUp() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> mineworldManager.cleanupCache(), 0, Constants.MINE_CACHE_PERIOD_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * Infinite run loop
     * <ul>
     *     <li>
     *         '--exit' stops the bots execution
     *     </li>
     *     <li>
     *         '--rec sql' connects to the sql database
     *     </li>
     * </ul>
     *
     * @param bot Bot instance
     */
    private static void run(Bot bot) {
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            while ((line = reader.readLine()) != null) {
                if (line.equalsIgnoreCase("--rec sql")) {

                    bot.establishSQLConnection();

                } else if (line.equalsIgnoreCase("--exit")) {
                    bot.getJda().getPresence().setStatus(OnlineStatus.OFFLINE);
                    bot.getJda().shutdown();

                    bot.closeSQLConnection();

                    LOGGER.info("Bot shutdown");
                    return;
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage());
        }
    }
}