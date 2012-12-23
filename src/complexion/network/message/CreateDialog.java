package complexion.network.message;

/** Create a Dialog object on the server given the class identifier and args. **/
public class CreateDialog {
	/** Unique identifier of the dialog, both server- and client-side. **/
	public int UID;
	
	/** Fully qualified identifier of class. **/
	public String classID;
	
	/** Arguments passed to the constructor. **/
	public Object args;
}
