package complexion.network.message;

/**
 * Message sent by a client to request a login.
 */
public class LoginRequest {
	public String account_name;
	public String password; // These should be md5-hashed on the client
	
	public LoginRequest() {};
	
	public LoginRequest(String account_name, String password)
	{
		this.account_name = account_name;
		this.password = password;
	}
}
