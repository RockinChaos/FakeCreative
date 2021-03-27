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

import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import me.RockinChaos.fakecreative.handlers.ConfigHandler;
import me.RockinChaos.fakecreative.handlers.PermissionsHandler;
import me.RockinChaos.fakecreative.handlers.PlayerHandler;
import me.RockinChaos.fakecreative.handlers.UpdateHandler;
import me.RockinChaos.fakecreative.utils.SchedulerUtils;
import me.RockinChaos.fakecreative.utils.StringUtils;
import me.RockinChaos.fakecreative.utils.api.LanguageAPI;

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
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6FakeCreative v" + FakeCreative.getInstance().getDescription().getVersion() + "&c by RockinChaos");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6Type &6&l/FakeCreative Help &6for the help menu.");
		} else if (Execute.HELP.accept(sender, args, 1)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l&m]----------------&6&l[&c FakeCreative &6&l]&6&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6ItemJoin v" + FakeCreative.getInstance().getDescription().getVersion() + "&c by RockinChaos");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/FakeCreative Help &7- &cThis help menu.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/FakeCreative Reload &7- &cReloads the .yml files.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/FakeCreative Updates &7- &cChecks for plugin updates.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/FakeCreative Upgrade &7- &cUpdate FakeCreative to latest version.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6Type &6&l/FakeCreative Help 2 &6for the next page.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l&m]---------------&6&l[&c Help Menu 1/3 &6&l]&6&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 2)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l&m]----------------&6&l[&c FakeCreative &6&l]&6&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/Gamemode Survival &7- &cSets the player to survival.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/Gamemode Creative &7- &cSets the player to creative.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/Gamemode Adventure &7- &cSets the player to adventure.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/Gamemode Spectator &7- &cSets the player to spectator.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6Alternative you can use &6&l/FakeCreative <Gamemode>&6 instead.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6Type &6&l/FakeCreative Help 3 &6for the next page.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l&m]---------------&6&l[&c Help Menu 2/3 &6&l]&6&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 3)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l&m]----------------&6&l[&c FakeCreative &6&l]&6&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/Gamemode 0 &7- &cSets the player to survival.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/Gamemode 1 &7- &cSets the player to creative.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/Gamemode 2 &7- &cSets the player to adventure.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l/Gamemode 3 &7- &cSets the player to spectator.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6Alternative you can use &6&l/FakeCreative <Gamemode>&6 instead.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&6&l&m]---------------&6&l[&c Help Menu 3/3 &6&l]&6&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.RELOAD.accept(sender, args, 0)) {
			ConfigHandler.getConfig().reloadConfigs(false);
			LanguageAPI.getLang(false).sendLangMessage("commands.default.configReload", sender);
		} else if (Execute.CREATIVE.accept(sender, args, 0)) {
			Player argsPlayer = (args.length > 1 ? PlayerHandler.getPlayerString(args[1]) : null);
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
			Player argsPlayer = (args.length > 1 ? PlayerHandler.getPlayerString(args[1]) : null);
			if (args.length > 1 && argsPlayer != null) {
				PlayerHandler.setMode(sender, argsPlayer, GameMode.SURVIVAL, false);
			} else if (args.length > 1) {
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[0] = "survival"; placeHolders[1] = args[1];
				LanguageAPI.getLang(false).sendLangMessage("commands.default.noTarget", sender, placeHolders);
			} else {
				PlayerHandler.setMode(sender, argsPlayer, GameMode.SURVIVAL, false);
			}
		} else if (Execute.ADVENTURE.accept(sender, args, 0)) {
			Player argsPlayer = (args.length > 1 ? PlayerHandler.getPlayerString(args[1]) : null);
			if (args.length > 1 && argsPlayer != null) {
				PlayerHandler.setMode(sender, argsPlayer, GameMode.ADVENTURE, false);
			}  else if (args.length > 1) {
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[0] = "adventure"; placeHolders[1] = args[1];
				LanguageAPI.getLang(false).sendLangMessage("commands.default.noTarget", sender, placeHolders);
			} else {
				PlayerHandler.setMode(sender, argsPlayer, GameMode.ADVENTURE, false);
			}
		} else if (Execute.SPECTATOR.accept(sender, args, 0)) {
			Player argsPlayer = (args.length > 1 ? PlayerHandler.getPlayerString(args[1]) : null);
			if (args.length > 1 && argsPlayer != null) {
				PlayerHandler.setMode(sender, argsPlayer, GameMode.SPECTATOR, false);
			}  else if (args.length > 1) {
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[0] = "spectator"; placeHolders[1] = args[1];
				LanguageAPI.getLang(false).sendLangMessage("commands.default.noTarget", sender, placeHolders);
			} else {
				PlayerHandler.setMode(sender, argsPlayer, GameMode.SPECTATOR, false);
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
	* Defines the config Command type for the command.
	* 
	*/
	public enum Execute {
		DEFAULT("", "fakecreative.use", false),
		HELP("help", "fakecreative.use", false),
		RELOAD("rl, reload", "fakecreative.reload", false),
		SURVIVAL("su, survival, 0", "fakecreative.mode.survival", true),
		CREATIVE("cr, creative, 1", "fakecreative.mode.creative", true),
		ADVENTURE("ad, adventure, 2", "fakecreative.mode.adventure", true),
		SPECTATOR("sp, spectator, 3", "fakecreative.mode.spectator", true),
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
			return (args.length >= 2 && args[1].equalsIgnoreCase(String.valueOf(page)) || true);
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