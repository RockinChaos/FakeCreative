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
package me.RockinChaos.fakecreative.handlers.modes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.RockinChaos.fakecreative.handlers.ItemHandler;
import me.RockinChaos.fakecreative.handlers.PlayerHandler;
import me.RockinChaos.fakecreative.handlers.modes.instance.PlayerObject;
import me.RockinChaos.fakecreative.handlers.modes.instance.PlayerPreferences;
import me.RockinChaos.fakecreative.utils.ReflectionUtils;
import me.RockinChaos.fakecreative.utils.SchedulerUtils;
import me.RockinChaos.fakecreative.utils.ServerUtils;
import me.RockinChaos.fakecreative.utils.api.LanguageAPI;
import me.RockinChaos.fakecreative.utils.interfaces.menus.Menu;
import me.RockinChaos.fakecreative.utils.sql.DataObject;
import me.RockinChaos.fakecreative.utils.sql.DataObject.Table;
import me.RockinChaos.fakecreative.utils.sql.SQL;

public class Creative {

	private static List<PlayerObject> creativePlayers = new ArrayList<PlayerObject>();
	
	private static final ItemStack creativeTab = ItemHandler.getItem("APPLE", 1, false, true, "&a&1&c&2&d&e&l&nCreative Tab", "&7", "&7&o*Access the creative menu to", "&7&oselect from a list of minecraft items.");
	private static final ItemStack pickTab = ItemHandler.getItem("STICK", 1, false, true, "&a&1&c&2&d&b&l&nPick Block", "&7", "&7&o*Allows you to clone", "&7&oa existing block item.");
	private static final ItemStack pickItem = ItemHandler.getItem("STICK", 1, true, true, "&d&1&c&2&a&b&l&nPick Block", "&7", "&7&o*Right-click a block to", "&7&oadd to your inventory.");
	private static final ItemStack saveTab = ItemHandler.getItem("PAPER", 1, false, true, "&a&1&c&2&d&a&l&nSaved Hotbars", "&7", "&7&o*Save or restore a hotbar", "&7&oto your current inventory.");
	private static final ItemStack userTab = ItemHandler.getItem("SKULL_ITEM:3", 1, false, true, "&a&1&c&2&d&6&l&nPreferences", "&7", "&7*Creative mode settings", "&7that are specific to you.");
	private static final ItemStack destroyTab = ItemHandler.getItem("LAVA_BUCKET", 1, false, true, "&a&1&c&2&d&c&l&nDestroy Item", "&7", "&7*Permanently destroy your items.", "&7", "&8&oDrop an item to delete it.", "&8&oShift-click to clear inventory.");

   /**
    * Puts the Player in Fake Creative Mode.
    * 
    * @param who - The executor.
    * @param altWho - The player to be set to Creative.
    */
    public static void setCreative(final CommandSender who, final Player altWho, final boolean refresh) {
    	final Player argsPlayer = (altWho != null ? altWho : (Player)who);
    	if (argsPlayer != null && (refresh || !isFakeCreativeMode(argsPlayer))) {
			if (!PlayerHandler.isSurvivalMode(argsPlayer)) { argsPlayer.setGameMode(GameMode.SURVIVAL); }
			if (!refresh) { creativePlayers.add(new PlayerObject(PlayerHandler.getPlayerID(argsPlayer), saveInventory(argsPlayer), argsPlayer.getHealth(), argsPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue(), argsPlayer.getFoodLevel(), argsPlayer.getFireTicks())); }
			setFlight(argsPlayer, true);
			argsPlayer.setInvulnerable(PlayerPreferences.god(argsPlayer));
			if (!refresh) { argsPlayer.setHealth(PlayerPreferences.health(argsPlayer)); }
			SchedulerUtils.run(() -> { 
				if (!refresh) { argsPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(((PlayerPreferences.heartScale(argsPlayer)) * 2)); }
				if (!PlayerPreferences.allowFire(argsPlayer)) {
					argsPlayer.setFireTicks(0);
				}
				argsPlayer.setFoodLevel(PlayerPreferences.foodLevel(argsPlayer));
				dropTargets(argsPlayer);
			});
			setTabs(argsPlayer);
			ServerUtils.logDebug(argsPlayer.getName() + " was set to fake creative.");
    	}
    	if (!refresh) { sendStatus(who, argsPlayer, GameMode.CREATIVE, false); }
    }
    
