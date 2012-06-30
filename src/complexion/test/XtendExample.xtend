package complexion.test

import complexion.server.Atom
import static complexion.common.Directions.*;

class XtendExample {
    def static void main(String[] args) 
    {
    	var a = new Atom;
    	a.direction = NORTH;
    	println(a.direction)
   	}
}