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
package me.RockinChaos.fakecreative.modes.instance;

import me.RockinChaos.core.Core;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.api.LegacyAPI;
import me.RockinChaos.fakecreative.FakeCreative;
import me.RockinChaos.fakecreative.modes.Mode;
import me.RockinChaos.fakecreative.modes.creative.Creative;
import me.RockinChaos.fakecreative.utils.sql.DataObject;
import me.RockinChaos.fakecreative.utils.sql.DataObject.Table;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;

public class PlayerStats {

    private final HashMap<Integer, String> hotbars = new HashMap<>();
    private boolean allowFlight = FakeCreative.getCore().getConfig("config.yml").getBoolean("Preferences.Allow-Flight");
    private double flySpeed = FakeCreative.getCore().getConfig("config.yml").getDouble("Preferences.Fly-Speed");
    private double breakSpeed = FakeCreative.getCore().getConfig("config.yml").getDouble("Preferences.Break-Speed");
    private int foodLevel = FakeCreative.getCore().getConfig("config.yml").getInt("Preferences.Food-Level");
    private int health = Math.min(FakeCreative.getCore().getConfig("config.yml").getInt("Preferences.Health"), ((int) FakeCreative.getCore().getConfig("config.yml").getDouble("Preferences.Heart-Scale")) * 2);
    private double heartScale = FakeCreative.getCore().getConfig("config.yml").getDouble("Preferences.Heart-Scale");
    private boolean allowHunger = FakeCreative.getCore().getConfig("config.yml").getBoolean("Preferences.Allow-Hunger");
    private boolean allowBurn = FakeCreative.getCore().getConfig("config.yml").getBoolean("Preferences.Allow-Burn");
    private boolean unbreakableItems = FakeCreative.getCore().getConfig("config.yml").getBoolean("Preferences.Unbreakable-Items");
    private boolean blockDrops = FakeCreative.getCore().getConfig("config.yml").getBoolean("Preferences.Block-Drops");
    private boolean swordBlock = FakeCreative.getCore().getConfig("config.yml").getBoolean("Preferences.Sword-Block");
    private boolean autoRestore = FakeCreative.getCore().getConfig("config.yml").getBoolean("Preferences.Auto-Restore");
    private boolean god = FakeCreative.getCore().getConfig("config.yml").getBoolean("Preferences.Invulnerable");
    private int godDelay = FakeCreative.getCore().getConfig("config.yml").getInt("Preferences.Invulnerable-Delay");
    private boolean storeInventory = FakeCreative.getCore().getConfig("config.yml").getBoolean("Preferences.Store-Inventory");
    private boolean destroyPickups = FakeCreative.getCore().getConfig("config.yml").getBoolean("Preferences.Destroy-Pickups");

    /**
     * Creates a new PlayerStats instance
     *
     * @param player - The Player being accessed.
     */
    public PlayerStats(final Player player) {
        if (player != null && this.isLocalePreferences(player)) {
            final DataObject allowFlight = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.ALLOW_FLIGHT, PlayerHandler.getPlayerID(player), true));
            if (allowFlight != null) {
                this.allowFlight = allowFlight.getBoolean();
            }

