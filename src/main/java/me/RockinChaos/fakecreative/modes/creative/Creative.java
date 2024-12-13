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
package me.RockinChaos.fakecreative.modes.creative;

import me.RockinChaos.core.Core;
import me.RockinChaos.core.handlers.ItemHandler;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.CompatUtils;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.core.utils.api.LegacyAPI;
import me.RockinChaos.core.utils.protocol.events.PlayerEnterCreativeEvent;
import me.RockinChaos.fakecreative.FakeCreative;
import me.RockinChaos.fakecreative.modes.Mode;
import me.RockinChaos.fakecreative.modes.instance.PlayerObject;
import me.RockinChaos.fakecreative.utils.menus.Menu;
import me.RockinChaos.fakecreative.utils.sql.DataObject;
import me.RockinChaos.fakecreative.utils.sql.DataObject.Table;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Creative {

    private static final List<PlayerObject> creativePlayers = new ArrayList<>();
    private static final List<String> activePickItem = new ArrayList<>();


    /**
     * Puts the Player in Fake Creative Mode if they have an existing DataObject.
     *
     * @param player - The player to have their creative restored.
     * @param silent - If the event should be silent.
     */
    public static void restart(final Player player, final boolean silent) {
        if (isCreativeMode(player, true)) {
            Bukkit.getPluginManager().callEvent(new PlayerEnterCreativeEvent(player, null, false, true, silent));
        }
    }

    /**
     * Attempts to put all players in Creative Mode that are currently online if they have an existing DataObject.
     *
     * @param silent - If the event should be silent.
     */
    public static void restart(final boolean silent) {
        final List<Object> dataObject = Core.getCore().getSQL().getDataList(new DataObject(Table.PLAYERSTATS));
        for (Object object : dataObject) {
            final DataObject playerStats = (DataObject) object;
            final Player player = PlayerHandler.getPlayerString(playerStats.getPlayerId());
            final PlayerObject playerObject = new PlayerObject(playerStats.getPlayerId(), playerStats.getHealth(), playerStats.getMaxHealth(), playerStats.getFood(), playerStats.getFireTicks());
            playerObject.setInventory64(playerStats.getInventory64());
            if (player != null && player.isOnline() && playerObject.getStats().autoRestore()) {
                creativePlayers.add(playerObject);
                {
                    Bukkit.getPluginManager().callEvent(new PlayerEnterCreativeEvent(player, null, false, true, silent));
                }
            } else {
                creativePlayers.add(playerObject);
            }
            Core.getCore().getSQL().removeData(playerStats);
        }
    }

    /**
     * Puts the Player in Fake Creative Mode.
     *
     * @param who     - The executor.
     * @param altWho  - The player to be set to Creative.
     * @param refresh - If the stats should be refreshed.
     * @param restore - If the creative stats should be restored.
     * @param silent  - If the status message should be sent.
     */
    public static void setCreative(final CommandSender who, final Player altWho, final boolean refresh, final boolean restore, final boolean silent) {
        final Player argsPlayer = (altWho != null ? altWho : (Player) who);
        if (argsPlayer != null && (refresh || restore || !isCreativeMode(argsPlayer, true))) {
            if (!PlayerHandler.isSurvivalMode(argsPlayer)) {
                argsPlayer.setGameMode(GameMode.SURVIVAL);
            }
            double health = argsPlayer.getHealth();
            double maxHealth = 20;
            try {
                maxHealth = (ServerUtils.hasSpecificUpdate("1_9") ? Objects.requireNonNull(argsPlayer.getAttribute((Attribute)CompatUtils.valueOf(Attribute.class, "GENERIC_MAX_HEALTH"))).getBaseValue() : (double) argsPlayer.getClass().getMethod("getMaxHealth").invoke(argsPlayer));
            } catch (Exception ignored) {
            }
            if (!refresh && !restore) {
                creativePlayers.add(new PlayerObject(PlayerHandler.getPlayerID(argsPlayer), (Math.min(maxHealth * 2, health)), maxHealth, argsPlayer.getFoodLevel(), argsPlayer.getFireTicks()));
            }
            get(argsPlayer).setInventory64(Mode.getInventory64(argsPlayer));
            final PlayerObject playerObject = get(argsPlayer);
            Mode.setFlight(argsPlayer, true);
            if (ServerUtils.hasSpecificUpdate("1_9")) {
                argsPlayer.setInvulnerable(playerObject.getStats().god());
            }
            if (!refresh) {
                try {
                    argsPlayer.setHealth(playerObject.getStats().health());
                } catch (IllegalArgumentException e) {
                    LegacyAPI.setHealth(argsPlayer, playerObject.getStats().health());
                    SchedulerUtils.run(() -> argsPlayer.setHealth(playerObject.getStats().health()));
                }
            }
            SchedulerUtils.run(() -> {
                if (!refresh) {
                    if (ServerUtils.hasSpecificUpdate("1_9")) {
                        Objects.requireNonNull(argsPlayer.getAttribute((Attribute)CompatUtils.valueOf(Attribute.class, "GENERIC_MAX_HEALTH"))).setBaseValue(((playerObject.getStats().heartScale()) * 2));
                    } else {
                        LegacyAPI.setMaxHealth(argsPlayer, ((playerObject.getStats().heartScale()) * 2));
                    }
                }
                if (!playerObject.getStats().allowBurn()) {
                    argsPlayer.setFireTicks(0);
                }
                argsPlayer.setFoodLevel(playerObject.getStats().foodLevel());
                Mode.dropTargets(argsPlayer);
            });
            Tabs.setTabs(argsPlayer);
            if (!restore) {
                ServerUtils.logDebug(argsPlayer.getName() + " was set to fake creative.");
            } else {
                ServerUtils.logDebug(argsPlayer.getName() + " had their fake creative restored.");
            }
        }
        if ((!refresh && !restore) || (!silent)) {
            Mode.sendStatus(who, argsPlayer, GameMode.CREATIVE, false);
        }
    }


    /**
     * Checks if the player is currently in creative mode.
     *
     * @param player - The player to be checked.
     * @return If the player is currently in fake creative mode.
     */
    private static boolean isFakeCreativeMode(final Player player) {
        final String str = PlayerHandler.getPlayerID(player);
        synchronized ("SET_CREATIVE") {
            for (final PlayerObject pl : creativePlayers) {
                if (pl.getPlayer().equalsIgnoreCase(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the player is currently in creative mode.
     *
     * @param player - The player to be checked.
     * @param isFake - If the player should be checked for fake creative mode.
     * @return If the player is currently in creative mode.
     */
    public static boolean isCreativeMode(final Player player, final boolean isFake) {
        if (!isFake && player.getGameMode() == GameMode.CREATIVE) {
            return true;
        } else if (isFake) {
            return isFakeCreativeMode(player);
        }
        return false;
    }

    /**
     * Adds the current Player as a Pick Item user.
     *
     * @param player - The Player being referenced.
     */
    public static void addPick(final Player player) {
        if (!activePickItem.contains(PlayerHandler.getPlayerID(player))) {
            activePickItem.add(PlayerHandler.getPlayerID(player));
        }
    }

    /**
     * Removes the current Player as a Pick Item user.
     *
     * @param player - The Player being referenced.
     */
    public static void removePick(final Player player) {
        activePickItem.remove(PlayerHandler.getPlayerID(player));
    }

    /**
     * Gets the current list of Fake Creative Players.
     *
     * @param player - The Player being referenced.
     */
    public static @Nonnull PlayerObject get(final Player player) {
        final String str = PlayerHandler.getPlayerID(player);
        synchronized ("SET_CREATIVE") {
            for (final PlayerObject pl : creativePlayers) {
                if (pl.getPlayer().equalsIgnoreCase(str)) {
                    return pl;
                }
            }
        }
        return new PlayerObject();
    }

    /**
     * Removes the Player from the stored Fake Creative Players
     *
     * @param player - The Player being referenced.
     */
    public static void remove(final Player player) {
        final String str = PlayerHandler.getPlayerID(player);
        synchronized ("SET_CREATIVE") {
            for (final PlayerObject pl : creativePlayers) {
                if (pl.getPlayer().equalsIgnoreCase(str)) {
                    creativePlayers.remove(pl);
                    return;
                }
            }
        }
    }

    /**
     * Saves the existing creative players to SQL.
     */
    public static void save() {
        for (PlayerObject playerObject : creativePlayers) {
            if (playerObject.getStats().autoRestore()) {
                Core.getCore().getSQL().saveData(new DataObject(Table.PLAYERSTATS, playerObject.getPlayer(), String.valueOf(playerObject.getHealth()), String.valueOf(playerObject.getMaxHealth()),
                        String.valueOf(playerObject.getFood()), String.valueOf(playerObject.getFireTicks()), playerObject.getInventory64()));
            }
        }
    }

    /**
     * Checks if the ItemStack Material is a blacklisted type.
     *
     * @param item - The ItemStack being checked.
     * @return If the ItemStack Material is a blacklisted type.
     */
    public static boolean isBlackListed(final ItemStack item) {
        return (FakeCreative.getCore().getConfig("config.yml").getString("Settings.Creative-Blacklist") != null && StringUtils.containsValue(FakeCreative.getCore().getConfig("config.yml").getStringList("Settings.Creative-Blacklist"), item.getType().name()));
    }

    @SuppressWarnings("unused")
    public enum Tabs {
        PICK_ITEM(ItemHandler.getItem("STICK", 1, true, true, "&d&1&c&2&a&b&l&nPick Block", "&7", "&7&o*Right-click a block to", "&7&oadd to your inventory.")),
        PICK(ItemHandler.getItem("STICK", 1, false, true, "&a&1&c&2&d&b&l&nPick Block", "&7", "&7&o*Allows you to clone", "&7&oa existing block item.")),
        CREATIVE(ItemHandler.getItem("APPLE", 1, false, true, "&a&1&c&2&d&e&l&nCreative Tab", "&7", "&7&o*Access the creative menu to", "&7&oselect from a list of minecraft items.")),
        SAVE(ItemHandler.getItem("PAPER", 1, false, true, "&a&1&c&2&d&a&l&nSaved Hotbars", "&7", "&7&o*Save or restore a hotbar", "&7&oto your current inventory.")),
        USER(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PLAYER_HEAD" : "SKULL_ITEM:3"), 1, false, true, "&a&1&c&2&d&6&l&nPreferences", "&7", "&7*Creative mode settings", "&7that are specific to you.")),
        DESTROY(ItemHandler.getItem("LAVA_BUCKET", 1, false, true, "&a&1&c&2&d&c&l&nDestroy Item", "&7", "&7*Permanently destroy your items.", "&7", "&8&oDrop an item to delete it.", "&8&oShift-click to clear inventory."));

        private final ItemStack itemStack;

        Tabs(final ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        /**
         * Checks if the ItemStack is a Creative Tab.
         *
         * @param item - The ItemStack being checked.
         * @return If the ItemStack is a Creative Tab.
         */
        public static boolean isItem(final ItemStack item) {
            for (Tabs tab : Tabs.values()) {
                if (ItemHandler.isSimilar(item, tab.getItem())) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Sets the Creative Tabs to the crafting items slots.
         *
         * @param player - The Player having their crafting items set.
         */
        public static void setTabs(final Player player) {
            if (PlayerHandler.isCraftingInv(player)) {
                final Inventory craftInventory = CompatUtils.getTopInventory(player);
                for (int i = 0; i <= 4; i++) {
                    if (craftInventory.getItem(i) != null && Objects.requireNonNull(craftInventory.getItem(i)).getType() != Material.AIR) {
                        final ItemStack drop = Objects.requireNonNull(craftInventory.getItem(i)).clone();
                        craftInventory.setItem(i, new ItemStack(Material.AIR));
                        if (!Tabs.isItem(drop) && player.getInventory().firstEmpty() != -1) {
                            player.getInventory().addItem(drop);
                        } else if (!Tabs.isItem(drop)) {
                            PlayerHandler.dropItem(player, drop);
                        }
                    }
                }
                final ItemStack userClone = Tabs.USER.getItem().clone();
                userClone.setItemMeta(ItemHandler.setSkullOwner(Objects.requireNonNull(userClone.getItemMeta()), player, player.getName()));
                if (!get(player).getStats().isLocalePreferences(player)) {
                    final ItemMeta userMeta = userClone.getItemMeta();
                    List<String> userLore = new ArrayList<>();
                    userLore.add(StringUtils.colorFormat("&7"));
                    userLore.add(StringUtils.colorFormat("&7*Creative mode settings"));
                    userLore.add(StringUtils.colorFormat("&7that are specific to you."));
                    userLore.add(StringUtils.colorFormat("&7"));
                    userLore.add(StringUtils.colorFormat("&c[✘] You do not have permission."));
                    userMeta.setLore(userLore);
                    userClone.setItemMeta(userMeta);
                }
                if (activePickItem.contains(PlayerHandler.getPlayerID(player))) {
                    if (player.getInventory().getItem(8) != null && Objects.requireNonNull(player.getInventory().getItem(8)).getType() != Material.AIR && !Objects.requireNonNull(player.getInventory().getItem(8)).isSimilar(Tabs.PICK_ITEM.getItem())) {
                        ItemStack drop = Objects.requireNonNull(player.getInventory().getItem(8)).clone();
                        player.getInventory().setItem(8, new ItemStack(Material.AIR));
                        player.getInventory().setItem(8, Tabs.PICK_ITEM.getItem());
                        if (player.getInventory().firstEmpty() != -1) {
                            player.getInventory().addItem(drop);
                        } else {
                            PlayerHandler.dropItem(player, drop);
                        }
                    } else {
                        player.getInventory().setItem(8, Tabs.PICK_ITEM.getItem());
                    }
                }
                craftInventory.setItem(1, Tabs.CREATIVE.getItem());
                craftInventory.setItem(2, Tabs.PICK.getItem());
                craftInventory.setItem(3, Tabs.SAVE.getItem());
                craftInventory.setItem(4, userClone);
                SchedulerUtils.run(() -> {
                    craftInventory.setItem(0, Tabs.DESTROY.getItem());
                    PlayerHandler.updateInventory(player);
                });
            }
        }

        /**
         * Clears the Creative Tabs from the crafting items slots.
         *
         * @param player - The Player having their crafting items cleared.
         */
        public static void clearTabs(final Player player) {
            if (Menu.isOpen(player)) {
                Menu.closeMenu();
            }
            for (final ItemStack item : player.getInventory()) {
                if (Tabs.isItem(item)) {
                    player.getInventory().remove(item);
                }
            }
            for (final ItemStack item : CompatUtils.getTopInventory(player)) {
                if (Tabs.isItem(item)) {
                    CompatUtils.getTopInventory(player).remove(item);
                }
            }
            removePick(player);
        }

        /**
         * Gets the Creative Tab ItemStack.
         *
         * @return The Creative Tab ItemStack.
         */
        public ItemStack getItem() {
            return this.itemStack;
        }

        /**
         * Checks if the ItemStack is a Creative Tab.
         *
         * @param item - The ItemStack being checked.
         * @return If the ItemStack is a Creative Tab.
         */
        public boolean isTab(final ItemStack item) {
            return ItemHandler.isSimilar(item, this.itemStack);
        }
    }
}