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
        return "Ваше имя";
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

        // Пример обработки плейсхолдера
        if (identifier.equals("example")) {
            return String.valueOf(Main.goldBlocks.size());
        }

        // Обработка плейсхолдера с префиксом "timeGoldBlock_"
        if (identifier.startsWith("toStart_")) {
            // Извлекаем значение из идентификатора
            String key = identifier.replace("toStart_", "");

            GoldBlock goldBlock = goldBlocks.get(key);
            if (goldBlock != null) {
                return color(String.format(goldBlock.getMessage().get("startMessage"), getFormat(goldBlock.getTimeUpdate())));
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
                    return color(String.format(goldBlock.getMessage().get("posMessage"), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                } else {
                    return "Золотой блок ещё не появился!";
                }

            }
        }
        if (identifier.startsWith("endTime_")){
            String key = identifier.replace("endTime_", "");
            GoldBlock goldBlock = goldBlocks.get(key);
            if (goldBlock != null){
                if (goldBlock.getRunTimeUpdate() < goldBlock.getTimeGoldBlock() && goldBlock.getRunTimeUpdate() > 0){
                    return color(String.format(goldBlock.getMessage().get("endTime"), getFormat(goldBlock.getRunTimeUpdate())));
                } else {
                    return "Золотой блок ещё не появился!";
                }
            }
        }

        return null;
    }
}
