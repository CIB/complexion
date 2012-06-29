package complexion.test;

import java.io.IOException;
import java.util.ArrayList;

import complexion.server.ServerListener;
import complexion.server.Server;

public class KryoTest {
	public static void main(String[] args) throws IOException, InterruptedException
	{
		Server server = new Server();
		ServerListener master = new ServerListener(server,1024);
		Thread.sleep(1000);
	}
}
