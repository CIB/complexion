package complexion.client;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import complexion.network.message.DialogSync;

import de.matthiasmann.twl.Widget;

/** This class represents a TWL dialog. This may be anything, ranging from a simple
 *  verb list to a fully fledged computer interface. 
 *  
 *  The GUI elements should be added to the gui using this.root.add(), this will ensure
 *  that they are automatically cleaned up upon destruction.
 **/
abstract public class Dialog {
	/** Root widget of this Dialog **/
	protected Widget root;
	
	/** UID of this Dialog. **/
	protected int UID;
	
	/** Buffer the received sync messages. **/
	Queue<Object> messageQueue = new ConcurrentLinkedQueue<Object>();
	
	/** Send a message to the server atom associated with this Dialog. 
	 * @param args Args to send, this must be something we can serialize with Kryo.
	 **/
	// TODO: make protected again, only public for testing purposes
	public void sendMessage(Object args)
	{
		DialogSync message = new DialogSync();
		message.UID = UID; message.message = args;
		Client.current.connection.send(message);
	}
	
	/** Callback handler invoked when a new message from the server has arrived and can be processed. 
	 * 
	 *  Overwrite this in your implementation to handle incoming messages. **/
	protected void processMessage(Object message) {};

	/** Callback handler which must be overwritten for each Dialog class.
	 *  
	 *  This function should initialize the root widget.
	 *  
	 *  @param args Arguments supplied by the server.**/
	abstract public void initialize(Object args);
	
	/** Called when the server destroys the dialog. **/
	abstract public void destroy(Object args);
}
