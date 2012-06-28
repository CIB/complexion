package complexion.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
		// Try to log into a preconfigured server.
		// This should become a user dialog later.
		try {
			connection = new ServerConnection(this, InetAddress.getByName("localhost"), 1024);
			System.out.println(connection.login("CIB", "password"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Initialize the client window.
		try {
			renderer = new Renderer();
		} catch (LWJGLException e) {
			Client.notifyError("Error setting up program window.. Exiting.",e);
			System.exit(1); // Exit with 1 to signify that an error occured
		}
		
		renderer.destroy();
	}
	
	private Renderer renderer;
	private ServerConnection connection;
}
