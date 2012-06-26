package complexion.test;

import java.util.HashMap;
import java.util.Map;

import complexion.server.Atom;
import complexion.common.Utils;
public class Test_verb {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Atom A = new Atom();
		Object[] met_args = new Object[2];
		met_args[0] = "Head";
		met_args[1] = "Beep";
		A.callVerb("printTest", met_args);
		Object[] con_args = {"Hey Head!"};
		HashMap<String,Object> class_variables = new HashMap<String,Object>();
		class_variables.put("UID", 55);
		Object B = Utils.createClass("complexion.server.Atom",con_args,class_variables);
		Atom C = (Atom)B;
		System.out.print(C.UID);
	}

}