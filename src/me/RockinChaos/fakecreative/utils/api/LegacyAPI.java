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
package me.RockinChaos.fakecreative.utils.api;

import java.util.EnumSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.RockinChaos.fakecreative.FakeCreative;
import me.RockinChaos.fakecreative.utils.ServerUtils;
import me.RockinChaos.fakecreative.utils.StringUtils;
import me.RockinChaos.fakecreative.handlers.ItemHandler;
import me.RockinChaos.fakecreative.listeners.Depletion;
import me.RockinChaos.fakecreative.listeners.legacy.Legacy_Depletion;
import me.RockinChaos.fakecreative.listeners.legacy.Legacy_Invulnerable;

/**
 * Welcome to the magical land of make-believe.
 * These are Deprecated Legacy Methods and/or non-functioning methods
 * that exist to support legacy versions of Minecraft.
 * 
 */
@SuppressWarnings("deprecation")
public class LegacyAPI {
	
	private static boolean legacyMaterial = false;
	
   /**
    * Updates the Players Inventory.
    * 
    * @param player - The Player to have their Inventory updated.
    */
    public static void updateInventory(final Player player) {
    	player.updateInventory();
    }
    
   /**
    * Gets the ItemStack in the Players Hand.
    * 
    * @param player - The Player to have its ItemStack found.
    * @return The found ItemStack.
    */
    public static ItemStack getInHandItem(final Player player) {
    	return player.getInventory().getItemInHand();
    }
    
   /**
    * Sets the ItemStack to the Players Hand.
    * 
    * @param player - The Player to have the ItemStack given.
    * @param item - The ItemStack to be set to the Players Hand.
    */
    public static void setInHandItem(final Player player, final ItemStack item) {
    	player.setItemInHand(item);
    }
	
   /**
    * Creates a new ItemStack.
    * 
    * @param material - The Material to be set to the ItemStack.
    * @param count - The ItemStack size.
    * @param dataValue - The Data Value to set to the ItemStack.
    * @return The new ItemStack.
    */
    public static ItemStack newItemStack(final Material material, final int count, final short dataValue) {
    	return new ItemStack(material, count, dataValue);
    }

   /**
    * Matches the Material from its Bukkit Material and Data Value.
    * 
    * @param typeID - The ID of the Material to be fetched.
    * @param dataValue - The Data value to be matched.
    * @return The found Bukkit Material.
    */
    public static org.bukkit.Material getMaterial(final int typeID, final byte dataValue) {
    	initializeLegacy();
		return FakeCreative.getInstance().getServer().getUnsafe().fromLegacy(new org.bukkit.material.MaterialData(findMaterial(typeID), dataValue));
    }
    
   /**
    * Matches the Material from its Bukkit Material and Data Value.
    * 
    * @param material - The Material to be matched.
    * @param dataValue - The Data value to be matched.
    * @return The found Bukkit Material.
    */
    public static org.bukkit.Material getMaterial(final Material material, final byte dataValue) {
    	initializeLegacy();
  		return FakeCreative.getInstance().getServer().getUnsafe().fromLegacy(new org.bukkit.material.MaterialData(material, dataValue));
    }
    
   /**
    * Gets the Material from its corresponding ID.
    * 
    * @param typeID - The ID of the Material to be fetched.
    * @return The found Bukkit Material.
    */
    public static org.bukkit.Material findMaterial(final int typeID) {
        final Material[] foundMaterial = new Material[1];
        EnumSet.allOf(Material.class).forEach(material -> { 
        	try { 
        		if (StringUtils.containsIgnoreCase(material.toString(), "LEGACY_") && material.getId() == typeID || !ServerUtils.hasSpecificUpdate("1_13") && material.getId() == typeID) { 
        			initializeLegacy();
        			foundMaterial[0] = material;
        		} 
        	} catch (Exception e) { }
        });
        return foundMaterial[0];
    }
    
