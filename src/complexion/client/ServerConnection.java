package complexion.client;

import java.io.IOException;
import java.net.InetAddress;
import com.esotericsoftware.kryonet.*;

import complexion.network.message.LoginAccepted;
import complexion.network.message.LoginRequest;
import complexion.network.message.RegisterClasses;

/**
 * This class is responsible for managing network connections.
 * It will mostly be responsible for accepting and creating new
 * connections.
 */
public class ServerConnection 
{
	/**
	 * Create a new ServerConnection, which will automatically
	 * connect to the specified host.
	 * @param port The port to host the server on.
	 * @throws IOException 
	 */
	public ServerConnection(Client client, InetAddress address, int port) throws IOException
	{
		// Remember the client
		this.client = client;
		
		
		// Start the connection and connect to the server
		connection = 
				new com.esotericsoftware.kryonet.Client();
		connection.addListener(new CIListener(this));
		connection.start();
		connection.connect(5000, address, port);
		
		// Setup message classes for transfer.
		RegisterClasses.registerClasses(connection.getKryo());
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
		if(object instanceof LoginAccepted)
		{
			this.loggedIn = true;
		}
		System.out.println(object.getClass());
	}
	
	/**
	 * Try to log into the server with the specified account name and password
	 * @param password An MD5(or otherwise) hashed password
	 * @return true on success, false otherwise
	 */
	public boolean login(String account_name, String password)
	{
		connection.sendTCP(new LoginRequest(account_name, password));
		
		// Wait for an answer. If none arrives, return false.
		long start = System.currentTimeMillis();

		while(!loggedIn)
		{
			long waitedFor = System.currentTimeMillis()-start;
			if(waitedFor > 5000) // timeout
			{
				return false;
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				continue;
			}
		}
		return true;
	}
	
	/// Private var to check whether we're logged in.
	private boolean loggedIn = false;
	
	/// Client this connection is running under.
	private Client client;
	
	/// TCP connection we're using.
	private com.esotericsoftware.kryonet.Client connection;
}

/**
 * Private class, only used to redirect incoming messages to another function.
 */
class CIListener extends Listener
{
	/**
	 * Simply initialize the listener with the given master.
	 */
	public CIListener(ServerConnection master)
	{
		this.master = master;
	}
	
	/**
	 * Simply redirect the message to the master connection.
	 */
	@Override
	public void received(Connection connection, Object object)
	{
		master.received(connection,object);
	}
	
	
	private ServerConnection master;
}