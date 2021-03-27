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
package me.RockinChaos.fakecreative.handlers;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import de.domedd.betternick.BetterNick;
import me.RockinChaos.fakecreative.handlers.events.PlayerEnterCreativeEvent;
import me.RockinChaos.fakecreative.handlers.events.PlayerExitCreativeEvent;
import me.RockinChaos.fakecreative.handlers.modes.Creative;
import me.RockinChaos.fakecreative.utils.SchedulerUtils;
import me.RockinChaos.fakecreative.utils.ServerUtils;
import me.RockinChaos.fakecreative.utils.api.LegacyAPI;

public class PlayerHandler {
	
	private final static int PLAYER_CRAFT_INV_SIZE = 5;
    
   /**
    * Safely closes the players inventory in order to call the InventoryCloseEvent.
    * Fixes a bug with player.closeInventory() not calling the event by default, breaking crafting items.
    * 
    */
    public static void safeInventoryClose(Player player) {
    	player.openInventory(Bukkit.createInventory(player.getInventory().getHolder(), 9));
    	player.closeInventory();	
    }
	
   /**
    * Checks if the InventoryView is a player crafting inventory.
    * 
    * @param view - The InventoryView to be checked.
    * @return If the currently open inventory is a player crafting inventory.
    */
    public static boolean isCraftingInv(final InventoryView view) {
        return view.getTopInventory().getSize() == PLAYER_CRAFT_INV_SIZE;
    }
    
   /**
    * Checks if the player is currently in creative mode.
    * 
    * @param player - The player to be checked.
    * @return If the player is currently in fake creative mode.
    */
    public static boolean isFakeCreativeMode(Player player) {
    	return Creative.isFakeCreativeMode(player);
    }
    
   /**
    * Sets the Player to Creative Mode.
    * 
    * @param who - The Player being referenced.
    * @param altWho - The other Player being referenced (if any).
    */
    public static void setCreative(CommandSender who, Player altWho) {
    	Bukkit.getPluginManager().callEvent(new PlayerEnterCreativeEvent(who, altWho));
    }
    
   /**
    * Removes the Player from Creative Mode.
    * 
    * @param who - The Player being referenced.
    * @param altWho - The other Player being referenced (if any).
    */
    public static void setMode(CommandSender who, Player altWho, GameMode gamemode, boolean silent) {
    	Bukkit.getPluginManager().callEvent(new PlayerExitCreativeEvent(who, altWho, gamemode, silent));
    }
	
   /**
    * Checks if the player is currently in creative mode.
    * 
    * @param player - The player to be checked.
    * @return If the player is currently in creative mode.
    */
	public static boolean isCreativeMode(final Player player) {
		if (player.getGameMode() == GameMode.CREATIVE) {
			return true;
		}
		return false;
	}
	
   /**
    * Checks if the player is currently in adventure mode.
    * 
    * @param player - The player to be checked.
    * @return If the player is currently in adventure mode.
    */
	public static boolean isAdventureMode(final Player player) {
		if (player.getGameMode() == GameMode.ADVENTURE) {
			return true;
		}
		return false;
	}
	
   /**
    * Checks if the player is currently in adventure mode.
    * 
    * @param player - The player to be checked.
    * @return If the player is currently in adventure mode.
    */
	public static boolean isSurvivalMode(final Player player) {
		if (player.getGameMode() == GameMode.SURVIVAL) {
			return true;
		}
		return false;
	}
	
   /**
    * Checks if the player is has an open menu while left clicking.
    * 
    * @param view - The InventoryView being compared.
    * @param view - The action being checked.
    * @return If the player is currently interacting with an open menu.
    */
	public static boolean isMenuClick(final InventoryView view, final Action action) {
		if (!isCraftingInv(view) && (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)) {
			return true;
		}
		return false;
	}
	
   /**
    * Naturally drops the ItemStack for the Player.
    * 
    * @param player - The Player being referenced.
    * @param item - The ItemStack to be dropped.
    */
	public static void dropItem(final Player player, final ItemStack item) { 
    	Location location = player.getLocation();
    	location.setY(location.getY() + 1);
    	Item dropped = player.getWorld().dropItem(location, item);
		dropped.setVelocity(location.getDirection().multiply(.30));
		dropped.setPickupDelay(40);
    }
	
   /**
    * Clears the entire inventory of the Player.
    * 
    * @param player - The Player being referenced.
    */
	public static void clearItems(final Player player) { 
		PlayerInventory inventory = player.getInventory();
		Inventory craftView = player.getOpenInventory().getTopInventory();
		inventory.setHelmet(new ItemStack(Material.AIR)); 
		inventory.setChestplate(new ItemStack(Material.AIR)); 
		inventory.setLeggings(new ItemStack(Material.AIR)); 
		inventory.setBoots(new ItemStack(Material.AIR)); 
		if (ServerUtils.hasSpecificUpdate("1_9")) { PlayerHandler.setOffHandItem(player, new ItemStack(Material.AIR)); }
		inventory.clear();
		craftView.clear();
    }
	
