package complexion.common;

/**
 * This contains global configuration values for the engine.
 * These configurations must be synchronized between client and server.
 * For the version objects, compatability must also be ensured.
 */
public class Config {
	/// Describes the game that this engine is running.
	public static final String gameID = "complexion";
	
	/// Describes the version of the game the engine is running.
	public static final int gameVersion = 1;
	
	/// Describes the version of the engine itself.
	public static final int engineVersion = 1;
	
	/// width and height of game tiles, this must be the same on client and server
	public static final int tileWidth  = 32;
	public static final int tileHeight = 32;
	public static final boolean debug = true;
	/// Duration of a gamelogic tick in milli-seconds
	public static final int tickLag = 100;
}
