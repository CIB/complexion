package complexion.server;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import complexion.network.message.CreateDialog;
import complexion.network.message.DialogSync;

/** This is a server-side handle on a Dialog object on the client.
 * 
 *  Creating this object will automatically issue a network message to create the
 *  appropriate object on the client.
 *
 */
public class DialogHandle {
	/** The last given out UID, increment this whenever creating a new one. **/
	private static int lastUID = 1;
	
	/** Unique identifier of this Dialog, will be transmitted to the client-side Dialog as well. **/
	private int UID;
	
	/** Client the remote Dialog instance is placed on. **/
	private Client client;

	/** Buffer the received sync messages. **/
	Queue<Object> messageQueue = new ConcurrentLinkedQueue<Object>();
	
	/** Create a new dialog on the client, using this instance as handle. 
	 *  
	 *  @param client The Client to create the new Dialog for
	 *  @param className Qualified identifier of Dialog class on remote end.
	 *  @param args Arguments that will be sent to the client-side Dialog, needs to be serializable by kryo.
	 *  **/
	public DialogHandle(Client client, String className, Object args)
	{
		// Give out a new UID by incrementing the last UID by 1
		UID = lastUID++;
		
		CreateDialog message = new CreateDialog();
		message.UID = UID; message.classID = className; message.args = args;
		
		client.connection.send(message);
	}
	
	/** Send a message to the server atom associated with this Dialog. 
	 * @param args Args to send, this must be something we can serialize with Kryo.
	 **/
	protected void sendMessage(Object args)
	{
		DialogSync message = new DialogSync();
		message.UID = UID; message.message = args;
		client.connection.send(message);
	}
	
	/** Poll a message from the received message queue. 
	 * @return The oldest message received from the server. Must be Kryo-serializable. **/
	protected Object pollMessage()
	{
		return messageQueue.poll();
	}
}
