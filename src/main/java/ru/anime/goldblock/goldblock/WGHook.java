package ru.anime.goldblock.goldblock;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.antlr.v4.runtime.misc.NotNull;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class WGHook {

    public static boolean isRegionEmpty(int radius, @NotNull Location location) {
        try {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get((BukkitAdapter.adapt(location.getWorld())));

            Location point1 = new Location(location.getWorld(), location.getBlockX() + radius, location.getBlockY() + radius, location.getBlockZ() + radius);
            Location point2 = new Location(location.getWorld(), location.getBlockX() - radius, location.getBlockY() - radius, location.getBlockZ() - radius);

            ProtectedCuboidRegion region = new ProtectedCuboidRegion(UUID.randomUUID() + "_region",
                    BlockVector3.at(point1.getBlockX(), point1.getBlockY(), point1.getBlockZ()),
                    BlockVector3.at(point2.getBlockX(), point2.getBlockY(), point2.getBlockZ()));

            Map<String, ProtectedRegion> rg = regions.getRegions();
            List<ProtectedRegion> candidates = new ArrayList<>(rg.values());

            List<ProtectedRegion> overlapping = region.getIntersectingRegions(candidates);

            return overlapping.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

    }

    public static void createRegion(Location location, int privateRadius) {
        WorldGuardPlugin worldGuard = getWorldGuard();
        if (worldGuard != null) {
            World world = location.getWorld();
            com.sk89q.worldguard.WorldGuard wg = com.sk89q.worldguard.WorldGuard.getInstance();
            RegionManager regionManager = wg.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
            if (regionManager != null) {
                // Указываем координаты приватной зоны напрямую
                BlockVector3 min = BlockVector3.at(location.getBlockX() - privateRadius, (location.getBlockY()) - privateRadius, location.getBlockZ() - privateRadius);
                BlockVector3 max = BlockVector3.at(location.getBlockX() + privateRadius, (location.getBlockY()) + privateRadius, location.getBlockZ() + privateRadius);
                ProtectedCuboidRegion region = new ProtectedCuboidRegion("GoldBlock" + location.getBlockX() + "_" + location.getBlockZ() + "_" + location.getBlockY(), min, max);
                regionManager.addRegion(region);
                StateFlag.State state = StateFlag.State.ALLOW;
                region.setFlag(Flags.PVP, state);
                try {
                    regionManager.save();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
            }
        } else {

        }
    }

    public static void removeRegion(Location location) {
        World world = location.getWorld(); // Получаем первый загруженный мир, измените это при необходимости
        com.sk89q.worldguard.WorldGuard wg = com.sk89q.worldguard.WorldGuard.getInstance();
        RegionManager regionManager = wg.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));

        // Удаляем зону по имени
        if (regionManager.hasRegion("GoldBlock" + location.getBlockX() + "_" + location.getBlockZ() + "_" + location.getBlockY())) {
            regionManager.removeRegion("GoldBlock" + location.getBlockX() + "_" + location.getBlockZ() + "_" + location.getBlockY());
            try {
                regionManager.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        }


    }

    private static WorldGuardPlugin getWorldGuard() {
        return (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");
    }
}
