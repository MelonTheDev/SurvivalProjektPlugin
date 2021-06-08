package wtf.melonthedev.survivalprojektplugin.others;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import wtf.melonthedev.survivalprojektplugin.Main;
import java.io.File;
import java.io.IOException;

public class Config {

    private static File configfile;

    public static FileConfiguration getCustomConfig(String configFileName) {
        if (!Main.getPlugin().getDataFolder().exists()) Main.getPlugin().getDataFolder().mkdirs();
        configfile = new File(Main.getPlugin().getDataFolder(), configFileName);
        if (!(configfile.exists())) {
            try {
                configfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return YamlConfiguration.loadConfiguration(configfile);
    }
    public static void saveCustomConfig(FileConfiguration config) {
        try {
            config.save(configfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveNewCustomConfig(FileConfiguration config, String configFileName) {
        configfile = new File(Main.getPlugin().getDataFolder(), configFileName);
        if (!(configfile.exists())) {
            try {
                configfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        try {
            config.save(configfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
