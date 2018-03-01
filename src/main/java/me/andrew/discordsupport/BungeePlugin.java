package me.andrew.discordsupport;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Created by Andrew on 21/11/2017.
 */

public class BungeePlugin extends Plugin {
    @Override
    public void onEnable() {
        // You should not put an enable message in your plugin.
        // BungeeCord already does so
        getLogger().info("Loading up");
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File guilds = new File(getDataFolder(), "guilds.json");

        if (!guilds.exists()) {
            try (InputStream in = getResourceAsStream("guilds.json")) {
                Files.copy(in, guilds.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Configuration configuration = null;
        try {
             configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(configuration != null){
            new DiscordSupportBot(configuration.getString("bottoken"),guilds);
        }
    }
}