   /**
    * Gets the current crafting slot contents of the player.
    * 
    * @param player - the Player to get the crafting contents of.
    * @return The ItemStack list of crafting slot contents.
    */
    public static ItemStack[] getTopContents(final Player player) {
		ItemStack[] tempContents = player.getOpenInventory().getTopInventory().getContents();
		ItemStack[] contents = new ItemStack[5];
		if (contents != null && tempContents != null) { 
			for (int i = 0; i <= 4; i++) { 
				contents[i] = tempContents[i].clone(); 
			} 
			return contents;
		}
		return tempContents;
    }
	
   /**
    * Sets the currently selected hotbar slot for the specified player.
    * 
    * @param player - The player to have their slot set.
    */
	public static void setHotbarSlot(final Player player, int slot) {
		if (slot != -1 && slot <= 8 && slot >= 0) {
			player.getInventory().setHeldItemSlot(slot);
		}
	}
	
   /**
    * Gets the current ItemStack in the players Main Hand,
    * If it is empty it will get the ItemStack in the Off Hand,
    * If the server version is below MC 1.9 it will use the 
    * legacy hand method to get the single hand.
    * 
    * @param player - The player to be checked.
    * @return The current ItemStack in the players hand.
    */
	public static ItemStack getHandItem(final Player player) {
		if (ServerUtils.hasSpecificUpdate("1_9") && player.getInventory().getItemInMainHand().getType() != null && player.getInventory().getItemInMainHand().getType() != Material.AIR) {
			return player.getInventory().getItemInMainHand();
		} else if (ServerUtils.hasSpecificUpdate("1_9") && player.getInventory().getItemInOffHand().getType() != null && player.getInventory().getItemInOffHand().getType() != Material.AIR) {
			return player.getInventory().getItemInOffHand();
		} else if (!ServerUtils.hasSpecificUpdate("1_9")) {
			//return LegacyAPI.getInHandItem(player);
		}
		return null;
	}
	
   /**
    * Gets the current ItemStack in the players hand.
    * If the server version is below MC 1.9 it will use the 
    * legacy hand method to get the single hand.
    * 
    * @param player - The player to be checked.
    * @param type - The hand type to get.
    * @return The current ItemStack in the players hand.
    */
	public static ItemStack getPerfectHandItem(final Player player, String type) {
		if (ServerUtils.hasSpecificUpdate("1_9") && type != null && type.equalsIgnoreCase("HAND")) {
			return player.getInventory().getItemInMainHand();
		} else if (ServerUtils.hasSpecificUpdate("1_9") && type != null && type.equalsIgnoreCase("OFF_HAND")) {
			return player.getInventory().getItemInOffHand();
		} else if (!ServerUtils.hasSpecificUpdate("1_9")) {
			//return LegacyAPI.getInHandItem(player);
		}
		return null;
	}
	
   /**
    * Gets the current ItemStack in the players Main Hand.
    * If the server version is below MC 1.9 it will use the 
    * legacy hand method to get the single hand.
    * 
    * @param player - The player to be checked.
    * @return The current ItemStack in the players hand.
    */
	public static ItemStack getMainHandItem(final Player player) {
		if (ServerUtils.hasSpecificUpdate("1_9")) {
			return player.getInventory().getItemInMainHand();
		} else if (!ServerUtils.hasSpecificUpdate("1_9")) {
			//return LegacyAPI.getInHandItem(player);
		}
		return null;
	}
	
   /**
    * Gets the current ItemStack in the players Off Hand.
    * If the server version is below MC 1.9 it will use the 
    * legacy hand method to get the single hand.
    * 
    * @param player - The player to be checked.
    * @return The current ItemStack in the players hand.
    */
	public ItemStack getOffHandItem(final Player player) {
		if (ServerUtils.hasSpecificUpdate("1_9")) {
			return player.getInventory().getItemInOffHand();
		} else if (!ServerUtils.hasSpecificUpdate("1_9")) {
			//return LegacyAPI.getInHandItem(player);
		}
		return null;
	}
	
   /**
    * Sets the specified ItemStack to the players Main Hand.
    * If the server version is below MC 1.9 it will use the 
    * legacy hand method to get the single hand.
    * 
    * @param player - The player to have the item set.
    * @param item - The ItemStack to be set.
    */
	public static void setMainHandItem(final Player player, final ItemStack item) {
		if (ServerUtils.hasSpecificUpdate("1_9")) {
			player.getInventory().setItemInMainHand(item);
		} else if (!ServerUtils.hasSpecificUpdate("1_9")) {
		//	LegacyAPI.setInHandItem(player, item);
		}
	}
	
