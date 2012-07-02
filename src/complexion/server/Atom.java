package complexion.server;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import complexion.common.Utils;
import complexion.common.Verb;
import complexion.resource.Cache;
import complexion.resource.Sprite;

/**
 * Server-side representation of a game Atom.
 * 
 * This includes game-logic stuff, such as atom-interaction(Bump, Enter, etc.),
 * verbs, user-defined functions and so on.
 */
public class Atom {
		/// OR'd into this.outdated if the sprite or sprite_state needs to be resent
		static int spriteOutdated = 1;
		
		/// OR'd into this.outdated if the name or any of the verbs are outdated.
		/// It's called textOudated because full strings have to be resent.
		static int textOutdated = 2;
		
		/// OR'd into this.outdated if the position(x,y,z,pixel_x,pixel_y),
		/// the layer or the direction changed
		static int positionOutdated = 4;
		
		/// This is a bitfield that describes which parts of the Atom have changed since
		/// the last tick and need to be resent to all clients.
		int outdated = 0;
	
		/// Unique ID of the object, generated by the server.
		private Integer UID; 
		
		/// Last UID that has been generated, incremented each time a new
		/// UID is given out.
		private static int lastUID;
		
		/// Sprite the object is currently associated with.
		private Sprite sprite;  
		
		/// Determines whether the object will be rendered above or
		/// below other objects
		private int layer;
		
		/// Each sprite has multiple states, which are more or less
		/// sprites of their own. sprite_state determines which state
		/// is used.
		private String sprite_state;
		
		
		private List<Atom> contents;
		// TODO: Add a way for the server to restart an animation
		//       (similar to the BYOND flick proc)
		
		/** Fetch the filename associated with the atom's current sprite.
		 */
		public String getSprite() {
			return sprite.filename;
		}
		
		/** Set the atom's sprite to the given filename.
		 * @param sprite Filename of the sprite to use, e.g. mask.dmi
		 */
		public void setSprite(String sprite) {
			this.sprite = Cache.getSprite(sprite);
			this.outdated |= Atom.spriteOutdated;
		}

		/// A sprite can define different appearances depending on the
		/// direction. This variable should be one of the constants defined
		/// in complexion.Directions
		private int direction;
		
		/// What this is located inside
		/// If this is at the bottom of the tile then it should not be assigned to anything
		private Atom location;

		public Atom() 
		{
			// Assign the atom a unique UID
			lastUID++;
			this.UID = lastUID;
		}
		
		/**
		 * @return The UID of this atom.
		 */
		public int getUID()
		{
			return UID;
		}
		
		/**
		 * @param new_state The new sprite state of the atom.
		 */
		public void setSpriteState(String new_state)
		{
			sprite_state = new_state;
			this.outdated |= Atom.spriteOutdated;
		}
		
		/**
		 * @return The current sprite state string.
		 */		
		public String getSpriteState()
		{
			return sprite_state;
		}
		
		/**
		 * @param new_dir The new facing of the object.
		 */
		public void setDirection(int new_dir)
		{
			direction = new_dir;
			this.outdated |= Atom.positionOutdated;
		}
		
		/**
		 * @return The current facing of the object.
		 */
		public int getDirection()
		{
			return direction;
		}
		
		/**
		 * @return Whatever this is directly inside of.
		 */
		public Atom getLoc()
		{
			if(location != null)
				return location;
			else
				return null;
		}
		
		/**
		 * For directly moving this inside something else
		 * 
		 * @param new_loc The new location of the atom.
		 */
		public void setLoc(Atom new_loc)
		{
			location.contents.remove(this);
			location = new_loc;
			new_loc.contents.add(this);
			this.outdated |= Atom.positionOutdated;
		}
		
		/**
		 * @return The tile at the bottom of whatever this is inside.
		 */
		public Atom getTile()
		{
			if(this instanceof Tile)
			{
				return (Tile)this;
			}
			else
			{
				Tile tile = null;
				Atom cur_loc = this.getLoc();
				while(tile == null)
				{
					if(cur_loc == null) // if we have null here we have problems
						return null; // so lets just quit out. TODO: Error message?
					if(cur_loc instanceof Tile)
					{
						tile = (Tile) cur_loc;break;
					}
					else
						cur_loc = cur_loc.getLoc();
				}
				if(tile != null)
					return tile;
			}
			return null;
		}
		
		/**
		 * @return The x location of the tile the atom is in.
		 */
		public int getX()
		{
			return this.getTile().getX();
		}
		
		/**
		 * @return The y location of the tile the atom is in.
		 */
		public int getY()
		{
			return this.getTile().getY();
		}
		
		
		/**
		 * @return A value representing at which layer(below or above other objects)
		 * 		   this atom should be rendered.
		 */
		public int getLayer() {
			return layer;
		}
	
		/**
		 * @param layer Sets a value that determines whether this object will be rendered above
		 * 				or below other atoms.
		 */
		public void setLayer(int layer) {
			this.layer = layer;
			this.outdated |= Atom.positionOutdated;
		}

		/** Function which is periodically called to process this atom.
		 */
		public void Tick()
		{
			
		}
		
		/** Event handler evoked when the user clicks this Atom.
		 * 
		 *  This function is called by the network handler when the client clicks an object
		 *  with the mouse.
		 *  
		 *  If any verbs are registered for this mouse button/key combination, Clicked() will not be called,
		 *  but instead a verb list will be sent to the client.
		 *  
		 * @param mouseButton LWJGL representation of the mouse button that was used to click the object.
		 * @param keyboardKey LWJGL representation of a key that was held down while clicking, or 0 if none.
		 */
		public void Clicked(int mouseButton, int keyboardKey)
		{
			
		}
		
		/** Event handler evoked when the user drags another atom onto this atom.
		 * 
		 * @param from The atom from which the mouse was dragged.
		 */
		public void Dragged(Atom from)
		{
			
		}

		/**
		 * Call an atom verb with the given name(key) and arguments. TODO: Make sure
		 * only functions we expressly make available over the network can be called
		 * this way.
		 * 
		 * @param key
		 *            Name of the function/verb to be called.
		 * @param args
		 *            A list of objects to be passed as args. These objects will be
		 *            type-checked before being passed to the function.
		 * @return true on success, false on failure
		 */
		@SuppressWarnings("rawtypes")
		public boolean callVerb(String key, Object[] args)
		{
			// convert our args to Class
			Class[] classes = Utils.toClasses(args);
			Method func;
			// Attempt to get the method specified.
			try
			{
				func = this.getClass().getMethod(key, classes);
			} 
			catch (SecurityException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} 
			catch (NoSuchMethodException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			// if @Verb does not exist fail nicely and don't allow it to be called.
			if (func.getAnnotation(Verb.class) == null)
			{
				System.err.println(key+ " method is not in the verbs list!.. error at line 80 in Atom.callVerb");
				return false;
			}
			// Try to call the function.
			try
			{
				func.invoke(this, args);
			} 
			catch (IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} 
			catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} 
			catch (InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			return true;
		}

}
