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
import me.RockinChaos.core.utils.CompatUtils;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.protocol.events.PlayerAutoCraftEvent;
import me.RockinChaos.fakecreative.modes.creative.Creative;
import me.RockinChaos.fakecreative.modes.creative.Creative.Tabs;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.function.Consumer;

public class Crafting implements Listener {

    private final HashMap<String, Long> closeDupe = new HashMap<>();

    /**
     * Prevents players from auto crafting with custom crafting items in their crafting slots.
     *
     * @param event - PlayerAutoCraftEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onAutoCraft(PlayerAutoCraftEvent event) {
        if (event.getContents() == null) {
            return;
        }
        ServerUtils.logDebug("{CRAFTING} Protocol-Packet auto recipe was triggered for the player " + event.getPlayer().getName() + ".");
        for (int i = 0; i <= 4; i++) {
            final ItemStack[] craftingContents = event.getContents().clone();
            if (!event.isCancelled() && Creative.isCreativeMode(event.getPlayer(), true) && Tabs.isItem(craftingContents[i])) {
                event.setCancelled(true);
            } else if (event.isCancelled()) {
                return;
            }
        }
    }

    /**
     * Removes custom crafting items from the players inventory when opening a GUI menu or storable inventory.
     *
     * @param event - InventoryOpenEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onCraftingOpen(InventoryOpenEvent event) {
        final Player player = (Player) event.getPlayer();
        if (Creative.isCreativeMode(player, true) && !PlayerHandler.getOpenCraftItems().containsKey(PlayerHandler.getPlayerID(player))) {
            ServerUtils.logDebug("{CRAFTING} Bukkit inventory was opened for the player " + event.getPlayer().getName() + ".");
            PlayerHandler.addOpenCraftItems(player, PlayerHandler.getTopContents(player));
            ItemHandler.removeCraftItems(player);
        }
    }

    /**
     * Gives the custom crafting items back when the player closes their inventory if they had items existing previously.
     *
     * @param event - InventoryCloseEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onCraftingClose(org.bukkit.event.inventory.InventoryCloseEvent event) {
        long dupeDuration = !this.closeDupe.isEmpty() && this.closeDupe.get(PlayerHandler.getPlayerID((Player) event.getPlayer())) != null ? System.currentTimeMillis() - this.closeDupe.get(PlayerHandler.getPlayerID((Player) event.getPlayer())) : -1;
        if (Creative.isCreativeMode(((Player) event.getPlayer()), true) && (!PlayerHandler.isCraftingInv(event.getView()) || (PlayerHandler.isCraftingInv(event.getView()) && (dupeDuration == -1 || dupeDuration > 30)))) {
            ServerUtils.logDebug("{CRAFTING} Bukkit inventory was closed for the player " + event.getPlayer().getName() + ".");
            ItemStack[] topContents = ItemHandler.cloneContents(CompatUtils.getTopInventory(event.getView()).getContents());
            this.handleClose(slot -> CompatUtils.getTopInventory(event.getView()).setItem(slot, new ItemStack(Material.AIR)), (Player) event.getPlayer(), event.getView(), topContents, true);
        }
    }

    /**
     * Gives the custom crafting items back when the player closes their inventory if they had items existing previously.
     *
     * @param event - InventoryCloseEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onCraftingClose(me.RockinChaos.core.utils.protocol.events.InventoryCloseEvent event) {
        if (Creative.isCreativeMode(event.getPlayer(), true) && PlayerHandler.isCraftingInv(event.getView())) {
            ServerUtils.logDebug("{CRAFTING} Protocol-Packet inventory was closed for the player " + event.getPlayer().getName() + ".");
            this.closeDupe.put(PlayerHandler.getPlayerID(event.getPlayer()), System.currentTimeMillis());
            this.handleClose(slot -> {
                if (!event.isCancelled()) {
                    event.setCancelled(true);
                }
            }, event.getPlayer(), event.getView(), event.getPreviousContents(true), false);
        }
    }

    /**
     * Removes all crafting items from the 2x2 crafting view when the player leaves the server.
     * Returns the custom crafting item to the player if it is dropped automagically when switching worlds,
     * typically via the nether portal causing duplication glitches.
     *
     * @param event - PlayerDropItemEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onCraftingDrop(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemCopy = event.getItemDrop().getItemStack().clone();
        double health;
        try {
            health = player.getHealth();
        } catch (Exception e) {
            health = (player.isDead() ? 0 : 1);
        }
        if (health > 0 && Creative.isCreativeMode(player, true) && Tabs.isItem(itemCopy)) {
            event.getItemDrop().remove();
            SchedulerUtils.runLater(2L, () -> {
                if (player.isOnline()) {
                    Tabs.setTabs(player);
                }
            });
        }
    }

    /**
     * Called on player switching worlds.
     * Removes any crafting items from the player which ended up in their inventory slots.
     *
     * @param event - PlayerChangedWorldEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void onCraftingWorldSwitch(PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        final ItemStack[] inventory = player.getInventory().getContents();
        if (!ItemHandler.isContentsEmpty(inventory)) {
            for (int i = 0; i < inventory.length; i++) {
                if (Creative.isCreativeMode(player, true) && Tabs.isItem(inventory[i])) {
                    player.getInventory().setItem(i, new ItemStack(Material.AIR));
                }
            }
        }
    }

    /**
     * Removes custom crafting items from the player when they enter creative mode.
     *
     * @param event - PlayerGameModeChangeEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onSwitchGamemode(PlayerGameModeChangeEvent event) {
        final Player player = event.getPlayer();
        if (event.getNewGameMode() == GameMode.CREATIVE) {
            PlayerHandler.addCreativeCraftItems(player, PlayerHandler.getCraftItems().get(PlayerHandler.getPlayerID(player)));
            ItemHandler.removeCraftItems(player);
        } else if (event.getNewGameMode() != GameMode.CREATIVE && PlayerHandler.getCreativeCraftItems().containsKey(PlayerHandler.getPlayerID(player))) {
            this.returnCrafting(event.getPlayer(), PlayerHandler.getCreativeCraftItems().get(PlayerHandler.getPlayerID(player)), 1L, false);
            PlayerHandler.addCraftItems(player, PlayerHandler.getCreativeCraftItems().get(PlayerHandler.getPlayerID(player)));
            PlayerHandler.addCraftItems(player, PlayerHandler.getCreativeCraftItems().get(PlayerHandler.getPlayerID(player)));
            PlayerHandler.removeCreativeCraftItems(player);
        }
        PlayerHandler.updateInventory(player, 1L);
    }

    /**
     * Attempts to save and return the prior open inventory crafting slots.
     *
     * @param input     - The methods to be executed.
     * @param player    - The Player being handled.
     * @param view      - The view being referenced.
     * @param inventory - The inventory being handled.
     */
    private void handleClose(final Consumer<Integer> input, final Player player, final Object view, final ItemStack[] inventory, final boolean slotZero) {
        if (PlayerHandler.isCraftingInv(view)) {
            if (!ItemHandler.isContentsEmpty(inventory)) {
                boolean isCrafting = false;
                for (int i = 0; i <= 4; i++) {
                    if (Tabs.isItem(inventory[i])) {
                        isCrafting = true;
                        input.accept(i);
                    }
                }
                for (int i = 0; i <= 4; i++) {
                    if (isCrafting && i != 0 && inventory[i] != null && inventory[i].getType() != Material.AIR) {
                        if (!Tabs.isItem(inventory[i])) {
                            final int k = i;
                            final ItemStack drop = inventory[i].clone();
                            SchedulerUtils.run(() -> {
                                double health;
                                try {
                                    health = player.getHealth();
                                } catch (Exception e) {
                                    health = (player.isDead() ? 0 : 1);
                                }
                                if (health > 0) {
                                    CompatUtils.getTopInventory(player).setItem(k, new ItemStack(Material.AIR));
                                    if (player.getInventory().firstEmpty() != -1) {
                                        player.getInventory().addItem(drop);
                                        ServerUtils.logDebug("{CRAFTING} An item was flagged as non-crafting, adding it back to the player " + player.getName());
                                    } else {
                                        Item itemDropped = player.getWorld().dropItem(player.getLocation(), drop);
                                        itemDropped.setPickupDelay(40);
                                        ServerUtils.logDebug("{CRAFTING} An item was flagged as non-crafting and the player " + player.getName() + " has a full inventory, item will instead be self-dropped.");
                                    }
                                }
                            });
                            inventory[i] = new ItemStack(Material.AIR);
                        }
                    }
                }
                if (isCrafting) {
                    if (!slotZero || Tabs.isItem(inventory[0])) {
                        this.returnCrafting(player, inventory, 1L, slotZero);
                    } else {
                        SchedulerUtils.runLater(1L, () -> {
                            CompatUtils.getTopInventory(player).setItem(0, new ItemStack(Material.AIR));
                            PlayerHandler.updateInventory(player, new ItemStack(Material.AIR), 1L);
                        });
                    }
                }
            }
        } else {
            SchedulerUtils.run(() -> {
                double health;
                try {
                    health = player.getHealth();
                } catch (Exception e) {
                    health = (player.isDead() ? 0 : 1);
                }
                if (health > 0 && PlayerHandler.isCraftingInv(player) && PlayerHandler.getOpenCraftItems().containsKey(PlayerHandler.getPlayerID(player))) {
                    ItemStack[] openCraftContents = PlayerHandler.getOpenCraftItems().get(PlayerHandler.getPlayerID(player));
                    if (openCraftContents != null && openCraftContents.length != 0) {
                        this.returnCrafting(player, openCraftContents, 1L, false);
                        PlayerHandler.addCraftItems(player, PlayerHandler.getOpenCraftItems().get(PlayerHandler.getPlayerID(player)));
                        PlayerHandler.removeOpenCraftItems(player);
                    }
                }
            });
        }
    }

    /**
     * Returns the custom crafting item to the player after the specified delay.
     *
     * @param player   - the Player having their item returned.
     * @param contents - the crafting contents to be returned.
     * @param delay    - the delay to wait before returning the item.
     */
    private void returnCrafting(final Player player, final ItemStack[] contents, final long delay, final boolean slotZero) {
        SchedulerUtils.runLater(delay, () -> {
            if (!player.isOnline()) {
                return;
            } else if (!PlayerHandler.isCraftingInv(player)) {
                this.returnCrafting(player, contents, 10L, slotZero);
                return;
            }
            if (!slotZero) {
                for (int i = 4; i >= 0; i--) {
                    if (contents[i] != null && Tabs.isItem(contents[i])) {
                        CompatUtils.getTopInventory(player).setItem(i, contents[i]);
                        if (i != 0) PlayerHandler.updateInventory(player, contents[i].clone(), 0L);
                        PlayerHandler.updateInventory(player, contents[0].clone(), 1L);
                    }
                }
            } else {
                if (contents[0] != null && Tabs.isItem(contents[0])) {
                    CompatUtils.getTopInventory(player).setItem(0, contents[0]);
                    PlayerHandler.updateInventory(player, contents[0].clone(), 1L);
                }
            }
        });
    }
}