   /**
    * Sets the specified ItemStack to the players Off Hand.
    * If the server version is below MC 1.9 it will use the 
    * legacy hand method to get the single hand.
    * 
    * @param player - The player to have the item set.
    * @param item - The ItemStack to be set.
    */
	public static void setOffHandItem(final Player player, final ItemStack item) {
		if (ServerUtils.hasSpecificUpdate("1_9")) {
			player.getInventory().setItemInOffHand(item);
		} else if (!ServerUtils.hasSpecificUpdate("1_9")) {
			//LegacyAPI.setInHandItem(player, item);
		}
	}
	
   /**
    * Resolves a bug where canceling an experience level event causes it to visually glitch
    * and remain showing the uncanceled experience levels.
    * 
    * This simply gets the players current experience levels and resets 
    * them to cause a clientside update.
    * 
    * @param player - The player to have their levels set.
    */
	public static void updateExperienceLevels(final Player player) {
        SchedulerUtils.runLater(1L, () -> {
            player.setExp(player.getExp());
            player.setLevel(player.getLevel());
        });
	}

   /**
    * Gets the Player instance from their String name.
    * 
    * @param playerName - The player name to be transformed.
    * @return The fetched Player instance.
    */
	@SuppressWarnings("unused")
	public static Player getPlayerString(final String playerName) {
		Player args = null;
		try { args = Bukkit.getPlayer(UUID.fromString(playerName)); } catch (Exception e) {}
		if (playerName != null && false) {
			try { 
				de.domedd.betternick.api.nickedplayer.NickedPlayer np = new de.domedd.betternick.api.nickedplayer.NickedPlayer(LegacyAPI.getPlayer(playerName));
				if (np.isNicked()) { return LegacyAPI.getPlayer(np.getRealName()); }
				else { return LegacyAPI.getPlayer(playerName); }
			} catch (NoClassDefFoundError e) {
				if (BetterNick.getApi().isPlayerNicked(LegacyAPI.getPlayer(playerName))) { return LegacyAPI.getPlayer(BetterNick.getApi().getRealName(LegacyAPI.getPlayer(playerName))); }
				else { return LegacyAPI.getPlayer(playerName); }
			}
		} else if (args == null) { return LegacyAPI.getPlayer(playerName); }
		return args;
	}
	
   /**
    * Gets the UUID of the Player.
    * If the UUID does not exist it will fetch their String name.
    * 
    * @param player - The player to have their UUID fetched.
    * @return The UUID of the player or if not found, their String name.
    */
	@SuppressWarnings("unused")
	public static String getPlayerID(final Player player) {
		if (player != null && player.getUniqueId() != null) {
			return player.getUniqueId().toString();
		} else if (player != null && false) {
			try {
				de.domedd.betternick.api.nickedplayer.NickedPlayer np = new de.domedd.betternick.api.nickedplayer.NickedPlayer(player);
				if (np.isNicked()) { return np.getRealName();
				} else { return player.getName(); }
			} catch (NoClassDefFoundError e) {
				if (BetterNick.getApi().isPlayerNicked(player)) { return BetterNick.getApi().getRealName(player);
				} else { return player.getName(); }
			}
		} else if (player != null) {
			return player.getName();
		}
		return "";
	}
	
   /**
    * Gets the UUID of the OfflinePlayer.
    * If the UUID does not exist it will fetch their String name.
    * 
    * @param player - The OfflinePlayer instance to have their UUID fetched.
    * @return The UUID of the player or if not found, their String name.
    */
	@SuppressWarnings("unused")
	public static String getOfflinePlayerID(final OfflinePlayer player) {
		if (player != null && player.getUniqueId() != null) {
			return player.getUniqueId().toString();
		} else if (player != null && false) {
			try {
				de.domedd.betternick.api.nickedplayer.NickedPlayer np = new de.domedd.betternick.api.nickedplayer.NickedPlayer((BetterNick) player);
				if (np.isNicked()) { return np.getRealName();
				} else { return player.getName(); }
			} catch (NoClassDefFoundError e) {
				if (BetterNick.getApi().isPlayerNicked((Player) player)) { return BetterNick.getApi().getRealName((Player) player);
				} else { return player.getName(); }
			}
		} else if (player != null) {
			return player.getName();
		}
		return "";
	}
	
   /**
    * Executes an input of methods for the currently online players.
    * 
    * @param input - The methods to be executed.
    */
    public static void forOnlinePlayers(final Consumer<Player> input) {
		try {
		  /** New method for getting the current online players.
			* This is for MC 1.12+
			*/
			if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
				for (Object objPlayer: ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]))) { 
					input.accept(((Player) objPlayer));
				}
			} 
		  /** New old for getting the current online players.
			* This is for MC versions below 1.12.
			* 
			* @deprecated Legacy version of getting online players.
			*/
			else {
				for (Player player: ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]))) {
					input.accept(player);
				}
			}
		} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
	}
    
   /**
    * Updates the specified players inventory.
    * 
    * @param player - The player to have their inventory updated.
    * @param delay - The ticks to wait before updating the inventory.
    */
	public static void updateInventory(final Player player, final long delay) {
		player.updateInventory();
	}
}