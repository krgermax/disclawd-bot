package com.github.krgermax.main;

import com.github.krgermax.buttons.ButtonManager;
import com.github.krgermax.commands.CommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import com.github.krgermax.tokens.BotTokens;
import com.github.krgermax.tokens.Constants;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

public class Bot {

    private static Bot INSTANCE;
    private final JDA jda;
    private Connection connection;

    private Bot() {

        Properties botProperties = loadProperties();

        establishSQLConnection();

        jda = JDABuilder.create(BotTokens.BOT_TOKEN, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)).build();
        jda.getPresence().setActivity(Activity.customStatus("The cake is a lie! " + botProperties.get("version")));

        addListeners();
        upsertCommands();

        Main.LOGGER.info("Bot finished loading");
    }

    /**
     * Calls the private constructor
     *
     * @return Bot instance
     */
    public static Bot getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Bot();
        }
        return INSTANCE;
    }

    /**
     * Creates a properties object using the bot.properties file
     *
     * @return Properties object
     */
    private Properties loadProperties() {
        Properties botProps = new Properties();
        try {
            String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();

            String configPath = rootPath + Constants.PROPERTIES_FILE_NAME;
            botProps.load(new FileInputStream(configPath));

        } catch (NullPointerException | IOException ex) {
            Main.LOGGER.severe("Error loading properties file: " + ex.getMessage());
        }
        return botProps;
    }

    /**
     * Establishes connection to the SQL database
     */
    public void establishSQLConnection() {
        try {
            Class.forName(Constants.SQL_CLASS_NAME);
            connection = DriverManager.getConnection("jdbc:mysql://" + BotTokens.JDBC_URL, BotTokens.SQL_USERNAME, "");

            if (connection != null && !connection.isClosed())
                Main.LOGGER.info("Successfully connected to SQL Database");

        } catch (ClassNotFoundException | SQLException ex) {
            Main.LOGGER.severe("Could not connect to SQL Database " + ex.getMessage());
        }
    }

    /**
     * Closes connection to the SQL database
     */
    public void closeSQLConnection() {
        try {
            if (connection != null && !connection.isClosed())
                this.connection.close();

            Main.LOGGER.info("SQL connection closed successfully");

        } catch (SQLException ex) {
            Main.LOGGER.severe("Could not close SQL connection" + ex.getMessage());
        }
    }

    private void addListeners() {
        jda.addEventListener(new CommandManager());
        jda.addEventListener(new ButtonManager());

        Main.LOGGER.info("Added listeners");
    }

    private void upsertCommands() {
        jda.upsertCommand(Constants.HELP_COMMAND_ID, "How does this work here?")
                .queue();
        jda.upsertCommand(Constants.BIOME_COMMAND_ID, "What do I have to mine?")
                .queue();
        jda.upsertCommand(Constants.INV_COMMAND_ID, "Show me my stuff!")
                .queue();
        jda.upsertCommand(Constants.SHOP_COMMAND_ID, "List me everything you have")
                .queue();
        jda.upsertCommand(Constants.ITEM_COMMAND_ID, "This one looks interesting")
                .addOption(OptionType.STRING, Constants.ITEM_COMMAND_OPTION_ID, "Yes exactly this one!", true, false)
                .queue();
        Main.LOGGER.info("Added commands");
    }

    public Connection getSQLConnection() {
        return this.connection;
    }

    public JDA getJda() {
        return this.jda;
    }
}
