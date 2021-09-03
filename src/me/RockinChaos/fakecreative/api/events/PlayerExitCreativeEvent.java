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
package me.RockinChaos.fakecreative.api.events;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
* Called when a player tries to pick block using the middle mouse button.
* 
*/
public class PlayerExitCreativeEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private CommandSender who;
	private GameMode gamemode;
	private boolean silent;
	private boolean doSave;
	private Result result;
	
   /**
	* Creates a new PlayerExitCreativeEvent instance.
	*
	* @param who - The Sender triggering the event.
	* @param altWho - The other Player being referenced.
	* @param gamemode - The GameMode to be set.
	*/
	public PlayerExitCreativeEvent(final CommandSender who, final Player altWho, final GameMode gamemode, final boolean silent, final boolean doSave) {
		super((altWho != null ? altWho : (Player)who));
		this.who = who;
		this.gamemode = gamemode;
		this.silent = silent;
		this.doSave = doSave;
		this.result = who == null ? Result.DENY : Result.ALLOW;
	}
	
   /**
	* Gets the cancellation state of this event. Set to true if you want to
	* prevent the pick block action from shifting materials in the players inventory, materials will not be lost.
	*
	* @return boolean cancellation state.
	*/
	public boolean isCancelled() {
		return this.returnResult() == Result.DENY;
	}
	
   /**
	* Sets the cancellation state of this event. A canceled event will not be
	* executed in the server, but will still pass to other plugins.
	* <p>
	* Canceling this event will prevent use of the autocraft feature (clicking an
	* item to autocraft will result in nothing happening, materials will not be lost.)
	*
	* @param cancel true if you wish to cancel this event.
	*/
	public void setCancelled(final boolean cancel) {
		this.getResult(cancel ? Result.DENY : this.returnResult() == Result.DENY ? Result.DEFAULT : this.returnResult());
	}
	
   /**
	* This controls the action to take with the pick block action.
	* When this is set to default, it will be allowed if no action
	* is taken on the pick block action.
	*
	* @return The action to take with the pick block action.
	*/
	public Result returnResult() {
		return this.result;
	}
	
   /**
    * Sets the creative mode action to be enabled or disabled.
    * 
	* @param exitCreative - the action to take with the creative mode action.
	*/
	public void getResult(final Result exitCreative) {
		this.result = exitCreative;
	}
	
   /**
    * Gets the Sender attempting to get the Player's GameMode.
    * 
	* @return The Sender.
	*/
	public CommandSender getSender() {
		return this.who;
	}
	
   /**
    * Gets the Target Gamemode.
    * 
	* @return The Targeted Gamemode.
	*/
	public GameMode getGameMode() {
		return this.gamemode;
	}
	
   /**
    * Gets if the GameMode change should be silent.
    * 
	* @return If the change is silent.
	*/
	public boolean isSilent() {
		return this.silent;
	}
	
   /**
    * Gets if the GameMode change should have the PlayerStats saved.
    * 
	* @return If the change should be saved.
	*/
	public boolean isDoSave() {
		return this.doSave;
	}
	
   /**
    * Gets the Handlers for the event.
    * 
	* @return The HandlerList for the event.
	*/
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
   /**
    * Gets the HandlerList for the event.
    * 
	* @return The HandlerList for the event.
	*/
	public static HandlerList getHandlerList() {
		return handlers;
	}
}