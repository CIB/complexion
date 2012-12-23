package complexion.test;
import java.util.ArrayList;
import java.util.HashMap;

import complexion.common.Utils;
import complexion.network.message.VerbParameter;
import complexion.network.message.VerbSignature;
import complexion.server.Atom;
import complexion.server.Movable;
public class Test_verb {

	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		TestAtom A = new TestAtom();
		
		Movable player = new Movable();
		ArrayList<VerbSignature> signatures = A.getVerbs(player).verbs;
		for(VerbSignature v : signatures)
		{
			System.out.println("Verb: "+v.verbName);
			for(VerbParameter p : v.parameters)
			{
				System.out.println("Param Type: "+p.type.toString());
			}
		}
		
		/**
		Object[] met_args = {"Head", "Beep"};
		Object[] con_args = {"Hey Head!"};
		if(A.callVerb("printTest", met_args)) // This should work.
			System.out.println("Called PrintTest(String,String) fine");
		else
			System.out.println("Called PrintTest(String,String) badly");
		if(A.callVerb("printTest", con_args))// This should fail because printTest(String A) does not have a @Verb
			System.out.println("Called PrintTest(String) perfectly fine, wait what?");
		else
			System.out.println("Called PrintTest(String)  failed as expected.");
				
		HashMap<String,Object> class_variables = new HashMap<String,Object>();
		class_variables.put("UID", 55);
		Object B = Utils.createClass("complexion.server.Atom",con_args,class_variables);
		Atom C = (Atom)B;
		System.out.print(C.getUID());
		**/
	}

}