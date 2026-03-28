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

import me.RockinChaos.core.handlers.PermissionsHandler;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.core.utils.types.PlaceHolder;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ModeExecutor implements CommandExecutor {

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
        if (executor == null) {
            if (Execute.CREATIVE.hasPermission(sender) || Execute.SURVIVAL.hasPermission(sender) || Execute.ADVENTURE.hasPermission(sender) || Execute.SPECTATOR.hasPermission(sender)) {
                FakeCreative.getCore().getLang().sendLangMessage("commands.default.unknownGamemode", sender);
            }
        } else if (Execute.CREATIVE.accept(sender, args, 0)) {
            final Player argsPlayer = (args.length > 1 ? PlayerHandler.getPlayerString(args[1]) : null);
            if (args.length > 1 && argsPlayer == null) {
                final PlaceHolder placeHolders = new PlaceHolder().with(PlaceHolder.Holder.TARGET_PLAYER, args[1]);
                FakeCreative.getCore().getLang().sendLangMessage("commands.default.noTarget", sender, placeHolders);
            } else {
                PlayerHandler.setCreative(sender, argsPlayer, false);
            }
        } else if (Execute.SURVIVAL.accept(sender, args, 0)) {
            final Player argsPlayer = (args.length > 1 ? PlayerHandler.getPlayerString(args[1]) : null);
            if (args.length > 1 && argsPlayer == null) {
                final PlaceHolder placeHolders = new PlaceHolder().with(PlaceHolder.Holder.TARGET_PLAYER, args[1]);
                FakeCreative.getCore().getLang().sendLangMessage("commands.default.noTarget", sender, placeHolders);
            } else {
                PlayerHandler.setMode(sender, argsPlayer, GameMode.SURVIVAL, false, false);
            }
        } else if (Execute.ADVENTURE.accept(sender, args, 0)) {
            final Player argsPlayer = (args.length > 1 ? PlayerHandler.getPlayerString(args[1]) : null);
            if (args.length > 1 && argsPlayer == null) {
                final PlaceHolder placeHolders = new PlaceHolder().with(PlaceHolder.Holder.TARGET_PLAYER, args[1]);
                FakeCreative.getCore().getLang().sendLangMessage("commands.default.noTarget", sender, placeHolders);
            } else {
                PlayerHandler.setMode(sender, argsPlayer, GameMode.ADVENTURE, false, false);
            }
        } else if (Execute.SPECTATOR.accept(sender, args, 0)) {
            final Player argsPlayer = (args.length > 1 ? PlayerHandler.getPlayerString(args[1]) : null);
            if (args.length > 1 && argsPlayer == null) {
                final PlaceHolder placeHolders = new PlaceHolder().with(PlaceHolder.Holder.TARGET_PLAYER, args[1]);
                FakeCreative.getCore().getLang().sendLangMessage("commands.default.noTarget", sender, placeHolders);
            } else {
                PlayerHandler.setMode(sender, argsPlayer, GameMode.SPECTATOR, false, false);
            }
        } else if (!executor.playerRequired(sender)) {
            FakeCreative.getCore().getLang().sendLangMessage("commands.default.noPlayer", sender);
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
        if (args.length == 0) return null;
        for (Execute command : Execute.values()) {
            if (command.acceptArgs(args)) {
                return command;
            }
        }
        return null;
    }

    /**
     * Defines the config Command type for the command.
     */
    public enum Execute {
        SURVIVAL("su, survival, 0", "fakecreative.mode.survival", true),
        CREATIVE("cr, creative, 1", "fakecreative.mode.creative", true),
        ADVENTURE("ad, adventure, 2", "fakecreative.mode.adventure", true),
        SPECTATOR("sp, spectator, 3", "fakecreative.mode.spectator", true);
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
                    || this.equals(Execute.ADVENTURE) || this.equals(Execute.SPECTATOR));
        }

        /**
         * Checks if the Player has permission to execute the Command.
         *
         * @param sender - Source of the command.
         */
        public boolean hasPermission(final CommandSender sender) {
            return PermissionsHandler.hasPermission(sender, this.permission);
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