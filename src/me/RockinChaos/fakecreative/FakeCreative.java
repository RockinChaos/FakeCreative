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

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.RockinChaos.fakecreative.handlers.ConfigHandler;
import me.RockinChaos.fakecreative.handlers.PlayerHandler;
import me.RockinChaos.fakecreative.handlers.modes.Creative;
import me.RockinChaos.fakecreative.utils.ServerUtils;
import me.RockinChaos.fakecreative.utils.interfaces.menus.Menu;
import me.RockinChaos.fakecreative.utils.protocol.ProtocolManager;

public class FakeCreative extends JavaPlugin {
	
  	private static FakeCreative instance;
  	private boolean isStarted = false;
  	
   /**
    * Called when the plugin is loaded.
    * 
    */
    @Override
    public void onLoad() {
    	instance = this;
    }
    
   /**
    * Called when the plugin is enabled.
    * 
    */
  	@Override
	public void onEnable() {
  		ConfigHandler.getConfig().registerEvents();
        ServerUtils.logInfo("has been Enabled.");
  	}
  	
   /**
    * Called when the plugin is disabled.
    * 
    */
  	@Override
	public void onDisable() {
		PlayerHandler.forOnlinePlayers(player -> Creative.setMode(player, null, player.getGameMode(), true));
  		Bukkit.getScheduler().cancelTasks(this);
  		Menu.getCreator().closeMenu();
	  	ProtocolManager.closeProtocol();
  		ServerUtils.logInfo("has been Disabled.");
  	}
  	
   /**
	* Checks if the plugin has fully loaded.
	* 
	*/
	public boolean isStarted() {
		return this.isStarted;
	}
	
   /**
	* Sets the plugin as fully loaded.
	* 
	*/
	public void setStarted(final boolean bool) {
		this.isStarted = bool;
	}
  	
   /**
    * Gets the Plugin File.
    * 
    * @return The Plugin File.
    */
  	public File getPlugin() {
  		return this.getFile();
  	}

   /**
    * Gets the static instance of the main class for FakeCreative. 
    * Notice: This class is not the actual API class, this is the main class that extends JavaPlugin.
    *
    * @return FakeCreative instance.
    */  	
  	public static FakeCreative getInstance() {
  		return instance;
  	}
}