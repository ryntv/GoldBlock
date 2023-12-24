package ru.anime.goldblock.command;

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
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.anime.goldblock.GoldBlock;
import ru.anime.goldblock.goldblock.GetHighestBlock;
import ru.anime.goldblock.goldblock.IsRegionEmpty;
import ru.anime.goldblock.util.UtilColor;
import ru.anime.goldblock.util.UtilHologram;

import java.util.*;

import static org.bukkit.Bukkit.getServer;
import static ru.anime.goldblock.util.UtilColor.color;


public class CreateGoldBlock implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("goldblock.create")){

            return true;
        }
        Chunk randomChunk;
        int y;
        while (true) {

            randomChunk = getRandomChunk(Bukkit.getWorlds().get(0));
            y = GetHighestBlock.getHighestBlock(randomChunk, 8, 8);
            if (y != -1) {
                // тут проверяет безопасно или нет
                break;
            }
        }
        int chunk_x = randomChunk.getX() * 16 + 8;
        int chunk_z = randomChunk.getZ() * 16 + 8;
        Location location_goldblock = new Location(Bukkit.getWorlds().get(0), chunk_x, y + 1, chunk_z);
        if (IsRegionEmpty.isRegionEmpty(20, location_goldblock)) {

            randomChunk.getBlock(8, y + 1, 8).setType(Material.GOLD_BLOCK);
            WorldGuardPlugin worldGuard = getWorldGuard();
            if (worldGuard != null) {
                World world = Bukkit.getWorlds().get(0); // Получаем первый загруженный мир, измените это при необходимости
                com.sk89q.worldguard.WorldGuard wg = com.sk89q.worldguard.WorldGuard.getInstance();
                RegionManager regionManager = wg.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
                if (regionManager != null) {
                    int privateRadius = GoldBlock.getCfg().getInt("radiusPay");
                    // Указываем координаты приватной зоны напрямую
                    BlockVector3 min = BlockVector3.at(chunk_x - privateRadius, (y + 1) - privateRadius, chunk_z - privateRadius);
                    BlockVector3 max = BlockVector3.at(chunk_x + privateRadius, (y + 1) + privateRadius, chunk_z + privateRadius);
                    ProtectedCuboidRegion region = new ProtectedCuboidRegion("GoldBlock" + chunk_x + "_" + chunk_z + "_" +y, min, max);
                    regionManager.addRegion(region);
                    StateFlag.State state = StateFlag.State.DENY;
                    region.setFlag(Flags.BUILD, state);
                    try {
                        regionManager.save();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                }
            } else {

            }
            String messagePosition = "&eКоординаты золотого блока" + " x: " + chunk_x + " y: " + (y + 1) + " z: " + chunk_z;
            // GoldBlock.getInstance().economy.depositPlayer(player, 100);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(color(messagePosition));
            }
            Chunk finalRandomChunk = randomChunk;
            int finalY = y;
            new BukkitRunnable() {
                int i = GoldBlock.getCfg().getInt("timeGoldBlock");
                Vector offsets = new Vector(
                        GoldBlock.getCfg().getDouble("hologramOffset.x"),
                        GoldBlock.getCfg().getDouble("hologramOffset.y"),
                        GoldBlock.getCfg().getDouble("hologramOffset.z")
                );
                String name = "goldblock" + chunk_x + "_" + finalY + "_" + chunk_z;

                @Override
                public void run() {

                    if (i > 0) {
                        List<String> lines = GoldBlock.getCfg().getStringList("hologramLines");
                        lines.replaceAll(s -> UtilColor.color(s.replace("{endtime}", GoldBlock.getFormat(i))));
                        UtilHologram.createOrUpdateHologram(lines, location_goldblock.clone().add(offsets), name);
                        int radiusPay = GoldBlock.getCfg().getInt("radiusPay");
                        List<Player> listPlayer = new ArrayList<>();
                        for (Entity entity : finalRandomChunk.getWorld().getNearbyEntities(location_goldblock, radiusPay, radiusPay, radiusPay)) {
                            if (entity instanceof Player) {
                                listPlayer.add((Player) entity);
                                Player nearbyPlayer = (Player) entity;
                            }
                        }
                        double count = GoldBlock.getCfg().getDouble("count");
                        if (!listPlayer.isEmpty()) {
                            count /= listPlayer.size();
                            for (Player player1 : listPlayer) {
                                GoldBlock.getInstance().economy.depositPlayer(player1, count);
                                player1.sendMessage(
                                        color(
                                                String.format(GoldBlock.getCfg().getString("message.youReceived"), count)
                                                        .replace(".0", "")
                                        )
                                );
                            }
                        }

                    } else {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(color(GoldBlock.getCfg().getString("message.endMessage")));
                        }
                        WorldGuardPlugin worldGuard = getWorldGuard();

                        World world = Bukkit.getWorlds().get(0); // Получаем первый загруженный мир, измените это при необходимости
                        com.sk89q.worldguard.WorldGuard wg = com.sk89q.worldguard.WorldGuard.getInstance();
                        RegionManager regionManager = wg.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));

                        // Удаляем зону по имени
                        if (regionManager.hasRegion("GoldBlock" + chunk_x + "_" + chunk_z + "_" + finalY)) {
                            regionManager.removeRegion("GoldBlock" + chunk_x + "_" + chunk_z + "_" + finalY);
                            try {
                                regionManager.save();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                        }


                        finalRandomChunk.getBlock(8, finalY + 1, 8).setType(Material.AIR);
                        cancel();
                        UtilHologram.remove(name);
                    }
                    i--;

                }

            }.runTaskTimer(GoldBlock.getInstance(), 0, 20);


        }

        return true;
    }


    public Chunk getRandomChunk(World world) {
        int randomX = (int) (GoldBlock.getCfg().getInt("center.x") + (int) ((Math.random() * 2 - 1) * GoldBlock.getCfg().getInt("radius.x")));
        int randomZ = (int) (GoldBlock.getCfg().getInt("center.z") + (int) ((Math.random() * 2 - 1) * GoldBlock.getCfg().getInt("radius.z")));

        return world.getChunkAt(randomX >> 4, randomZ >> 4);
    }



    private WorldGuardPlugin getWorldGuard() {
        return (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");
    }


}

