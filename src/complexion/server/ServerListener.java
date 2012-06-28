package complexion.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.esotericsoftware.kryonet.*;
import complexion.network.message.LoginRequest;

/**
 * This class is responsible for managing network connections.
 * It will mostly be responsible for accepting and creating new
 * connections.
 */
public class ServerListener 
{
	/**
	 * Create a new MasterConnection, which will automatically
	 * bind a TCP socket to localhost
	 * @param port The port to host the server on.
	 * @throws IOException 
	 */
	public ServerListener(Server server, short port) throws IOException
	{
		// Create the server(huge name needed due to naming conflict)
		com.esotericsoftware.kryonet.Server server_socket = 
				new com.esotericsoftware.kryonet.Server();
		
		// Register our custom listener
		server_socket.addListener(new CIListener(this));
		
		// Start listening on a separate thread
		server_socket.start();
	}
	

	/**
	 * Handle the incoming message.
	 * 
	 * Note that this will be running in a separate thread. Thus, all operations
	 * performed from this function(and its descendends) must either operate on local
	 * values, or operate on threadsafe calls.
	 * 
	 * Anything interacting with game-world data must be done  by writing 
	 * requests into a thread-safe structure, and having the master controller
	 * poll this structure.
	 */
	public void received(Connection connection, Object object)
	{
		// Check if this is the first message from that connection.
		if(!server.clientsByIP.containsKey(connection))
		{
			// New connection, add it to the client list, and do it concurrently
			Client new_client = new Client();
			Client previous_value = 
					server.clientsByIP.putIfAbsent(connection, new_client);
			
			// Now make sure we didn't have another thread trying to register itself
			// at the same time.
			if(previous_value != null)
			{
				Thread.dumpStack();
				System.out.println("Unhandled concurrency violation.");
				System.exit(1);
			}
			
			// Try to handle a login message
			boolean login_success = false;
			
			// Okay, looks like we're the only one. Try to register the client.
			if(object instanceof LoginRequest)
			{
				LoginRequest request = (LoginRequest) object;
				if(server.handleLoginRequest(request.account_name, request.password))
				{
					login_success = true; // We're good, bro
				}
			}
			
			if(!login_success)
			{
				// Login seems to have failed, kill the connection
				connection.close();
			}
		}
	}
	
	private Server server;
}

/**
 * Private class, only used to redirect incoming messages to another function.
 */
class CIListener extends Listener
{
	/**
	 * Simply initialize the listener with the given master.
	 */
	public CIListener(ServerListener master)
	{
		this.master = master;
	}
	
	/**
	 * Simply redirect the message to the master connection.
	 */
	public void received(Connection connection, Object object)
	{
		master.received(connection,object);
	}
	
	
	private ServerListener master;
}