package complexion.resource;

import java.io.IOException;
import java.util.HashMap;


/**
 * This class caches resource files and ensures that only one resource of the
 * same filename is loaded at once.
 */
public class Cache {
	/**
	 * Loads a sprite resource object at the given file path from the cache.
	 * If the file is not cached yet, load the file from disk into the cache.
	 * 
	 * @param filename The name of the resource file relative to the resource directory.
	 * @return The sprite loaded from the given file, or null if not present.
	 */
	public static Sprite getSprite(String filename)
	{
		// Try to load the sprite from the cache
		Sprite rval = (Sprite) resourceByString.get(filename);
		
		// If it's not in the cache, try to load it from disk
		if(rval == null)
		{
			try {
				rval = new Sprite(filename);
				resourceByString.put(filename, rval);
			} catch (IOException e) {
				// File not there/wrong format, return null
				return null;
			}
		}
		
		return rval;
	}
	
	/**
	 * Returns a map which maps resource file identifiers to CRC's.
	 * This can be used to determine which resource objects need to be sent
	 * to the client.
	 * 
	 * The HashMap will normally be serialized and sent over the network.
	 * 
	 * @return A HashMap that maps resource file identifiers to CRC's
	 */
	public static HashMap<String,Long> getCRCMap()
	{
		// Create a hashmap as return value and populate it with
		// entries of key - hashID
		HashMap<String,Long> rval = new HashMap<String,Long>();
		for(String key : resourceByString.keySet())
		{
			Resource resource = resourceByString.get(key);
			rval.put(key, resource.hashID);
		}
		
		return rval;
	}
	
	/// Uniquely maps filenames to resource objects 
	private static HashMap<String,Resource> resourceByString =
			new HashMap<String,Resource>();
}