            final DataObject flySpeed = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.SPEED_FLIGHT, PlayerHandler.getPlayerID(player), 0.0));
            if (flySpeed != null) {
                this.flySpeed = flySpeed.getDouble();
            }

            final DataObject breakSpeed = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.SPEED_BREAK, PlayerHandler.getPlayerID(player), 0.0));
            if (breakSpeed != null) {
                this.breakSpeed = breakSpeed.getDouble();
            }

            final DataObject foodLevel = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.SET_FOOD, PlayerHandler.getPlayerID(player), 0));
            if (foodLevel != null) {
                this.foodLevel = foodLevel.getInt();
            }

            final DataObject health = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.SET_HEALTH, PlayerHandler.getPlayerID(player), 0));
            if (health != null) {
                this.health = health.getInt();
            }

            final DataObject heartScale = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.SET_SCALE, PlayerHandler.getPlayerID(player), 0.0));
            if (heartScale != null) {
                this.heartScale = heartScale.getDouble();
            }

            final DataObject allowHunger = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.ALLOW_HUNGER, PlayerHandler.getPlayerID(player), true));
            if (allowHunger != null) {
                this.allowHunger = allowHunger.getBoolean();
            }

            final DataObject allowBurn = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.ALLOW_BURN, PlayerHandler.getPlayerID(player), true));
            if (allowBurn != null) {
                this.allowBurn = allowBurn.getBoolean();
            }

            final DataObject unbreakableItems = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.UNBREAKABLE_ITEMS, PlayerHandler.getPlayerID(player), true));
            if (unbreakableItems != null) {
                this.unbreakableItems = unbreakableItems.getBoolean();
            }

            final DataObject blockDrops = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.DROPS_BLOCK, PlayerHandler.getPlayerID(player), true));
            if (blockDrops != null) {
                this.blockDrops = blockDrops.getBoolean();
            }

            final DataObject swordBlock = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.SWORD_BLOCK, PlayerHandler.getPlayerID(player), true));
            if (swordBlock != null) {
                this.swordBlock = swordBlock.getBoolean();
            }

            final DataObject autoRestore = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.AUTO_RESTORE, PlayerHandler.getPlayerID(player), true));
            if (autoRestore != null) {
                this.autoRestore = autoRestore.getBoolean();
            }

            final DataObject god = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.SET_GOD, PlayerHandler.getPlayerID(player), true));
            if (god != null) {
                this.god = god.getBoolean();
            }

            final DataObject godDelay = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.DELAY_GOD, PlayerHandler.getPlayerID(player), 0));
            if (godDelay != null) {
                this.godDelay = godDelay.getInt();
            }

            final DataObject storeInventory = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.STORE_INVENTORY, PlayerHandler.getPlayerID(player), true));
            if (storeInventory != null) {
                this.storeInventory = storeInventory.getBoolean();
            }

            final DataObject destroyPickups = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.DESTROY_PICKUPS, PlayerHandler.getPlayerID(player), true));
            if (destroyPickups != null) {
                this.destroyPickups = destroyPickups.getBoolean();
            }

        }

        for (int i = 1; i <= 9; i++) {
            final DataObject dataObject = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.HOTBAR, PlayerHandler.getPlayerID(player), Integer.toString(i), "64"));
            if (dataObject != null) {
                this.hotbars.put(Integer.valueOf(dataObject.getPosition()), dataObject.getInventory64());
            }
        }
    }

    /**
     * Checks if the Player is allowed to fly.
     *
     * @return If the Player is allowed to fly.
     */
    public boolean allowFlight() {
        return this.allowFlight;
    }

    /**
     * Sets the current state of Allow Flight.
     *
     * @param player      - The Player being referenced.
     * @param allowFlight - If the Player should be allowed to fly in creative mode.
     */
    public void setFlight(final Player player, final boolean allowFlight) {
        synchronized ("FK_SQL") {
            this.allowFlight = allowFlight;
            final DataObject dataObject = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.ALLOW_FLIGHT, PlayerHandler.getPlayerID(player), allowFlight));
            if (dataObject != null) {
                Core.getCore().getSQL().removeData(dataObject);
            }
            Core.getCore().getSQL().saveData(new DataObject(Table.ALLOW_FLIGHT, PlayerHandler.getPlayerID(player), allowFlight));
            player.setAllowFlight(allowFlight);
            player.setFlying(allowFlight);
        }
    }

    /**
     * Gets the fly speed for the Player.
     *
     * @return The fly speed.
     */
    public double flySpeed() {
        return this.flySpeed;
    }

    /**
     * Sets the current state of Fly Speed.
     *
     * @param player   - The Player being referenced.
     * @param flySpeed - The speed that the Player will fly at when flight is activated.
     */
    public void setFlySpeed(final Player player, final double flySpeed) {
        synchronized ("FK_SQL") {
            this.flySpeed = (flySpeed >= 0 ? flySpeed : 1);
            final DataObject dataObject = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.SPEED_FLIGHT, PlayerHandler.getPlayerID(player), flySpeed));
            if (dataObject != null) {
                Core.getCore().getSQL().removeData(dataObject);
            }
            Core.getCore().getSQL().saveData(new DataObject(Table.SPEED_FLIGHT, PlayerHandler.getPlayerID(player), flySpeed));
            player.setFlySpeed(Float.parseFloat((Double.toString((flySpeed) / 10))));
        }
    }

    /**
     * Gets the break speed for the Player.
     *
     * @return The break speed.
     */
    public double breakSpeed() {
        return this.breakSpeed;
    }

    /**
     * Sets the current state of Break Speed.
     *
     * @param player     - The Player being referenced.
     * @param breakSpeed - The speed that the player breaks blocks or objects in the world.
     */
    public void setBreakSpeed(final Player player, final double breakSpeed) {
        synchronized ("FK_SQL") {
            this.breakSpeed = (breakSpeed >= 0 ? breakSpeed : 3);
            final DataObject dataObject = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.SPEED_BREAK, PlayerHandler.getPlayerID(player), breakSpeed));
            if (dataObject != null) {
                Core.getCore().getSQL().removeData(dataObject);
            }
            Core.getCore().getSQL().saveData(new DataObject(Table.SPEED_BREAK, PlayerHandler.getPlayerID(player), breakSpeed));
        }
    }

    /**
     * Gets the food level for the Player.
     *
     * @return The food level.
     */
    public int foodLevel() {
        return this.foodLevel;
    }

    /**
     * Sets the current state of Food.
     *
     * @param player    - The Player being referenced.
     * @param foodLevel - The food size for the Player.
     */
    public void setFoodLevel(final Player player, final int foodLevel) {
        synchronized ("FK_SQL") {
            this.foodLevel = (foodLevel >= 0 ? foodLevel : 20);
            final DataObject dataObject = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.SET_FOOD, PlayerHandler.getPlayerID(player), foodLevel));
            if (dataObject != null) {
                Core.getCore().getSQL().removeData(dataObject);
            }
            Core.getCore().getSQL().saveData(new DataObject(Table.SET_FOOD, PlayerHandler.getPlayerID(player), foodLevel));
            player.setFoodLevel(foodLevel);
        }
    }

    /**
     * Gets the health for the Player.
     *
     * @return The health.
     */
    public int health() {
        return this.health;
    }

    /**
     * Sets the current state of Health.
     *
     * @param player      - The Player being referenced.
     * @param health      - The health size for the Player.
     * @param scaleUpdate - If the trigger is from a heart-scale update.
     */
    public void setHealth(final Player player, final int health, final boolean scaleUpdate) {
        synchronized ("FK_SQL") {
            this.health = (health >= 0 ? health : 20);
            final DataObject dataObject = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.SET_HEALTH, PlayerHandler.getPlayerID(player), health));
            if (dataObject != null) {
                Core.getCore().getSQL().removeData(dataObject);
            }
            Core.getCore().getSQL().saveData(new DataObject(Table.SET_HEALTH, PlayerHandler.getPlayerID(player), health));
            if (!scaleUpdate) {
                player.setHealth(health);
            }
        }
    }

    /**
     * Gets the heart scale for the Player.
     *
     * @return The heart scale.
     */
    public double heartScale() {
        return this.heartScale;
    }

    /**
     * Sets the current state of Heart Scale.
     *
     * @param player     - The Player being referenced.
     * @param heartScale - The number of hearts to display for the Player.
     */
    public void setScale(final Player player, final double heartScale) {
        synchronized ("FK_SQL") {
            this.heartScale = (heartScale >= 0 ? heartScale : 10);
            final DataObject dataObject = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.SET_SCALE, PlayerHandler.getPlayerID(player), heartScale));
            if (dataObject != null) {
                Core.getCore().getSQL().removeData(dataObject);
            }
            Core.getCore().getSQL().saveData(new DataObject(Table.SET_SCALE, PlayerHandler.getPlayerID(player), heartScale));
            if (ServerUtils.hasSpecificUpdate("1_9")) {
                SchedulerUtils.run(() -> Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(((heartScale) * 2)));
            } else {
                SchedulerUtils.run(() -> LegacyAPI.setMaxHealth(player, (heartScale) * 2));
            }
        }
    }

    /**
     * Checks if the Player is allowed to starve.
     *
     * @return If the Player is allowed to starve.
     */
    public boolean allowHunger() {
        return this.allowHunger;
    }

    /**
     * Sets the current state of Allow Hunger.
     *
     * @param player      - The Player being referenced.
     * @param allowHunger - If the Player should be allowed to starve.
     */
    public void setAllowHunger(final Player player, final boolean allowHunger) {
        synchronized ("FK_SQL") {
            this.allowHunger = allowHunger;
            final DataObject dataObject = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.ALLOW_HUNGER, PlayerHandler.getPlayerID(player), allowHunger));
            if (dataObject != null) {
                Core.getCore().getSQL().removeData(dataObject);
            }
            Core.getCore().getSQL().saveData(new DataObject(Table.ALLOW_HUNGER, PlayerHandler.getPlayerID(player), allowHunger));
        }
    }

    /**
     * Checks if the Player is allowed to burn.
     *
     * @return If the Player is allowed to burn.
     */
    public boolean allowBurn() {
        return this.allowBurn;
    }

    /**
     * Sets the current state of Allow Burn.
     *
     * @param player    - The Player being referenced.
     * @param allowBurn - If the Player should be allowed to take burn damage from lava or fire.
     */
    public void setAllowBurn(final Player player, final boolean allowBurn) {
        synchronized ("FK_SQL") {
            this.allowBurn = allowBurn;
            final DataObject dataObject = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.ALLOW_BURN, PlayerHandler.getPlayerID(player), allowBurn));
            if (dataObject != null) {
                Core.getCore().getSQL().removeData(dataObject);
            }
            Core.getCore().getSQL().saveData(new DataObject(Table.ALLOW_BURN, PlayerHandler.getPlayerID(player), allowBurn));
            if (!allowBurn) {
                player.setFireTicks(0);
            } else {
                player.setFireTicks(Creative.get(player).getFireTicks());
            }
        }
    }

    /**
     * Checks if the Player is allowed to damage items.
     *
     * @return If the Player is allowed to damage items.
     */
    public boolean unbreakableItems() {
        return this.unbreakableItems;
    }

    /**
     * Sets the current state of Unbreakable Items.
     *
     * @param player           - The Player being referenced.
     * @param unbreakableItems - If the Player should be allowed to damage their items while in creative mode.
     */
    public void setUnbreakableItems(final Player player, final boolean unbreakableItems) {
        synchronized ("FK_SQL") {
            this.unbreakableItems = unbreakableItems;
            final DataObject dataObject = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.UNBREAKABLE_ITEMS, PlayerHandler.getPlayerID(player), unbreakableItems));
            if (dataObject != null) {
                Core.getCore().getSQL().removeData(dataObject);
            }
            Core.getCore().getSQL().saveData(new DataObject(Table.UNBREAKABLE_ITEMS, PlayerHandler.getPlayerID(player), unbreakableItems));
        }
    }

    /**
     * Checks if the Player is allowed to have blocks drop in the world.
     *
     * @return If the Player is allowed to have blocks drop in the world.
     */
    public boolean blockDrops() {
        return this.blockDrops;
    }

    /**
     * Sets the current state of Block Drops.
     *
     * @param player     - The Player being referenced.
     * @param blockDrops - If the World should drop blocks for Creative Players when breaking objects.
     */
    public void setBlockDrops(final Player player, final boolean blockDrops) {
        synchronized ("FK_SQL") {
            this.blockDrops = blockDrops;
            final DataObject dataObject = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.DROPS_BLOCK, PlayerHandler.getPlayerID(player), blockDrops));
            if (dataObject != null) {
                Core.getCore().getSQL().removeData(dataObject);
            }
            Core.getCore().getSQL().saveData(new DataObject(Table.DROPS_BLOCK, PlayerHandler.getPlayerID(player), blockDrops));
        }
    }

    /**
     * Checks if the Player is allowed to break blocks with a sword in their hand.
     *
     * @return If the Player is allowed to break blocks with a sword in their hand.
     */
    public boolean swordBlock() {
        return this.swordBlock;
    }

    /**
     * Sets the current state of Sword Block.
     *
     * @param player     - The Player being referenced.
     * @param swordBlock - If the Player should be allowed to break blocks with a sword in their hand.
     */
    public void setSwordBlock(final Player player, final boolean swordBlock) {
        synchronized ("FK_SQL") {
            this.swordBlock = swordBlock;
            final DataObject dataObject = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.SWORD_BLOCK, PlayerHandler.getPlayerID(player), swordBlock));
            if (dataObject != null) {
                Core.getCore().getSQL().removeData(dataObject);
            }
            Core.getCore().getSQL().saveData(new DataObject(Table.SWORD_BLOCK, PlayerHandler.getPlayerID(player), swordBlock));
        }
    }

    /**
     * Checks if the Player is allowed to have their creative restored after a reload or restart.
     *
     * @return If the Player is allowed to have their creative restored after a reload or restart.
     */
    public boolean autoRestore() {
        return this.autoRestore;
    }

    /**
     * Sets the current state of Auto Restore.
     *
     * @param player      - The Player being referenced.
     * @param autoRestore - If the Player should have their creative mode auto restored on reload or restart.
     */
    public void setRestore(final Player player, final boolean autoRestore) {
        synchronized ("FK_SQL") {
            this.autoRestore = autoRestore;
            final DataObject dataObject = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.AUTO_RESTORE, PlayerHandler.getPlayerID(player), autoRestore));
            if (dataObject != null) {
                Core.getCore().getSQL().removeData(dataObject);
            }
            Core.getCore().getSQL().saveData(new DataObject(Table.AUTO_RESTORE, PlayerHandler.getPlayerID(player), autoRestore));
        }
    }

    /**
     * Checks if the Player is allowed to be invulnerable.
     *
     * @return If the Player is allowed to be invulnerable.
     */
    public boolean god() {
        return this.god;
    }

    /**
     * Sets the current state of Invulnerability.
     *
     * @param player - The Player being referenced.
     * @param god    - If the Player should be Invulnerable.
     */
    public void setGod(final Player player, final boolean god) {
        synchronized ("FK_SQL") {
            this.god = god;
            final DataObject dataObject = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.SET_GOD, PlayerHandler.getPlayerID(player), god));
            if (dataObject != null) {
                Core.getCore().getSQL().removeData(dataObject);
            }
            Core.getCore().getSQL().saveData(new DataObject(Table.SET_GOD, PlayerHandler.getPlayerID(player), god));
            player.setInvulnerable(god);
        }
    }

    /**
     * Gets the invulnerable delay for the Player.
     *
     * @return The invulnerable delay.
     */
    public int godDelay() {
        return this.godDelay;
    }

    /**
     * Sets the Delay to wait before the player will become vulnerable.
     *
     * @param player   - The Player being referenced.
     * @param godDelay - How long to wait before the player will become vulnerable.
     */
    public void setGodDelay(final Player player, final int godDelay) {
        synchronized ("FK_SQL") {
            this.godDelay = (godDelay >= 0 ? godDelay : 3);
            final DataObject dataObject = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.DELAY_GOD, PlayerHandler.getPlayerID(player), godDelay));
            if (dataObject != null) {
                Core.getCore().getSQL().removeData(dataObject);
            }
            Core.getCore().getSQL().saveData(new DataObject(Table.DELAY_GOD, PlayerHandler.getPlayerID(player), godDelay));
        }
    }

    /**
     * Checks if the Player should have their previous gamemodes inventory saved.
     *
     * @return If the Player should have their previous gamemodes inventory saved.
     */
    public boolean storeInventory() {
        return this.storeInventory;
    }

    /**
     * Sets the current state of Inventory Storing.
     *
     * @param player         - The Player being referenced.
     * @param storeInventory - If the Player inventory should be stored.
     */
    public void setStore(final Player player, final boolean storeInventory) {
        synchronized ("FK_SQL") {
            this.storeInventory = storeInventory;
            final DataObject dataObject = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.STORE_INVENTORY, PlayerHandler.getPlayerID(player), storeInventory));
            if (dataObject != null) {
                Core.getCore().getSQL().removeData(dataObject);
            }
            Core.getCore().getSQL().saveData(new DataObject(Table.STORE_INVENTORY, PlayerHandler.getPlayerID(player), storeInventory));
            if (!storeInventory) {
                Mode.restoreInventory(player);
                {
                    Creative.get(player).setInventory64("");
                }
            } else {
                Creative.get(player).setInventory64(Mode.getInventory64(player));
            }
        }
    }

    /**
     * Checks if the Player should have their pickup items destroyed if their inventory is full.
     *
     * @return If the Player should have their pickup items destroyed.
     */
    public boolean destroyPickups() {
        return this.destroyPickups;
    }

    /**
     * Sets the current state of Destroying Pickups.
     *
     * @param player         - The Player being referenced.
     * @param destroyPickups - If the Player should destroy pickups.
     */
    public void setPickups(final Player player, final boolean destroyPickups) {
        synchronized ("FK_SQL") {
            this.destroyPickups = destroyPickups;
            final DataObject dataObject = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.DESTROY_PICKUPS, PlayerHandler.getPlayerID(player), destroyPickups));
            if (dataObject != null) {
                Core.getCore().getSQL().removeData(dataObject);
            }
            Core.getCore().getSQL().saveData(new DataObject(Table.DESTROY_PICKUPS, PlayerHandler.getPlayerID(player), destroyPickups));
        }
    }

    /**
     * Gets the current list of hotbars.
     *
     * @return The current list of hotbars.
     */
    public HashMap<Integer, String> getHotbars() {
        return this.hotbars;
    }

    /**
     * Saves the Fake Creative HotBar to the Player's Hotbar list.
     *
     * @param player        - The Player being referenced.
     * @param inventoryData - The Inventory64 to be saved.
     * @param hotbar        - The hotbar number to be saved.
     */
    public void saveHotbar(final Player player, final String inventoryData, final int hotbar) {
        synchronized ("FK_SQL") {
            this.hotbars.put(hotbar, inventoryData);
            final DataObject dataObject = (DataObject) Core.getCore().getSQL().getData(new DataObject(Table.HOTBAR, PlayerHandler.getPlayerID(player), Integer.toString(hotbar), ""));
            if (dataObject != null) {
                Core.getCore().getSQL().removeData(dataObject);
            }
            Core.getCore().getSQL().saveData(new DataObject(Table.HOTBAR, PlayerHandler.getPlayerID(player), Integer.toString(hotbar), inventoryData));
        }
    }

    /**
     * Checks if the Player is allowed to have custom FakeCreative Preferences.
     *
     * @param player - The Player being referenced.
     * @return If the Player is allowed to have custom FakeCreative Preferences.
     */
    public boolean isLocalePreferences(final Player player) {
        final boolean PermissionNeeded = FakeCreative.getCore().getConfig("config.yml").getBoolean("Permissions.Preferences");
        final boolean OPPermissionNeeded = FakeCreative.getCore().getConfig("config.yml").getBoolean("Permissions.Preferences-OP");
        if (player.isOp()) {
            return (!OPPermissionNeeded || (player.isPermissionSet("fakecreative.preferences") && player.hasPermission("fakecreative.preferences")));
        } else {
            return (!PermissionNeeded || (player.isPermissionSet("fakecreative.preferences") && player.hasPermission("fakecreative.preferences")));
        }
    }
}