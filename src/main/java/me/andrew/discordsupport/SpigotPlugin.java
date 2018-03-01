package me.andrew.discordsupport;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Created by Andrew on 21/11/2017.
 */
public class SpigotPlugin extends JavaPlugin {
    public void onEnable() {
        saveDefaultConfig();
        this.saveResource("guilds.json", false);
        File guilds = new File(getDataFolder(), "guilds.json");
        new DiscordSupportBot(getConfig().getString("bottoken"),guilds);
    }
}
