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
package me.RockinChaos.fakecreative.utils.protocol.packet;

import java.lang.reflect.Field;
import java.util.HashMap;

import me.RockinChaos.fakecreative.utils.ServerUtils;

public class PacketContainer {
	
	protected Object packet;
	HashMap<Integer, PacketObject> dataFields = new HashMap<Integer, PacketObject>();
	
	/**
	* Creates a new PacketContainer instance.
	* 
	* @param packet - The packet being deciphered.
	*/
	public PacketContainer(Object packet) {
		this.packet = packet;
		int fieldNumber = 0;
		try {
			for (Field field : this.packet.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				dataFields.put(fieldNumber, new PacketObject(field.getName(), field.get(this.packet)));
				fieldNumber++;
			}
		} catch (Exception e) { ServerUtils.sendSevereTrace(e); }
	}
	
	/**
	* Gets the data fields of the Packet Object.
	* 
	* @return The HashMap of the Fields for the Packet Object.
	*/
	public HashMap<Integer, PacketObject> getStrings() {
		return this.dataFields;
	}
	
	/**
	* Attempts to read a line from the PacketObject.
	* 
	* @param readable - The line to be read.
	* @return The found PacketObject of the Data Field.
	*/
	public PacketObject read(int readable) {
		return this.dataFields.get(readable);
	}
}