   /**
    * Puts the Player in the specified GameMode.
    * Exits Fake Creative Mode.
    * 
    * @param who - The executor.
    * @param altWho - The player to be set to GameMode.
    * @param gamemode - The GameMode to be set.
    * @param silent - If the status message should be sent.
    */
    public static void setMode(final CommandSender who, final Player altWho, final GameMode gamemode, final boolean silent) {
    	final Player argsPlayer = (altWho != null ? altWho : (Player)who);
        if (isFakeCreativeMode(argsPlayer)) {
    		final PlayerObject playerObject = get(argsPlayer);
    		setFlight(argsPlayer, false);
	    	argsPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(playerObject.getMaxHealth());
			argsPlayer.setFoodLevel(playerObject.getFood());
			argsPlayer.setFireTicks(playerObject.getFireTicks());
			SchedulerUtils.runLater(4L, () -> argsPlayer.setHealth(playerObject.getHealth()));
    		clearTabs(argsPlayer);
    		restoreInventory(playerObject, argsPlayer);
    		dropInvulnerable(argsPlayer);
    		remove(argsPlayer);
    		ServerUtils.logDebug(argsPlayer.getName() + " is no longer set as fake creative.");
        }
        sendStatus(who, altWho, gamemode, silent);
        argsPlayer.setGameMode(gamemode);
    }
    
   /**
    * Saves and Clears the Player Inventory.
    * 
    * @param player - The Player having their Inventory saved.
    * @return The generated Inventory64 Byte.
    */
    private static String saveInventory(final Player player) {
		if (PlayerPreferences.storeInventory(player)) {
			final PlayerInventory inventory = player.getInventory();
			final Inventory craftView = player.getOpenInventory().getTopInventory();
			final Inventory saveInventory = Bukkit.createInventory(null, 54);
			for (int i = 0; i <= 47; i++) {
				if (i <= 41 && inventory.getSize() >= i && inventory.getItem(i) != null) {
					saveInventory.setItem(i, inventory.getItem(i).clone());
				} else if (i >= 42 && PlayerHandler.isCraftingInv(player.getOpenInventory()) && craftView.getItem(i - 42) != null) {
					saveInventory.setItem(i, craftView.getItem(i - 42).clone());
				}
			}
			PlayerHandler.clearItems(player);
			return ItemHandler.serializeInventory(saveInventory);
		}
		return null;
    }
    
   /**
    * Clears and Restores the Player Inventory that was previously saved.
    * 
    * @param playerOject - The Object instance being referenced.
    * @param player - The Player having their Inventory restored.
    */
    private static void restoreInventory(final PlayerObject playerObject, final Player player) {
    	if (playerObject.getInventory64() != null) {
			final PlayerInventory inventory = player.getInventory();
			final Inventory craftView = player.getOpenInventory().getTopInventory();
			PlayerHandler.clearItems(player);
			final Inventory inventory64 = ItemHandler.deserializeInventory(playerObject.getInventory64());
			for (int i = 47; i >= 0; i--) {
				if (inventory64 != null && inventory64.getItem(i) != null && inventory64.getItem(i).getType() != Material.AIR) {
					if (i <= 41) {
						inventory.setItem(i, inventory64.getItem(i).clone());
					} else if (i >= 42 && PlayerHandler.isCraftingInv(player.getOpenInventory())) {
						craftView.setItem(i - 42, inventory64.getItem(i).clone());
						PlayerHandler.updateInventory(player, 1L);
					}
				}
			}
    	}	
    }
    
   /**
    * Sets the Creative Tabs to the crafting items slots.
    * 
    * @param player - The Player having their crafting items set.
    */
    public static void setTabs(final Player player) {
		final Inventory craftInventory = player.getOpenInventory().getTopInventory();
		for (int i = 0; i <= 4; i++) {
			if (craftInventory.getItem(i) != null && craftInventory.getItem(i).getType() != Material.AIR) {
				final ItemStack drop = craftInventory.getItem(i).clone();
				craftInventory.setItem(i, new ItemStack(Material.AIR));
				if (!isItem(drop) && player.getInventory().firstEmpty() != -1) {
					player.getInventory().addItem(drop);
				} else if (!isItem(drop)) { PlayerHandler.dropItem(player, drop); }
			}
		}
		final ItemStack userClone = userTab.clone();
		userClone.setItemMeta(ItemHandler.setSkullOwner(userClone.getItemMeta(), player.getName()));
		craftInventory.setItem(1, creativeTab);
		craftInventory.setItem(2, pickTab);
		craftInventory.setItem(3, saveTab);
		craftInventory.setItem(4, userClone);
		SchedulerUtils.run(() -> { 
			craftInventory.setItem(0, destroyTab);
			player.updateInventory();
		});
    }
    
