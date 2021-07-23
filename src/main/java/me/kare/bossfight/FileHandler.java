package me.kare.bossfight;

import de.leonhard.storage.Json;

import java.io.File;

public class FileHandler {
    public static Json data;
    public void start(){
        data = new Json("flags",getDataDir().getPath());
        data.setDefault("ZombieKilled", false);
    }

    public File getDataDir(){
        var plugin = BossFight.getInstance();
        if(!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        return plugin.getDataFolder();
    }

    public static void save() {
        data.write();
    }
}
