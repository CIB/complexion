package complexion.common;

/** This exception is thrown when a network message couldn't be parsed properly.
 *  It should be caught by the network handler and most likely kill the connection, as with TCP,
 *  malformed messages usually mean something's broken, or someone's trying to cheat.
 * @author blub
 *
 */
public class NetworkMessageException extends RuntimeException {
	private static final long serialVersionUID = -2027569946995735153L;
}
