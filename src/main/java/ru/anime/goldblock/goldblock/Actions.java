package ru.anime.goldblock.goldblock;


import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static ru.anime.goldblock.util.UtilColor.color;

public class Actions {

    public static int ActionsCount = 0;

    public static void Actions(Player player, String command) {
        String[] args = command.split(" ");
        String withoutCMD = command.replace(args[0] + " ", "");


        switch (args[0]) {
            case "[MESSAGE]": {
                player.sendMessage(color(withoutCMD));
                break;
            }
            case "[COUNT]": {
                try {
                    ActionsCount = Integer.parseInt(withoutCMD);
                } catch (NumberFormatException e) {
                    player.sendMessage("Ошибка: неверный формат для [COUNT]");
                }
                break;
            }
            case "[PLAYER]": {
                Bukkit.dispatchCommand(player, color(withoutCMD.replace("%player%", player.getName())));
                break;
            }
            case "[CONSOLE]": {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), color(withoutCMD.replace("%player%", player.getName())));
                break;
            }
            case "[ACTIONBAR]": {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(color(withoutCMD
                        .replace("%player%", player.getName()))));
                break;
            }
            case "[SOUND]": {
                float volume = 1.0f;
                float pitch = 1.0f;
                for (String arg : args) {
                    if (arg.startsWith("-volume:")) {
                        volume = Float.parseFloat(arg.replace("-volume:", ""));
                        continue;
                    }
                    if (!arg.startsWith("-pitch:")) continue;
                    pitch = Float.parseFloat(arg.replace("-pitch:", ""));
                }
                player.playSound(player.getLocation(), Sound.valueOf((String) args[1]), volume, pitch);
                break;
            }
            case "[EFFECT]": {
                int strength = 0;
                int duration = 1;
                for (String arg : args) {
                    if (arg.startsWith("-strength:")) {
                        strength = Integer.parseInt(arg.replace("-strength:", ""));
                        continue;
                    }
                    if (!arg.startsWith("-duration:")) continue;
                    duration = Integer.parseInt(arg.replace("-duration:", ""));
                }
                PotionEffectType effectType = PotionEffectType.getByName((String) args[1]);
                if (effectType == null) {
                    return;
                }
                if (player.hasPotionEffect(effectType)) {
                    return;
                }
                player.addPotionEffect(new PotionEffect(effectType, duration * 20, strength));
                break;
            }
            case "[TITLE]": {
                String title = "";
                String subTitle = "";
                int fadeIn = 1;
                int stay = 3;
                int fadeOut = 1;
                for (String arg : args) {
                    if (arg.startsWith("-fadeIn:")) {
                        fadeIn = Integer.parseInt(arg.replace("-fadeIn:", ""));
                        withoutCMD = withoutCMD.replace(arg, "");
                        continue;
                    }
                    if (arg.startsWith("-stay:")) {
                        stay = Integer.parseInt(arg.replace("-stay:", ""));
                        withoutCMD = withoutCMD.replace(arg, "");
                        continue;
                    }
                    if (!arg.startsWith("-fadeOut:")) continue;
                    fadeOut = Integer.parseInt(arg.replace("-fadeOut:", ""));
                    withoutCMD = withoutCMD.replace(arg, "");
                }
                String[] message = color(withoutCMD).split(";");
                if (message.length >= 1) {
                    title = message[0];
                    if (message.length >= 2) {
                        subTitle = message[1];
                    }
                }
                player.sendTitle(title, subTitle, fadeIn * 20, stay * 20, fadeOut * 20);
            }
        }
    }

}
