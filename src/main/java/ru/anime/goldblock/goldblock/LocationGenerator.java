package ru.anime.goldblock.goldblock;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import ru.anime.goldblock.Main;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class LocationGenerator {
    public static int getHighestBlock(Chunk chunk, int x, int z){
        List<Material> list = new ArrayList<>();
        list.add(Material.GRASS_BLOCK);
        list.add(Material.SAND);
        boolean upBlockIsAir = false;
        for (int y = 80; y > 50; y--) {
            if (!chunk.getBlock(x, y, z).getType().isAir()) {
                if (!list.contains(chunk.getBlock(x, y, z).getType())) {
                    return -1;
                }
                if (upBlockIsAir)
                    return y;
                else
                    return -1;
            } else {
                upBlockIsAir = true;
            }
        }
        return -1;
    }
    public static Chunk getRandomChunk(World world) {
        int randomX = (int) (Main.getCfg().getInt("center.x") + (int) ((Math.random() * 2 - 1) * Main.getCfg().getInt("radius.x")));
        int randomZ = (int) (Main.getCfg().getInt("center.z") + (int) ((Math.random() * 2 - 1) * Main.getCfg().getInt("radius.z")));

        return world.getChunkAt(randomX >> 4, randomZ >> 4);
    }
    @Nullable
    public static Location getRandomLocation(World world){
        Chunk chunk = getRandomChunk(world);
        int y = getHighestBlock(chunk, 8 ,8);
        if (y != -1){
            Location l = chunk.getBlock(8, y+1 ,8).getLocation();
            if (WGHook.isRegionEmpty(Main.getCfg().getInt("radiusPay"), l)){
                return l;
            }
        }
        return null;
    }
}
