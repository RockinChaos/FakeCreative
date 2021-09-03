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

import me.RockinChaos.fakecreative.api.events.PlayerCloneItemEvent;
import me.RockinChaos.fakecreative.handlers.PlayerHandler;
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
		final Player player = (Player) event.getWhoClicked();
		final int slot = event.getSlot();
		final Inventory inventory = ((!PlayerHandler.isCraftingInv(event.getView()) && event.getRawSlot() > event.getView().getTopInventory().getSize()) ? event.getView().getBottomInventory() : ((!PlayerHandler.isCraftingInv(event.getView()) ? event.getView().getTopInventory() : event.getView().getBottomInventory())));
		if (event.getClick() == ClickType.MIDDLE && PlayerHandler.isCreativeMode(player, true) && inventory != null && ((Menu.getCreator().isOpen(player)
				&& inventory != player.getOpenInventory().getTopInventory()) || !Menu.getCreator().isOpen(player))) {
			if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR && !PlayerHandler.isCreativeItem(inventory.getItem(slot))) {
				final ItemStack item = event.getCurrentItem().clone();
				item.setAmount(item.getMaxStackSize());
				player.setItemOnCursor(item);
			} else if (player.getItemOnCursor() != null && player.getItemOnCursor().getType() != Material.AIR && !PlayerHandler.isCreativeItem(player.getItemOnCursor())) {
				if (inventory.getSize() >= slot) {
					inventory.setItem(slot, player.getItemOnCursor().clone());
				}
			}
			PlayerHandler.updateInventory(player, 1L);
		} else if (event.getRawSlot() >= event.getWhoClicked().getOpenInventory().getTopInventory().getSize() && Menu.getCreator().isOpen(player) && (event.getClick() == ClickType.SHIFT_LEFT 
				|| event.getClick() == ClickType.SHIFT_RIGHT) && PlayerHandler.isCreativeMode(player, true)) {
			event.setCancelled(true);
			event.getWhoClicked().getOpenInventory().getBottomInventory().setItem(event.getSlot(), new ItemStack(Material.AIR));
			PlayerHandler.updateInventory(player, 1L);
		} else if (event.getRawSlot() <= event.getWhoClicked().getOpenInventory().getTopInventory().getSize() && Menu.getCreator().isOpen(player) && (event.getClick() == ClickType.SHIFT_LEFT 
				|| event.getClick() == ClickType.SHIFT_RIGHT || event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT) && PlayerHandler.isCreativeMode(player, true)
			   && (event.getWhoClicked().getOpenInventory().getTopInventory().getSize() >= event.getSlot() && event.getSlot() >= 0 && (event.getWhoClicked().getOpenInventory().getTopInventory().getItem(event.getSlot()) == null 
			   || event.getWhoClicked().getOpenInventory().getTopInventory().getItem(event.getSlot()).getType() == Material.AIR))) {
			event.setCancelled(true);
			player.setItemOnCursor(new ItemStack(Material.AIR));
			PlayerHandler.updateInventory(player, 1L);
		}
	}
	
   /**
	* Copies any item the Player selects from their inventory or creative tab using their creative actions.
	* 
	* @param event - PlayerCloneItemEvent
	*/
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onClone(final PlayerCloneItemEvent event) {
		int tempSlot = 0;
		Inventory tempInventory = null;
		if (!PlayerHandler.isCraftingInv(event.getView()) && event.getSlot() > event.getView().getTopInventory().getSize()) {
			tempInventory = event.getView().getBottomInventory();
			tempSlot = event.getSlot() - event.getView().getTopInventory().getSize() + 9;
			tempSlot = (tempSlot <= 35 ? tempSlot : (tempSlot >= 45 ? (tempSlot - 5) : (tempSlot - 36)));
		} else if (!PlayerHandler.isCraftingInv(event.getView())) {
			tempInventory = event.getView().getTopInventory();
			tempSlot = event.getSlot();
		} else {
			tempInventory = event.getView().getBottomInventory();
			tempSlot = (event.getSlot() <= 35 ? event.getSlot() : (event.getSlot() >= 45 ? (event.getSlot() - 5) : (event.getSlot() - 36)));
		}
		final int slot = tempSlot;
		final Inventory inventory = tempInventory;
		final Player player = event.getPlayer();
		final ItemStack cursorItem = (player.getItemOnCursor() != null ? player.getItemOnCursor().clone() : player.getItemOnCursor());
		final ItemStack slotItem = (inventory.getItem(slot) != null && inventory.getItem(slot).getType() != Material.AIR ? inventory.getItem(slot).clone() : inventory.getItem(slot));
		SchedulerUtils.run(() -> { 
			if (event.getClick() == ClickType.MIDDLE && PlayerHandler.isCreativeMode(player, true) && inventory != null && ((Menu.getCreator().isOpen(player)
					&& inventory != player.getOpenInventory().getTopInventory()) || !Menu.getCreator().isOpen(player))) {
				if (cursorItem != null && cursorItem.getType() != Material.AIR && !PlayerHandler.isCreativeItem(cursorItem) && (inventory.getItem(slot) == null || inventory.getItem(slot).getType() == Material.AIR 
						|| (slotItem != null && slotItem.getType() != Material.AIR && cursorItem != null && cursorItem.getType() != Material.AIR && slotItem.isSimilar(cursorItem)))) {
					cursorItem.setAmount(cursorItem.getMaxStackSize());
					inventory.setItem(slot, cursorItem);
					SchedulerUtils.run(() -> { 
						player.setItemOnCursor(new ItemStack(Material.AIR));
					});
					PlayerHandler.updateInventory(player, 1L);
				}
			} 
		});
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
							PlayerHandler.removePick(player);
							removed = true;
						}
					}
					if (!removed) {
						if (player.getInventory().getItem(8) != null && player.getInventory().getItem(8).getType() != Material.AIR) {
							ItemStack drop = player.getInventory().getItem(8).clone();
							player.getInventory().setItem(8, new ItemStack(Material.AIR));
							player.getInventory().setItem(8, PlayerHandler.getCreativeItem("pickItem"));
							PlayerHandler.addPick(player);
							if (player.getInventory().firstEmpty() != -1) {
								player.getInventory().addItem(drop);
							} else {
								PlayerHandler.dropItem(player, drop);
							}
						} else {
							player.getInventory().setItem(8, PlayerHandler.getCreativeItem("pickItem"));
							PlayerHandler.addPick(player);
						}
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