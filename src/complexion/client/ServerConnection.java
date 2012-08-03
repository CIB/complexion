package complexion.client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import complexion.common.Utils;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import complexion.network.message.AtomDelta;
import complexion.network.message.CreateDialog;
import complexion.network.message.DialogSync;
import complexion.network.message.LoginAccepted;
import complexion.network.message.LoginRequest;
import complexion.network.message.RegisterClasses;

/**
 * This class is responsible for managing network connections.
 * It will mostly be responsible for accepting and creating new
 * connections.
 */
public class ServerConnection extends Listener
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
				new com.esotericsoftware.kryonet.Client(8192,3072);
		connection.addListener(this);
		connection.start();
		connection.connect(5000, address, port);
		
		// Setup message classes for transfer.
		RegisterClasses.registerClasses(connection.getKryo());
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
			Utils.sleep(1);
		}
		return true;
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
		if(object instanceof LoginAccepted)
		{
			this.loggedIn = true;
			LoginAccepted data = (LoginAccepted) object;
			client.setTick(data.tick);
			connection.sendTCP(new LoginAccepted());
		}
		else if(object instanceof AtomDelta)
		{
			// If it's an AtomDelta, put it into client.atomDeltas and
			// allow the main thread to process it. We'll be sorting it
			// into its specified tick.
			AtomDelta delta = (AtomDelta) object;
			client.atomDeltas.add(delta);
		}
		else if(object instanceof CreateDialog)
		{
			// If it's a CreateDialog, try to create the specified dialog.
			CreateDialog create = (CreateDialog) object;
			
			try {
				// We're creating the class from a string, this is a bit hacky.
				
				// First check what class was specified
				@SuppressWarnings("rawtypes")
				Class cl = Class.forName(create.classID);
				
				// Now make sure the class is actually a Dialog class
				@SuppressWarnings("unchecked")
				Class<Dialog> dialogClass = cl.asSubclass(Dialog.class);
				
				// Now initialize the dialog
				Dialog dialog = dialogClass.newInstance();
				dialog.UID = create.UID;
				dialog.initialize(create.args);
				if(dialog.root != null)
				{
					Client.current.gui.getRootPane().add(dialog.root);
				}
				Client.current.dialogsByUID.put(dialog.UID, dialog);
			} catch (ClassNotFoundException e) {
				System.err.println("Server asked to create non-existing dialog.");
				return;
			} catch (InstantiationException e) {
				System.err.println("Unable to instantiate given Dialog class.");
				return;
			} catch (IllegalAccessException e) {
				System.err.println("Unable to instantiate given Dialog class.");
				return;
			} catch(ClassCastException e) {
				System.err.println("Server attempted to create illegal Dialog class!");
				return;
			}
		}
		else if(object instanceof DialogSync)
		{
			// If it's a DialogSync, forward the message to the correct Dialog instance
			DialogSync sync = (DialogSync) object;
			Dialog dialog = Client.current.dialogsByUID.get(sync.UID);
			if(dialog == null)
			{
				System.err.println("Received DialogSync for Dialog UID that doesn't exist.");
				return;
			}
			
			dialog.messageQueue.add(sync.message);
		}
	}
	public void send(Object data)
	{
		if(!connection.isConnected())
			return;
		connection.sendTCP(data);
	}
	/// Private var to check whether we're logged in.
	private boolean loggedIn = false;
	
	/// Client this connection is running under.
	private Client client;
	
	/// TCP connection we're using.
	private com.esotericsoftware.kryonet.Client connection;
}
