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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChatTab implements TabCompleter {

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
        final boolean isGamemode = command.getName().equalsIgnoreCase("gm") || command.getName().equalsIgnoreCase("gamemode");
        if (args.length == 3 && args[0].equalsIgnoreCase("purge") && PermissionsHandler.hasPermission(sender, "fakecreative.purge")) {
            PlayerHandler.forOfflinePlayers(player -> commands.add(player.getName()));
            PlayerHandler.forOnlinePlayers(player -> commands.add(player.getName()));
        } else if (args.length == 2) {
            if (!isGamemode && args[0].equalsIgnoreCase("help") && PermissionsHandler.hasPermission(sender, "fakecreative.use")) {
                commands.addAll(Arrays.asList("2", "3", "4"));
            } else if (((args[0].equalsIgnoreCase("creative") && PermissionsHandler.hasPermission(sender, "fakecreative.mode.creative"))
                    || (args[0].equalsIgnoreCase("survival") && PermissionsHandler.hasPermission(sender, "fakecreative.mode.survival"))
                    || (args[0].equalsIgnoreCase("adventure") && PermissionsHandler.hasPermission(sender, "fakecreative.mode.adventure"))
                    || (args[0].equalsIgnoreCase("spectator") && PermissionsHandler.hasPermission(sender, "fakecreative.mode.spectator")))) {
                PlayerHandler.forOnlinePlayers(player -> commands.add(player.getName()));
            } else if (args[0].equalsIgnoreCase("purge") && PermissionsHandler.hasPermission(sender, "fakecreative.purge")) {
                commands.addAll(Arrays.asList("allow-flight", "speed-flight", "speed-break", "set-food", "set-health", "set-scale", "allow-hunger", "allow-burn", "unbreakable-items", "drops-block", "sword-block", "auto-restore", "set-god", "delay-god", "store-inventory", "destroy-pickups", "hotbar", "playerstats"));
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
            if (!isGamemode && PermissionsHandler.hasPermission(sender, "fakecreative.use")) {
                commands.add("help");
            }
            if (!isGamemode && PermissionsHandler.hasPermission(sender, "fakecreative.permissions")) {
                commands.add("permissions");
            }
            if (!isGamemode && PermissionsHandler.hasPermission(sender, "fakecreative.purge")) {
                commands.add("purge");
            }
            if (!isGamemode && PermissionsHandler.hasPermission(sender, "fakecreative.dump")) {
                commands.add("dump");
            }
            if (!isGamemode && PermissionsHandler.hasPermission(sender, "fakecreative.reload")) {
                commands.add("reload");
            }
            if (!isGamemode && PermissionsHandler.hasPermission(sender, "fakecreative.updates")) {
                commands.add("updates");
            }
            if (!isGamemode && PermissionsHandler.hasPermission(sender, "fakecreative.upgrade")) {
                commands.add("upgrade");
            }
        }
        StringUtil.copyPartialMatches(args[(args.length - 1)], commands, completions);
        Collections.sort(completions);
        return completions;
    }
}