package complexion.network.message;

import java.util.HashMap;

/**
 * Transfer an overview of all resource files and their CRC32 hashes.
 * This makes it possible to find out which resources on the client are outdated.
 * The client might send this to the server as a form of request for receiving all
 * outdated files.
 *
 */
public class SendResourceHashes {
	public SendResourceHashes(){};
	public HashMap<String,Long> filenameToCRC;
}
