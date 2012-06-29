package complexion.test;

import java.io.IOException;

import complexion.server.Server;
import complexion.server.ServerListener;

public class KryoTest {
	public static void main(String[] args) throws IOException, InterruptedException
	{
		Server server = new Server();
		ServerListener master = new ServerListener(server,1024);
		Thread.sleep(1000);
	}
}
