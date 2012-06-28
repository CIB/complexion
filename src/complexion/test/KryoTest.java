package complexion.test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import complexion.server.ServerListener;
import complexion.server.Server;

class WorldDelta
{
	ArrayList<AtomData> atoms = new ArrayList<AtomData>();
}

class AtomData
{
	int classID;
	int pos_x;
	int pos_y;
	int pos_z;
}

public class KryoTest {
	public static void main(String[] args) throws IOException, InterruptedException
	{
		Server server = new Server();
		ServerListener master = new ServerListener(server, (short) 1024);
		
		Thread.sleep(10000);
	}
}
