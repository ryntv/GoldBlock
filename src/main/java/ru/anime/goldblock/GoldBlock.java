package ru.anime.goldblock;

import org.bukkit.plugin.java.JavaPlugin;

public final class GoldBlock extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("GoldBlock Started!");

    }

    @Override
    public void onDisable() {
        getLogger().info("GoldBlock Disabled!");
    }
}
