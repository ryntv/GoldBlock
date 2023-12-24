package ru.anime.goldblock.goldblock;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.antlr.v4.runtime.misc.NotNull;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IsRegionEmpty {
    public static boolean isRegionEmpty(int radius, @NotNull Location location) {
        try {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get((BukkitAdapter.adapt(location.getWorld())));

            Location point1 = new Location(location.getWorld(), location.getX() + radius, location.getY() + radius, location.getZ() + radius);
            Location point2 = new Location(location.getWorld(), location.getX() - radius, location.getY() - radius, location.getZ() - radius);

            ProtectedCuboidRegion region = new ProtectedCuboidRegion(UUID.randomUUID() + "_region",
                    BlockVector3.at(point1.getX(), point1.getY(), point1.getZ()),
                    BlockVector3.at(point2.getX(), point2.getY(), point2.getZ()));

            Map<String, ProtectedRegion> rg = regions.getRegions();
            List<ProtectedRegion> candidates = new ArrayList<>(rg.values());

            List<ProtectedRegion> overlapping = region.getIntersectingRegions(candidates);

            return overlapping.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

    }
}
