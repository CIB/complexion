package complexion.test;

import complexion.client.Dialog;

public class TestDialog extends Dialog {

	@Override
	public void initialize(Object args) {
		System.out.println("Dialog created!");
	}

	@Override
	public void destroy(Object args) {
		System.out.println("Dialog destroyed!");
	}
	
}
