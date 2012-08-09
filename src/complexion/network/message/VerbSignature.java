package complexion.network.message;

import java.util.ArrayList;

/** Contains a list of parameters for a verb, as sent from Server to Client. **/
public class VerbSignature {
	/** Name of the verb we're using. **/
	public String verbName;
	
	public ArrayList<VerbParameter> parameters;
}
