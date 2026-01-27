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

import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.CompatUtils;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.core.utils.protocol.events.PlayerCloneItemEvent;
import me.RockinChaos.fakecreative.FakeCreative;
import me.RockinChaos.fakecreative.modes.creative.Creative;
import me.RockinChaos.fakecreative.modes.creative.Creative.Tabs;
import me.RockinChaos.fakecreative.utils.menus.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class Clicking implements Listener {

    /**
     * Registers version-specific clicking event listeners based on server version.
     * Listeners are conditionally registered to avoid NoClassDefFoundError on older versions.
     */
    public Clicking() {
        if (ServerUtils.hasSpecificUpdate("1_9") && StringUtils.isRegistered(Clicking_1_9.class.getSimpleName())) {
            FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Clicking_1_9(), FakeCreative.getCore().getPlugin());
        }
    }

    /**
     * Copies any item the Player selects from their inventory or creative tab using their creative actions.
     *
     * @param event - InventoryClickEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onCopy(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory topInventory = CompatUtils.getTopInventory(event.getWhoClicked());
        final int slot = event.getSlot();
        final Inventory inventory = ((!PlayerHandler.isCraftingInv(event.getView()) && event.getRawSlot() >= CompatUtils.getTopInventory(event.getView()).getSize()) ? CompatUtils.getBottomInventory(event.getView()) : ((!PlayerHandler.isCraftingInv(event.getView()) ? CompatUtils.getTopInventory(event.getView()) : CompatUtils.getBottomInventory(event.getView()))));
        if (event.getClick() == ClickType.MIDDLE && Creative.isCreativeMode(player, true) && (Menu.isOpen(player) && inventory != CompatUtils.getTopInventory(player) || !Menu.isOpen(player))) {
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR && !Tabs.isItem(event.getCurrentItem())) {
                final ItemStack item = event.getCurrentItem().clone();
                item.setAmount(item.getMaxStackSize());
                player.setItemOnCursor(item);
            } else {
                if (player.getItemOnCursor().getType() != Material.AIR && !Tabs.isItem(player.getItemOnCursor())) {
                    if (inventory.getSize() >= slot && slot >= 0) {
                        inventory.setItem(slot, player.getItemOnCursor().clone());
                    }
                }
            }
            PlayerHandler.updateInventory(player, 1L);
        } else if (event.getRawSlot() >= topInventory.getSize() && Menu.isOpen(player) && (event.getClick() == ClickType.SHIFT_LEFT
                || event.getClick() == ClickType.SHIFT_RIGHT) && Creative.isCreativeMode(player, true)) {
            event.setCancelled(true);
            CompatUtils.getBottomInventory(event.getWhoClicked()).setItem(event.getSlot(), new ItemStack(Material.AIR));
            PlayerHandler.updateInventory(player, 1L);
        } else if (event.getRawSlot() <= topInventory.getSize() && Menu.isOpen(player) && (event.getClick() == ClickType.SHIFT_LEFT
                || event.getClick() == ClickType.SHIFT_RIGHT || event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT) && Creative.isCreativeMode(player, true)
                && (topInventory.getSize() >= event.getSlot() && event.getSlot() >= 0 && (topInventory.getItem(event.getSlot()) == null
                || Objects.requireNonNull(topInventory.getItem(event.getSlot())).getType() == Material.AIR))) {
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
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onClone(final PlayerCloneItemEvent event) {
        int tempSlot;
        Inventory tempInventory;
        if (!PlayerHandler.isCraftingInv(event.getView()) && event.getSlot() >= CompatUtils.getTopInventory(event.getView()).getSize()) {
            tempInventory = CompatUtils.getBottomInventory(event.getView());
            tempSlot = event.getSlot() - CompatUtils.getTopInventory(event.getView()).getSize() + 9;
            tempSlot = (tempSlot <= 35 ? tempSlot : (tempSlot >= 45 ? (tempSlot - 5) : (tempSlot - 36)));
        } else if (!PlayerHandler.isCraftingInv(event.getView())) {
            tempInventory = CompatUtils.getTopInventory(event.getView());
            tempSlot = event.getSlot();
        } else {
            tempInventory = CompatUtils.getBottomInventory(event.getView());
            tempSlot = (event.getSlot() <= 35 ? event.getSlot() : (event.getSlot() >= 45 ? (event.getSlot() - 5) : (event.getSlot() - 36)));
        }
        final int slot = tempSlot;
        final Inventory inventory = tempInventory;
        final Player player = event.getPlayer();
        final ItemStack cursorItem = player.getItemOnCursor().clone();
        final ItemStack slotItem = (inventory.getItem(slot) != null ? Objects.requireNonNull(inventory.getItem(slot)).clone() : inventory.getItem(slot));
        SchedulerUtils.run(() -> {
            if (event.getClick() == ClickType.MIDDLE && Creative.isCreativeMode(player, true) && (Menu.isOpen(player) && inventory != CompatUtils.getTopInventory(player) || !Menu.isOpen(player))) {
                if (cursorItem.getType() != Material.AIR && !Tabs.isItem(cursorItem) && (inventory.getItem(slot) == null || Objects.requireNonNull(inventory.getItem(slot)).getType() == Material.AIR || slotItem != null && slotItem.getType() != Material.AIR && cursorItem.getType() != Material.AIR && slotItem.isSimilar(cursorItem))) {
                    cursorItem.setAmount(cursorItem.getMaxStackSize());
                    inventory.setItem(slot, cursorItem);
                    SchedulerUtils.run(() -> player.setItemOnCursor(new ItemStack(Material.AIR)));
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
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onTab(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        if (Creative.isCreativeMode(player, true)) {
            if (Tabs.CREATIVE.isTab(event.getCurrentItem())) {
                event.setCancelled(true);
                SchedulerUtils.run(() -> Menu.creativeMenu(player, 0, null));
            } else if (Tabs.PICK.getItem().getType() != Material.BARRIER && Tabs.PICK_ITEM.isTab(event.getCurrentItem())) {
                event.setCancelled(true);
            } else if (Tabs.PICK.isTab(event.getCurrentItem())) {
                event.setCancelled(true);
                if (Tabs.PICK.getItem().getType() != Material.BARRIER) {
                    SchedulerUtils.run(() -> {
                        boolean removed = false;
                        for (ItemStack item : player.getInventory()) {
                            if (Tabs.PICK_ITEM.isTab(item)) {
                                player.getInventory().remove(item);
                                Creative.removePick(player);
                                removed = true;
                            }
                        }
                        if (!removed) {
                            if (player.getInventory().getItem(8) != null && Objects.requireNonNull(player.getInventory().getItem(8)).getType() != Material.AIR) {
                                ItemStack drop = Objects.requireNonNull(player.getInventory().getItem(8)).clone();
                                player.getInventory().setItem(8, new ItemStack(Material.AIR));
                                player.getInventory().setItem(8, Tabs.PICK_ITEM.getItem());
                                Creative.addPick(player);
                                if (player.getInventory().firstEmpty() != -1) {
                                    player.getInventory().addItem(drop);
                                } else {
                                    PlayerHandler.dropItem(player, drop);
                                }
                            } else {
                                player.getInventory().setItem(8, Tabs.PICK_ITEM.getItem());
                                Creative.addPick(player);
                            }
                        }
                    });
                }
            } else if (Tabs.HOTBARS.isTab(event.getCurrentItem())) {
                event.setCancelled(true);
                SchedulerUtils.run(() -> Menu.hotbarMenu(player));
            } else if (Tabs.PREFERENCES.isTab(event.getCurrentItem())) {
                event.setCancelled(true);
                SchedulerUtils.run(() -> {
                    if (Creative.get(player).getStats().isLocalePreferences(player) || Creative.get(player).getStats().isAnyPreferenceOverride(player)) {
                        Menu.userMenu(player);
                    }
                });
            } else if (Tabs.DESTROY.isTab(event.getCurrentItem())) {
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

    /**
     * Listeners for item movement.
     * Automatically registered when the parent Clicking class is instantiated on 1.9+ servers.
     *
     * @since 1.9
     */
    private static class Clicking_1_9 implements Listener {

        /**
         * Prevents the Player from moving the Pick Item to their offhand.
         *
         * @param event - InventoryClickEvent
         */
        @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
        private void onSwapPickItem(final PlayerSwapHandItemsEvent event) {
            if (Tabs.PICK_ITEM.isTab(event.getOffHandItem())) {
                event.setCancelled(true);
            }
        }
    }
}