   /**
    * Clears the Creative Tabs from the crafting items slots.
    * 
    * @param player - The Player having their crafting items cleared.
    */
    private static void clearTabs(final Player player) {
    	if (Menu.getCreator().isOpen(player)) {
    		Menu.getCreator().closeMenu();
    	}
    	for (final ItemStack item : player.getInventory()) {
    		if (isItem(item)) {
    			player.getInventory().remove(item);
    		}
    	}
    	for (final ItemStack item : player.getOpenInventory().getTopInventory()) {
    		if (isItem(item)) {
    			player.getOpenInventory().getTopInventory().remove(item);
    		}
    	}
    }
    
   /**
    * Sets the Player's Flight (Mode).
    * 
    * @param player - The Player being referenced.
    * @param setFly - If flight should be enabled or disabled.
    */
    private static void setFlight(final Player player, final boolean setFly) {
		if (PlayerPreferences.allowFlight(player)) {
			if (setFly) {
				player.setAllowFlight(true);
				final double speed = (PlayerPreferences.flySpeed(player));
				player.setFlySpeed(Float.parseFloat((Double.toString(speed/10))));
			} else {
				player.setFlying(false);
				player.setAllowFlight(false);
		    	player.setFlySpeed(0.1f);
			}
	    }	
    }
    
   /**
    * Disables invulnerability from the Player after x seconds have passed.
    * 
    * @param player - The Player being referenced.
    */
    private static void dropInvulnerable(final Player player) {
    	SchedulerUtils.runLater((PlayerPreferences.godDelay(player)) * 20, () -> {
    		if (!isFakeCreativeMode(player)) {
    			player.setInvulnerable(false);
    		}
    	});	
    }
    
   /**
    * Drops all Entity Targets that are currently Targeting / Attacking the Player.
    * 
    * @param player - The Player being referenced.
    */
    private static void dropTargets(final Player player) {
    	for (final Entity current : player.getWorld().getNearbyEntities(player.getLocation(), 20, 20, 20)){
	        if (!(current instanceof Item) && !(current instanceof Player) && ReflectionUtils.setTargetExists(current)) {
	        	try {
					current.getClass().getMethod("setTarget", LivingEntity.class).invoke(current, (LivingEntity)null);
				} catch (Exception e) { e.printStackTrace(); }
	        }
	    }
    }
    
   /**
    * Sends the set Mode messages to the Player.
    * 
    * @param player - The Player being referenced.
    */
    private static void sendStatus(final CommandSender who, final Player altWho, final GameMode gamemode, final boolean silent) {
    	if (!silent) {
    		final String[] placeHolders = LanguageAPI.getLang(false).newString();
			placeHolders[0] = gamemode.name().toLowerCase(); placeHolders[1] = altWho.getName();
			LanguageAPI.getLang(false).sendLangMessage("commands.gamemode.setMode", who, placeHolders);
			if (who instanceof ConsoleCommandSender || (!(who instanceof ConsoleCommandSender) && !((Player)who).equals(altWho))) {
				placeHolders[1] = who.getName();
				LanguageAPI.getLang(false).sendLangMessage("commands.gamemode.setModeTarget", altWho, placeHolders);
			}
    	}
    }
    
   /**
    * Checks if the player is currently in creative mode.
    * 
    * @param player - The player to be checked.
    * @return If the player is currently in fake creative mode.
    */
    public static boolean isFakeCreativeMode(final Player player) {
    	final String str = PlayerHandler.getPlayerID(player);
    	synchronized ("SET_CREATIVE") {
	    	for (final PlayerObject pl: creativePlayers) {
	    		if (pl.getPlayer().equalsIgnoreCase(str)) {
	    			return true;
	    		}
	    	}
    	}
		return false;
    }
    
