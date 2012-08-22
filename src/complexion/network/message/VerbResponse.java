package complexion.network.message;

import java.util.ArrayList;

/** Response sent from Client -> Server, when the client executed a verb.
 *  Contains all the arguments passed to the verb.
 */
public class VerbResponse {
	/** Name of the verb that was used. **/
	public String verbName;
	
	/** ID of the atom the verb belongs to. **/
	public int UID; 
	
	/** Arguments to the verb. Must match the parameter types given in VerbParameter. **/
	public ArrayList<Object> arguments;
}
