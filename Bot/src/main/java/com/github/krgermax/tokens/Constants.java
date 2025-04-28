package com.github.krgermax.tokens;

import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.io.File;

public final class Constants {
    public static final String LOGGER_NAME = "DISCLAWD BOT";
    public static final String SQL_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    public static final String PROPERTIES_FILE_NAME = "bot.properties";

    /*
     * SQL Column labels
     */

    public static final String USER_ID_COLUMN_LABEL = "userID";
    public static final String MINED_COLUMN_LABEL = "minedCount";
    public static final String XP_COLUMN_LABEL = "xpCount";
    public static final String GOLD_COLUMN_LABEL = "goldCount";
    public static final String MOB_KILLS_COLUMN_LABEL = "mobKills";
    public static final String BOSS_KILLS_COLUMN_LABEL = "bossKills";
    public static final String ITEM_ID_COLUMN_LABEL = "itemID";
    public static final String EQUIPPED_ITEM_COLUMN_LABEL = "equipedItemID"; // I know it's a typo, too lazy to fix, will do it one day

    /*
     * JSON String constants
     */

    public static final String JSON_BASE_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "jsonfiles";

    public static final String ITEMS_JSON_FILEPATH = File.separator + "items.json";
    public static final String ITEMS_JSON_ITEMS = "items";
    public static final String MOBS_JSON_FILEPATH = File.separator + "mobs.json";
    public static final String MOBS_JSON_MOBS = "mobs";
    public static final String BIOMES_JSON_FILEPATH = File.separator + "biome.json";
    public static final String BIOMES_JSON_BIOMES = "biomes";

    /*
     * Image base paths
     */

    public static final String BIOME_IMAGE_BASE_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "images" + File.separator + "biomes";
    public static final String ITEM_IMAGE_BASE_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "images" + File.separator + "items";
    public static final String MOB_IMAGE_BASE_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "images" + File.separator + "mobs";

    /*
     * THRESHOLDS for item params
     */

    public static final double ITEM_DROP_CHANCE_UPPER_B = 1;
    public static final double XP_MULTIPLIER_LOWER_B = 1;
    public static final double GOLD_MULTIPLIER_LOWER_B = 0;
    public static final double DMG_MULTIPLIER_LOWER_B = 0;

    /*
     * THRESHOLDS for mob params
     */

    public static final double MOB_SPAWN_CHANCE_LOWER_B = 0;
    public static final double MOB_SPAWN_CHANCE_UPPER_B = 1;
    public static final double XP_DROP_AMOUNT_LOWER_B = 1;
    public static final double GOLD_DROP_AMOUNT_LOWER_B = 1;

    /*
     * Command Id's
     */

    public static final String BIOME_COMMAND_ID = "biome";
    public static final String INV_COMMAND_ID = "inventory";
    public static final String SHOP_COMMAND_ID = "shop";
    public static final String ITEM_COMMAND_ID = "item";
    public static final String HELP_COMMAND_ID = "help";
    public static final String RANK_COMMAND_ID = "rank";

    /*
     * Command option Id's
     */

    public static final String ITEM_COMMAND_OPTION_ID = "name";
    public static final String RANK_COMMAND_OPTION_ID = "category";
    public static final String XP_OPTION_ID = "XP";
    public static final String GOLD_OPTION_ID = "Gold";
    public static final String KILLS_OPTION_ID = "Kills";
    public static final String MINED_OPTION_ID = "Mined";

    /*
     * Button Id's
     */

    public static final String MINE_BUTTON_ID = "mine";
    public static final String NEXT_SHOP_BUTTON_ID = "next-shop";
    public static final String BACK_SHOP_BUTTON_ID = "back-shop";
    public static final String HOME_SHOP_BUTTON_ID = "home-shop";
    public static final String NEXT_INV_BUTTON_ID = "next-inv";
    public static final String HOME_INV_BUTTON_ID = "home-inv";
    public static final String BACK_INV_BUTTON_ID = "back-inv";
    public static final String BUY_BUTTON_ID = "buy-";
    public static final String EQUIP_BUTTON_ID = "equip-";
    public static final String HIT_BUTTON_ID = "hit-mob-";

    /*
     * Button emoji's
     */

    public static final Emoji MINE_BUTTON_EMOJI = Emoji.fromUnicode("U+1F4A5");
    public static final Emoji NEXT_BUTTON_EMOJI = Emoji.fromUnicode("U+25B6");
    public static final Emoji HOME_BUTTON_EMOJI = Emoji.fromUnicode("U+1F504");
    public static final Emoji BACK_BUTTON_EMOJI = Emoji.fromUnicode("U+25C0");

    /*
     * Embedded message emoji's
     */

    public static final String BLACK_SMALL_SQUARE = ":black_small_square:";
    public static final String RED_CROSS = ":x:";

    /*
     * Base stat constants
     */

    public static final double BASE_DAMAGE_MULTIPLIER = 1.0;
    public static final double BASE_XP_MULTIPLIER = 1.0;
    public static final double BASE_GOLD_MULTIPLIER = 1.0;

    /*
     * Others
     */

    public static final int ITEMS_PER_PAGE = 3;
    public static final String PATH_LOGO_IMG = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "images" + File.separator + "disclawd_logo.png";
    public static final int MAX_MINE_NOT_INTERACTED_MINUTES = 2;
    public static final long CACHE_EXPIRY_MINUTES = 5;
    public static final int CACHE_PERIOD_MINUTES = 2;

    /*
     * Help command description
     */

    public static final String HELP_COMMAND_DESC = ">>> Hi there again :sloth: Welcome to the **Helpcenter**! From here" +
            " I will try to guide you trough the basics of **Disclawd**, this sounds great right? So let's begin!";
    public static final String HELP_WHAT_IS = ">>> What is **Disclawd**? Simple put, it is a clicker game for Discord. Ore's" +
            " from a pretty familiar game are to be mined! While doings this collect, **XP :sparkles:**, **GOLD :coin:** and Items. Also do not" +
            " forget to fight the **monsters** that can spawn!";
    public static final String HELP_END_TEXT = ">>> But now enough of reading! On the next page you will find the most important commands" +
            " and more to get started. Have fun! ~ max";
}
