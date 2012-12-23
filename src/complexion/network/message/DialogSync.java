package complexion.network.message;

/** This is a message both sent Client -> Server and Server -> Client.
 *  It's used to synchronize data between a client.Dialog and a server.DialogHandle instance.
 *
 */
public class DialogSync {
	/** ID of the dialog we'll be sending stuff to. **/
	public int UID;
	
	/** Data we'll be sending. **/
	public Object message;
	
	/** Special boolean flag for destroying dialogs rather than sync'ing them. **/
	public boolean destroy;
}
