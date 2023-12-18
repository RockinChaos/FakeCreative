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
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.core.utils.api.LegacyAPI;
import me.RockinChaos.core.utils.types.Altered;
import me.RockinChaos.fakecreative.modes.creative.Creative;
import me.RockinChaos.fakecreative.modes.creative.Creative.Tabs;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class Interact implements Listener {

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
                if (player.getInventory().getHeldItemSlot() == slot) {
                    PlayerHandler.setMainHandItem(player, item);
                } else if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
                    player.getInventory().setItem(slot, item);
                }
            });
        }
    }

    /**
     * Refills the  item to its original stack size when placing the item into an itemframe.
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
                            if (event.getHand().equals(org.bukkit.inventory.EquipmentSlot.HAND)) {
                                PlayerHandler.setMainHandItem(player, itemStack);
                            } else if (event.getHand().equals(org.bukkit.inventory.EquipmentSlot.OFF_HAND)) {
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
     * Gives the player the item that they are attempting to pick block.
     *
     * @param event - PlayerInteractEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPickItem(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && Creative.isCreativeMode(event.getPlayer(), true) && Tabs.PICK_ITEM.isTab(event.getItem())) {
            Block block = event.getClickedBlock();
            if (block == null || block.getType().name().toUpperCase().contains("LAVA") || block.getType().name().toUpperCase().contains("WATER")) {
                block = PlayerHandler.getTargetBlock(event.getPlayer(), 6);
            }
            if (block != null && block.getType() != Material.AIR) {
                ItemStack item;
                if (ServerUtils.hasSpecificUpdate("1_13")) {
                    item = new ItemStack(Altered.getAlter(block.getType()));
                } else {
                    item = LegacyAPI.newItemStack(block.getType(), 1, LegacyAPI.getBlockData(block));
                }
                try {
                    if (StringUtils.containsIgnoreCase(item.getType().name(), "SHULKER")) {
                        final BlockStateMeta tempMeta = (BlockStateMeta) item.getItemMeta();
                        if (tempMeta != null) {
                            tempMeta.setBlockState(block.getState());
                            item.setItemMeta(tempMeta);
                        }
                    }
                } catch (Exception ignored) {
                }
                event.getPlayer().getInventory().addItem(item);
                PlayerHandler.updateInventory(event.getPlayer(), 0);
                event.setCancelled(true);
            }
        }
    }
}