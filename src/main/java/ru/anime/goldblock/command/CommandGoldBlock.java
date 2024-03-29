package ru.anime.goldblock.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.anime.goldblock.Main;
import ru.anime.goldblock.goldblock.GoldBlock;
import ru.anime.goldblock.goldblock.LoadGoldBlockCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ru.anime.goldblock.Main.goldBlocks;
import static ru.anime.goldblock.util.UtilColor.color;


public class CommandGoldBlock implements CommandExecutor, TabCompleter {
   static List<String> list = new ArrayList<>();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("goldblock.admin")) {

            return true;
        }
        if (args.length == 0){
            sender.sendMessage("Укажите аргумент");
            return true;
        }
        if (args[0].equals("reload")){
            Main.getInstance().reload();
            sender.sendMessage(color(Objects.requireNonNull(Main.getCfg().getString("messageReload"))));
        }

        if (args[0].equals("create")) {
            if (args[1] != null) {
                Map<String, GoldBlock> loadedGoldBlocks = LoadGoldBlockCommand.loadGoldBlock(Main.getCfg(), args[1]);
                goldBlocks.putAll(loadedGoldBlocks);
            }
        }
        if (args[0].equals("stop")) {
            if (args.length != 2) {
                sender.sendMessage("Укажите имя золотого блока");
                return true;
            }
            String name = args[1];
            GoldBlock block = Main.goldBlocks.get(name);
            if (block == null) {
                sender.sendMessage("Такого блока нету");
                return true;
            }
            block.stop();
        }
        if (args[0].equals("tp")) {
            if (args.length != 2) {
                sender.sendMessage("Укажите имя золотого блока");
                return true;
            }
            String name = args[1];
            GoldBlock block = Main.goldBlocks.get(name);
            if (block == null) {
                sender.sendMessage("Такого блока нету");
                return true;
            }
            if (block.getLocation() == null) {
                sender.sendMessage("Золотой блок ещё не появился");
                return true;
            }
            if (sender instanceof Player player) {
                player.teleport(block.getLocation());

            } else {
                sender.sendMessage("Команда только для игрока!");
            }
        }
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("goldblock.admin")) {
            return null;
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("create");
            completions.add("stop");
            completions.add("tp");
            completions.add("reload");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("stop")) {
                completions.addAll(goldBlocks.keySet());
            } else if (args[0].equalsIgnoreCase("create")) {
                for (Map.Entry<String, GoldBlock> entry : goldBlocks.entrySet()) {
                    String key = entry.getKey();
                    GoldBlock value = entry.getValue();
                    if (value.getIsDefaultGold() == 1) {
                        completions.add(key);
                    }
                }
            }
        }

        // Фильтруем результаты по введенному тексту
        String input = args[args.length - 1].toLowerCase();
        completions.removeIf(option -> !option.toLowerCase().startsWith(input));

        return completions;
    }
}

