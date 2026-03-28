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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModeTab implements TabCompleter {

    /**
     * Called when a Player tries to TabComplete.
     *
     * @param sender  - Source of the command.
     * @param command - Command which was executed.
     * @param label   - Alias of the command which was used.
     * @param args    - Passed command arguments.
     * @return The String List of TabComplete commands.
     */
    @Override
    public List<String> onTabComplete(final @Nonnull CommandSender sender, final @Nonnull Command command, final @Nonnull String label, final @Nonnull String[] args) {
        final List<String> completions = new ArrayList<>();
        final List<String> commands = new ArrayList<>();
        if (args.length == 2) {
            if (((args[0].equalsIgnoreCase("creative") || args[0].equals("cr") || args[0].equals("1")) && PermissionsHandler.hasPermission(sender, "fakecreative.mode.creative"))
                    || ((args[0].equalsIgnoreCase("survival") || args[0].equals("su") || args[0].equals("0")) && PermissionsHandler.hasPermission(sender, "fakecreative.mode.survival"))
                    || ((args[0].equalsIgnoreCase("adventure") || args[0].equals("ad") || args[0].equals("2")) && PermissionsHandler.hasPermission(sender, "fakecreative.mode.adventure"))
                    || ((args[0].equalsIgnoreCase("spectator") || args[0].equals("sp") || args[0].equals("3")) && PermissionsHandler.hasPermission(sender, "fakecreative.mode.spectator"))) {
                PlayerHandler.forOnlinePlayers(player -> commands.add(player.getName()));
            }
        } else if (args.length == 1) {
            if (PermissionsHandler.hasPermission(sender, "fakecreative.mode.creative")) {
                commands.add("creative");
            }
            if (PermissionsHandler.hasPermission(sender, "fakecreative.mode.survival")) {
                commands.add("survival");
            }
            if (PermissionsHandler.hasPermission(sender, "fakecreative.mode.adventure")) {
                commands.add("adventure");
            }
            if (PermissionsHandler.hasPermission(sender, "fakecreative.mode.spectator")) {
                commands.add("spectator");
            }
        }
        StringUtil.copyPartialMatches(args[(args.length - 1)], commands, completions);
        Collections.sort(completions);
        return completions;
    }
}