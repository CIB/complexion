package complexion.server;

public class Mob extends Movable
{
	private Client client;
	public Mob()
	{
		// TODO Auto-generated constructor stub
	}
	public Client getClient()
	{
		return client;
	}
	public void setClient(Client client)
	{
		// Don't allow someone to replace a client.
		if(this.client != null)
			return;
		this.client = client;
	}
	public boolean Step(int direction)
	{
		boolean result = super.Step(direction);
		if(result)
		{
			//client.setEye(this);
			return true;
		}
		return false;
	}

}
