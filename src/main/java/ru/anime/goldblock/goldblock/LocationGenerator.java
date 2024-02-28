package ru.anime.goldblock.goldblock;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class LocationGenerator {
    public static int getHighestBlock(Chunk chunk, int x, int z, List<Material> materialList, int heightMin, int heightMax){


        boolean upBlockIsAir = false;
        for (int y = heightMax; y > heightMin; y--) {
            if (!chunk.getBlock(x, y, z).getType().isAir()) {
                if (!materialList.contains(chunk.getBlock(x, y, z).getType())) {
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
    public static Chunk getRandomChunk(World world, int centerX, int centerZ, int radius) {
        // Генерация случайных координат в пределах радиуса
        int randomX = (int) (Math.random() * (2 * radius + 1) - radius);
        int randomZ = (int) (Math.random() * (2 * radius + 1) - radius);

        // Получение чанка по случайным координатам
        int chunkX = (centerX + randomX) >> 4;
        int chunkZ = (centerZ + randomZ) >> 4;
        return world.getChunkAt(chunkX, chunkZ);
    }
    @Nullable
    public static Location getRandomLocation(World world, int centerX, int centerZ, List<Material> materialList, int heightMin, int heightMax, int radius, int radiusPay){
        Chunk chunk = getRandomChunk(world, centerX, centerZ, radius);
        int y = getHighestBlock(chunk, 8 ,8, materialList, heightMin, heightMax);
        if (y != -1){
            Location l = chunk.getBlock(8, y+1 ,8).getLocation();
             if (WGHook.isRegionEmpty(radiusPay, l)){
                return l;
            }

        }
        return null;
    }
}
