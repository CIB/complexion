package complexion.network.message;

import com.esotericsoftware.kryo.Kryo;

/**
 * This class is responsible for registering the classes we want to use as messages.
 * It needs to be called both from client and server when the network is set up.
 */
public class RegisterClasses {
	public static void registerClasses(Kryo kryo)
	{
		kryo.register(LoginRequest.class);
		kryo.register(LoginAccepted.class);
	}
}
