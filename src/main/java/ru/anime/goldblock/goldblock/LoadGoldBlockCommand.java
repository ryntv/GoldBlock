package ru.anime.goldblock.goldblock;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;
import ru.anime.goldblock.Main;

import java.util.*;

import static ru.anime.goldblock.Main.goldBlocks;


public class LoadGoldBlockCommand {
    public static Map<String, GoldBlock> loadGoldBlock(FileConfiguration config, String blockType) {

        if (config.getConfigurationSection("GoldBlocks") == null) {
            return goldBlocks;
        }

        ConfigurationSection goldBlocksSection = config.getConfigurationSection("GoldBlocks");

        for (String key : goldBlocksSection.getKeys(false)) {
            ConfigurationSection goldBlockSection = goldBlocksSection.getConfigurationSection(key);

            // Проверка, соответствует ли тип блока требуемому типу (default или super)
            if (!goldBlockSection.getString("type").equalsIgnoreCase(blockType)) {
                continue;
            }

            String world = goldBlockSection.getString("world");
            String economy = goldBlockSection.getString("economy");
            String blockMovementType = goldBlockSection.getString("blockMovementType");
            Vector posGoldBlock;
            int radius;
            int heightMax;
            int heightMin;
            int centerX;
            int centerZ;
            if (blockMovementType.equals("static")) {
                posGoldBlock = new Vector(
                        goldBlockSection.getDouble("posGoldBlock.x"),
                        goldBlockSection.getDouble("posGoldBlock.y"),
                        goldBlockSection.getDouble("posGoldBlock.z")
                );
                radius = -1;
                heightMax = -1;
                heightMin = -1;
                centerX = -1;
                centerZ = -1;
            } else if (blockMovementType.equals("random")) {
                radius = goldBlockSection.getInt("radius");
                heightMax = goldBlockSection.getInt("height.max");
                heightMin = goldBlockSection.getInt("height.min");
                centerX = goldBlockSection.getInt("center.x");
                centerZ = goldBlockSection.getInt("center.z");
                posGoldBlock = new Vector();
            } else {
                break;

            }

            int timeGoldBlock = goldBlockSection.getInt("timeGoldBlock");
            int minPlayers = goldBlockSection.getInt("minPlayers");
            int count = goldBlockSection.getInt("count");
            boolean shareCount = goldBlockSection.getBoolean("shareCount");
            int radiusPay = goldBlockSection.getInt("radiusPay");

            String materialGoldBlockName = goldBlockSection.getString("materialGoldBlock");
            Material materialGoldBlock = Material.getMaterial(materialGoldBlockName);

            List<Material> materialList = new ArrayList<>();
            for (String materialName : goldBlockSection.getStringList("materialList")) {
                materialList.add(Material.getMaterial(materialName));
            }

            int time = goldBlockSection.getInt("time");
            List<String> hologramLines = goldBlockSection.getStringList("hologramLines");

            Vector hologramOffset = new Vector(
                    goldBlockSection.getDouble("hologramOffset.x"),
                    goldBlockSection.getDouble("hologramOffset.y"),
                    goldBlockSection.getDouble("hologramOffset.z")
            );

            Map<String, String> message = new HashMap<>();
            message.put("posMessage", goldBlockSection.getString("message.posMessage"));
            message.put("endMessage", goldBlockSection.getString("message.endMessage"));
            message.put("youReceived", goldBlockSection.getString("message.youReceived"));
            message.put("startMessage", goldBlockSection.getString("message.startMessage"));
            message.put("endTime", goldBlockSection.getString("message.endTime"));

            List<Integer> reportMessage = goldBlockSection.getIntegerList("reportMessage");
            List<Location> locationList = new ArrayList<>();
            String name = UUID.randomUUID().toString();

            GoldBlock goldBlock = new GoldBlock(name, world, economy, blockMovementType, radius, heightMax, heightMin,
                    centerX, centerZ, posGoldBlock, timeGoldBlock, minPlayers, count, shareCount, radiusPay, materialGoldBlock,
                    materialList, time, 0, hologramLines, hologramOffset, message, reportMessage, null, locationList,0, timeGoldBlock);

            goldBlocks.put(name, goldBlock);
            Main.getInstance().getLogger().info("Загружен золотой блок: " + name);
        }

        return goldBlocks;
    }
}
