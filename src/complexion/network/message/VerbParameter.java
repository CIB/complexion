package complexion.network.message;

import java.util.ArrayList;

/** Describes a verb parameter as sent from Server to Client to inform the client
 *  what they can enter as arguments.
 */
public class VerbParameter {
	public enum Type
	{
		STRING,
		INTEGER,
		FLOAT,
		LIST
	}
	
	/** The type of this parameter. **/
	public VerbParameter.Type type = VerbParameter.Type.STRING;
	
	/** The default value of this parameter, which will be shown to the user. **/
	public Object defaultValue;
	
	/** Only applies to type = LIST. A list of all the options to choose from. **/
	public ArrayList<Object> chooseFrom;
}
