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
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public class Interact implements Listener {

    /**
     * Registers version-specific interact event listeners based on server version.
     * Listeners are conditionally registered to avoid NoClassDefFoundError on older versions.
     */
    public Interact() {
        if (ServerUtils.hasPreciseUpdate("1_21_4") && StringUtils.isRegistered(Interact_1_21_4.class.getSimpleName())) {
            FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Interact_1_21_4(), FakeCreative.getCore().getPlugin());
        }
        if (StringUtils.isRegistered(InfiniteArrowListener.class.getSimpleName())) {
            FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new InfiniteArrowListener(), FakeCreative.getCore().getPlugin());
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
        final Player player = event.getPlayer();
        final int slot = player.getInventory().getHeldItemSlot();
        if (event.getRightClicked() instanceof ItemFrame && Creative.isCreativeMode(event.getPlayer(), true)) {
            try {
                ItemStack item;
                if (ServerUtils.hasSpecificUpdate("1_9")) {
                    item = PlayerHandler.getPerfectHandItem(event.getPlayer(), event.getHand().toString());
                } else {
                    item = PlayerHandler.getPerfectHandItem(event.getPlayer(), "");
                }
                final ItemStack itemStack = item.clone();
                if (itemStack.getType() != Material.AIR) {
                    SchedulerUtils.run(() -> {
                        if (ServerUtils.hasSpecificUpdate("1_9")) {
                            if (event.getHand().equals(EquipmentSlot.HAND)) {
                                if (player.getInventory().getHeldItemSlot() == slot) {
                                    PlayerHandler.setMainHandItem(player, itemStack);
                                } else {
                                    player.getInventory().setItem(slot, itemStack);
                                }
                            } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
                                PlayerHandler.setOffHandItem(player, itemStack);
                            }
                        } else {
                            if (player.getInventory().getHeldItemSlot() == slot) {
                                PlayerHandler.setMainHandItem(player, itemStack);
                            } else {
                                player.getInventory().setItem(slot, itemStack);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                ServerUtils.sendDebugTrace(e);
            }
        }
    }

    /**
     * Refills consumable items (food, potions, etc.) after consumption.
     *
     * @param event - PlayerItemConsumeEvent
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onConsumeLock(final PlayerItemConsumeEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = event.getItem().clone();
        final int slot = player.getInventory().getHeldItemSlot();
        if (Creative.isCreativeMode(player, true)) {
            SchedulerUtils.run(() -> {
                if (ServerUtils.hasSpecificUpdate("1_9")) {
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
     * Refills bucket items after emptying.
     *
     * @param event - PlayerBucketEmptyEvent
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onBucketLockEmpty(final PlayerBucketEmptyEvent event) {
        final Player player = event.getPlayer();
        if (!Creative.isCreativeMode(player, true)) return;
        final int slot = player.getInventory().getHeldItemSlot();
        final ItemStack originalItem = (!ServerUtils.hasSpecificUpdate("1_9") || event.getHand() == EquipmentSlot.HAND) ? PlayerHandler.getMainHandItem(player) : PlayerHandler.getOffHandItem(player);
        if (originalItem.getType() == Material.AIR) return;
        final ItemStack item = originalItem.clone();
        final int originalCount = item.getAmount();
        event.setItemStack(new ItemStack(Material.AIR));
        SchedulerUtils.run(() -> {
            final ItemStack currentItem = (!ServerUtils.hasSpecificUpdate("1_9") || event.getHand() == EquipmentSlot.HAND) ? PlayerHandler.getMainHandItem(player) : PlayerHandler.getOffHandItem(player);
            final int currentCount = currentItem.getType() != Material.AIR ? currentItem.getAmount() : 0;
            if (currentCount != originalCount) {
                if (ServerUtils.hasSpecificUpdate("1_9")) {
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
            }
        });
    }

    /**
     * Empties bucket items after filling.
     *
     * @param event - PlayerBucketFillEvent
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onBucketLockFill(final PlayerBucketFillEvent event) {
        final Player player = event.getPlayer();
        if (!Creative.isCreativeMode(player, true)) return;
        final int slot = player.getInventory().getHeldItemSlot();
        final ItemStack originalItem = (!ServerUtils.hasSpecificUpdate("1_9") || event.getHand() == EquipmentSlot.HAND) ? PlayerHandler.getMainHandItem(player) : PlayerHandler.getOffHandItem(player);
        if (originalItem.getType() == Material.AIR) return;
        final ItemStack item = originalItem.clone();
        final int originalCount = item.getAmount();
        event.setItemStack(new ItemStack(Material.AIR));
        SchedulerUtils.run(() -> {
            final ItemStack currentItem = (!ServerUtils.hasSpecificUpdate("1_9") || event.getHand() == EquipmentSlot.HAND) ? PlayerHandler.getMainHandItem(player) : PlayerHandler.getOffHandItem(player);
            final int currentCount = currentItem.getType() != Material.AIR ? currentItem.getAmount() : 0;
            if (currentCount != originalCount) {
                if (ServerUtils.hasSpecificUpdate("1_9")) {
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
            }
        });
    }

    /**
     * Refills throwable items (snowballs, ender pearls, eggs, etc.) after throwing.
     *
     * @param event - ProjectileLaunchEvent
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onProjectileLock(final ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        final Player player = (Player) event.getEntity().getShooter();
        if (!Creative.isCreativeMode(player, true)) return;
        final int slot = player.getInventory().getHeldItemSlot();
        final ItemStack mainHandItem = PlayerHandler.getMainHandItem(player);
        final ItemStack offHandItem = PlayerHandler.getOffHandItem(player);
        ItemStack originalItem = isThrowableItem(mainHandItem, event.getEntity()) ? mainHandItem : isThrowableItem(offHandItem, event.getEntity()) ? offHandItem : null;
        boolean isMainHand = originalItem == mainHandItem;
        if (originalItem == null || originalItem.getType() == Material.AIR) return;
        final ItemStack item = originalItem.clone();
        final int originalCount = item.getAmount();
        SchedulerUtils.run(() -> {
            final ItemStack currentItem = isMainHand ? PlayerHandler.getMainHandItem(player) : PlayerHandler.getOffHandItem(player);
            final int currentCount = currentItem.getType() != Material.AIR ? currentItem.getAmount() : 0;
            if (currentCount != originalCount) {
                if (ServerUtils.hasSpecificUpdate("1_9")) {
                    if (isMainHand) {
                        if (player.getInventory().getHeldItemSlot() == slot) {
                            PlayerHandler.setMainHandItem(player, item);
                        } else {
                            player.getInventory().setItem(slot, item);
                        }
                    } else {
                        PlayerHandler.setOffHandItem(player, item);
                    }
                } else {
                    if (player.getInventory().getHeldItemSlot() == slot) {
                        PlayerHandler.setMainHandItem(player, item);
                    } else {
                        player.getInventory().setItem(slot, item);
                    }
                }
            }
        });
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
            if (!Creative.isCreativeMode(event.getPlayer(), true) || event.getBlock() == null || event.getBlock().getType() == Material.AIR) return;
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

    /**
     * Listener class that provides infinite arrow functionality for players in creative mode.
     * Temporarily adds arrows to a player's inventory when using a bow or crossbow without arrows,
     * then restores the original inventory state after shooting.
     */
    public static class InfiniteArrowListener implements Listener {

        private final Map<String, ReplacedSlot> active = new HashMap<>();
        private final Map<String, Integer> crossbowTasks = new HashMap<>();
        private static final long CROSSBOW_DRAW_TICKS = 40L;

        /**
         * Handles player interactions with bows and crossbows.
         * Temporarily adds an arrow to the inventory if the player is in creative mode and doesn't have any arrows available.
         *
         * @param event - PlayerInteractEvent
         */
        @EventHandler(priority = EventPriority.NORMAL)
        public void onInfiniteInteract(final PlayerInteractEvent event) {
            final Player player = event.getPlayer();
            if (!Creative.isCreativeMode(player, true) || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.PHYSICAL) {
                this.cleanup(player);
                return;
            }
            final ItemStack mainHandItem = event.getAction() == Action.RIGHT_CLICK_AIR ? PlayerHandler.getMainHandItem(player) : PlayerHandler.getOffHandItem(player);
            final ItemStack offHandItem = event.getAction() == Action.RIGHT_CLICK_AIR ? PlayerHandler.getOffHandItem(player) : PlayerHandler.getMainHandItem(player);
            final boolean isBow = offHandItem.getType() == Material.BOW || mainHandItem.getType() == Material.BOW;
            final boolean isCrossbow = !isBow && ServerUtils.hasSpecificUpdate("1_14") && (offHandItem.getType().name().equals("CROSSBOW") || mainHandItem.getType().name().equals("CROSSBOW"));
            if (!isBow && !isCrossbow) return;
            final String playerId = PlayerHandler.getPlayerID(player);
            final boolean isActive = active.containsKey(playerId);
            if (this.hasAnyArrow(player) && !isActive) return;
            if (!isActive) {
                final int arrowSlot = this.findReplaceableSlot(player);
                if (arrowSlot == -1) return;
                final ItemStack originalItem = player.getInventory().getItem(arrowSlot);
                player.getInventory().setItem(arrowSlot, new ItemStack(Material.ARROW, 1));
                active.put(playerId, new ReplacedSlot(arrowSlot, originalItem == null ? null : originalItem.clone()));
            }
            if (isCrossbow) {
                if (isActive) {
                    SchedulerUtils.cancelTask(crossbowTasks.get(playerId));
                }
                crossbowTasks.put(playerId, SchedulerUtils.runLater(CROSSBOW_DRAW_TICKS, () -> {
                    final ReplacedSlot data = active.remove(playerId);
                    if (data != null) this.restore(player, data);
                    crossbowTasks.remove(playerId);
                }));
            }
        }

        /**
         * Handles the bow/crossbow shooting event.
         * Restores the player's original inventory after shooting and sets the arrow
         * pickup status to creative-only mode. If the player had real arrows, increments
         * the arrow count to simulate infinite arrows.
         *
         * @param event - EntityShootBowEvent
         */
        @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
        public void onInfiniteShoot(final EntityShootBowEvent event) {
            if (!(event.getEntity() instanceof Player)) return;
            final Player player = (Player) event.getEntity();
            if (!Creative.isCreativeMode(player, true)) {
                this.cleanup(player);
                return;
            }
            final String playerId = PlayerHandler.getPlayerID(player);
            final ReplacedSlot data = active.remove(playerId);
            final Integer task = crossbowTasks.remove(playerId);
            if (task != null) SchedulerUtils.cancelTask(task);
            if (data != null) {
                if (ServerUtils.hasSpecificUpdate("1_12") && event.getProjectile() instanceof Arrow) {
                    ((Arrow) event.getProjectile()).setPickupStatus(Arrow.PickupStatus.CREATIVE_ONLY);
                }
                this.restore(player, data);
                return;
            }
            int arrowSlot = this.findFirstArrowSlot(player);
            if (arrowSlot != -1) {
                final ItemStack arrowItem = player.getInventory().getItem(arrowSlot);
                if (arrowItem != null && arrowItem.getType() != Material.AIR) arrowItem.setAmount(arrowItem.getAmount() + 1);
            }
        }

        /**
         * Cleans up temporary arrows when a player switches held items.
         *
         * @param event - PlayerItemHeldEvent
         */
        @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
        public void onSwitchCleanup(final PlayerItemHeldEvent event) {
            this.cleanup(event.getPlayer());
        }

        /**
         * Cleans up temporary arrows when a player quits the server.
         *
         * @param event - PlayerQuitEvent
         */
        @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
        public void onQuitCleanup(final PlayerQuitEvent event) {
            this.cleanup(event.getPlayer());
        }

        /**
         * Removes any temporary arrows and restores the original inventory state for a player.
         * Also cancels any pending crossbow reload tasks.
         *
         * @param player the player to clean up
         */
        private void cleanup(final Player player) {
            final String playerId = PlayerHandler.getPlayerID(player);
            final ReplacedSlot data = active.remove(playerId);
            if (data != null) this.restore(player, data);
            final Integer task = crossbowTasks.remove(playerId);
            if (task != null) {
                SchedulerUtils.cancelTask(task);
            }
        }

        /**
         * Restores the original item to the player's inventory slot.
         *
         * @param player the player whose inventory to restore
         * @param data the slot data containing the original item and slot number
         */
        private void restore(final Player player, final ReplacedSlot data) {
            player.getInventory().setItem(data.slot, data.original);
        }

        /**
         * Checks if the player has any arrows in their inventory.
         *
         * @param player the player to check
         * @return true if the player has at least one arrow, false otherwise
         */
        private boolean hasAnyArrow(final Player player) {
            for (final ItemStack item : player.getInventory().getContents()) {
                if (item == null) continue;
                if (item.getType().name().equals("ARROW") || item.getType().name().endsWith("_ARROW")) return true;
            }
            return false;
        }

        /**
         * Finds the first inventory slot containing arrows.
         *
         * @param player the player whose inventory to search
         * @return the slot index of the first arrow stack, or -1 if no arrows are found
         */
        private int findFirstArrowSlot(final Player player) {
            final ItemStack[] contents = player.getInventory().getContents();
            for (int i = 0; i < contents.length; i++) {
                final ItemStack item = contents[i];
                if (item == null) continue;
                if (item.getType().name().equals("ARROW") || item.getType().name().endsWith("_ARROW")) return i;
            }
            return -1;
        }

        /**
         * Finds an empty or replaceable slot in the player's main inventory (excluding hotbar).
         * If no empty slot is found, returns slot 9 as a fallback.
         *
         * @param player the player whose inventory to search
         * @return the slot index of an empty slot, or 9 if no empty slots are found
         */
        private int findReplaceableSlot(final Player player) {
            for (int i = 9; i < 36; i++) {
                final ItemStack item = player.getInventory().getItem(i);
                if (item == null || item.getType() == Material.AIR) return i;
            }
            return 9;
        }

        /**
         * Data class that stores information about a temporarily replaced inventory slot.
         */
        private static class ReplacedSlot {
            final int slot;
            final ItemStack original;

            /**
             * Creates a new ReplacedSlot record.
             *
             * @param slot the inventory slot index
             * @param original the original item stack (or null if the slot was empty)
             */
            ReplacedSlot(int slot, ItemStack original) {
                this.slot = slot;
                this.original = original;
            }
        }
    }

    /**
     * Checks if an item matches the projectile type that was launched.
     *
     * @param item - The item to check
     * @param projectile - The projectile entity
     * @return true if the item matches the projectile type
     */
    private boolean isThrowableItem(final ItemStack item, final Projectile projectile) {
        if (item == null || item.getType() == Material.AIR) return false;
        final Material itemType = item.getType();
        if (projectile instanceof Snowball) {
            return itemType.name().equals("SNOWBALL") || itemType.name().equals("SNOW_BALL");
        } else if (projectile instanceof Egg) {
            return itemType.name().equals("EGG");
        } else if (projectile instanceof EnderPearl) {
            return itemType.name().equals("ENDER_PEARL");
        } else if (projectile instanceof ThrownExpBottle) {
            return itemType.name().equals("EXPERIENCE_BOTTLE") || itemType.name().equals("EXP_BOTTLE");
        } else if (projectile instanceof ThrownPotion) {
            return itemType.name().contains("POTION");
        }
        return false;
    }
}