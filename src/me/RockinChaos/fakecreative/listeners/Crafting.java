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
package me.RockinChaos.fakecreative.listeners;

import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.fakecreative.api.events.PlayerAutoCraftEvent;
import me.RockinChaos.fakecreative.handlers.ItemHandler;
import me.RockinChaos.fakecreative.handlers.PlayerHandler;
import me.RockinChaos.fakecreative.utils.SchedulerUtils;
import me.RockinChaos.fakecreative.utils.ServerUtils;

public class Crafting implements Listener {
	
   /**
    * Prevents players from autocrafting with custom crafting items in their crafting slots.
    * 
    * @param event - PlayerAutoCraftEvent
    */
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	private void onAutoCraft(final PlayerAutoCraftEvent event) {
		for (int i = 0; i <= 4; i++) {
  			final ItemStack[] craftingContents = event.getContents().clone();
  			if (!event.isCancelled() && PlayerHandler.isCreativeItem(craftingContents[i])) {
  				event.setCancelled(true);
  			} else if (event.isCancelled()) { return; }
  		}
	}
	
   /**
	* Removes custom crafting items from the players inventory when opening a GUI menu or storable inventory.
	* 
	* @param event - InventoryOpenEvent
	*/
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    private void onCraftingOpen(final InventoryOpenEvent event) {
    	final Player player = (Player) event.getPlayer();
    	if (PlayerHandler.isCreativeMode(player, true)) {
    		Inventory craftInventory = player.getOpenInventory().getTopInventory();
	    	for (ItemStack item: craftInventory) {
	    		if (PlayerHandler.isCreativeItem(item)) {
	    			craftInventory.remove(item);
	    		}
	    	}
    	}
    }
	
   /**
	* Gives the custom crafting items back when the player closes their inventory if they had items existing previously.
	* 
	* @param event - InventoryCloseEvent
	*/
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	private void onCraftingClose(final org.bukkit.event.inventory.InventoryCloseEvent event) {
		if (!ServerUtils.hasSpecificUpdate("1_8") || !PlayerHandler.isCraftingInv(event.getView())) {
			ItemStack[] topContents = ItemHandler.cloneContents(event.getView().getTopInventory().getContents());
	    	this.handleClose(slot -> { 
	    		event.getView().getTopInventory().setItem(slot, new ItemStack(Material.AIR));
	    	}, (Player)event.getPlayer(), event.getView(), topContents, true);
    	}
    }
    
   /**
	* Gives the custom crafting items back when the player closes their inventory if they had items existing previously.
	* 
	* @param event - InventoryCloseEvent
	*/
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    private void onCraftingClose(final me.RockinChaos.fakecreative.api.events.InventoryCloseEvent event) {
		if (ServerUtils.hasSpecificUpdate("1_8") && PlayerHandler.isCraftingInv(event.getView())) {
	    	this.handleClose(slot -> { 
	    		if (!event.isCancelled()) { event.setCancelled(true); }
	    	}, event.getPlayer(), event.getView(), event.getPreviousContents(true), false);
		}
    }
    
   /**
	* Called on player switching worlds.
	* Removes any crafting items from the player which ended up in their inventory slots.
	* 
	* @param event - PlayerChangedWorldEvent
	*/
	@EventHandler(ignoreCancelled = true)
	private void onCraftingWorldSwitch(final PlayerChangedWorldEvent event) {
		final Player player = event.getPlayer();
		final ItemStack[] inventory = player.getInventory().getContents();
		if (!ItemHandler.isContentsEmpty(inventory)) {
			for (int i = 0; i < inventory.length; i++) {
				if (PlayerHandler.isCreativeMode(player, true) && PlayerHandler.isCreativeItem(inventory[i])) {
					player.getInventory().setItem(i, new ItemStack(Material.AIR));
				}
			}
		}
		SchedulerUtils.runLater(2L, () -> { 
		    double health = 1;
			try { health = (ServerUtils.hasSpecificUpdate("1_8") ? player.getHealth() : (double)player.getClass().getMethod("getHealth", double.class).invoke(player)); } catch (Exception e) { health = (player.isDead() ? 0 : 1);  }
	    	if (health > 0 &&!event.getFrom().equals(player.getWorld())) {
			    PlayerHandler.setCreativeTabs(player);
			}
		});
	}
	
