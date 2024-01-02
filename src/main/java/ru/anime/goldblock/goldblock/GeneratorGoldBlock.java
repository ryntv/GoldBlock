package ru.anime.goldblock.goldblock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import ru.anime.goldblock.Main;

import java.util.HashMap;
import java.util.Map;

public class GeneratorGoldBlock {
    private World world;
    private int i;
    private static BukkitTask task;

    private Location location;

    public static final Map<String, Location> listGoldBlocks = new HashMap<>();

    public GeneratorGoldBlock(World world) {
        this.world = world;
        i = 10;
    }

    public void start() {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), this::listGoldBlock);
    }

    private void listGoldBlock() {
        task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), this::tick, 0, 200);


    }


    private void tick() {
        if (i > 0){
            location = LocationGenerator.getRandomLocation(world);
            if (location != null){
                String goldBlock = location.getBlockX()+"_"+location.getBlockY()+"_"+location.getBlockZ();
                listGoldBlocks.put(goldBlock, location);
            }
        }

    }

    static public void close(){
        task.cancel();
    }
}
