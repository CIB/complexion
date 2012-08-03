package complexion.client;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
	
	/** UID of the Atom with which this Dialog exchanges messages. **/
	protected int remoteAtomUID;
	
	private Queue<Object> messageQueue = new ConcurrentLinkedQueue<Object>();
	
	/** Create the dialog with the given args which were submitted by the server over the network.
	 *  
	 *  @param args Arguments supplied by the server.**/
	public Dialog(int remoteAtomUID, Object args)
	{
		this.remoteAtomUID = remoteAtomUID;
		initialize(args);
	}
	
	/** Send a message to the server atom associated with this Dialog. 
	 * @param args Args to send, this must be something we can serialize with Kryo.
	 **/
	protected void sendMessage(Object args)
	{
		// TODO: implement
	}
	
	/** Poll a message from the received message queue. 
	 * @return The oldest message received from the server. Must be Kryo-serializable. **/
	protected Object pollMessage()
	{
		return messageQueue.poll();
	}

	/** Callback handler which must be overwritten for each Dialog class.
	 *  
	 *  This function should initialize the root widget.
	 *  
	 *  @param args Arguments supplied by the server.**/
	abstract public void initialize(Object args);
}
