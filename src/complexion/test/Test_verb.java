package complexion.test;

public class Test_verb {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestAtom A = new TestAtom();
		Object[] met_args = new Object[2];
		met_args[0] = "Head";
		met_args[1] = "Beep";
		A.callVerb("printTest", met_args);
		
	}

}