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
        List<String> materialNames = Main.getCfg().getStringList("materialList");

        for (String materialName : materialNames) {
            Material material = Material.matchMaterial(materialName);
            if (material != null) {
                list.add(material);
            } else {
                break;
            }
        }

        boolean upBlockIsAir = false;
        for (int y = Main.getCfg().getInt("height.max"); y > Main.getCfg().getInt("height.min"); y--) {
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
