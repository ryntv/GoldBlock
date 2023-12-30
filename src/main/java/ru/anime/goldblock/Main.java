package ru.anime.goldblock;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.anime.goldblock.command.CommandGoldBlock;
import ru.anime.goldblock.goldblock.GoldBlock;
import ru.anime.goldblock.util.Metrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.anime.goldblock.util.UtilColor.color;

public final class Main extends JavaPlugin {

    private static Main instance;

    public static Main getInstance(){
        return instance;
    }

    public Economy economy;
    private static FileConfiguration cfg;

    private List<Integer> a;

    public static final Map<String, GoldBlock> goldBlocks = new HashMap<>();
    @Override
    public void onEnable() {
            instance = this;

            if (!setupEconomy()){
                getLogger().info("Vault не найден! Сервер будет выключен");
            }

            saveDefaultConfig();

        cfg = getConfig();
        String startMessage = cfg.getString("message.startMessage");

        getLogger().info(startMessage);
        CommandGoldBlock cmd = new CommandGoldBlock();
        getCommand("GoldBlock").setExecutor(cmd);
        getCommand("GoldBlock").setTabCompleter(cmd);
        a = cfg.getIntegerList("reportMessage");
        goldBlockPause();

        new Metrics(this, 20545);


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
        goldBlocks.values().forEach(GoldBlock::stop);
        goldBlocks.clear();
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
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gb create");
                    time = cfg.getInt("time")+cfg.getInt("timeGoldBlock");
                }
                if (a.contains(time)){
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(color(String.format(cfg.getString("message.startMessage"), getFormat(time))));
                    }
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
