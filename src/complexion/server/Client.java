package complexion.server;

/**
 * This class represents a single client connected to the server.
 * The class can be used to send messages to the server etc.
 */
public class Client 
{
	/// Identifies the client in the (as of yet non-existent)
	/// login system
	String account_name;

	public String getAccountName() {
		return account_name;
	}
}
