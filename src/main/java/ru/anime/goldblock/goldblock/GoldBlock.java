package ru.anime.goldblock.goldblock;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import ru.anime.goldblock.Main;
import ru.anime.goldblock.util.UtilHologram;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;
import static ru.anime.goldblock.Main.getFormat;
import static ru.anime.goldblock.Main.goldBlocks;
import static ru.anime.goldblock.util.UtilColor.color;


public class GoldBlock {
    private final String id;
    private final String world;
    private final String economy;
    private final String blockMovementType;
    private final Integer radius;
    private final Integer heightMax;
    private final Integer heightMin;
    private final Integer centerX;
    private final Integer centerZ;
    private final Vector posGoldBlock;
    private final Integer timeGoldBlock;
    private final Integer minPlayer;
    private final Integer count;
    private final Boolean shareCount;
    private final Integer radiusPay;
    private final Material materialGoldBlock;
    private final List<Material> materialList;
    private final Integer time;
    private Integer timeUpdate;
    private final List<String> hologramLines;
    private final Vector hologramOffset;
    private final Map<String, String> message;
    private final List<Integer> reportMessage;
    private BukkitTask task;
    private List<Location> generateLocationList;
    private final Integer isDefaultGold;
    private Integer runTimeUpdate;
  //  private final   Map<Integer, List<String>> commandManager; - будущий функционал

    public GoldBlock(String id, String world, String economy, String blockMovementType, Integer radius, Integer heightMax,
                     Integer heightMin, Integer centerX, Integer centerZ, Vector posGoldBlock, Integer timeGoldBlock,
                     Integer minPlayer, Integer count, Boolean shareCount, Integer radiusPay, Material materialGoldBlock,
                     List<Material> materialList, Integer time, Integer timeUpdate, List<String> hologramLines,
                     Vector hologramOffset, Map<String, String> message, List<Integer> reportMessage, BukkitTask task,List<Location> generateLocationList, Integer isDefaultGold, Integer runTimeUpdate) {
        this.id = id;
        this.world = world;
        this.economy = economy;
        this.blockMovementType = blockMovementType;
        this.radius = radius;
        this.heightMax = heightMax;
        this.heightMin = heightMin;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.posGoldBlock = posGoldBlock;
        this.timeGoldBlock = timeGoldBlock;
        this.minPlayer = minPlayer;
        this.count = count;
        this.shareCount = shareCount;
        this.radiusPay = radiusPay;
        this.materialGoldBlock = materialGoldBlock;
        this.materialList = materialList;
        this.time = time;
        this.timeUpdate = timeUpdate;
        this.hologramLines = hologramLines;
        this.hologramOffset = hologramOffset;
        this.message = message;
        this.reportMessage = reportMessage;
        this.task = task;
        this.generateLocationList = generateLocationList;
        this.isDefaultGold = isDefaultGold;
        this.runTimeUpdate = runTimeUpdate;
       // this.commandManager = commandManager;
    }

