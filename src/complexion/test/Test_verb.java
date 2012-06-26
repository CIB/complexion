package complexion.test;

import java.util.HashMap;

import complexion.server.TestAtom;
import complexion.common.Utils;
public class Test_verb {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestAtom A = new TestAtom();
		Object[] met_args = {"Head", "Beep"};
		A.callVerb("printTest", met_args);
		Object[] con_args = {"Hey Head!"};
		HashMap<String,Object> class_variables = new HashMap<String,Object>();
		class_variables.put("UID", 55);
		Object B = Utils.createClass("complexion.server.Atom",con_args,class_variables);
		Atom C = (Atom)B;
		System.out.print(C.UID);
	}

}