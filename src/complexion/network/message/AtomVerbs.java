package complexion.network.message;

import java.util.ArrayList;

/** Describes a list of verbs for an atom sent to the client. **/
public class AtomVerbs {
	/** UID of the atom these verbs belong to. **/
	public int atomUID;
	
	public ArrayList<VerbSignature> verbs;
}
