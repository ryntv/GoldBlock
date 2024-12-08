package ru.anime.goldblock;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import ru.anime.goldblock.command.CommandGoldBlock;
import ru.anime.goldblock.goldblock.GoldBlock;
import ru.anime.goldblock.goldblock.LoadGoldBlock;
import ru.anime.goldblock.util.FormatTime;
import ru.anime.goldblock.util.GoldBlockPlaceholderExpansion;
import ru.anime.goldblock.util.Metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Main extends JavaPlugin {
    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    public Economy economy;
    private static FileConfiguration cfg;
    public static Map<String, GoldBlock> goldBlocks = new HashMap<>();
    private GoldBlockPlaceholderExpansion placeholderExpansion;

    @Override
    public void onEnable() {
        instance = this;

        if (!setupEconomy()) {
            getLogger().info("Vault не найден!");
        }

        saveDefaultConfig();

        cfg = getConfig();
        LoadGoldBlock.loadGoldBlock(cfg);
        new Metrics(this, 20545);
        getCommand("goldblock").setExecutor(new CommandGoldBlock());
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholderExpansion = new GoldBlockPlaceholderExpansion(this);
            placeholderExpansion.register();
        } else {
            getLogger().warning("PlaceholderAPI не обнаружен! Некоторые функции могут быть недоступны.");
        }

        Bukkit.getScheduler().runTaskTimer(this, this::tick, 0, 20);


    }

    public boolean setupEconomy() {
        RegisteredServiceProvider<Economy> registeredServiceProvider = getServer().getServicesManager().getRegistration(Economy.class);

        if (registeredServiceProvider == null) {
            return false;
        }
        economy = registeredServiceProvider.getProvider();
        return true;
    }

    @Override
    public void onDisable() {
        goldBlocks.values().forEach(GoldBlock::stop);
        goldBlocks.clear();
        getLogger().info("GoldBlock Disabled!");
        if (placeholderExpansion != null) {
            placeholderExpansion.unregister();
        }
    }
    public void reload(){
        goldBlocks.values().forEach(GoldBlock::stop);
        goldBlocks.clear();

        reloadConfig();
        cfg = getConfig();

        LoadGoldBlock.loadGoldBlock(cfg);
        getLogger().info("GoldBlock Reload!");
    }

    public static FileConfiguration getCfg() {
        return cfg;
    }

    private void goldBlockPause() {

    }

    private void tick() {
        goldBlocks.values().forEach(GoldBlock::tick);
    }

    public static String getFormat(int Sec) {
        int hour = Sec / 3600;//3600
        int min = Sec % 3600 / 60;
        int sec = Sec % 60;
        if (Objects.equals(cfg.getString("TypeFormat"), "integer")) {
            return FormatTime.integerFormat(hour, min, sec);
        } else if (Objects.equals(cfg.getString("TypeFormat"), "string")) {
            return FormatTime.stringFormat(hour, min, sec);
        }
        return FormatTime.integerFormat(hour, min, sec);

    }

    // Повторить задержки через 5 минут
    //  Bukkit.getScheduler().runTaskLater(this, this::goldBlockPause, 20 * 60 * 5L); // 20 тиков * 60 секунд * 5 минут

}
