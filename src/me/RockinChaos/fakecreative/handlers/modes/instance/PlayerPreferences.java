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
package me.RockinChaos.fakecreative.handlers.modes.instance;

import org.bukkit.entity.Player;

import me.RockinChaos.fakecreative.handlers.ConfigHandler;

public class PlayerPreferences {
	
	private static boolean PermissionNeeded = ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Preferences");
	private static boolean OPPermissionNeeded = ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Preferences-OP");

	public static boolean allowFlight(final Player player) {
		final boolean allowFly = ConfigHandler.getConfig().getFile("config.yml").getBoolean("Preferences.allow-flight");
		if ((OPPermissionNeeded && player.isOp()) || ((!PermissionNeeded && !player.isOp()) || (!OPPermissionNeeded && player.isOp())) || (!PermissionNeeded && !player.isOp()) 
		|| (!OPPermissionNeeded && player.isOp()) || (player.isPermissionSet("fakecreative.preferences") && player.hasPermission("fakecreative.preferences"))) {
			//return true; // fetch preferences.
		}
		return allowFly;
	}
	
	public static int flySpeed(final Player player) {
		final int flySpeed = ConfigHandler.getConfig().getFile("config.yml").getInt("Preferences.fly-speed");
		if ((OPPermissionNeeded && player.isOp()) || ((!PermissionNeeded && !player.isOp()) || (!OPPermissionNeeded && player.isOp())) || (!PermissionNeeded && !player.isOp()) 
		|| (!OPPermissionNeeded && player.isOp()) || (player.isPermissionSet("fakecreative.preferences") && player.hasPermission("fakecreative.preferences"))) {
			//return true; // fetch preferences.
		}
		return flySpeed;
	}
	
	public static int health(final Player player) {
		final int health = ConfigHandler.getConfig().getFile("config.yml").getInt("Preferences.health");
		if ((OPPermissionNeeded && player.isOp()) || ((!PermissionNeeded && !player.isOp()) || (!OPPermissionNeeded && player.isOp())) || (!PermissionNeeded && !player.isOp()) 
		|| (!OPPermissionNeeded && player.isOp()) || (player.isPermissionSet("fakecreative.preferences") && player.hasPermission("fakecreative.preferences"))) {
			//return true; // fetch preferences.
		}
		return health;
	}
	
	public static double heartScale(final Player player) {
		final double heartScale = ConfigHandler.getConfig().getFile("config.yml").getDouble("Preferences.heart-scale");
		if ((OPPermissionNeeded && player.isOp()) || ((!PermissionNeeded && !player.isOp()) || (!OPPermissionNeeded && player.isOp())) || (!PermissionNeeded && !player.isOp()) 
		|| (!OPPermissionNeeded && player.isOp()) || (player.isPermissionSet("fakecreative.preferences") && player.hasPermission("fakecreative.preferences"))) {
			//return true; // fetch preferences.
		}
		return heartScale;
	}
	
	public static double breakSpeed(final Player player) {
		final double breakSpeed = ConfigHandler.getConfig().getFile("config.yml").getDouble("Preferences.break-speed");
		if ((OPPermissionNeeded && player.isOp()) || ((!PermissionNeeded && !player.isOp()) || (!OPPermissionNeeded && player.isOp())) || (!PermissionNeeded && !player.isOp()) 
		|| (!OPPermissionNeeded && player.isOp()) || (player.isPermissionSet("fakecreative.preferences") && player.hasPermission("fakecreative.preferences"))) {
			//return true; // fetch preferences.
		}
		return breakSpeed;
	}
	
	public static boolean hunger(final Player player) {
		final boolean hunger = ConfigHandler.getConfig().getFile("config.yml").getBoolean("Preferences.hunger-depletion");
		if ((OPPermissionNeeded && player.isOp()) || ((!PermissionNeeded && !player.isOp()) || (!OPPermissionNeeded && player.isOp())) || (!PermissionNeeded && !player.isOp()) 
		|| (!OPPermissionNeeded && player.isOp()) || (player.isPermissionSet("fakecreative.preferences") && player.hasPermission("fakecreative.preferences"))) {
			//return true; // fetch preferences.
		}
		return hunger;
	}
	
	public static boolean unbreakable(final Player player) {
		final boolean unbreakable = ConfigHandler.getConfig().getFile("config.yml").getBoolean("Preferences.unbreakable-items");
		if ((OPPermissionNeeded && player.isOp()) || ((!PermissionNeeded && !player.isOp()) || (!OPPermissionNeeded && player.isOp())) || (!PermissionNeeded && !player.isOp()) 
		|| (!OPPermissionNeeded && player.isOp()) || (player.isPermissionSet("fakecreative.preferences") && player.hasPermission("fakecreative.preferences"))) {
			//return true; // fetch preferences.
		}
		return unbreakable;
	}
	
