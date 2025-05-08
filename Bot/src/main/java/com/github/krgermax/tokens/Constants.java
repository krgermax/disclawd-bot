package com.github.krgermax.tokens;

import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.io.File;

public final class Constants {
    public static final String LOGGER_NAME = "DISCLAWD";

    /*
     * JSON String constants
     */

    public static final String JSON_BASE_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "jsonfiles";

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

    /*
     * Help command description
     */

    public static final String HELP_COMMAND_DESC = """
        >>> Hi there again :sloth: Welcome to the **Helpcenter**! From here I will try to guide you \
        through the basics of **Disclawd**, this sounds great right? So let's begin!
        """;

    public static final String HELP_WHAT_IS = """
        >>> What is **Disclawd**? Simply put, it is a clicker game for Discord. Ores from a pretty \
        familiar game are to be mined! While doing this, collect **XP :sparkles:**, **GOLD :coin:** \
        and items. Also, do not forget to fight the **monsters** that can spawn!
        """;

    public static final String HELP_END_TEXT = """
        >>> But now enough of reading! On the next page you will find the most important commands \
        and more to get started. Have fun! ~ max
        """;
}
