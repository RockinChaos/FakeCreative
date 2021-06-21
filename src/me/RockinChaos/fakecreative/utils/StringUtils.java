/*
 * FakeCreative
 * Copyright (C) CraftationGaming <https://www.craftationgaming.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.RockinChaos.fakecreative.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.domedd.betternick.BetterNick;
import me.RockinChaos.fakecreative.utils.api.DependAPI;
import me.clip.placeholderapi.PlaceholderAPI;

public class StringUtils {
	
   /**
    * Checks if string1 contains string2.
    * 
    * @param string1 - The String to be checked if it contains string2.
    * @param string2- The String that should be inside string1.
    * @return If string1 contains string2.
    */
	public static boolean containsIgnoreCase(final String string1, final String string2) {
		if (string1 != null && string2 != null && string1.toLowerCase().contains(string2.toLowerCase())) {
			return true;
		}
		return false;
	}
	
   /**
    * Checks if string1 contains string2.
    * 
    * @param string1 - The String to be checked if it contains string2.
    * @param string2- The String that should be inside string1.
    * @param argument - The argument to be split between the string.
    * @return If string1 contains string2.
    */
	public static boolean splitIgnoreCase(final String string1, final String string2, final String argument) {
		String[] parts = string1.split(argument);
		boolean splitParts = string1.contains(argument);
		for (int i = 0; i < (splitParts ? parts.length : 1); i++) {
			if ((splitParts && parts[i] != null && string2 != null && parts[i].toLowerCase().contains(string2.toLowerCase()))
			|| (!splitParts && string1 != null && string2 != null && string1.toLowerCase().equalsIgnoreCase(string2.toLowerCase()))) {
				return true;
			}
		}
		return false;
	}
	
   /**
    * Checks if the List contains the String.
    * 
    * @param list - The List to be checked if it contains the String.
    * @param str - The String that should be inside the List.
    * @return If the List contained the String.
    */
	public static boolean containsValue(final List<?> list, final String str) {
		boolean bool = false;
		for (Object l : list) { if (l.toString().equalsIgnoreCase(str)) { bool = true; break; } }
		return bool;
	}
	
   /**
    * Splits the String with proper formatting and ordering.
    * 
    * @param str - The String to be Split.
    * @return The newly formatted String[].
    */
	public static String[] softSplit(final String str) {
		if (str.split(", ").length < 3) { return str.split("` "); }
		String splitTest = ""; int index = 1;
	    for (String sd : str.split(", ")) { if (index == 3) { splitTest += sd + "` "; index = 1; } else { splitTest += sd + ", "; index++; } }
	    if (splitTest.endsWith(", ")) { splitTest = splitTest.substring(0, splitTest.length() - 2); }
	    return splitTest.split("` ");
	}
	
   /**
    * Splits the String to a List.
    * 
    * @param str - The String to be Split.
    * @return The split String as a List.
    */
	public static List<String> split(final String str) {
		List<String> splitList = new ArrayList<String>();
		for (String split : str.split(", ")) {
			splitList.add(split);
		}
		return splitList;
	}
	
   /**
    * Checks if the specified String is an Integer Value.
    * 
    * @param str - The String to be checked.
    * @return If the String is an Integer Value.
    */
	public static boolean isInt(final String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) { return false; }
		return true;
	}
	
   /**
    * Checks if the specified String is an Double Value.
    * 
    * @param str - The String to be checked.
    * @return If the String is an Double Value.
    */
	public static boolean isDouble(final String str) {
		try {
			Double.parseDouble(str);
		} catch (NumberFormatException e) { return false; }
		return true;
	}
 
   /**
    * Gets a random Integer between the upper and lower limits.
    * 
    * @param lower - The lower limit.
    * @param upper - The upper limit.
    * @return The randomly selected Integer between the limits.
    */
	public static int getRandom(final int lower, final int upper) {
		return new Random().nextInt((upper - lower) + 1) + lower;
	}
	
   /**
    * Converts a BufferedReader to a String output.
    * 
    * @param reader - the BufferedReader to be converted.
    * @return The resulting appended String.
    */
	public static String toString(BufferedReader reader) throws IOException {
		StringBuilder result = new StringBuilder();
		String line = null; while ((line = reader.readLine()) != null) { result.append(line); }
		return result.toString();
	}
	
   /**
    * Checks if the input is NULL, and returns either NONE for NULL / EMPTY,
    * or the properly formatted list as a String.
    * 
    * @param input - The String to be checked.
    * @return The newly formatted String.
    */
	public static String nullCheck(String input) {
		if (input == null || input.equalsIgnoreCase("NULL") || input.contains("[]") || input.contains("{}") || input.equals("0&7") || input.equals("-1&a%") || input.equals("") || input.equals(" ")) {
			return "NONE";
		}
		if (input.startsWith("[") && input.endsWith("]")) {
			input = input.substring(0, input.length() - 1).substring(1);
		}
		if (input.startsWith("{") && input.endsWith("}")) {
			input = input.replace("{", "").replace("}", "").replace("=", ":");
		}
		return input;
	}
	
   /**
    * Translated the specified String by formatting its hex color codes.
    * 
    * @param str - The String to have its Color Codes properly Converted to Mojang Hex Colors.
    * @return The translated string.
    */
    public static String translateHexColorCodes(final String str) {
    	final char COLOR_CHAR = ChatColor.COLOR_CHAR;
    	Matcher matcher = Pattern.compile("&#([A-Fa-f0-9]{6})").matcher(str);
    	StringBuffer buffer = new StringBuffer(str.length() + 4 * 8);
    	while (matcher.find()) {
    		String group = matcher.group(1);
    		matcher.appendReplacement(buffer, COLOR_CHAR + "x" + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1) + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3) + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5));
    	}
    	return matcher.appendTail(buffer).toString();
    }
	
   /**
    * Formats any color codes found in the String to Bukkit Colors so the
    * text will be colorfully formatted.
    * 
    * @param str - The String to have its Color Codes properly Converted to Bukkit Colors.
    * @return The newly formatted String.
    */
	public static String colorFormat(final String str) {
		return ChatColor.translateAlternateColorCodes('&', translateHexColorCodes(str));
	}
	
   /**
    * Translates the specified String by foramtting its color codes and replacing placeholders.
    * 
    * @param str - The String being translated.
    * @param player - The Player having their String translated.
    * @param placeHolder - The placeholders to be replaced into the String.
    * @return The newly translated String.
    */
	public static String translateLayout(String str, final Player player, final String...placeHolder) {
		String playerName = "EXEMPT";
		if (player != null && DependAPI.getDepends(false).nickEnabled()) {
			try {
				de.domedd.betternick.api.nickedplayer.NickedPlayer np = new de.domedd.betternick.api.nickedplayer.NickedPlayer(player);
				if (np.isNicked()) {
					playerName = np.getRealName();
				} else { playerName = player.getName(); }
			} catch (NoClassDefFoundError e) {
				try {
					if (BetterNick.getApi().isPlayerNicked(player)) {
						playerName = BetterNick.getApi().getRealName(player);
					} else { playerName = player.getName(); }	
				} catch (NullPointerException e2) { playerName = player.getName(); }
			}
		} else if (player != null) { playerName = player.getName(); }
		if (playerName != null && player != null && !(player instanceof ConsoleCommandSender)) {
			try { str = str.replace("%player%", playerName); } catch (Exception e) { ServerUtils.sendDebugTrace(e); }
			try { str = str.replace("%mob_kills%", String.valueOf(player.getStatistic(Statistic.MOB_KILLS))); } catch (Exception e) { ServerUtils.sendDebugTrace(e); }
			try { str = str.replace("%player_kills%", String.valueOf(player.getStatistic(Statistic.PLAYER_KILLS))); } catch (Exception e) { ServerUtils.sendDebugTrace(e); }
			try { str = str.replace("%player_deaths%", String.valueOf(player.getStatistic(Statistic.DEATHS))); } catch (Exception e) { ServerUtils.sendDebugTrace(e); }
			try { str = str.replace("%player_food%", String.valueOf(player.getFoodLevel())); } catch (Exception e) { ServerUtils.sendDebugTrace(e); }
			try { 
				final double health = (ServerUtils.hasSpecificUpdate("1_8") ? player.getHealth() : (double)player.getClass().getMethod("getHealth", double.class).invoke(player));
				str = str.replace("%player_health%", String.valueOf(health)); 
			} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
			try { str = str.replace("%player_location%", player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ()); } catch (Exception e) { ServerUtils.sendDebugTrace(e); }
			try { if (placeHolder != null && placeHolder.length >= 1 && placeHolder[0] != null) { str = str.replace("%player_hit%", placeHolder[0]); } } catch (Exception e) { ServerUtils.sendDebugTrace(e); } }
			if (player == null) { try { str = str.replace("%player%", "CONSOLE"); } catch (Exception e) { ServerUtils.sendDebugTrace(e); } }
			str = ChatColor.translateAlternateColorCodes('&', translateHexColorCodes(str));
		if (DependAPI.getDepends(false).placeHolderEnabled()) {
			try { try { return PlaceholderAPI.setPlaceholders(player, str); } 
			catch (NoSuchFieldError e) { ServerUtils.logWarn("An error has occured when setting the PlaceHolder " + e.getMessage() + ", if this issue persits contact the developer of PlaceholderAPI."); return str; }
			} catch (Exception e) { }
		}
		return str;
	}
}