	public static boolean blockDrops(final Player player) {
		final boolean blockDrops = ConfigHandler.getConfig().getFile("config.yml").getBoolean("Preferences.block-drops");
		if ((OPPermissionNeeded && player.isOp()) || ((!PermissionNeeded && !player.isOp()) || (!OPPermissionNeeded && player.isOp())) || (!PermissionNeeded && !player.isOp()) 
		|| (!OPPermissionNeeded && player.isOp()) || (player.isPermissionSet("fakecreative.preferences") && player.hasPermission("fakecreative.preferences"))) {
			//return true; // fetch preferences.
		}
		return blockDrops;
	}
	
	public static boolean swordBlock(final Player player) {
		final boolean swordBlock = ConfigHandler.getConfig().getFile("config.yml").getBoolean("Preferences.sword-block");
		if ((OPPermissionNeeded && player.isOp()) || ((!PermissionNeeded && !player.isOp()) || (!OPPermissionNeeded && player.isOp())) || (!PermissionNeeded && !player.isOp()) 
		|| (!OPPermissionNeeded && player.isOp()) || (player.isPermissionSet("fakecreative.preferences") && player.hasPermission("fakecreative.preferences"))) {
			//return true; // fetch preferences.
		}
		return swordBlock;
	}
	
	public static boolean god(final Player player) {
		final boolean god = ConfigHandler.getConfig().getFile("config.yml").getBoolean("Preferences.invulnerable");
		if ((OPPermissionNeeded && player.isOp()) || ((!PermissionNeeded && !player.isOp()) || (!OPPermissionNeeded && player.isOp())) || (!PermissionNeeded && !player.isOp()) 
		|| (!OPPermissionNeeded && player.isOp()) || (player.isPermissionSet("fakecreative.preferences") && player.hasPermission("fakecreative.preferences"))) {
			//return true; // fetch preferences.
		}
		return god;
	}
	
	public static int godDelay(final Player player) {
		final int godDelay = ConfigHandler.getConfig().getFile("config.yml").getInt("Preferences.invulnerable-delay");
		if ((OPPermissionNeeded && player.isOp()) || ((!PermissionNeeded && !player.isOp()) || (!OPPermissionNeeded && player.isOp())) || (!PermissionNeeded && !player.isOp()) 
		|| (!OPPermissionNeeded && player.isOp()) || (player.isPermissionSet("fakecreative.preferences") && player.hasPermission("fakecreative.preferences"))) {
			//return true; // fetch preferences.
		}
		return godDelay;
	}
	
	public static boolean storeInventory(final Player player) {
		final boolean storeInventory = ConfigHandler.getConfig().getFile("config.yml").getBoolean("Preferences.store-inventory");
		if ((OPPermissionNeeded && player.isOp()) || ((!PermissionNeeded && !player.isOp()) || (!OPPermissionNeeded && player.isOp())) || (!PermissionNeeded && !player.isOp()) 
		|| (!OPPermissionNeeded && player.isOp()) || (player.isPermissionSet("fakecreative.preferences") && player.hasPermission("fakecreative.preferences"))) {
			//return true; // fetch preferences.
		}
		return storeInventory;
	}
	
	public static boolean allowFire(final Player player) {
		final boolean allowFire = ConfigHandler.getConfig().getFile("config.yml").getBoolean("Preferences.allow-fire");
		if ((OPPermissionNeeded && player.isOp()) || ((!PermissionNeeded && !player.isOp()) || (!OPPermissionNeeded && player.isOp())) || (!PermissionNeeded && !player.isOp()) 
		|| (!OPPermissionNeeded && player.isOp()) || (player.isPermissionSet("fakecreative.preferences") && player.hasPermission("fakecreative.preferences"))) {
			//return true; // fetch preferences.
		}
		return allowFire;
	}
	
	public static int foodLevel(final Player player) {
		final int foodLevel = ConfigHandler.getConfig().getFile("config.yml").getInt("Preferences.food-level");
		if ((OPPermissionNeeded && player.isOp()) || ((!PermissionNeeded && !player.isOp()) || (!OPPermissionNeeded && player.isOp())) || (!PermissionNeeded && !player.isOp()) 
		|| (!OPPermissionNeeded && player.isOp()) || (player.isPermissionSet("fakecreative.preferences") && player.hasPermission("fakecreative.preferences"))) {
			//return true; // fetch preferences.
		}
		return foodLevel;
	}
}