package complexion.client;

import org.lwjgl.LWJGLException;

/**
 * Class representing the entire client application, and global
 * client state.
 */
public class Client {
	public static void main(String[] args)
	{
		Client client = new Client(args);
	}
	
	/**
	 * Notify the user of an error. This function should always work, even when there's no
	 * valid LWJGL context.
	 * @param user_error User-friendly message describing what went wrong.
	 * @param e          Internal stack-trace, useful for debugging.
	 */
	public static void notifyError(String user_error, Exception e)
	{
		System.out.println(user_error);
	}
	
	/**
	 * Client program initialization.
	 */
	public Client(String[] args)
	{
		// Initialize the client window.
		try {
			renderer = new Renderer();
		} catch (LWJGLException e) {
			Client.notifyError("Error setting up program window.. Exiting.",e);
			System.exit(1); // Exit with 1 to signify that an error occured
		}
	}
	
	private Renderer renderer;
}
