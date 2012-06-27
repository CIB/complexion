package complexion.test;

import java.io.IOException;

import com.esotericsoftware.kryonet.*;

public class KryoTest {
	public static void main(String[] args) throws IOException
	{
		Server server = new Server();
		server.start();
		server.bind(54555, 54777);
	}
}
