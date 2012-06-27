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
	public void printTest(String A)
	{
		System.out.println(A);
	}
}