    public void tick() {
        if (timeUpdate == 0) {
            if (blockMovementType.equals("static") && generateLocationList.isEmpty()){
                Location location = new Location(Bukkit.getWorld(world), posGoldBlock.getX(), posGoldBlock.getY(), posGoldBlock.getZ());
                generateLocationList.add(location);
                WGHook.createRegion(location, radiusPay);
            }
            if (!generateLocationList.isEmpty()){
                createGoldBlock();
            } else {
                while (generateLocationList.isEmpty()){
                    Location location = LocationGenerator.getRandomLocation(Bukkit.getWorld(world), centerX, centerZ, materialList, heightMin, heightMax, radius, radiusPay);
                    if (location != null){
                        generateLocationList.add(location);
                        createGoldBlock();
                    }
                }
            }

            timeUpdate = time + timeGoldBlock;
        } else if(timeUpdate.equals(time) && Bukkit.getOnlinePlayers().size() < minPlayer) {
          //  System.out.println("Не достаточно игроков для начала золотого блока!");
        } else {
            if (reportMessage.contains(timeUpdate) && isDefaultGold == 1){
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(color(String.format(message.get("startMessage"), getFormat(timeUpdate))));
                }
            }
            if (timeUpdate % 10 == 0 && blockMovementType.equals("random") && generateLocationList.size() < 5){
                Location location = LocationGenerator.getRandomLocation(Bukkit.getWorld(world), centerX, centerZ, materialList, heightMin, heightMax, radius, radiusPay);
                if (location != null){
                    generateLocationList.add(location);
                }
            }
            timeUpdate--;
        }

    }

    public  void createGoldBlock(){
        for (int i = generateLocationList.size(); i > 0; i--) {
            if (!generateLocationList.isEmpty()){
                Location location = generateLocationList.get(0);
                if (WGHook.isRegionEmpty(radiusPay, location) || blockMovementType.equals("static")){
                    task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), this::tickGoldBlock, 0, 20);
                    String messagePosition = String.format(message.get("posMessage"), location.getBlockX(), location.getBlockY(), location.getBlockZ());
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(color(messagePosition));
                    }
                    if (blockMovementType.equals("random")) {
                        WGHook.createRegion(location, radiusPay);
                    }
                    break;
                }
            }
            generateLocationList.remove(0);

        }

    }
    private void tickGoldBlock(){
        if (runTimeUpdate >= 0){
            Location location = generateLocationList.get(0);
            location.getBlock().setType(materialGoldBlock);
                List<String> lines = new ArrayList<>(hologramLines);
                lines.replaceAll(s -> color(s.replace("{endtime}", Main.getFormat(runTimeUpdate))));
                String name = generateLocationList.get(0).getBlockX()+"_"+generateLocationList.get(0).getBlockY()+ "_"+generateLocationList.get(0).getBlockZ();
                UtilHologram.createOrUpdateHologram(lines, location.clone().add(hologramOffset), name);
            List<Player> playerList = new ArrayList<>();
            for (Entity entity: location.getWorld().getNearbyEntities(location, radiusPay, radiusPay, radiusPay)){
                if (entity instanceof Player){
                    playerList.add((Player) entity);
                }
            }
            double countPay = (double) count;
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            if (!playerList.isEmpty()){
                if (shareCount){
                    countPay /= playerList.size();}
                else {
                    countPay = count;
                }
                    String result = decimalFormat.format(countPay);
                    for (Player player1 : playerList){
                        if (economy.equals("Vault")){
                            Main.getInstance().economy.depositPlayer(player1, count);
                            player1.sendMessage(
                                    color(
                                            String.format(message.get("youReceived"), result)
                                                    .replace(",00", "")
                                    )
                            );
                        } else if(economy.equals("PlayerPoint")){
                            PlayerPoints playerPoints = (PlayerPoints) getServer().getPluginManager().getPlugin("PlayerPoints");
                            assert playerPoints != null;
                            PlayerPointsAPI pointsAPI = playerPoints.getAPI();
                            UUID playerUUID = player1.getUniqueId();
                            int payCount = (int) Math.round(count);
                            pointsAPI.give(playerUUID, payCount);
                            player1.sendMessage(
                                    color(
                                            String.format(message.get("youReceived"), result)
                                                    .replace(",00", "")
                                    )
                            );
                        }
                    }
            }
        } else {
            runTimeUpdate = timeGoldBlock+1;
            stop();
        }
        runTimeUpdate--;
    }
    public void stop(){
        for (Player player : Bukkit.getOnlinePlayers()){
            player.sendMessage(color(message.get("endMessage")));
        }
        if (!generateLocationList.isEmpty()) {
            generateLocationList.get(0).getBlock().setType(Material.AIR);
            if (task != null) {
                task.cancel();
            }
            WGHook.removeRegion(generateLocationList.get(0));
            String name = generateLocationList.get(0).getBlockX() + "_" + generateLocationList.get(0).getBlockY() + "_" + generateLocationList.get(0).getBlockZ();
            UtilHologram.remove(name);
            generateLocationList.remove(0);
            if (isDefaultGold == 0){
                goldBlocks.remove(id);
            }
        }



    }

    public Location getLocation() {
        if (!generateLocationList.isEmpty()) {
            return generateLocationList.get(0);
        }
        return null;
    }

    public Integer getIsDefaultGold() {
        return isDefaultGold;
    }

    public Integer getRunTimeUpdate() { // время работы золотого блока
        return runTimeUpdate;
    }

    public Integer getTimeUpdate() { // через сколько появится золотой блок
        return timeUpdate;
    }

    public Integer getTimeGoldBlock() {
        return timeGoldBlock;
    }

    public Map<String, String> getMessage() {
        return message;
    }
    /*  public Map<Integer, List<String>> getCommandManager() {
        return commandManager;
    }

   */
}



