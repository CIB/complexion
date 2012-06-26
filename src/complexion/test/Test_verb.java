package complexion.test;

public class Test_verb {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestAtom A = new TestAtom();
		Object[] met_args = {"Head", "Beep"};
		A.callVerb("printTest", met_args);
		
	}

}