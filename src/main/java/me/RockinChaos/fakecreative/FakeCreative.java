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

import me.RockinChaos.core.Core;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.protocol.ProtocolManager;
import me.RockinChaos.fakecreative.modes.Mode;
import me.RockinChaos.fakecreative.modes.creative.Creative;
import me.RockinChaos.fakecreative.utils.menus.Menu;
import org.bukkit.plugin.java.JavaPlugin;

public class FakeCreative extends JavaPlugin {

    private static Core core;

    /**
     * Gets the static instance of the main class for ChaosCore.
     * Notice: This class is not the actual API class, this is the secondary main class for the JavaPlugin plugin.
     * For API methods, use the static methods available from the class: {@link me.RockinChaos.fakecreative.api.FakeCreativeAPI}.
     *
     * @return ChaosCore instance.
     */
    public static Core getCore() {
        return core;
    }

    /**
     * Called when the plugin is loaded.
     */
    @Override
    public void onLoad() {
        core = new Core(this, this.getFile(), this.getName());
    }

    /**
     * Called when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        if (core.isEnabled()) {
            PluginData.getData().registerEvents();
            PluginData.getData().registerClasses(false);
            Creative.restart(true);
            SchedulerUtils.runAsync(() -> {
                core.getUpdater();
                ServerUtils.logDebug("has been Enabled.");
            });
        }
    }

    /**
     * Called when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        Creative.save();
        PlayerHandler.forOnlinePlayers(player -> {
            if (Creative.isCreativeMode(player, true)) {
                Mode.setMode(player, null, player.getGameMode(), true, true);
            }
        });
        SchedulerUtils.cancelTasks();
        Menu.closeMenu();
        ProtocolManager.closeProtocol();
        ServerUtils.logInfo("has been Disabled.");
    }
}