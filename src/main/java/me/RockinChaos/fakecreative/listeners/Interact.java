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
import me.RockinChaos.core.utils.api.LegacyAPI;
import me.RockinChaos.core.utils.types.Altered;
import me.RockinChaos.fakecreative.modes.creative.Creative;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PistonExtensionMaterial;

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
                } else if (PlayerHandler.isCraftingInv(player)) {
                    player.getInventory().setItem(slot, item);
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
     * Gives the player the item that they are attempting to pick block when using the Pick Item.
     *
     * @param event - PlayerInteractEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPickItem(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && Creative.isCreativeMode(event.getPlayer(), true) && Creative.Tabs.PICK_ITEM.isTab(event.getItem())) {
            final Player player = event.getPlayer();
            final Block block = event.getClickedBlock();
            if (block == null || block.getType() == Material.AIR) return;
            Material blockType = block.getType();
            if (blockType.name().equals("PISTON_HEAD") || blockType.name().equals("PISTON_EXTENSION")) {
                if (ServerUtils.hasSpecificUpdate("1_13")) {
                    blockType = ((PistonHead) block.getBlockData()).getType() == PistonHead.Type.STICKY ? Material.STICKY_PISTON : Material.PISTON;
                } else {
                    final MaterialData data = block.getState().getData();
                    if (data instanceof PistonExtensionMaterial) {
                        blockType = ((PistonExtensionMaterial) data).isSticky() ? Material.valueOf("PISTON_STICKY_BASE") : Material.valueOf("PISTON_BASE");
                    }
                }
            }
            final Material finalBlockType = blockType;
            ItemStack item;
            if (ServerUtils.hasSpecificUpdate("1_13")) {
                item = new ItemStack(Altered.getAlter(finalBlockType));
            } else {
                Material itemMaterial = Altered.getAlter(finalBlockType);
                try {
                    final Material itemVariant = Material.valueOf(Altered.getAlter(finalBlockType).name() + "_ITEM");
                    if (itemVariant != Material.AIR) {
                        itemMaterial = itemVariant;
                    }
                } catch (IllegalArgumentException ignored) {}
                short durability = 0;
                if (itemMaterial.name().equals("SKULL") || itemMaterial.name().equals("SKULL_ITEM")) {
                    final BlockState blockState = block.getState();
                    if (blockState instanceof Skull) {
                        final Enum<?> skullType = LegacyAPI.getSkullType((Skull) blockState);
                        if (skullType != null) {
                            durability = (short) skullType.ordinal();
                        }
                    }
                }
                item = LegacyAPI.newItemStack(itemMaterial, 1, durability);
            }
            if (item.getType() == Material.AIR || Creative.isBlackListed(item)) return;
            try {
                final BlockState blockState = block.getState();
                final ItemMeta itemMeta = item.getItemMeta();
                if (StringUtils.containsIgnoreCase(item.getType().name(), "BANNER") && blockState instanceof Banner) {
                    if (itemMeta instanceof BannerMeta) {
                        final Banner bannerState = (Banner) blockState;
                        final BannerMeta bannerMeta = (BannerMeta) itemMeta;
                        if (ServerUtils.hasSpecificUpdate("1_12")) {
                            bannerMeta.setPatterns(bannerState.getPatterns());
                        } else {
                            bannerMeta.setPatterns(bannerState.getPatterns());
                            bannerMeta.getClass().getMethod("setBaseColor", DyeColor.class).invoke(bannerMeta, bannerState.getBaseColor());
                        }
                        item.setItemMeta(bannerMeta);
                    }
                } else if (StringUtils.containsIgnoreCase(item.getType().name(), "SKULL") || StringUtils.containsIgnoreCase(item.getType().name(), "HEAD")) {
                    if (blockState instanceof Skull) {
                        final Skull skullState = (Skull) blockState;
                        if (itemMeta instanceof SkullMeta) {
                            final SkullMeta skullMeta = (SkullMeta) itemMeta;
                            if (ServerUtils.hasSpecificUpdate("1_12")) {
                                if (skullState.hasOwner()) {
                                    skullMeta.setOwningPlayer(skullState.getOwningPlayer());
                                }
                            } else {
                                if (skullState.hasOwner()) {
                                    try {
                                        final String skullOwner = LegacyAPI.getSkullOwner(skullState);
                                        if (skullOwner != null) {
                                            LegacyAPI.setSkullOwner(player, skullMeta, skullOwner);
                                        }
                                    } catch (Exception e) {
                                        ServerUtils.sendDebugTrace(e);
                                    }
                                }
                            }
                            item.setItemMeta(skullMeta);
                        }
                    }
                } else {
                    if (itemMeta instanceof BlockStateMeta) {
                        ((BlockStateMeta) itemMeta).setBlockState(blockState);
                        item.setItemMeta(itemMeta);
                    }
                }
            } catch (Exception ignored) {}
            addItemToHotbar(player, item);
        }
    }

    /**
     * Gives the player the spawn egg of the entity they attempt to pick block.
     *
     * @param event - PlayerInteractEntityEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPickEntity(final PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player || !Creative.isCreativeMode(event.getPlayer(), true) || !Creative.Tabs.PICK_ITEM.isTab(PlayerHandler.getMainHandItem(event.getPlayer()))) return;
        final Player player = event.getPlayer();
        final Entity entity = event.getRightClicked();
        final ItemStack item = ItemHandler.getEntityItem(entity);
        if (item.getType() != Material.AIR && !Creative.isBlackListed(item)) {
            addItemToHotbar(player, item);
        }
    }

                        }
                    }
                } catch (Exception ignored) {
                }
                event.getPlayer().getInventory().addItem(item);
                PlayerHandler.updateInventory(event.getPlayer(), 0);
                event.setCancelled(true);
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