   /**
    * Sends a info/debug message if the server is running Minecraft 1.13+ and is attempting to call a Legacy material.
    * 
    */
    private static void initializeLegacy() {
		if (ServerUtils.hasSpecificUpdate("1_13") && !legacyMaterial) {
			legacyMaterial = true;
			ServerUtils.logInfo("Initializing Legacy Material Support ..."); 
			ServerUtils.logDebug("The plugin attempted to register one or more item(s) containing a numerical id and/or data values."); 
			ServerUtils.logDebug("The plugin will continue to function but some things may not look as expected.");
			ServerUtils.logDebug("This is likely a bug, please report it to the developer!"); 
			try {
				throw new Exception("Invalid usage of item id, this does not cause any severe issues!");
			} catch (Exception e) {
				ServerUtils.sendDebugTrace(e);
			}
		}
    }
    
   /**
    * Sets the owner to the SkullMeta.
    * 
    * @param skullMeta - The SkullMeta to have its owner set.
    * @param owner - The owner to be set to the SkullMeta.
    * @return The newly set SkullMeta.
    */
	public static org.bukkit.inventory.meta.ItemMeta setSkullOwner(final org.bukkit.inventory.meta.SkullMeta skullMeta, final String owner) {
		skullMeta.setOwner(owner);
		if (!ServerUtils.hasSpecificUpdate("1_13") && ServerUtils.hasSpecificUpdate("1_8")) {
			Location loc = new Location(Bukkit.getWorlds().get(0), 200, 1, 200);
			BlockState blockState = loc.getBlock().getState();
			try {
				loc.getBlock().setType(Material.valueOf("SKULL"));
				Skull skull = (Skull)loc.getBlock().getState();
				skull.setSkullType(SkullType.PLAYER);
				skull.setOwner(owner);
				skull.update();
				final String texture = ItemHandler.getSkullTexture(skull);
				if (texture != null && !texture.isEmpty()) {
					ItemHandler.setSkullTexture(skullMeta, texture);
				}
			} catch (Exception e) { }
			blockState.update(true);
		}
		return skullMeta;
	}
	
   /**
	* Checks if setTarget exists for the Entity.
	* 
    * @param current - The Entity to be checked.
	* @return If setTarget exists for the Entity.
	*/
    public static boolean setTargetExists(final Entity current) {
    	try {
    		current.getClass().getMethod("setTarget", LivingEntity.class);
    		return true;
    	} catch (NoSuchMethodException e) { return false; }
    }
    
   /**
	* Sets the Max Health of the Player.
	* 
    * @param player - The Player to have their max health set.
    * @param maxHealth - The Max Health to be set.
	*/
    public static void setMaxHealth(final Player player, final double maxHealth) {
    	player.setMaxHealth(maxHealth);
    }
    
   /**
    * Gets the Block Data from the specified Block.
    * 
    * @param block - The Block being referenced.
    * @return The determined Block Data.
    */
	public static byte getBlockData(final Block block) {
		return block.getData();
	}
	
   /**
    * Gets the Bukkit Player from their String name.
    * 
    * @param playerName - The String name of the Bukkit Player.
    * @return The found Player.
    */
	public static Player getPlayer(final String playerName) {
		return Bukkit.getPlayer(playerName);
	}
	
   /**
    * Gets the Bukkit OfflinePlayer from their String name.
    * 
    * @param playerName - The String name of the Bukkit OfflinePlayer.
    * @return The found OfflinePlayer.
    */
	public static OfflinePlayer getOfflinePlayer(final String playerName) {
		return Bukkit.getOfflinePlayer(playerName);
	}
	
   /**
    * Registers the Legacy Depletion Listener.
    * Only called when the Server version is below 1.14.
    * 
    */
	public static void registerDepletion() {
  		if (ServerUtils.hasSpecificUpdate("1_14")) {
  			FakeCreative.getInstance().getServer().getPluginManager().registerEvents(new Depletion(), FakeCreative.getInstance());
  		} else {
  			FakeCreative.getInstance().getServer().getPluginManager().registerEvents(new Legacy_Depletion(), FakeCreative.getInstance());
  		}
	}
	
   /**
    * Registers the Legacy Invulnerable Listener.
    * Only called when the Server version is below 1.9.
    * 
    */
	public static void registerInvulnerable() {
  		if (!ServerUtils.hasSpecificUpdate("1_9")) {
  			FakeCreative.getInstance().getServer().getPluginManager().registerEvents(new Legacy_Invulnerable(), FakeCreative.getInstance());
  		}
	}
}