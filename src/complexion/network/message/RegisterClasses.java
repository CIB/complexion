package complexion.network.message;

import java.util.ArrayList;

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
		kryo.register(SendResourceHashes.class);
		kryo.register(FullAtomUpdate.class);
		kryo.register(AtomDelta.class);
		kryo.register(ArrayList.class);
		kryo.register(InputData.class);
		kryo.register(SendMovementEvent.class);
		kryo.register(DialogSync.class);
		kryo.register(CreateDialog.class);
		kryo.register(VerbParameter.class);
		kryo.register(VerbSignature.class);
		kryo.register(VerbResponse.class);
	}
}
