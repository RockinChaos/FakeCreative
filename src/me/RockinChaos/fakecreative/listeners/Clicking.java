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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.fakecreative.handlers.PlayerHandler;
import me.RockinChaos.fakecreative.handlers.modes.Creative;
import me.RockinChaos.fakecreative.utils.SchedulerUtils;
import me.RockinChaos.fakecreative.utils.interfaces.menus.Menu;

public class Clicking implements Listener {
	
   /**
	* Copies any item the Player selects from their inventory or creative tab using their creative actions.
	* 
	* @param event - InventoryClickEvent
	*/
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onCopy(final InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		int slot = event.getSlot();
		if (event.getClick() == ClickType.MIDDLE && PlayerHandler.isFakeCreativeMode(player) && event.getClickedInventory() != null && ((Menu.getCreator().isOpen(player) && event.getClickedInventory() != player.getOpenInventory().getTopInventory()) || !Menu.getCreator().isOpen(player))) {
			if (event.getClickedInventory().getItem(slot) != null && event.getClickedInventory().getItem(slot).getType() != Material.AIR) {
				ItemStack item = event.getClickedInventory().getItem(slot).clone();
				item.setAmount(item.getMaxStackSize());
				player.setItemOnCursor(item);
			} else if (player.getItemOnCursor() != null && player.getItemOnCursor().getType() != Material.AIR) {
				event.getClickedInventory().setItem(slot, player.getItemOnCursor());
			}
			PlayerHandler.updateInventory(player, 1L);
		} else if (event.getClickedInventory() != event.getWhoClicked().getOpenInventory().getTopInventory() && Menu.getCreator().isOpen(player) && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) && PlayerHandler.isFakeCreativeMode(player)) {
			event.setCancelled(true);
			event.getClickedInventory().setItem(event.getSlot(), new ItemStack(Material.AIR));
			PlayerHandler.updateInventory(player, 1L);
		} else if (event.getClickedInventory() == event.getWhoClicked().getOpenInventory().getTopInventory() && Menu.getCreator().isOpen(player) && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT || event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT) && PlayerHandler.isFakeCreativeMode(player)
			   && (event.getWhoClicked().getOpenInventory().getTopInventory().getItem(event.getSlot()) == null || event.getWhoClicked().getOpenInventory().getTopInventory().getItem(event.getSlot()).getType() == Material.AIR)) {
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
	private void onTab(InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		if (PlayerHandler.isFakeCreativeMode(player)) {
			if (Creative.isItem(event.getCurrentItem(), "creativeTab")) {
				event.setCancelled(true);
				SchedulerUtils.run(() -> Menu.getCreator().creativeMenu(player, 0, null));
			} else if (Creative.isItem(event.getCurrentItem(), "pickTab")) {
				event.setCancelled(true);
				SchedulerUtils.run(() -> {
					boolean removed = false;
					for (ItemStack item: player.getInventory()) {
						if (item != null && item.isSimilar(Creative.getPickItem())) {
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
						player.getInventory().setItem(8, Creative.getPickItem());
					}
				});
			} else if (Creative.isItem(event.getCurrentItem(), "saveTab")) {
				event.setCancelled(true);
				SchedulerUtils.run(() -> Menu.getCreator().hotbarMenu(player));
			} else if (Creative.isItem(event.getCurrentItem(), "unkTab")) {
				event.setCancelled(true);
			} else if (Creative.isItem(event.getCurrentItem(), "destroyTab")) {
				event.setCancelled(true);
				if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
					player.setItemOnCursor(new ItemStack(Material.AIR));
				} else {
					if (this.destroyPlayers != null && !this.destroyPlayers.isEmpty()) {
						if (this.destroyPlayers.contains(PlayerHandler.getPlayerID(player))) {
							player.getInventory().clear();
							PlayerHandler.setOffHandItem(player, new ItemStack(Material.AIR));
							player.getInventory().setHelmet(new ItemStack(Material.AIR));
							player.getInventory().setChestplate(new ItemStack(Material.AIR));
							player.getInventory().setLeggings(new ItemStack(Material.AIR));
							player.getInventory().setBoots(new ItemStack(Material.AIR));
						}
					} else {
						this.destroyPlayers.add(PlayerHandler.getPlayerID(player));
						SchedulerUtils.runLater(40, () -> {
							this.destroyPlayers.remove(PlayerHandler.getPlayerID(player));
						});
					}
				}
			}
		}
	}
	private List<String> destroyPlayers = new ArrayList<String>();
}