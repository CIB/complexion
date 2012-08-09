package complexion.client;

import complexion.common.NetworkMessageException;
import complexion.network.message.AtomVerbs;
import complexion.network.message.VerbSignature;
import complexion.test.TestDialog;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;

/** This Dialog takes care of displaying a choice of verbs for an atom, and letting
 *  the user select between them.
 */
public class DialogVerb extends Dialog {
	
	/** This class is used to store data relevant for verb button callbacks. **/
	class ButtonCallback implements Runnable
	{
		/** Verb that will be called when this button is clicked. **/
		VerbSignature verb; 
		
		ButtonCallback(VerbSignature verb)
		{
			this.verb = verb;
		}
		
		@Override
		public void run() {
			System.out.println("Clicked verb "+this.verb.verbName+" with "+this.verb.parameters.size()+" parameters.");
			// TODO: get the parameters as input and send back a message to the server.
		}
		
		
	}
	
	@Override
	public void initialize(Object args) throws NetworkMessageException
	{
		// Store the list of verbs we've received here here
		AtomVerbs verbs;
		
		if(args instanceof AtomVerbs)
		{
			verbs = (AtomVerbs) args;
		}
		else
		{
			// If the args aren't of the right type, tell the caller
			throw new NetworkMessageException();
		}
		
		// Build the necessary GUI widgets
		
		// --- VERB WINDOW ---
		
		DialogLayout verbPanel = new DialogLayout();
		// TODO: use some more generic theme than "login-panel"
		verbPanel.setTheme("login-panel");
		verbPanel.setInnerSize(100, 100);
		verbPanel.setPosition(200, 120);
		
		// --- VERB WINDOW LAYOUT ---
		DialogLayout.Group horizontalGroup = verbPanel.createSequentialGroup();
		DialogLayout.Group verticalGroup = verbPanel.createSequentialGroup();
		
		// --- VERB BUTTONS ---
		for(VerbSignature verb : verbs.verbs)
		{
			Button verbButton = new Button(verb.verbName);
			horizontalGroup.addWidget(verbButton);
			verticalGroup.addWidget(verbButton);
			verbButton.setMinSize(60, 25);
			verbButton.adjustSize();
			verbButton.addCallback(new ButtonCallback(verb));
		}
		
		// --- FINALIZE ---
		verbPanel.setHorizontalGroup(horizontalGroup);
		verbPanel.setVerticalGroup(verticalGroup);
		
		this.root = verbPanel;
	}

	@Override
	public void destroy(Object args) {
		if(this.root != null) this.root.destroy();
	}
}
