package complexion.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.esotericsoftware.kryonet.*;

import complexion.network.message.LoginAccepted;
import complexion.network.message.LoginRequest;
import complexion.network.message.RegisterClasses;

/**
 * This class is responsible for managing network connections.
 * It will mostly be responsible for accepting and creating new
 * connections.
 */
public class ServerListener extends Listener
{
	/**
	 * Create a new ServerListener, which will automatically
	 * bind a TCP socket to localhost
	 * @param port The port to host the server on.
	 * @throws IOException 
	 */
	public ServerListener(Server server, int port) throws IOException
	{
		// Remember the server we were created from
		this.server = server;
		
		// Create the server(huge name needed due to naming conflict)
		server_socket = 
				new com.esotericsoftware.kryonet.Server();
		
		// Setup message classes for transfer.
		RegisterClasses.registerClasses(server_socket.getKryo());
		
		// Register our custom listener
		server_socket.addListener(this);
		server_socket.bind(port);
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
	@Override
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
					connection.sendTCP(new LoginAccepted());
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
	
	/// The game server instance this listener was crated from.
	private Server server;
	private com.esotericsoftware.kryonet.Server server_socket;
}