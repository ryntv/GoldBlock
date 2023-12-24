package ru.anime.goldblock.goldblock;

import org.bukkit.Chunk;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class GetHighestBlock {
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
}
