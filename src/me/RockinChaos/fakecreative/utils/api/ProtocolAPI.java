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

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import me.RockinChaos.fakecreative.FakeCreative;
import me.RockinChaos.fakecreative.utils.ServerUtils;

public class ProtocolAPI {
	
	private static ProtocolManager protocolManager;
	
   /**
    * Handles both server side and client side protocol packets.
    * This is specific to ProtocolLib and is required for this to function.
    * 
    */
	public static void handleProtocols() {
		if (protocolManager == null) { protocolManager = ProtocolLibrary.getProtocolManager(); }
		
		PacketType[] packetType = null;
		if (ServerUtils.hasSpecificUpdate("1_13")) {
			packetType = new PacketType[3];
			packetType[0] = PacketType.Play.Client.CLOSE_WINDOW;
			packetType[1] = PacketType.Play.Client.WINDOW_CLICK;
			packetType[2] = PacketType.Play.Client.AUTO_RECIPE;
		} else {
			packetType = new PacketType[2];
			packetType[1] = PacketType.Play.Client.WINDOW_CLICK;
			packetType[0] = PacketType.Play.Client.CLOSE_WINDOW;
		}
		
		protocolManager.addPacketListener(new PacketAdapter(FakeCreative.getInstance(), ListenerPriority.LOWEST, packetType) {
  		   /**
  		    * Handles incomming client packets.
  		    * 
            * @param event - PacketEvent
  		    */
		    @Override
		    public void onPacketReceiving(final PacketEvent event) {
		    	String packetName = (event.getPacket() != null && ServerUtils.hasSpecificUpdate("1_13") && event.getPacketType() == PacketType.Play.Client.AUTO_RECIPE ? "PacketPlayInAutoRecipe" : 
		    						(event.getPacket() != null && event.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW ? "PacketPlayInCloseWindow" : 
		    						(event.getPacket() != null && event.getPacketType() == PacketType.Play.Client.WINDOW_CLICK ? "PacketPlayInWindowClick" : null)));
		        if (me.RockinChaos.fakecreative.utils.protocol.ProtocolManager.manageEvents(event.getPlayer(), packetName, event.getPacket())) {
		        	event.setCancelled(true);
		        }
		    }
		});
	}
	
   /**
    * Checks if the protocol handler(s) are open.
    * 
    * @return If the protocol handler(s) are open.
    */
	public static boolean isHandling() {
		return (protocolManager != null);
	}
}