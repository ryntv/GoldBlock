package ru.anime.goldblock;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.anime.goldblock.command.CreateGoldBlock;

import java.util.ArrayList;
import java.util.List;

import static ru.anime.goldblock.util.UtilColor.color;

public final class GoldBlock extends JavaPlugin {

    private static GoldBlock instance;

    public static GoldBlock getInstance(){
        return instance;
    }

    public Economy economy;
    private static FileConfiguration cfg;

    private List<Integer> a;
    @Override
    public void onEnable() {
            instance = this;

            if (!setupEconomy()){
                getLogger().info("Vault не найден! Сервер будет выключен");
                getServer().getPluginManager().disablePlugin(this);
            }

            saveDefaultConfig();

        cfg = getConfig();
        String startMessage = cfg.getString("message.startMessage");

        getLogger().info(startMessage);
        getCommand("GoldBlock").setExecutor(new CreateGoldBlock());
        a = cfg.getIntegerList("a");
        goldBlockPause();

    }

    public boolean setupEconomy(){
        RegisteredServiceProvider<Economy> registeredServiceProvider = getServer().getServicesManager().getRegistration(Economy.class);

        if(registeredServiceProvider == null){
            return false;
        }
        economy = registeredServiceProvider.getProvider();
        return true;
    }

    @Override
    public void onDisable() {

        getLogger().info("GoldBlock Disabled!");
    }

    public static FileConfiguration getCfg() {
        return cfg;
    }

    private void goldBlockPause() {
        new BukkitRunnable(){
            int time = cfg.getInt("time");
            @Override
            public void run() {
                if (time == 0){
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gb");
                    time = cfg.getInt("time")+cfg.getInt("timeGoldBlock");
                }
                if (a.contains(time)){
                    Bukkit.broadcastMessage(color(String.format(cfg.getString("message.startMessage"), getFormat(time))));
                }
                time--;
            }
        }.runTaskTimer(this , 0 ,20);

    }

    public static String getFormat(int Sec) {
        int hour = Sec / 3600;//3600
        int min = Sec % 3600 / 60;
        int sec = Sec % 60;
        return String.format("%02d:%02d:%02d", hour, min, sec);
    }

        // Повторить задержки через 5 минут
      //  Bukkit.getScheduler().runTaskLater(this, this::goldBlockPause, 20 * 60 * 5L); // 20 тиков * 60 секунд * 5 минут

}
