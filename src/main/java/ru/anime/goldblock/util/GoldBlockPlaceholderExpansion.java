package ru.anime.goldblock.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.anime.goldblock.Main;
import ru.anime.goldblock.goldblock.GoldBlock;

import static ru.anime.goldblock.Main.getFormat;
import static ru.anime.goldblock.Main.goldBlocks;
import static ru.anime.goldblock.util.UtilColor.color;

public class GoldBlockPlaceholderExpansion extends PlaceholderExpansion {
    private final Main plugin;

    public GoldBlockPlaceholderExpansion(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRegister() {
        // Возвращаем true, если можно регистрировать плейсхолдеры
        return true;
    }

    @Override
    public String getAuthor() {
        // Возвращает имя автора плейсхолдера
        return "Anime";
    }
    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String getIdentifier() {
        // Возвращает идентификатор плейсхолдера
        return "gb";
    }

    @Override
    public String getVersion() {
        // Возвращает версию плейсхолдера
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        // Обработка запроса плейсхолдера
        if (player == null) {
            return "";
        }
        // Обработка плейсхолдера с префиксом "timeGoldBlock_"
        if (identifier.startsWith("toStart_")) {
            // Извлекаем значение из идентификатора
            String key = identifier.replace("toStart_", "");

            GoldBlock goldBlock = goldBlocks.get(key);
            if (goldBlock != null) {
                return color(String.format(getFormat(goldBlock.getTimeUpdate())));
            } else {
                return "Золотой блок не обнаружен!";
            }

        }
        if (identifier.startsWith("xyz_")) {
            String key = identifier.replace("xyz_", "");
            GoldBlock goldBlock = goldBlocks.get(key);
            if (goldBlock != null) {
                if (goldBlock.getLocation() != null && goldBlock.getRunTimeUpdate() < goldBlock.getTimeGoldBlock()) {
                    Location location = goldBlock.getLocation();
                    return "x:" + location.getBlockX() + " y:" + location.getBlockY() + " z:" + location.getBlockZ();
                } else {
                    return color(Main.getCfg().getString("absentGoldBlock"));
                }

            }
        }
        if (identifier.startsWith("endTime_")){
            String key = identifier.replace("endTime_", "");
            GoldBlock goldBlock = goldBlocks.get(key);
            if (goldBlock != null){
                if (goldBlock.getRunTimeUpdate() < goldBlock.getTimeGoldBlock() && goldBlock.getRunTimeUpdate() > 0){
                    return color(String.format(getFormat(goldBlock.getRunTimeUpdate())));
                } else {
                    return color(Main.getCfg().getString("absentGoldBlock"));
                }
            }
        }

        return null;
    }
}
