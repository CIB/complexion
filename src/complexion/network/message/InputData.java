package complexion.network.message;


public class InputData
{
	public int key = 0;
	public boolean key_state = false; // if it was released or pressed (false == released , true == pressed);
	public InputData(int trig_key,boolean state)
	{
		key = trig_key;
		key_state = state;
	}
	public InputData()
	{
		
	}
}
