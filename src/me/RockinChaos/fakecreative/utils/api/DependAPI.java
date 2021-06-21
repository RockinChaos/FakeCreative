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
package me.RockinChaos.fakecreative.utils.api;

import org.bukkit.Bukkit;

import com.mojang.authlib.properties.Property;

import me.RockinChaos.fakecreative.utils.ServerUtils;
import me.RockinChaos.fakecreative.utils.api.MetricsAPI.SimplePie;
import me.RockinChaos.fakecreative.utils.ReflectionUtils;

public class DependAPI {
	
	private static DependAPI depends;
	
   /**
    * Creates a new DependAPI instance.
    * 
    */
	public DependAPI() { }
    
   /**
    * Checks if PlaceHolderAPI is Enabled.
    * 
    * @return If PlaceHolderAPI is Enabled.
    */
    public boolean placeHolderEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
    }
    
   /**
    * Checks if BetterNick is Enabled.
    * 
    * @return If BetterNick is Enabled.
    */
    public boolean nickEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("BetterNick");
    }
    
   /**
    * Checks if ProtocolLib is Enabled.
    * 
    * @return If ProtocolLib is Enabled.
    */
    public boolean protocolEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib");
    }
    
   /**
    * Checks if SkinsRestorer is Enabled.
    * 
    * @return If SkinsRestorer is Enabled.
    */
    public boolean skinsRestorerEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("SkinsRestorer");
    }
    
   /**
    * Gets the set SkinsRestorer skin.
    * 
    * @param owner - The skull owner to have their skin fetched.
    * @return The found Skin Texture value.
    */
    public String getSkinValue(final String owner) {
    	Class<?> netty = null;
    	try {
    		netty = ReflectionUtils.getClass("net.skinsrestorer.bukkit.SkinsRestorer");
    	} catch (Exception e1) {
    		try {
    			netty = ReflectionUtils.getClass("skinsrestorer.bukkit.SkinsRestorer");
    		} catch (Exception e2) {
    			ServerUtils.sendDebugTrace(e2);
    			ServerUtils.logSevere("{DependAPI} [1] Unsupported SkinsRestorer version detected, unable to set the skull owner " + owner + ".");
    		}
    	}
    	if (netty != null) {
	    	try {
				final Object skinsRestorer = netty.getMethod("getInstance").invoke(null);
				final Object skinsAPI = skinsRestorer.getClass().getMethod("getSkinsRestorerBukkitAPI").invoke(skinsRestorer);
				final Object playerData = skinsAPI.getClass().getMethod("getSkinName", String.class).invoke(skinsAPI, owner);
				final String ownerData = (playerData != null ? (String) playerData : owner);
				final Object skinData = skinsAPI.getClass().getMethod("getSkinData", String.class).invoke(skinsAPI, ownerData);
				return (skinData != null ? ((Property) skinData).getValue() : null);
			} catch (Exception e1) {
				try {
					netty = ReflectionUtils.getClass("net.skinsrestorer.api.SkinsRestorerAPI");
					final Object skinsRestorer = netty.getMethod("getApi").invoke(null);
					final Object playerData = skinsRestorer.getClass().getMethod("getSkinName", String.class).invoke(skinsRestorer, owner);
					final String ownerData = (playerData != null ? (String) playerData : owner);
					final Object skinData = skinsRestorer.getClass().getMethod("getSkinData", String.class).invoke(skinsRestorer, ownerData);
					return (skinData != null ? ((Property) skinData).getValue() : null);
				} catch (Exception e2) {
					ServerUtils.sendDebugTrace(e2);
					ServerUtils.logSevere("{DependAPI} [2] Unsupported SkinsRestorer version detected, unable to set the skull owner " + owner + ".");
				}
			}
    	}
    	return null;
    }
    
   /**
    * Sends a logging message of the found and enabled soft dependencies.
    * 
    */
	public void sendUtilityDepends() {
		String enabledPlugins = (this.nickEnabled() ? "BetterNick, " : "") + (this.placeHolderEnabled() ? "PlaceholderAPI, " : "") + (this.protocolEnabled() ? "ProtocolLib, " : "") + (this.skinsRestorerEnabled() ? "SkinsRestorer, " : "");
		if (!enabledPlugins.isEmpty()) { ServerUtils.logInfo("Hooked into { " + enabledPlugins.substring(0, enabledPlugins.length() - 2) + " }"); }	
	}

   /**
    * Adds Custom Charts to the Metrics.
    * 
    * @param metrics - The referenced Metrics connection.
    */
	public void addCustomCharts(final MetricsAPI metrics) {
		metrics.addCustomChart(new SimplePie("language", () -> LanguageAPI.getLang(false).getLanguage()));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.nickEnabled() ? "BetterNick" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.placeHolderEnabled() ? "PlaceholderAPI" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.protocolEnabled() ? "ProtocolLib" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.skinsRestorerEnabled() ? "SkinsRestorer" : ""));
	} 
	
   /**
    * Gets the instance of the DependAPI.
    * 
    * @param regen - If the DependAPI should have a new instance created.
    * @return The DependAPI instance.
    */
    public static DependAPI getDepends(final boolean regen) { 
        if (depends == null || regen) { depends = new DependAPI(); }
        return depends; 
    }
}