   /**
	* Returns the custom crafting item to the player if it is dropped automagically when switching worlds,
	* typically via the nether portal causing duplication glitches.
	* 
	* @param event - PlayerDropItemEvent
	*/
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    private void onCraftingDrop(final PlayerDropItemEvent event) {
    	final Player player = (Player) event.getPlayer();
    	final World world = player.getWorld();
    	final ItemStack item = event.getItemDrop().getItemStack().clone();
    	if (PlayerHandler.isCreativeMode(player, true) && PlayerHandler.isCreativeItem(item)) {
		    event.getItemDrop().getItemStack().setItemMeta(null);
		    event.getItemDrop().remove();
		    SchedulerUtils.runLater(2L, () -> { 
		    double health = 1;
			try { health = (ServerUtils.hasSpecificUpdate("1_8") ? player.getHealth() : (double)player.getClass().getMethod("getHealth", double.class).invoke(player)); } catch (Exception e) { health = (player.isDead() ? 0 : 1);  }
	    		if (health > 0 && world.equals(player.getWorld())) {
	    			PlayerHandler.setCreativeTabs(player);
	    		}
		    });
    	}
    }
    
   /**
    * Attempts to save and return the prior open inventory crafting slots.
    * 
    * @param input - The methods to be executed.
    * @param player - The Player being handled.
    * @param view - The view being referenced.
    * @param inventory - The inventory being handled.
    * @param slotZero - if the slot is zero.
    */
	private void handleClose(final Consumer < Integer > input, final Player player, final InventoryView view, final ItemStack[] inventory, final boolean slotZero) {
		if (PlayerHandler.isCraftingInv(view)) {
			if (!ItemHandler.isContentsEmpty(inventory)) {
				boolean isCrafting = false;
				for (int i = 0; i <= 4; i++) {
					if (inventory[i] != null && PlayerHandler.isCreativeItem(inventory[i])) {
						isCrafting = true;
						input.accept(i);
					}
				}
				for (int i = 0; i <= 4; i++) {
					if (isCrafting && i != 0 && inventory[i] != null && inventory[i].getType() != Material.AIR) {
						if (inventory[i] != null && !PlayerHandler.isCreativeItem(inventory[i])) {
							final int k = i;
							ItemStack drop = inventory[i].clone();
							SchedulerUtils.run(() -> { 
								player.getOpenInventory().getTopInventory().setItem(k, new ItemStack(Material.AIR));
								if (player.getInventory().firstEmpty() != -1) {
									player.getInventory().addItem(drop);
								} else { PlayerHandler.dropItem(player, drop); }
							});
							inventory[i] = new ItemStack(Material.AIR);
						}
					}
				}
				this.returnCrafting(player, inventory, 1L, !slotZero);
			}
		} else {
			SchedulerUtils.run(() -> { 
				double health = 1;
				try { health = (ServerUtils.hasSpecificUpdate("1_8") ? player.getHealth() : (double)player.getClass().getMethod("getHealth", double.class).invoke(player)); } catch (Exception e) { health = (player.isDead() ? 0 : 1);  }
    			if (health > 0 && PlayerHandler.isCraftingInv(player.getOpenInventory()) && PlayerHandler.isCreativeMode(player, true)) {
					Inventory craftInventory = player.getOpenInventory().getTopInventory();
					if (craftInventory.getItem(1) != null && craftInventory.getItem(1).getType() != Material.AIR) {
						ItemStack drop = craftInventory.getItem(1).clone();
						craftInventory.setItem(1, new ItemStack(Material.AIR));
						if (player.getInventory().firstEmpty() != -1) {
								player.getInventory().addItem(drop);
						} else {  PlayerHandler.dropItem(player, drop); }
					}
					PlayerHandler.setCreativeTabs(player);
				}
			});
		}
	}
	
   /**
    * Returns the custom crafting item to the player after the specified delay.
    * 
    * @param player - the Player having their item returned.
    * @param contents - the crafting contents to be returned.
    * @param delay - the delay to wait before returning the item.
    * @param slotZero - if the slot is zero.
    */
	private void returnCrafting(final Player player, final ItemStack[] contents, final long delay, final boolean slotZero) {
		SchedulerUtils.runLater(delay, () -> { 
			double health = 1;
			try { health = (ServerUtils.hasSpecificUpdate("1_8") ? player.getHealth() : (double)player.getClass().getMethod("getHealth", double.class).invoke(player)); } catch (Exception e) { health = (player.isDead() ? 0 : 1);  }
			if (!player.isOnline() || !(health > 0)) { return; } else if (!PlayerHandler.isCraftingInv(player.getOpenInventory())) { this.returnCrafting(player, contents, 10L, slotZero); return; }
			if (!slotZero) {
				for (int i = 4; i >= 0; i--) {
					player.getOpenInventory().getTopInventory().setItem(i, contents[i]);
				}
			} else { 
				player.getOpenInventory().getTopInventory().setItem(0, contents[0]);
			}
			player.updateInventory();
		});
	}
}