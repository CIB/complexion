package complexion.test;
import java.util.HashMap;
import complexion.common.Utils;
import complexion.server.Atom;
public class Test_verb {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestAtom A = new TestAtom();
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

	}

}