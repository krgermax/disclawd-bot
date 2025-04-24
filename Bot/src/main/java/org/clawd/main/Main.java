package org.clawd.main;

import net.dv8tion.jda.api.OnlineStatus;
import org.clawd.data.Generator;
import org.clawd.data.Mineworld;
import org.clawd.data.biomes.Biome;
import org.clawd.data.inventory.InventoryHandler;
import org.clawd.data.items.Item;
import org.clawd.data.mobs.Mob;
import org.clawd.data.MobSpawner;
import org.clawd.data.shop.ShopHandler;
import org.clawd.parser.BiomeParser;
import org.clawd.parser.ItemParser;
import org.clawd.parser.MobParser;
import org.clawd.parser.exceptions.FailedDataParseException;
import org.clawd.sql.SQLHandler;
import org.clawd.tokens.Constants;

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

    public static SQLHandler sqlHandler;
    public static InventoryHandler inventoryHandler;
    public static ShopHandler shopHandler;

    public static Mineworld mineworld;
    public static Generator generator;
    public static MobSpawner mobSpawner;

    public static void main(String[] args) {
        LOGGER = Logger.getLogger(Constants.LOGGER_NAME);
        sqlHandler = new SQLHandler();

        try {
            initialize();
            scheduleCacheCleanUp();
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
        ItemParser itemParser = new ItemParser();
        List<Item> itemList = itemParser.parseItems();

        MobParser mobParser = new MobParser();
        List<Mob> mobList = mobParser.parseMobs();

        BiomeParser biomeParser = new BiomeParser();
        List<Biome> biomeList = biomeParser.parseBiomes();

        shopHandler = new ShopHandler(itemList);
        inventoryHandler = new InventoryHandler();

        mineworld = new Mineworld(biomeList);
        mobSpawner = new MobSpawner(mobList);
        generator = new Generator();

        bot = Bot.getInstance();
    }

    /**
     * This method schedules the timing for inventory cache clean up periodically specified by CACHE_PERIOD_MINUTES
     */
    private static void scheduleCacheCleanUp() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> Main.inventoryHandler.inventoryCache.cleanUpCache(), 0, Constants.CACHE_PERIOD_MINUTES, TimeUnit.MINUTES);
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