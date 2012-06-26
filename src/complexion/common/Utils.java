package complexion.common;

public class Utils {
	/**
	 * From a list of objects, extracts a list of corresponding classes.
	 * @param args An arbitrary list of objects
	 * @return A list that for each input object contains the corresponding class.
	 */
	@SuppressWarnings("rawtypes")
	public static  final Class[] toClasses(Object[] args)
	{
		Class[] value = new Class[args.length];
		int iter = 0;
		for(Object A : args)
		{
			value[iter] = A.getClass();
			iter++;
		}
		return value;
	}
}