   /**
    * Gets the Fake Creative HotBar from the Player's Hotbar list.
    * 
    * @param player - The Player being referenced.
    * @param hotbat - The hotbar number to be fetched.
    * @return The found inventory64 Byte.
    */
    public static String getHotbar(final Player player, final int hotbar) {
    	final String str = PlayerHandler.getPlayerID(player);
    	synchronized ("SET_CREATIVE") {
    		final PlayerObject pl = get(player);
	    	if (pl != null && pl.getPlayer().equalsIgnoreCase(str)) {
	    		return pl.getHotbars().get(hotbar);
	    	}
    	}
		return null;
    }
    
   /**
    * Saves the Fake Creative HotBar to the Player's Hotbar list.
    * 
    * @param player - The Player being referenced.
    * @param inventoryData - The Inventory64 to be saved.
    * @param hotbar - The hotbar number to be saved.
    */
    public static void saveHotbar(final Player player, final String inventoryData, final int hotbar) {
    	final String str = PlayerHandler.getPlayerID(player);
    	synchronized ("SET_CREATIVE") {
    		final PlayerObject pl = get(player);
	    	if (pl != null && pl.getPlayer().equalsIgnoreCase(str)) {
	    		final HashMap<Integer, String> hotbars = pl.getHotbars();
	    		hotbars.put(hotbar, inventoryData);
	    		pl.setHotbars(hotbars);
	    		final DataObject dataObject = SQL.getData().getData(new DataObject(Table.HOTBAR, PlayerHandler.getPlayerID(player), player.getWorld().getName(), Integer.toString(hotbar), inventoryData));
	    		if (dataObject != null) { SQL.getData().removeData(dataObject); }
	    		SQL.getData().saveData(new DataObject(Table.HOTBAR, PlayerHandler.getPlayerID(player), player.getWorld().getName(), Integer.toString(hotbar), inventoryData));
	    	}
    	}
    }
    
   /**
    * Checks if the ItemStack and itemName combo is a Fake Creative Tab.
    * 
    * @param itemStack - The ItemStack being referenced.
    * @param item - The itemName to be looked up.
    * @return If the ItemStack and itemName combo is a Fake Creative Tab.
    */
    public static boolean isItem(final ItemStack itemStack, final String item) {
    	return itemStack != null && ((item.equals("creativeTab") && ItemHandler.isSimilar(itemStack, creativeTab)) || (item.equals("pickTab") && ItemHandler.isSimilar(itemStack, pickTab)) 
    			|| (item.equals("pickItem") && ItemHandler.isSimilar(itemStack, pickItem)) || (item.equals("saveTab") && ItemHandler.isSimilar(itemStack, saveTab)) 
    			|| (item.equals("userTab") && ItemHandler.isSimilar(itemStack, userTab)) || (item.equals("destroyTab") && ItemHandler.isSimilar(itemStack, destroyTab))); 
    }
    
   /**
    * Checks if the ItemStack is a Fake Creative Tab.
    * 
    * @param item - The ItemStack being referenced.
    * @return If the ItemStack is a Fake Creative Tab.
    */
    public static boolean isItem(final ItemStack item) {
    	return item != null && (ItemHandler.isSimilar(item, creativeTab) || ItemHandler.isSimilar(item, pickTab) || ItemHandler.isSimilar(item, pickItem) || ItemHandler.isSimilar(item, saveTab) 
    		|| ItemHandler.isSimilar(item, userTab) || ItemHandler.isSimilar(item, destroyTab));
    }
    
   /**
    * Gets the Fake Creative Pick-Block item instance.
    * 
    * @return The Pick-Block item instance.
    */
    public static ItemStack getPickItem() {
		return pickItem;
    }
    
   /**
    * Gets the current list of Fake Creative Players.
    * 
    * @param player - The Player being referenced.
    */
    private static PlayerObject get(final Player player) {
    	final String str = PlayerHandler.getPlayerID(player);
    	synchronized ("SET_CREATIVE") {
	    	for (final PlayerObject pl: creativePlayers) {
	    		if (pl.getPlayer().equalsIgnoreCase(str)) {
	    			return pl;
	    		}
	    	}
    	}
		return null;
    }
    
   /**
    * Removes the Player from the stored Fake Creative Players
    * 
    * @param player - The Player being referenced.
    */
    private static void remove(final Player player) {
    	final String str = PlayerHandler.getPlayerID(player);
    	synchronized ("SET_CREATIVE") {
	    	for (final PlayerObject pl: creativePlayers) {
	    		if (pl.getPlayer().equalsIgnoreCase(str)) {
	    			creativePlayers.remove(pl);
	    			return;
	    		}
	    	}
    	}
    }	
}