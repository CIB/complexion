package complexion.test;

import complexion.client.Dialog;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;

public class TestDialog extends Dialog {

	@Override
	public void initialize(Object args) {
		Button test = new Button("LOGIN");
		test.setMinSize(50, 30);
		test.adjustSize();
		test.setPosition(10, 10);        
		test.addCallback(new Runnable() {
		    public void run() {
		        System.out.println("Button clicked!");
		    }
		});
		
		DialogLayout loginPanel = new DialogLayout();
		loginPanel.setTheme("login-panel");
		loginPanel.setInnerSize(100, 100);
		loginPanel.setPosition(100, 100);
		
		loginPanel.setHorizontalGroup(loginPanel.createSequentialGroup().addGap().addWidget(test).addGap());
		loginPanel.setVerticalGroup(loginPanel.createSequentialGroup().addGap().addWidget(test));
		
		this.root = loginPanel;
	}

	@Override
	public void destroy(Object args) {
		System.out.println("Dialog destroyed!");
	}
	
}
