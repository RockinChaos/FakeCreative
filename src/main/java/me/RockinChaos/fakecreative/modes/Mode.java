package me.RockinChaos.fakecreative.modes;

import me.RockinChaos.core.handlers.ItemHandler;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.CompatUtils;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.api.LegacyAPI;
import me.RockinChaos.fakecreative.FakeCreative;
import me.RockinChaos.fakecreative.modes.creative.Creative;
import me.RockinChaos.fakecreative.modes.instance.PlayerObject;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

public class Mode {

    /**
     * Puts the Player in the specified GameMode.
     * Exits Fake Creative Mode if the Player is already in Fake Creative.
     *
     * @param who      - The executor.
     * @param altWho   - The player to be set to GameMode.
     * @param gamemode - The GameMode to be set.
     * @param silent   - If the status message should be sent.
     * @param doSave   - If the mode switch should be saved.
     */
    public static void setMode(final CommandSender who, final Player altWho, final GameMode gamemode, final boolean silent, final boolean doSave) {
        final Player argsPlayer = (altWho != null ? altWho : (Player) who);
        if (Creative.isCreativeMode(argsPlayer, true)) {
            final PlayerObject playerObject = Creative.get(argsPlayer);
            setFlight(argsPlayer, false);
            if (ServerUtils.hasSpecificUpdate("1_9")) {
                Objects.requireNonNull(argsPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(playerObject.getMaxHealth());
            } else {
                LegacyAPI.setMaxHealth(argsPlayer, playerObject.getMaxHealth());
            }
            argsPlayer.setFoodLevel(playerObject.getFood());
            argsPlayer.setFireTicks(playerObject.getFireTicks());
            SchedulerUtils.runLater(4L, () -> argsPlayer.setHealth(Math.min(playerObject.getHealth(), playerObject.getMaxHealth() * 2)));
            Creative.Tabs.clearTabs(argsPlayer);
            restoreInventory(argsPlayer);
            dropInvulnerable(argsPlayer);
            if (!doSave || !playerObject.getStats().autoRestore()) {
                ServerUtils.logDebug(argsPlayer.getName() + " is no longer set as fake creative.");
                Creative.remove(argsPlayer);
            }
        }
        sendStatus(who, altWho, gamemode, silent);
        argsPlayer.setGameMode(gamemode);
    }

    /**
     * Saves and Clears the Player Inventory.
     *
     * @param player - The Player having their Inventory saved.
     * @return The generated Inventory64 Byte.
     */
    public static String getInventory64(final Player player) {
        if (Creative.get(player).getStats().storeInventory()) {
            final PlayerInventory inventory = player.getInventory();
            final Inventory craftView = CompatUtils.getTopInventory(player);
            final Inventory saveInventory = Bukkit.createInventory(null, 54);
            for (int i = 0; i <= 47; i++) {
                if (i <= 41 && inventory.getSize() >= i) {
                    final ItemStack item = inventory.getItem(i);
                    if (item != null) {
                        saveInventory.setItem(i, item.clone());
                    }
                } else if (i >= 42 && PlayerHandler.isCraftingInv(player)) {
                    final ItemStack item = craftView.getItem(i - 42);
                    if (item != null) {
                        saveInventory.setItem(i, item.clone());
                    }
                }
            }
            PlayerHandler.clearItems(player);
            return ItemHandler.serializeInventory(saveInventory);
        }
        return null;
    }

    /**
     * Clears and Restores the Player Inventory that was previously saved.
     *
     * @param player - The Player having their Inventory restored.
     */
    public static void restoreInventory(final Player player) {
        if (Creative.get(player).getInventory64() != null) {
            final PlayerInventory inventory = player.getInventory();
            final Inventory craftView = CompatUtils.getTopInventory(player);
            PlayerHandler.clearItems(player);
            final Inventory inventory64 = ItemHandler.deserializeInventory(Creative.get(player).getInventory64());
            for (int i = 47; i >= 0; i--) {
                if (inventory64 != null && inventory64.getItem(i) != null && Objects.requireNonNull(inventory64.getItem(i)).getType() != Material.AIR) {
                    if (i <= 41) {
                        inventory.setItem(i, Objects.requireNonNull(inventory64.getItem(i)).clone());
                    } else if (PlayerHandler.isCraftingInv(player)) {
                        craftView.setItem(i - 42, Objects.requireNonNull(inventory64.getItem(i)).clone());
                        PlayerHandler.updateInventory(player, 1L);
                    }
                }
            }
        }
    }


    /**
     * Sets the Player's Flight (Mode).
     *
     * @param player - The Player being referenced.
     * @param setFly - If flight should be enabled or disabled.
     */
    public static void setFlight(final Player player, final boolean setFly) {
        if (Creative.get(player).getStats().allowFlight()) {
            if (setFly) {
                player.setAllowFlight(true);
                player.setFlying(true);
                final double speed = (Creative.get(player).getStats().flySpeed());
                player.setFlySpeed(Float.parseFloat((Double.toString(speed / 10))));
            } else {
                player.setFlying(false);
                player.setAllowFlight(false);
                player.setFlySpeed(0.1f);
            }
        }
    }

    /**
     * Disables invulnerability from the Player after x seconds have passed.
     *
     * @param player - The Player being referenced.
     */
    private static void dropInvulnerable(final Player player) {
        SchedulerUtils.runLater((Creative.get(player).getStats().godDelay()) * 20L, () -> {
            if (!Creative.isCreativeMode(player, true)) {
                if (ServerUtils.hasSpecificUpdate("1_9")) {
                    player.setInvulnerable(false);
                }
            }
        });
    }

    /**
     * Drops all Entity Targets that are currently Targeting / Attacking the Player.
     *
     * @param player - The Player being referenced.
     */
    public static void dropTargets(final Player player) {
        for (final Entity current : player.getNearbyEntities(20, 20, 20)) {
            if (!(current instanceof Item) && !(current instanceof Player) && LegacyAPI.setTargetExists(current)) {
                try {
                    current.getClass().getMethod("setTarget", LivingEntity.class).invoke(current, (LivingEntity) null);
                } catch (Exception e) {
                    ServerUtils.sendSevereTrace(e);
                }
            }
        }
    }

    /**
     * Sends the set Mode messages to the Player.
     *
     * @param who      - The executor.
     * @param altWho   - The player to be set to GameMode.
     * @param gamemode - The GameMode to be set.
     * @param silent   - If the status message should be sent.
     */
    public static void sendStatus(final CommandSender who, final Player altWho, final GameMode gamemode, final boolean silent) {
        if (!silent) {
            final String[] placeHolders = FakeCreative.getCore().getLang().newString();
            placeHolders[19] = gamemode.name().toLowerCase();
            placeHolders[1] = altWho.getName();
            FakeCreative.getCore().getLang().sendLangMessage("commands.gamemode.setMode", who, placeHolders);
            if (who instanceof ConsoleCommandSender || !who.equals(altWho)) {
                placeHolders[1] = who.getName();
                FakeCreative.getCore().getLang().sendLangMessage("commands.gamemode.setModeTarget", altWho, placeHolders);
            }
        }
    }
}