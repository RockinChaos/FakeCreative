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

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.fakecreative.handlers.PlayerHandler;
import me.RockinChaos.fakecreative.utils.SchedulerUtils;
import me.RockinChaos.fakecreative.utils.ServerUtils;
import me.RockinChaos.fakecreative.utils.interfaces.menus.Menu;

public class Clicking implements Listener {
	
   /**
	* Copies any item the Player selects from their inventory or creative tab using their creative actions.
	* 
	* @param event - InventoryClickEvent
	*/
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onCopy(final InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		final int slot = event.getSlot();
		final Inventory inventory = (ServerUtils.hasSpecificUpdate("1_14") ? event.getClickedInventory() : event.getInventory());
		if (event.getClick() == ClickType.MIDDLE && PlayerHandler.isCreativeMode(player, true) && inventory != null && ((Menu.getCreator().isOpen(player)
				&& inventory != player.getOpenInventory().getTopInventory()) || !Menu.getCreator().isOpen(player))) {
			if (inventory.getItem(slot) != null && inventory.getItem(slot).getType() != Material.AIR && !PlayerHandler.isCreativeItem(inventory.getItem(slot))) {
				final ItemStack item = inventory.getItem(slot).clone();
				item.setAmount(item.getMaxStackSize());
				player.setItemOnCursor(item);
			} else if (player.getItemOnCursor() != null && player.getItemOnCursor().getType() != Material.AIR&& !PlayerHandler.isCreativeItem(player.getItemOnCursor())) {
				inventory.setItem(slot, player.getItemOnCursor());
			}
			PlayerHandler.updateInventory(player, 1L);
		} else if (inventory != event.getWhoClicked().getOpenInventory().getTopInventory() && Menu.getCreator().isOpen(player) && (event.getClick() == ClickType.SHIFT_LEFT 
				|| event.getClick() == ClickType.SHIFT_RIGHT) && PlayerHandler.isCreativeMode(player, true)) {
			event.setCancelled(true);
			inventory.setItem(event.getSlot(), new ItemStack(Material.AIR));
			PlayerHandler.updateInventory(player, 1L);
		} else if (inventory == event.getWhoClicked().getOpenInventory().getTopInventory() && Menu.getCreator().isOpen(player) && (event.getClick() == ClickType.SHIFT_LEFT 
				|| event.getClick() == ClickType.SHIFT_RIGHT || event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT) && PlayerHandler.isCreativeMode(player, true)
			   && (event.getWhoClicked().getOpenInventory().getTopInventory().getSize() >= event.getSlot() && event.getSlot() >= 0 && (event.getWhoClicked().getOpenInventory().getTopInventory().getItem(event.getSlot()) == null 
			   || event.getWhoClicked().getOpenInventory().getTopInventory().getItem(event.getSlot()).getType() == Material.AIR))) {
			event.setCancelled(true);
			player.setItemOnCursor(new ItemStack(Material.AIR));
			PlayerHandler.updateInventory(player, 1L);
		}
	}
	
   /**
	* Prevents the Player from moving around any Creative Tabs.
	* 
	* @param event - InventoryClickEvent
	*/
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	private void onTab(final InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		if (PlayerHandler.isCreativeMode(player, true)) {
			if (PlayerHandler.isCreativeItem(event.getCurrentItem(), "creativeTab")) {
				event.setCancelled(true);
				SchedulerUtils.run(() -> Menu.getCreator().creativeMenu(player, 0, null));
			} else if (PlayerHandler.isCreativeItem(event.getCurrentItem(), "pickTab")) {
				event.setCancelled(true);
				SchedulerUtils.run(() -> {
					boolean removed = false;
					for (ItemStack item: player.getInventory()) {
						if (item != null && item.isSimilar(PlayerHandler.getCreativeItem("pickItem"))) {
							player.getInventory().remove(item);
							removed = true;
						}
					}
					if (!removed) {
						if (player.getInventory().getItem(8) != null && player.getInventory().getItem(8).getType() != Material.AIR) {
							ItemStack drop = player.getInventory().getItem(8).clone();
							player.getInventory().setItem(8, new ItemStack(Material.AIR));
							if (player.getInventory().firstEmpty() != -1) {
								player.getInventory().addItem(drop);
							} else {
								PlayerHandler.dropItem(player, drop);
							}
						}
						player.getInventory().setItem(8, PlayerHandler.getCreativeItem("pickItem"));
					}
				});
			} else if (PlayerHandler.isCreativeItem(event.getCurrentItem(), "saveTab")) {
				event.setCancelled(true);
				SchedulerUtils.run(() -> Menu.getCreator().hotbarMenu(player));
			} else if (PlayerHandler.isCreativeItem(event.getCurrentItem(), "userTab")) {
				event.setCancelled(true);
				SchedulerUtils.run(() -> Menu.getCreator().userMenu(player));
			} else if (PlayerHandler.isCreativeItem(event.getCurrentItem(), "destroyTab")) {
				event.setCancelled(true);
				if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
					player.setItemOnCursor(new ItemStack(Material.AIR));
				} else if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
					player.getInventory().clear();
					PlayerHandler.setOffHandItem(player, new ItemStack(Material.AIR));
					player.getInventory().setHelmet(new ItemStack(Material.AIR));
					player.getInventory().setChestplate(new ItemStack(Material.AIR));
					player.getInventory().setLeggings(new ItemStack(Material.AIR));
					player.getInventory().setBoots(new ItemStack(Material.AIR));
				}
			}
		}
	}
}