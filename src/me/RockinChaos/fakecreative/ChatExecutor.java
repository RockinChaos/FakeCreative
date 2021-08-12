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

import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import me.RockinChaos.fakecreative.ChatComponent.ClickAction;
import me.RockinChaos.fakecreative.handlers.ConfigHandler;
import me.RockinChaos.fakecreative.handlers.PermissionsHandler;
import me.RockinChaos.fakecreative.handlers.PlayerHandler;
import me.RockinChaos.fakecreative.handlers.UpdateHandler;
import me.RockinChaos.fakecreative.utils.SchedulerUtils;
import me.RockinChaos.fakecreative.utils.ServerUtils;
import me.RockinChaos.fakecreative.utils.StringUtils;
import me.RockinChaos.fakecreative.utils.api.LanguageAPI;
import me.RockinChaos.fakecreative.utils.api.LegacyAPI;
import me.RockinChaos.fakecreative.utils.sql.DataObject;
import me.RockinChaos.fakecreative.utils.sql.DataObject.Table;
import me.RockinChaos.fakecreative.utils.sql.SQL;

public class ChatExecutor implements CommandExecutor {
	
   /**
	* Called when the CommandSender executes a command.
    * @param sender - Source of the command.
    * @param command - Command which was executed.
    * @param label - Alias of the command which was used.
    * @param args - Passed command arguments.
    * @return true if the command is valid.
	*/
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (Execute.DEFAULT.accept(sender, args, 0)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, ("&6FakeCreative v" + FakeCreative.getInstance().getDescription().getVersion() + "&c by RockinChaos"), "&bThis should be the version submitted to the developer \n&bwhen submitting a bug or feature request.", "https://github.com/RockinChaos/FakeCreative/issues", ClickAction.OPEN_URL);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6Type &6&l/FakeCreative Help &6for the help menu.", "&eClick to View the Help Menu.", "/fakecreative help", ClickAction.RUN_COMMAND);
		} else if (Execute.HELP.accept(sender, args, 1)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l&m]----------------&6&l[&c FakeCreative &6&l]&6&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, ("&6FakeCreative v" + FakeCreative.getInstance().getDescription().getVersion() + "&c by RockinChaos"), "&bThis should be the version submitted to the developer \n&bwhen submitting a bug or feature request.", "https://github.com/RockinChaos/FakeCreative/issues", ClickAction.OPEN_URL);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/FakeCreative Help &7- &cThis help menu.", "&aExecuting this command shows this help menu!", "/fakecreative help", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/FakeCreative Reload &7- &cReloads the .yml files.", "&aFully reloads the plugin, fetching \n&aany changes made to the .yml files. \n\n&aBe sure to save changes made to your .yml files!", "/fakecreative reload", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/FakeCreative Updates &7- &cChecks for plugin updates.", "&aChecks to see if there are any updates available for this plugin.", "/fakecreative updates", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/FakeCreative Upgrade &7- &cUpdates to latest version.", "&aAttempts to Upgrade this plugin to the latest version. \n&aYou will need to restart the server for this process to complete.", "/fakecreative upgrade", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6Type &6&l/FakeCreative Help 2 &6for the next page.", "&eClick to View the Next Page.", "/fakecreative help 2", ClickAction.RUN_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l&m]---------------&6&l[&c Help Menu 1/4 &6&l]&6&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 2)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l&m]----------------&6&l[&c FakeCreative &6&l]&6&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/FakeCreative Permissions &7- &cLists your permissions.", "&aLists the Permissions for your Player. \n\n&aGreen &bmeans you have permission whereas \n&cRed &bmeans you do not have permission.", "/fakecreative permissions", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/FakeCreative Purge &7- &cDeletes the database file.", "&c&l[DANGER] &eThe Following Destroys Data &nPermanently!&e&c&l [DANGER] \n\n&aPurges ALL Player Data from the PlayerData database file! \n\n&c&n&lTHIS CANNOT BE UNDONE.", "/fakecreative purge", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/FakeCreative Purge <User> &7- &cDeletes the users data.", "&c&l[DANGER] &eThe Following Destroys Data &nPermanently!&e&c&l [DANGER] \n\n&aPurges The specified users data from the PlayerData database file! \n\n&c&n&lTHIS CANNOT BE UNDONE.", "/fakecreative purge ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6Type &6&l/FakeCreative Help 3 &6for the next page.", "&eClick to View the Next Page.", "/fakecreative help 3", ClickAction.RUN_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l&m]---------------&6&l[&c Help Menu 2/4 &6&l]&6&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 3)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l&m]----------------&6&l[&c FakeCreative &6&l]&6&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/Gamemode Survival &7- &cSets the player to survival.", "&aSets the player to survival mode. \n\n&aAlias; \n&6/Gamemode 0 \n&6/FakeCreative 0 \n&6/FakeCreative Survival", "/gamemode survival", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/Gamemode Creative &7- &cSets the player to creative.", "&aSets the player to creative mode. \n\n&aAlias; \n&6/Gamemode 1 \n&6/FakeCreative 1 \n&6/FakeCreative Creative", "/gamemode creative", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/Gamemode Adventure &7- &cSets the player to adventure.", "&aSets the player to adventure mode. \n\n&aAlias; \n&6/Gamemode 2 \n&6/FakeCreative 2 \n&6/FakeCreative Adventure", "/gamemode adventure", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/Gamemode Spectator &7- &cSets the player to spectator.", "&aSets the player to adventure mode. \n\n&aAlias; \n&6/Gamemode 3 \n&6/FakeCreative 3 \n&6/FakeCreative Spectator", "/gamemode spectator", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6Type &6&l/FakeCreative Help 4 &6for the next page.", "&eClick to View the Next Page.", "/fakecreative help 4", ClickAction.RUN_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l&m]---------------&6&l[&c Help Menu 3/4 &6&l]&6&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 4)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l&m]----------------&6&l[&c FakeCreative &6&l]&6&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/Gamemode Survival <User> &7- &cSets user to survival.", "&aSets the specified player to survival mode. \n\n&aAlias; \n&6/Gamemode 0 <User> \n&6/FakeCreative 0 <User> \n&6/FakeCreative Survival <User>", "/gamemode survival ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/Gamemode Creative <User> &7- &cSets user to creative.", "&aSets the specified player to creative mode. \n\n&aAlias; \n&6/Gamemode 2 <User> \n&6/FakeCreative 2 <User> \n&6/FakeCreative Creative <User>", "/gamemode creative ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/Gamemode Adventure <User> &7- &cSets user to adventure.", "&aSets the specified player to adventure mode. \n\n&aAlias; \n&6/Gamemode 2 <User> \n&6/FakeCreative 2 <User> \n&6/FakeCreative Adventure <User>", "/gamemode adventure ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/Gamemode Spectator <User> &7- &cSets user to spectator.", "&aSets the specified player to spectator mode. \n\n&aAlias; \n&6/Gamemode 3 <User> \n&6/FakeCreative 3 <User> \n&6/FakeCreative Spectator <User>", "/gamemode spectator ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6Found a bug? Report it @");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6https://github.com/RockinChaos/FakeCreative/issues", "&eClick to Submit a Bug or Feature Request.", "https://github.com/RockinChaos/FakeCreative/issues", ClickAction.OPEN_URL);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l&m]---------------&6&l[&c Help Menu 4/4 &6&l]&6&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.RELOAD.accept(sender, args, 0)) {
			ConfigHandler.getConfig().reloadConfigs(false);
			LanguageAPI.getLang(false).sendLangMessage("commands.default.configReload", sender);
		} else if (Execute.PERMISSIONS.accept(sender, args, 0)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l&m]----------------&6&l[&c FakeCreative &6&l]&6&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.*") ? "&a[\u2714]" : "&c[\u2718]") + " FakeCreative.*");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.all") ? "&a[\u2714]" : "&c[\u2718]") + " FakeCreative.All");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.use") ? "&a[\u2714]" : "&c[\u2718]") + " FakeCreative.Use");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.reload") ? "&a[\u2714]" : "&c[\u2718]") + " FakeCreative.Reload");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.updates") ? "&a[\u2714]" : "&c[\u2718]") + " FakeCreative.Updates");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.upgrade") ? "&a[\u2714]" : "&c[\u2718]") + " FakeCreative.Upgrade");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.permissions") ? "&a[\u2714]" : "&c[\u2718]") + " FakeCreative.Permissions");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.mode.creative") ? "&a[\u2714]" : "&c[\u2718]") + " FakeCreative.Mode.Creative");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.mode.survival") ? "&a[\u2714]" : "&c[\u2718]") + " FakeCreative.Mode.Survival");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.mode.adventure") ? "&a[\u2714]" : "&c[\u2718]") + " FakeCreative.Mode.Adventure");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "fakecreative.mode.spectator") ? "&a[\u2714]" : "&c[\u2718]") + " FakeCreative.Mode.Spectator");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l&m]-------------&6&l[&c Permissions Menu 1/1 &6&l]&6&l&m------------[");
		} else if (Execute.PURGE.accept(sender, args, 0)) {
			if (args.length == 1) { this.purge(sender, "Database", "All Players"); } 
			else { this.purge(sender, "Database", args[1]); }
		} else if (Execute.CREATIVE.accept(sender, args, 0)) {
			final Player argsPlayer = (args.length > 1 ? PlayerHandler.getPlayerString(args[1]) : null);
			if (args.length > 1 && argsPlayer != null) {
				PlayerHandler.setCreative(sender, argsPlayer);
			}  else if (args.length > 1) {
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[0] = "creative"; placeHolders[1] = args[1];
				LanguageAPI.getLang(false).sendLangMessage("commands.default.noTarget", sender, placeHolders);
			} else {
				PlayerHandler.setCreative(sender, argsPlayer);
			}
		} else if (Execute.SURVIVAL.accept(sender, args, 0)) {
			final Player argsPlayer = (args.length > 1 ? PlayerHandler.getPlayerString(args[1]) : null);
			if (args.length > 1 && argsPlayer != null) {
				PlayerHandler.setMode(sender, argsPlayer, GameMode.SURVIVAL, false, false);
			} else if (args.length > 1) {
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[0] = "survival"; placeHolders[1] = args[1];
				LanguageAPI.getLang(false).sendLangMessage("commands.default.noTarget", sender, placeHolders);
			} else {
				PlayerHandler.setMode(sender, argsPlayer, GameMode.SURVIVAL, false, false);
			}
		} else if (Execute.ADVENTURE.accept(sender, args, 0)) {
			final Player argsPlayer = (args.length > 1 ? PlayerHandler.getPlayerString(args[1]) : null);
			if (args.length > 1 && argsPlayer != null) {
				PlayerHandler.setMode(sender, argsPlayer, GameMode.ADVENTURE, false, false);
			}  else if (args.length > 1) {
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[0] = "adventure"; placeHolders[1] = args[1];
				LanguageAPI.getLang(false).sendLangMessage("commands.default.noTarget", sender, placeHolders);
			} else {
				PlayerHandler.setMode(sender, argsPlayer, GameMode.ADVENTURE, false, false);
			}
		} else if (ServerUtils.hasSpecificUpdate("1_8") && Execute.SPECTATOR.accept(sender, args, 0)) {
			final Player argsPlayer = (args.length > 1 ? PlayerHandler.getPlayerString(args[1]) : null);
			if (args.length > 1 && argsPlayer != null) {
				PlayerHandler.setMode(sender, argsPlayer, GameMode.SPECTATOR, false, false);
			}  else if (args.length > 1) {
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[0] = "spectator"; placeHolders[1] = args[1];
				LanguageAPI.getLang(false).sendLangMessage("commands.default.noTarget", sender, placeHolders);
			} else {
				PlayerHandler.setMode(sender, argsPlayer, GameMode.SPECTATOR, false, false);
			}
		} else if (Execute.UPDATE.accept(sender, args, 0)) {
			LanguageAPI.getLang(false).sendLangMessage("commands.updates.checkRequest", sender);
			SchedulerUtils.runAsync(() -> {
				UpdateHandler.getUpdater(false).checkUpdates(sender, false); 
			});
		} else if (Execute.UPGRADE.accept(sender, args, 0)) {
			LanguageAPI.getLang(false).sendLangMessage("commands.updates.updateRequest", sender);
			SchedulerUtils.runAsync(() -> {
				UpdateHandler.getUpdater(false).forceUpdates(sender); 
			});
		} else if (this.matchExecutor(args) == null) {
			LanguageAPI.getLang(false).sendLangMessage("commands.default.unknownCommand", sender);
		} else if (!this.matchExecutor(args).playerRequired(sender, args)) {
			LanguageAPI.getLang(false).sendLangMessage("commands.default.noPlayer", sender);
		} else if (!this.matchExecutor(args).hasPermission(sender, args)) {
			LanguageAPI.getLang(false).sendLangMessage("commands.default.noPermission", sender);
		}
		return true;
	}
	
   /**
	* Called when the CommandSender fails to execute a command.
	* @param args - Passed command arguments.
	* @return The found Executor.
	* 
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
	* Called when the CommandSender executes the Purge command.
	* @param sender - Source of the command. 
	* @param data - The table being purged of data.
	* @param args - The player name having their data purged.
	* 
	*/
	private HashMap < String, Boolean > confirmationRequests = new HashMap < String, Boolean > ();
	private void purge(final CommandSender sender, final String data, final String args) {
		String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args; placeHolders[5] = data; 
		OfflinePlayer foundPlayer = LegacyAPI.getOfflinePlayer(args);;
		if (!data.equalsIgnoreCase("Database")) { 
			placeHolders[4] = "/ij purge <player>"; 
			if (foundPlayer == null) { LanguageAPI.getLang(false).sendLangMessage("commands.default.noTarget", sender, placeHolders); return; } 
		} else { placeHolders[4] = "/ij purge"; }
		if (this.confirmationRequests.get(data + sender.getName()) != null && this.confirmationRequests.get(data + sender.getName()).equals(true)) {
			if (!data.equalsIgnoreCase("Database")) { 
				PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args));
				for (Table table : Table.values()) {
				DataObject dataObject = new DataObject(table, PlayerHandler.getOfflinePlayerID(foundPlayer), false);
					if (dataObject != null) { SQL.getData().removeData(dataObject); }
				}
			} 
			else {
			FakeCreative.getInstance().setStarted(false);
				SQL.getData().purgeDatabase(); {
					SchedulerUtils.runAsync(() -> {
						SQL.newData(false); {
							SchedulerUtils.runAsyncLater(2L, () -> {
								SchedulerUtils.runSingleAsync(() -> {
									FakeCreative.getInstance().setStarted(true);	
								});
							});
						}
					});
				}
			}
			LanguageAPI.getLang(false).sendLangMessage("commands.database.purgeSuccess", sender, placeHolders);
			this.confirmationRequests.remove(data + sender.getName());
		} else {
			this.confirmationRequests.put(data + sender.getName(), true);
			LanguageAPI.getLang(false).sendLangMessage("commands.database.purgeWarn", sender, placeHolders);
			LanguageAPI.getLang(false).sendLangMessage("commands.database.purgeConfirm", sender, placeHolders);
			SchedulerUtils.runLater(100L, () -> {
				if (this.confirmationRequests.get(data + sender.getName()) != null && this.confirmationRequests.get(data + sender.getName()).equals(true)) {
					LanguageAPI.getLang(false).sendLangMessage("commands.database.purgeTimeOut", sender);
					this.confirmationRequests.remove(data + sender.getName());
				} 
			});
		}
	}
	
   /**
	* Defines the config Command type for the command.
	* 
	*/
	public enum Execute {
		DEFAULT("", "fakecreative.use", false),
		HELP("help", "fakecreative.use", false),
		RELOAD("rl, reload", "fakecreative.reload", false),
		PERMISSIONS("permission, permissions", "fakecreative.permissions", true),
		SURVIVAL("su, survival, 0", "fakecreative.mode.survival", true),
		CREATIVE("cr, creative, 1", "fakecreative.mode.creative", true),
		ADVENTURE("ad, adventure, 2", "fakecreative.mode.adventure", true),
		SPECTATOR("sp, spectator, 3", "fakecreative.mode.spectator", true),
		PURGE("purge", "fakecreative.purge", false),
		UPDATE("update, updates", "fakecreative.updates", false),
		UPGRADE("upgrade", "fakecreative.upgrade", false);
		private final String command;
		private final String permission;
		private final boolean player;
		
       /**
	    * Creates a new Execute instance.
	    * @param command - The expected command argument. 
	    * @param permission - The expected command permission requirement.
	    * @param player - If the command is specific to a player instance, cannot be executed by console.
	    * 
	    */
		private Execute(final String command, final String permission, final boolean player) { 
			this.command = command; this.permission = permission; this.player = player; 
		}
		
       /**
	    * Called when the CommandSender executes a command.
	    * @param sender - Source of the command. 
	    * @param args - Passed command arguments.
	    * @param page - The page number to be expected.
	    * 
	    */
		public boolean accept(final CommandSender sender, final String[] args, final int page) { 
			return (args.length == 0 || (StringUtils.splitIgnoreCase(this.command, args[0], ",") 
			  && this.hasSyntax(args, page)))
			  && ((this.playerRequired(sender, args)) || ((args.length > 1) && (this.equals(Execute.CREATIVE) || this.equals(Execute.SURVIVAL) || this.equals(Execute.ADVENTURE) || this.equals(Execute.SPECTATOR))))
			  && this.hasPermission(sender, args); 
		}
		
       /**
	    * Checks if the executed command is the same as the executor.
	    * @param sender - Source of the command. 
	    * @param args - Passed command arguments.
	    * @param page - The page number to be expected.
	    * 
	    */
		public boolean acceptArgs(final String[] args) {
			return StringUtils.splitIgnoreCase(this.command, args[0], ",");
		}
		
       /**
	    * Checks if the Command being executed has the proper formatting or syntax.
	    * @param args - Passed command arguments.
	    * @param page - The page number to be expected.
	    * 
	    */
		private boolean hasSyntax(final String[] args, final int page) {
			return ((args.length >= 2 && args[1].equalsIgnoreCase(String.valueOf(page))) || !(args.length >= 2)  || this.equals(Execute.CREATIVE) || this.equals(Execute.SURVIVAL) 
				  || this.equals(Execute.ADVENTURE) || this.equals(Execute.SPECTATOR) || this.equals(Execute.PURGE));
		}
		
       /**
	    * Checks if the Player has permission to execute the Command.
	    * @param sender - Source of the command. 
	    * @param args - Passed command arguments.
	    * 
	    */
		public boolean hasPermission(final CommandSender sender, final String[] args) {
			return PermissionsHandler.hasPermission(sender, this.permission);
		}
		
       /**
	    * Checks if the Command requires the instance to be a Player.
	    * @param sender - Source of the command. 
	    * @param args - Passed command arguments.
	    * 
	    */
		public boolean playerRequired(final CommandSender sender, final String[] args) {
			return (!this.player || (!(sender instanceof ConsoleCommandSender)));
		}
	}
}