package ru.anime.goldblock.goldblock;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import ru.anime.goldblock.Main;
import ru.anime.goldblock.util.UtilHologram;
import org.black_ixx.playerpoints.PlayerPointsAPI;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;
import static ru.anime.goldblock.util.UtilColor.color;

public class GoldBlock {

    private Location location;
    private BukkitTask task;

    private final World world;

    private int i;

    private Vector offsets;

    private String name;



    public GoldBlock(World world) {
        this.world = world;
        i = Main.getCfg().getInt("timeGoldBlock");
        offsets = new Vector(
                Main.getCfg().getDouble("hologramOffset.x"),
                Main.getCfg().getDouble("hologramOffset.y"),
                Main.getCfg().getDouble("hologramOffset.z")
        );
        name = UUID.randomUUID().toString();
    }
    private void tryStart(){
        location = LocationGenerator.getRandomLocation(world);
        if (location != null) {

            String messagePosition = String.format(Main.getCfg().getString("message.posMessage"), location.getBlockX(), location.getBlockY(), location.getBlockZ());
         //   Bukkit.broadcastMessage(color(messagePosition));
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(color(messagePosition));
            }
            WGHook.createRegion(location);
            task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), this::tick, 0, 20);
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), this::tryStart);
        }
    }
    public void start() {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), this::tryStart);
    }

    private void tick() {
        if (i > 0) {
            if (location.getBlock().getType() != Material.valueOf(Main.getCfg().getString("materialGoldBlock"))) {
                location.getBlock().setType(Material.valueOf(Main.getCfg().getString("materialGoldBlock")));
            }
            List<String> lines = Main.getCfg().getStringList("hologramLines");
            lines.replaceAll(s -> color(s.replace("{endtime}", Main.getFormat(i))));
            UtilHologram.createOrUpdateHologram(lines, location.clone().add(offsets), name);
            int radiusPay = Main.getCfg().getInt("radiusPay");
            List<Player> listPlayer = new ArrayList<>();
            for (Entity entity : world.getNearbyEntities(location, radiusPay, radiusPay, radiusPay)) {
                if (entity instanceof Player) {
                    listPlayer.add((Player) entity);
                    Player nearbyPlayer = (Player) entity;
                }
            }
            double count = Main.getCfg().getDouble("count");
            DecimalFormat decimalFormat = new DecimalFormat("#.00");




            if (!listPlayer.isEmpty()) {
                count /= listPlayer.size();
                String result = decimalFormat.format(count);
                for (Player player1 : listPlayer) {
                    if (Objects.equals(Main.getCfg().getString("economy"), "Vault")){
                        Main.getInstance().economy.depositPlayer(player1, count);
                        player1.sendMessage(
                                color(
                                        String.format(Main.getCfg().getString("message.youReceived"), result)
                                                .replace(".0", "")
                                )
                        );
                    } else if (Objects.equals(Main.getCfg().getString("economy"), "PlayerPoint")) {
                        PlayerPoints playerPoints = (PlayerPoints) getServer().getPluginManager().getPlugin("PlayerPoints");
                        assert playerPoints != null;
                        PlayerPointsAPI pointsAPI = playerPoints.getAPI();
                        UUID playerUUID = player1.getUniqueId();
                        int payCount = (int) Math.round(count);
                        pointsAPI.give(playerUUID, payCount);
                        player1.sendMessage(
                                color(
                                        String.format(Main.getCfg().getString("message.youReceived"), result)
                                                .replace(".0", "")
                                )
                        );
                    }

                }
            }

        } else {
            stop();
        }
        i--;
    }

    public void stop() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(color(Main.getCfg().getString("message.endMessage")));
        }


        location.getBlock().setType(Material.AIR);
        task.cancel();
        WGHook.removeRegion(location);
        UtilHologram.remove(name);
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }
}
