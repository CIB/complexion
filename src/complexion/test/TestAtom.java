package complexion.test;

import complexion.common.Verb;
import complexion.server.Atom;

public class TestAtom extends Atom
{
	@Verb(name = "Atom", category = "", desc = "", verb_icon = "")
	public void printTest(String A,String B)
	{
		System.out.println(A+B);
	}
	@Verb(name = "Atom", category = "", desc = "", verb_icon = "")
	public void simpleVerb()
	{
		System.out.println("IT WORKS!");
	}
	public void printTest(String A)
	{
		System.out.println(A);
	}
}