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
package me.RockinChaos.fakecreative;

import com.google.common.io.Files;
import me.RockinChaos.core.handlers.PermissionsHandler;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.ChatComponent.ClickAction;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.core.utils.api.LegacyAPI;
import me.RockinChaos.core.utils.api.PasteAPI;
import me.RockinChaos.core.utils.types.PlaceHolder;
import me.RockinChaos.core.utils.types.PlaceHolder.Holder;
import me.RockinChaos.fakecreative.modes.creative.Creative;
import me.RockinChaos.fakecreative.utils.sql.DataObject;
import me.RockinChaos.fakecreative.utils.sql.DataObject.Table;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChatExecutor implements CommandExecutor {

    private final HashMap<String, Boolean> confirmationRequests = new HashMap<>();

    /**
     * Called when the CommandSender executes a command.
     *
     * @param sender  - Source of the command.
     * @param command - Command which was executed.
     * @param label   - Alias of the command which was used.
     * @param args    - Passed command arguments.
     * @return true if the command is valid.
     */
    @Override
    public boolean onCommand(final @Nonnull CommandSender sender, final @Nonnull Command command, final @Nonnull String label, final @Nonnull String[] args) {
        final Execute executor = this.matchExecutor(args);
        if (Execute.DEFAULT.accept(sender, args, 0)) {
            FakeCreative.getCore().getLang().dispatchMessage(sender, ("&6FakeCreative v" + FakeCreative.getCore().getPlugin().getDescription().getVersion() + "&c by RockinChaos"), "&bThis should be the version submitted to the developer \n&bwhen submitting a bug or feature request.", "https://github.com/RockinChaos/FakeCreative/issues", ClickAction.OPEN_URL);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6Type &6&l/FakeCreative Help &6for the help menu.", "&eClick to View the Help Menu.", "/fakecreative help", ClickAction.RUN_COMMAND);
        } else if (Execute.HELP.accept(sender, args, 1)) {
            FakeCreative.getCore().getLang().dispatchMessage(sender, "");
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l&m]----------------&6&l[&c FakeCreative &6&l]&6&l&m---------------[");
            FakeCreative.getCore().getLang().dispatchMessage(sender, ("&6FakeCreative v" + FakeCreative.getCore().getPlugin().getDescription().getVersion() + "&c by RockinChaos"), "&bThis should be the version submitted to the developer \n&bwhen submitting a bug or feature request.", "https://github.com/RockinChaos/FakeCreative/issues", ClickAction.OPEN_URL);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l/FakeCreative Help &7- &cThis help menu.", "&aExecuting this command shows this help menu!", "/fakecreative help", ClickAction.SUGGEST_COMMAND);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l/FakeCreative Dump &7- &cGets a debug link for support.", "&aSends a pastebin link of their configuration files. \n&cThis should be sent to the plugin developer and NOT SHARED PUBLICLY.", "/fakecreative dump", ClickAction.SUGGEST_COMMAND);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l/FakeCreative Reload &7- &cReloads the .yml files.", "&aFully reloads the plugin, fetching \n&aany changes made to the .yml files. \n\n&aBe sure to save changes made to your .yml files!", "/fakecreative reload", ClickAction.SUGGEST_COMMAND);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l/FakeCreative Updates &7- &cChecks for plugin updates.", "&aChecks to see if there are any updates available for this plugin.", "/fakecreative updates", ClickAction.SUGGEST_COMMAND);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l/FakeCreative Upgrade &7- &cUpdates to latest version.", "&aAttempts to Upgrade this plugin to the latest version. \n&aYou will need to restart the server for this process to complete.", "/fakecreative upgrade", ClickAction.SUGGEST_COMMAND);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6Type &6&l/FakeCreative Help 2 &6for the next page.", "&eClick to View the Next Page.", "/fakecreative help 2", ClickAction.RUN_COMMAND);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l&m]---------------&6&l[&c Help Menu 1/4 &6&l]&6&l&m---------------[");
            FakeCreative.getCore().getLang().dispatchMessage(sender, "");
        } else if (Execute.HELP.accept(sender, args, 2)) {
            FakeCreative.getCore().getLang().dispatchMessage(sender, "");
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l&m]----------------&6&l[&c FakeCreative &6&l]&6&l&m---------------[");
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l/FakeCreative Permissions &7- &cLists your permissions.", "&aLists the Permissions for your Player. \n\n&aGreen&b means you have permission whereas \n&cRed&b means you do not have permission.", "/fakecreative permissions", ClickAction.SUGGEST_COMMAND);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l/FakeCreative Purge &7- &cDeletes the database file.", "&c&l[DANGER] &eThe Following Destroys Data &nPermanently!&e&c&l [DANGER] \n\n&aPurges ALL Player Data from the PlayerData database file! \n\n&c&n&lTHIS CANNOT BE UNDONE.", "/fakecreative purge", ClickAction.SUGGEST_COMMAND);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l/FakeCreative Purge <User> &7- &cDeletes the users data.", "&c&l[DANGER] &eThe Following Destroys Data &nPermanently!&e&c&l [DANGER] \n\n&aPurges The specified users data from the PlayerData database file! \n\n&c&n&lTHIS CANNOT BE UNDONE.", "/fakecreative purge ", ClickAction.SUGGEST_COMMAND);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6Type &6&l/FakeCreative Help 3 &6for the next page.", "&eClick to View the Next Page.", "/fakecreative help 3", ClickAction.RUN_COMMAND);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l&m]---------------&6&l[&c Help Menu 2/4 &6&l]&6&l&m---------------[");
            FakeCreative.getCore().getLang().dispatchMessage(sender, "");
        } else if (Execute.HELP.accept(sender, args, 3)) {
            FakeCreative.getCore().getLang().dispatchMessage(sender, "");
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l&m]----------------&6&l[&c FakeCreative &6&l]&6&l&m---------------[");
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l/Gamemode Survival &7- &cSets the player to survival.", "&aSets the player to survival mode. \n\n&aAlias; \n&6/Gamemode 0 \n&6/FakeCreative 0 \n&6/FakeCreative Survival", "/gamemode survival", ClickAction.SUGGEST_COMMAND);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l/Gamemode Creative &7- &cSets the player to creative.", "&aSets the player to creative mode. \n\n&aAlias; \n&6/Gamemode 1 \n&6/FakeCreative 1 \n&6/FakeCreative Creative", "/gamemode creative", ClickAction.SUGGEST_COMMAND);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l/Gamemode Adventure &7- &cSets the player to adventure.", "&aSets the player to adventure mode. \n\n&aAlias; \n&6/Gamemode 2 \n&6/FakeCreative 2 \n&6/FakeCreative Adventure", "/gamemode adventure", ClickAction.SUGGEST_COMMAND);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l/Gamemode Spectator &7- &cSets the player to spectator.", "&aSets the player to adventure mode. \n\n&aAlias; \n&6/Gamemode 3 \n&6/FakeCreative 3 \n&6/FakeCreative Spectator", "/gamemode spectator", ClickAction.SUGGEST_COMMAND);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6Type &6&l/FakeCreative Help 4 &6for the next page.", "&eClick to View the Next Page.", "/fakecreative help 4", ClickAction.RUN_COMMAND);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l&m]---------------&6&l[&c Help Menu 3/4 &6&l]&6&l&m---------------[");
            FakeCreative.getCore().getLang().dispatchMessage(sender, "");
        } else if (Execute.HELP.accept(sender, args, 4)) {
            FakeCreative.getCore().getLang().dispatchMessage(sender, "");
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l&m]----------------&6&l[&c FakeCreative &6&l]&6&l&m---------------[");
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l/Gamemode Survival <User> &7- &cSets user to survival.", "&aSets the specified player to survival mode. \n\n&aAlias; \n&6/Gamemode 0 <User> \n&6/FakeCreative 0 <User> \n&6/FakeCreative Survival <User>", "/gamemode survival ", ClickAction.SUGGEST_COMMAND);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l/Gamemode Creative <User> &7- &cSets user to creative.", "&aSets the specified player to creative mode. \n\n&aAlias; \n&6/Gamemode 2 <User> \n&6/FakeCreative 2 <User> \n&6/FakeCreative Creative <User>", "/gamemode creative ", ClickAction.SUGGEST_COMMAND);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l/Gamemode Adventure <User> &7- &cSets user to adventure.", "&aSets the specified player to adventure mode. \n\n&aAlias; \n&6/Gamemode 2 <User> \n&6/FakeCreative 2 <User> \n&6/FakeCreative Adventure <User>", "/gamemode adventure ", ClickAction.SUGGEST_COMMAND);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l/Gamemode Spectator <User> &7- &cSets user to spectator.", "&aSets the specified player to spectator mode. \n\n&aAlias; \n&6/Gamemode 3 <User> \n&6/FakeCreative 3 <User> \n&6/FakeCreative Spectator <User>", "/gamemode spectator ", ClickAction.SUGGEST_COMMAND);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6Found a bug? Report it @");
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6https://github.com/RockinChaos/FakeCreative/issues", "&eClick to Submit a Bug or Feature Request.", "https://github.com/RockinChaos/FakeCreative/issues", ClickAction.OPEN_URL);
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l&m]---------------&6&l[&c Help Menu 4/4 &6&l]&6&l&m---------------[");
            FakeCreative.getCore().getLang().dispatchMessage(sender, "");
        } else if (Execute.DUMP.accept(sender, args, 0)) {
            this.dump(sender);
        } else if (Execute.RELOAD.accept(sender, args, 0)) {
            PluginData.getData().hardReload(false);
            FakeCreative.getCore().getLang().sendLangMessage("commands.default.configReload", sender);
        } else if (Execute.PERMISSIONS.accept(sender, args, 0)) {
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l&m]----------------&6&l[&c FakeCreative &6&l]&6&l&m---------------[");
            FakeCreative.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.*") ? "&a[✔]" : "&c[✘]") + " FakeCreative.*");
            FakeCreative.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.all") ? "&a[✔]" : "&c[✘]") + " FakeCreative.All");
            FakeCreative.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.use") ? "&a[✔]" : "&c[✘]") + " FakeCreative.Use");
            FakeCreative.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.reload") ? "&a[✔]" : "&c[✘]") + " FakeCreative.Reload");
            FakeCreative.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.updates") ? "&a[✔]" : "&c[✘]") + " FakeCreative.Updates");
            FakeCreative.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.upgrade") ? "&a[✔]" : "&c[✘]") + " FakeCreative.Upgrade");
            FakeCreative.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.permissions") ? "&a[✔]" : "&c[✘]") + " FakeCreative.Permissions");
            FakeCreative.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.purge") ? "&a[✔]" : "&c[✘]") + " FakeCreative.Purge");
            FakeCreative.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.preferences") ? "&a[✔]" : "&c[✘]") + " FakeCreative.Preferences");
            FakeCreative.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.mode.creative") ? "&a[✔]" : "&c[✘]") + " FakeCreative.Mode.Creative");
            FakeCreative.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.mode.survival") ? "&a[✔]" : "&c[✘]") + " FakeCreative.Mode.Survival");
            FakeCreative.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.mode.adventure") ? "&a[✔]" : "&c[✘]") + " FakeCreative.Mode.Adventure");
            FakeCreative.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.mode.spectator") ? "&a[✔]" : "&c[✘]") + " FakeCreative.Mode.Spectator");
            FakeCreative.getCore().getLang().dispatchMessage(sender, "&6&l&m]-------------&6&l[&c Permissions Menu 1/1 &6&l]&6&l&m------------[");
        } else if (Execute.PURGE.accept(sender, args, 0)) {
            if (args.length == 1) {
                this.purge(sender, "Database", "All Players");
            } else if (args[1].equalsIgnoreCase("speed-flight") || args[1].equalsIgnoreCase("speed-break") || args[1].equalsIgnoreCase("set-food") || args[1].equalsIgnoreCase("set-health") || args[1].equalsIgnoreCase("set-scale")
                    || args[1].equalsIgnoreCase("allow-hunger") || args[1].equalsIgnoreCase("allow-burn") || args[1].equalsIgnoreCase("unbreakable-items") || args[1].equalsIgnoreCase("drops-block") || args[1].equalsIgnoreCase("sword-block") || args[1].equalsIgnoreCase("auto-restore") || args[1].equalsIgnoreCase("set-god") || args[1].equalsIgnoreCase("delay-god") || args[1].equalsIgnoreCase("store-inventory") || args[1].equalsIgnoreCase("destroy-pickups") || args[1].equalsIgnoreCase("self-drops") || args[1].equalsIgnoreCase("item-store") || args[1].equalsIgnoreCase("hotbar") || args[1].equalsIgnoreCase("playerstats")) {
                this.purge(sender, args[1], args[2]);
            }
        } else if (Execute.CREATIVE.accept(sender, args, 0)) {
            final Player argsPlayer = (args.length > 1 ? PlayerHandler.getPlayerString(args[1]) : null);
            PlayerHandler.setCreative(sender, argsPlayer, false);
        } else if (Execute.SURVIVAL.accept(sender, args, 0)) {
            final Player argsPlayer = (args.length > 1 ? PlayerHandler.getPlayerString(args[1]) : null);
            PlayerHandler.setMode(sender, argsPlayer, GameMode.SURVIVAL, false, false);
        } else if (Execute.ADVENTURE.accept(sender, args, 0)) {
            final Player argsPlayer = (args.length > 1 ? PlayerHandler.getPlayerString(args[1]) : null);
            PlayerHandler.setMode(sender, argsPlayer, GameMode.ADVENTURE, false, false);
        } else if (Execute.SPECTATOR.accept(sender, args, 0)) {
            final Player argsPlayer = (args.length > 1 ? PlayerHandler.getPlayerString(args[1]) : null);
            PlayerHandler.setMode(sender, argsPlayer, GameMode.SPECTATOR, false, false);
        } else if (Execute.UPDATE.accept(sender, args, 0)) {
            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.PLAYER, sender.getName());
            FakeCreative.getCore().getLang().sendLangMessage("commands.updates.checkRequest", Bukkit.getServer().getConsoleSender(), placeHolders);
            SchedulerUtils.runAsync(() -> FakeCreative.getCore().getUpdater().checkUpdates(sender, true));
        } else if (Execute.UPGRADE.accept(sender, args, 0)) {
            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.PLAYER, sender.getName());
            FakeCreative.getCore().getLang().sendLangMessage("commands.updates.updateRequest", Bukkit.getServer().getConsoleSender(), placeHolders);
            SchedulerUtils.runAsync(() -> FakeCreative.getCore().getUpdater().forceUpdates(sender));
        }  else if (Execute.DEBUG.accept(sender, args, 0)) {
            if (ServerUtils.devListening()) {
                FakeCreative.getCore().getLang().dispatchMessage(sender, FakeCreative.getCore().getData().getPluginPrefix() + " &aYou are &nnow listening&a for debug messages.");
            } else {
                FakeCreative.getCore().getLang().dispatchMessage(sender, FakeCreative.getCore().getData().getPluginPrefix() + "&cYou are &nno longer&c listening for debug messages.");
            }
        } else if (executor == null) {
            FakeCreative.getCore().getLang().sendLangMessage("commands.default.unknownCommand", sender);
        } else if (!executor.playerRequired(sender)) {
            FakeCreative.getCore().getLang().sendLangMessage("commands.default.noPlayer", sender);
        } else if (!executor.hasSyntax(args, 0)) {
            if (executor.equals(Execute.PURGE)) {
                FakeCreative.getCore().getLang().sendLangMessage("commands.default.unknownCommand", sender);
            }
        } else if (!executor.hasPermission(sender)) {
            FakeCreative.getCore().getLang().sendLangMessage("commands.default.noPermission", sender);
        }
        return true;
    }

    /**
     * Attempts to match the command arguments with an applicable Executor.
     *
     * @param args - Passed command arguments.
     * @return The found Executor.
     */
    private Execute matchExecutor(final String[] args) {
        for (Execute command : Execute.values()) {
            if (command.acceptArgs(args)) {
                return command;
            }
        }
        return null;
    }

    /**
     * Called when the CommandSender executes the Dump command.
     *
     * @param sender - Source of the command.
     */
    private void dump(final CommandSender sender) {
        try {
            final Map<String, String> files = new HashMap<>();
            files.put("latest.log", Files.asCharSource(new File("logs/latest.log"), StandardCharsets.UTF_8).read());
            files.put("config.yml", Files.asCharSource(new File(FakeCreative.getCore().getPlugin().getDataFolder() + "/config.yml"), StandardCharsets.UTF_8).read());
            files.put("lang.yml", Files.asCharSource(new File(FakeCreative.getCore().getPlugin().getDataFolder() + "/" + FakeCreative.getCore().getLang().getFile()), StandardCharsets.UTF_8).read());
            final PasteAPI pasteURI = new PasteAPI(sender, Collections.singletonList("ExploitFixer"), files);
            pasteURI.getPaste(pasteURL -> {
                if (pasteURL != null) {
                    ServerUtils.logInfo(sender.getName() + " has generated a debug paste at " + pasteURL);
                    if (!(sender instanceof ConsoleCommandSender)) {
                        FakeCreative.getCore().getLang().dispatchMessage(sender, "%prefix% &a" + pasteURL, "&eClick me to copy the url.", pasteURL, ClickAction.OPEN_URL);
                    }
                } else {
                    if (!(sender instanceof ConsoleCommandSender)) {
                        FakeCreative.getCore().getLang().dispatchMessage(sender, "%prefix% &cFailed to generate the DUMP URL, please try again later.");
                    }
                    ServerUtils.logSevere("{ChatExecutor} Failed to generate the DUMP URL, this is not necessarily a bug.");
                }
            });
        } catch (Exception e) {
            ServerUtils.logSevere("{ChatExecutor} Failed to execute the DUMP command.");
            ServerUtils.sendSevereTrace(e);
        }
    }

    /**
     * Called when the CommandSender executes the Purge command.
     *
     * @param sender - Source of the command.
     * @param table  - The table being purged of data.
     * @param args   - The player name having their data purged.
     */
    private void purge(final CommandSender sender, final String table, final String args) {
        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.TARGET_PLAYER, args).with(Holder.PURGE_DATA, table);
        OfflinePlayer foundPlayer = null;
        if (!table.equalsIgnoreCase("Database")) {
            placeHolders.with(Holder.COMMAND, "/ij purge " + table + " <player>");
            if (!table.equalsIgnoreCase("map-ids")) {
                foundPlayer = LegacyAPI.getOfflinePlayer(args);
            }
            if (foundPlayer == null && !args.equalsIgnoreCase("ALL")) {
                FakeCreative.getCore().getLang().sendLangMessage("commands.default.noTarget", sender, placeHolders);
                return;
            }
        } else {
            placeHolders.with(Holder.COMMAND, "/ij purge");
        }
        if (this.confirmationRequests.get(table + sender.getName()) != null && this.confirmationRequests.get(table + sender.getName()).equals(true)) {
            if (!table.equalsIgnoreCase("Database")) {
                PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args));
                DataObject dataObject = ((table.replace("-", "_").equalsIgnoreCase("allow_flight")
                        ? new DataObject(Table.ALLOW_FLIGHT, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "") : (table.replace("-", "_").equalsIgnoreCase("speed_flight")
                        ? new DataObject(Table.SPEED_FLIGHT, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "") : (table.replace("-", "_").equalsIgnoreCase("speed_break")
                        ? new DataObject(Table.SPEED_BREAK, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "") : (table.replace("-", "_").equalsIgnoreCase("set_food")
                        ? new DataObject(Table.SET_FOOD, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "") : (table.replace("-", "_").equalsIgnoreCase("set_health")
                        ? new DataObject(Table.SET_HEALTH, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "") : (table.replace("-", "_").equalsIgnoreCase("set_scale")
                        ? new DataObject(Table.SET_SCALE, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "") : (table.replace("-", "_").equalsIgnoreCase("allow_hunger")
                        ? new DataObject(Table.ALLOW_HUNGER, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "") : (table.replace("-", "_").equalsIgnoreCase("allow_burn")
                        ? new DataObject(Table.ALLOW_BURN, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "") : (table.replace("-", "_").equalsIgnoreCase("unbreakable_items")
                        ? new DataObject(Table.UNBREAKABLE_ITEMS, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "") : (table.replace("-", "_").equalsIgnoreCase("drops_block")
                        ? new DataObject(Table.DROPS_BLOCK, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "") : (table.replace("-", "_").equalsIgnoreCase("sword_block")
                        ? new DataObject(Table.SWORD_BLOCK, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "") : (table.replace("-", "_").equalsIgnoreCase("auto_restore")
                        ? new DataObject(Table.AUTO_RESTORE, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "") : (table.replace("-", "_").equalsIgnoreCase("set_god")
                        ? new DataObject(Table.SET_GOD, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "") : (table.replace("-", "_").equalsIgnoreCase("delay_god")
                        ? new DataObject(Table.DELAY_GOD, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "") : (table.replace("-", "_").equalsIgnoreCase("store_inventory")
                        ? new DataObject(Table.STORE_INVENTORY, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "") : (table.replace("-", "_").equalsIgnoreCase("destroy_pickups")
                        ? new DataObject(Table.DESTROY_PICKUPS, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "") : (table.replace("-", "_").equalsIgnoreCase("self_drops")
                        ? new DataObject(Table.SELF_DROPS, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "") : (table.replace("-", "_").equalsIgnoreCase("item_store")
                        ? new DataObject(Table.ITEM_STORE, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "") : (table.replace("-", "_").equalsIgnoreCase("hotbar")
                        ? new DataObject(Table.HOTBAR, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "") : (table.replace("-", "_").equalsIgnoreCase("playerstats")
                        ? new DataObject(Table.PLAYERSTATS, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "") : null)))))))))))))))))))));
                if (dataObject != null) {
                    FakeCreative.getCore().getSQL().removeData(dataObject);
                    SchedulerUtils.run(() -> {
                        final Player playerString = PlayerHandler.getPlayerString(args);
                        if (playerString != null && Creative.isCreativeMode(playerString, true)) {
                            PlayerHandler.setMode(playerString, null, playerString.getGameMode(), true, false);
                            {
                                PlayerHandler.setCreative(playerString, null, true);
                            }
                        }
                    });
                }
            } else {
                FakeCreative.getCore().getData().setStarted(false);
                FakeCreative.getCore().getSQL().purgeDatabase();
                {
                    SchedulerUtils.runAsyncLater(20L, () -> {
                        FakeCreative.getCore().getSQL().refresh();
                        {
                            SchedulerUtils.runAsyncLater(2L, () -> {
                                SchedulerUtils.runSingleAsync(() -> FakeCreative.getCore().getData().setStarted(true));
                                SchedulerUtils.run(() -> PlayerHandler.forOnlinePlayers(player -> {
                                    if (Creative.isCreativeMode(player, true)) {
                                        PlayerHandler.setMode(player, null, player.getGameMode(), true, false);
                                        {
                                            PlayerHandler.setCreative(player, null, true);
                                        }
                                    }
                                }));
                            });
                        }
                    });
                }
            }
            FakeCreative.getCore().getLang().sendLangMessage("commands.database.purgeSuccess", sender, placeHolders);
            this.confirmationRequests.remove(table + sender.getName());
        } else {
            this.confirmationRequests.put(table + sender.getName(), true);
            FakeCreative.getCore().getLang().sendLangMessage("commands.database.purgeWarn", sender, placeHolders);
            FakeCreative.getCore().getLang().sendLangMessage("commands.database.purgeConfirm", sender, placeHolders);
            SchedulerUtils.runLater(100L, () -> {
                if (this.confirmationRequests.get(table + sender.getName()) != null && this.confirmationRequests.get(table + sender.getName()).equals(true)) {
                    FakeCreative.getCore().getLang().sendLangMessage("commands.database.purgeTimeOut", sender);
                    this.confirmationRequests.remove(table + sender.getName());
                }
            });
        }
    }

    /**
     * Defines the config Command type for the command.
     */
    public enum Execute {
        DEFAULT("", "fakecreative.use", false),
        HELP("help", "fakecreative.use", false),
        DUMP("dump", "fakecreative.dump", false),
        RELOAD("rl, reload", "fakecreative.reload", false),
        PERMISSIONS("permission, permissions", "fakecreative.permissions", true),
        SURVIVAL("su, survival, 0", "fakecreative.mode.survival", true),
        CREATIVE("cr, creative, 1", "fakecreative.mode.creative", true),
        ADVENTURE("ad, adventure, 2", "fakecreative.mode.adventure", true),
        SPECTATOR("sp, spectator, 3", "fakecreative.mode.spectator", true),
        PURGE("purge", "fakecreative.purge", false),
        UPDATE("update, updates", "fakecreative.updates", false),
        UPGRADE("upgrade", "fakecreative.upgrade", false),
        DEBUG("debug", "fakecreative.use", true);
        private final String command;
        private final String permission;
        private final boolean player;

        /**
         * Creates a new Execute instance.
         *
         * @param command    - The expected command argument.
         * @param permission - The expected command permission requirement.
         * @param player     - If the command is specific to a player instance, cannot be executed by console.
         */
        Execute(final String command, final String permission, final boolean player) {
            this.command = command;
            this.permission = permission;
            this.player = player;
        }

        /**
         * Called when the CommandSender executes a command.
         *
         * @param sender - Source of the command.
         * @param args   - Passed command arguments.
         * @param page   - The page number to be expected.
         */
        public boolean accept(final CommandSender sender, final String[] args, final int page) {
            return (args.length == 0 || (StringUtils.splitIgnoreCase(this.command, args[0], ",")
                    && this.hasSyntax(args, page)))
                    && ((this.playerRequired(sender)) || ((args.length > 1) && (this.equals(Execute.CREATIVE) || this.equals(Execute.SURVIVAL) || this.equals(Execute.ADVENTURE) || this.equals(Execute.SPECTATOR))))
                    && this.hasPermission(sender);
        }

        /**
         * Checks if the executed command is the same as the executor.
         *
         * @param args - Passed command arguments.
         */
        public boolean acceptArgs(final String[] args) {
            return (args.length == 0 || StringUtils.splitIgnoreCase(this.command, args[0], ","));
        }

        /**
         * Checks if the Command being executed has the proper formatting or syntax.
         *
         * @param args - Passed command arguments.
         * @param page - The page number to be expected.
         */
        private boolean hasSyntax(final String[] args, final int page) {
            return ((args.length >= 2 && args[1].equalsIgnoreCase(String.valueOf(page))) || !(args.length >= 2) || this.equals(Execute.CREATIVE) || this.equals(Execute.SURVIVAL)
                    || this.equals(Execute.ADVENTURE) || this.equals(Execute.SPECTATOR) || (this.equals(Execute.PURGE) && args.length >= 3));
        }

        /**
         * Checks if the Player has permission to execute the Command.
         *
         * @param sender - Source of the command.
         */
        public boolean hasPermission(final CommandSender sender) {
            return PermissionsHandler.hasPermission(sender, this.permission) && (!this.equals(Execute.DEBUG) || PermissionsHandler.isDeveloper(sender));
        }

        /**
         * Checks if the Command requires the instance to be a Player.
         *
         * @param sender - Source of the command.
         */
        public boolean playerRequired(final CommandSender sender) {
            return (!this.player || (!(sender instanceof ConsoleCommandSender)));
        }
    }
}