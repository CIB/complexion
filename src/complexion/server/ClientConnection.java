package complexion.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import complexion.server.Client;
import complexion.network.message.DialogSync;
import complexion.network.message.InputData;

/**
 * This class represents a single TCP connection to a client.
 */
public class ClientConnection extends Listener {
	/**
	 * Create a new connection associated with the given client
	 * 
	 * The client connections are created by ServerListener(the server socket),
	 * so this class can not create them itself. Instead, the ClientConnection
	 * has to be created with a KryoNet connection as parameter.
	 * 
	 * @param client     The client for which this connection will be used.
	 * @param connection The TCP connection this class is handling.
	 */
	public ClientConnection(Client client, Connection connection)
	{
		// Remember the client this connection is associated with.
		this.client = client;
		
		// Remember the connection this client has been created with
		this.connection = connection;
		
		// Cause the connection to call this class' received method
		connection.addListener(this);
		
		// TODO: figure out whether we need to start the connection, or whether the
		//		 master connection handles this for us
		
		
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
	public void received(Connection connection, Object message)
	{
		System.out.println("ClientConnection: "+message);
		if(message instanceof InputData)
		{
			InputData data = (InputData) message;
			String has = "pressed";
			System.out.println(client.getAccountName() + " has " + has + data.key);
			client.ProcessInput(data.key);
		}
		if(message instanceof DialogSync)
		{
			// If it's a DialogSync, forward the message to the correct Dialog instance
			DialogSync sync = (DialogSync) message;
			DialogHandle dialog = client.dialogsByUID.get(sync.UID);
			if(dialog == null)
			{
				System.err.println("Received DialogSync for Dialog UID that doesn't exist.");
				return;
			}
			
			dialog.messageQueue.add(sync.message);
		}
	}
	
	/**
	 * Send an object to the respective remote client.
	 */
	public void send(Object o)
	{
		this.connection.sendTCP(o);
	}
	
	private Client client;
	private Connection connection;
}