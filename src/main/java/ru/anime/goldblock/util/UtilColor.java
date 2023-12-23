package ru.anime.goldblock.util;

import org.bukkit.ChatColor;

public class UtilColor {
    public static String color(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
