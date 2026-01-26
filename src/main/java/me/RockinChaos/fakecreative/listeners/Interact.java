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

import me.RockinChaos.core.handlers.ItemHandler;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.core.utils.protocol.events.PlayerPickBlockEvent;
import me.RockinChaos.core.utils.protocol.events.PlayerPickEntityEvent;
import me.RockinChaos.fakecreative.FakeCreative;
import me.RockinChaos.fakecreative.modes.creative.Creative;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Interact implements Listener {

    /**
     * Registers version-specific interact event listeners based on server version.
     * Listeners are conditionally registered to avoid NoClassDefFoundError on older versions.
     */
    public Interact() {
        if (ServerUtils.hasPreciseUpdate("1_21_4") && StringUtils.isRegistered(Interact_1_21_4.class.getSimpleName())) {
            FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Interact_1_21_4(), FakeCreative.getCore().getPlugin());
        }
    }

    /**
     * Refills the custom item to its original stack size when placing the item.
     *
     * @param event - PlayerInteractEvent
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onCountLock(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = (event.getItem() != null ? event.getItem().clone() : event.getItem());
        final int slot = player.getInventory().getHeldItemSlot();
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && item != null && item.getType() != Material.AIR && Creative.isCreativeMode(player, true)) {
            SchedulerUtils.run(() -> {
                if (ServerUtils.hasSpecificUpdate("1_9") && event.getHand() != null) {
                    if (event.getHand().equals(EquipmentSlot.HAND)) {
                        if (player.getInventory().getHeldItemSlot() == slot) {
                            PlayerHandler.setMainHandItem(player, item);
                        } else {
                            player.getInventory().setItem(slot, item);
                        }
                    } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
                        PlayerHandler.setOffHandItem(player, item);
                    }
                } else {
                    if (player.getInventory().getHeldItemSlot() == slot) {
                        PlayerHandler.setMainHandItem(player, item);
                    } else {
                        player.getInventory().setItem(slot, item);
                    }
                }
            });
        }
    }

    /**
     * Refills the item to its original stack size when placing the item into an item frame.
     *
     * @param event - PlayerInteractEntityEvent
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onFrameLock(final PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame) {
            try {
                ItemStack item;
                if (ServerUtils.hasSpecificUpdate("1_9")) {
                    item = PlayerHandler.getPerfectHandItem(event.getPlayer(), event.getHand().toString());
                } else {
                    item = PlayerHandler.getPerfectHandItem(event.getPlayer(), "");
                }
                final ItemStack itemStack = item.clone();
                Player player = event.getPlayer();
                if (Creative.isCreativeMode(event.getPlayer(), true)) {
                    SchedulerUtils.run(() -> {
                        if (ServerUtils.hasSpecificUpdate("1_9")) {
                            if (event.getHand().equals(EquipmentSlot.HAND)) {
                                PlayerHandler.setMainHandItem(player, itemStack);
                            } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
                                PlayerHandler.setOffHandItem(player, itemStack);
                            }
                        } else {
                            PlayerHandler.setMainHandItem(player, itemStack);
                        }
                    });
                }
            } catch (Exception e) {
                ServerUtils.sendDebugTrace(e);
            }
        }
    }

    /**
     * Gives the player the item that they are attempting to pick block when using the Pick Item.
     *
     * @param event - PlayerInteractEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPickItem(final PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getItem() == null || ServerUtils.hasPreciseUpdate("1_21_4") || !Creative.isCreativeMode(event.getPlayer(), true) || !Creative.Tabs.PICK_ITEM.isTab(event.getItem())) return;
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();
        if (block == null || block.getType() == Material.AIR) return;
        event.setCancelled(true);
        final ItemStack item = ItemHandler.getItemStack(block, player);
        if (item.getType() != Material.AIR && !Creative.isBlackListed(item)) {
            addItemToHotbar(player, item);
        }
    }

    /**
     * Gives the player the item used to produce the entity they attempt to Pick Item.
     *
     * @param event - PlayerInteractEntityEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPickEntity(final PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player || ServerUtils.hasPreciseUpdate("1_21_4") || !Creative.isCreativeMode(event.getPlayer(), true) || !Creative.Tabs.PICK_ITEM.isTab(PlayerHandler.getMainHandItem(event.getPlayer()))) return;
        final Player player = event.getPlayer();
        final Entity entity = event.getRightClicked();
        final ItemStack item = ItemHandler.getEntityItem(entity);
        event.setCancelled(true);
        if (item.getType() != Material.AIR && !Creative.isBlackListed(item)) {
            addItemToHotbar(player, item);
        }
    }

    /**
     * Listeners for pick item.
     * Automatically registered when the parent Interact class is instantiated on 1.21.4+ servers.
     *
     * @since 1.21.4
     */
    private static class Interact_1_21_4 implements Listener {

        /**
         * Gives the player the item that they are attempting to pick block.
         *
         * @param event - PlayerPickItemEvent
         */
        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        private void onPickTargetItem(final PlayerPickBlockEvent event) {
            if (!Creative.isCreativeMode(event.getPlayer(), true) || event.getBlock() == null || event.getBlock().getType() == Material.AIR)
                return;
            final Player player = event.getPlayer();
            SchedulerUtils.run(() -> {
                ItemStack item = null;
                try {
                    item = event.getItemStack();
                } catch (Exception ignored) {}
                if (item != null && item.getType() != Material.AIR && !Creative.isBlackListed(item)) {
                    setItemToInventory(player, item);
                }
            });
        }

        /**
         * Gives the player the item of the entity they attempt to pick item.
         *
         * @param event - PlayerInteractEntityEvent
         */
        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        private void onPickTargetEntity(final PlayerPickEntityEvent event) {
            if (!Creative.isCreativeMode(event.getPlayer(), true)) return;
            final Player player = event.getPlayer();
            SchedulerUtils.run(() -> {
                ItemStack item = null;
                try {
                    item = event.getItemStack();
                } catch (Exception ignored) {}
                if (item != null && item.getType() != Material.AIR && !Creative.isBlackListed(item)) {
                    setItemToInventory(player, item);
                }
            });
        }

        /**
         * Places an item in the player's inventory using the smart slot selection logic.
         * Used by onPickTargetItem and onPickTargetEntity.
         *
         * @param player - The player to give the item to
         * @param item   - The item to place in inventory
         */
        private void setItemToInventory(final Player player, final ItemStack item) {
            final PlayerInventory currentInventory = player.getInventory();
            final int currentSlot = currentInventory.getHeldItemSlot();
            final ItemStack currentItem = currentInventory.getItem(currentSlot);
            if (currentItem != null) {
                if (!currentItem.isSimilar(item)) {
                    if (currentInventory.firstEmpty() != -1) {
                        final int nextSlot = PlayerHandler.getNextBestSlot(currentInventory, currentSlot);
                        currentInventory.setItem(nextSlot < 9 ? currentSlot : nextSlot, currentItem);
                        currentInventory.setItem(nextSlot < 9 ? nextSlot : currentSlot, item);
                        if (nextSlot < 9) player.getInventory().setHeldItemSlot(nextSlot);
                    } else {
                        currentInventory.setItem(currentSlot, item);
                    }
                }
            } else {
                currentInventory.setItem(currentSlot, item);
            }
            PlayerHandler.updateInventory(player, 0);
        }
    }

    /**
     * Adds an item to the player's hotbar using intelligent placement logic.
     * If the item already exists, switches to it. Otherwise, adds it with right-to-left preference.
     * Excludes slot 8 (reserved for pick item tool).
     *
     * @param player - The player to give the item to
     * @param item - The item to add to the hotbar
     */
    private void addItemToHotbar(final Player player, final ItemStack item) {
        final PlayerInventory inventory = player.getInventory();
        int existingSlot = -1;
        for (int i = 0; i <= 7; i++) {
            final ItemStack hotbarItem = inventory.getItem(i);
            if (hotbarItem != null && hotbarItem.isSimilar(item)) {
                existingSlot = i;
                break;
            }
        }
        if (existingSlot == -1) {
            for (int i = 9; i < 36; i++) {
                final ItemStack invItem = inventory.getItem(i);
                if (invItem != null && invItem.isSimilar(item)) {
                    existingSlot = i;
                    break;
                }
            }
        }
        if (existingSlot != -1 && existingSlot <= 7) {
            final int exactSlot = existingSlot;
            SchedulerUtils.runLater(2L, () -> player.getInventory().setHeldItemSlot(exactSlot));
        } else if (existingSlot != -1) {
            final ItemStack existingItem = inventory.getItem(existingSlot);
            int emptyHotbarSlot = -1;
            for (int i = 7; i >= 0; i--) {
                final ItemStack hotbarItem = inventory.getItem(i);
                if (hotbarItem == null || hotbarItem.getType() == Material.AIR) {
                    emptyHotbarSlot = i;
                    break;
                }
            }
            if (emptyHotbarSlot != -1) {
                inventory.setItem(emptyHotbarSlot, existingItem);
                inventory.setItem(existingSlot, null);
                final int exactSlot = emptyHotbarSlot;
                SchedulerUtils.runLater(2L, () -> player.getInventory().setHeldItemSlot(exactSlot));
            } else {
                final ItemStack displacedItem = inventory.getItem(7);
                final int emptySlot = inventory.firstEmpty();

                if (emptySlot != -1) {
                    inventory.setItem(emptySlot, displacedItem);
                } else {
                    inventory.setItem(existingSlot, displacedItem);
                }
                inventory.setItem(7, existingItem);
                SchedulerUtils.runLater(2L, () -> player.getInventory().setHeldItemSlot(7));
            }
        } else {
            int emptyHotbarSlot = -1;
            for (int i = 7; i >= 0; i--) {
                final ItemStack hotbarItem = inventory.getItem(i);
                if (hotbarItem == null || hotbarItem.getType() == Material.AIR) {
                    emptyHotbarSlot = i;
                    break;
                }
            }
            if (emptyHotbarSlot != -1) {
                inventory.setItem(emptyHotbarSlot, item);
                final int exactSlot = emptyHotbarSlot;
                SchedulerUtils.runLater(2L, () -> player.getInventory().setHeldItemSlot(exactSlot));
            } else {
                final ItemStack displacedItem = inventory.getItem(7);
                final int emptySlot = inventory.firstEmpty();
                if (emptySlot != -1) {
                    inventory.setItem(emptySlot, displacedItem);
                }
                inventory.setItem(7, item);
                SchedulerUtils.runLater(2L, () -> player.getInventory().setHeldItemSlot(7));
            }
        }
        PlayerHandler.updateInventory(player, 0);
    }
}