package ru.anime.goldblock.command;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.anime.goldblock.Main;
import ru.anime.goldblock.goldblock.GeneratorGoldBlock;
import ru.anime.goldblock.goldblock.GoldBlock;

import java.util.ArrayList;
import java.util.List;


public class CommandGoldBlock implements CommandExecutor, TabCompleter {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("goldblock.admin")) {

            return true;
        }
        if (args.length == 0){
            sender.sendMessage("Укажите аргумент");
            return true;
        }

       if(args[0].equals("generated")){
           if(sender instanceof Player player){
                sender.sendMessage("Команда только для консоли!");
           } else {
               GeneratorGoldBlock tryBlock = new GeneratorGoldBlock(Bukkit.getWorld(Main.getCfg().getString("world")));
               tryBlock.start();
           }
       }

        if (args[0].equals("create")){
            GoldBlock block = new GoldBlock(Bukkit.getWorld(Main.getCfg().getString("world")));
            block.start();
            Main.goldBlocks.put(block.getName(), block);
        }
        if (args[0].equals("stop")){
            if (args.length != 2){
                sender.sendMessage("Укажите имя золотого блока");
                return true;
            }
            String name = args[1];
            GoldBlock block = Main.goldBlocks.get(name);
            if (block == null){
                sender.sendMessage("Такого блока нету");
                return true;
            }
            block.stop();
            Main.goldBlocks.remove(name);
        }
        if (args[0].equals("tp")){
            if (args.length != 2){
                sender.sendMessage("Укажите имя золотого блока");
                return true;
            }
            String name = args[1];
            GoldBlock block = Main.goldBlocks.get(name);
            if (block == null){
                sender.sendMessage("Такого блока нету");
                return true;
            }
            if (block.getLocation() == null){
                sender.sendMessage("Золотой блок ещё не появился");
                return true;
            }
            if (sender instanceof Player player){
                player.teleport(block.getLocation());

            }else {
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
        if (args.length <= 1){
            return List.of(
                    "create",
                    "stop",
                    "tp"
            );
        } else {
            return new ArrayList<>(Main.goldBlocks.keySet());
        }

    }
}

