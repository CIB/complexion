package complexion.network.message;


public class InputData
{
	public int key = -1;
	public int modifiers = -1;
	public InputData(int trig_key)
	{
		key = trig_key;
	}
	public InputData(int trig_key,int modifier)
	{
		key = trig_key;
		modifiers = modifier;
	}
	public InputData()
	{
		
	}
}
