package complexion.network.message;

import java.util.ArrayList;

/** Contains a list of parameters for a verb, as sent from Server to Client. **/
public class VerbSignature {
	/** Name of the verb we're using. **/
	public String verbName;
	
	/** ID of the atom the verb belongs to. **/
	public int UID; 
	
	public ArrayList<VerbParameter> parameters;
}
