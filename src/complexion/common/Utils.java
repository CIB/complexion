package complexion.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class Utils {
	/**
	 * From a list of objects, extracts a list of corresponding classes.
	 * @param args An arbitrary list of objects
	 * @return A list that for each input object contains the corresponding class.
	 */
	@SuppressWarnings("rawtypes")
	public static  final Class[] toClasses(Object[] args)
	{
		if(args.length <= 0)
			return new Class[0];
		Class[] value = new Class[args.length];
		int iter = 0;
		for(Object A : args)
		{
			value[iter] = A.getClass();
			iter++;
		}
		return value;
	}
	/**
	 * This functions creates a class based on classpath and decides the constructor use with args and optionally preset
	 * fields using the map variables.
	 * @param The class name and path ex "complexion.server.Atom"
	 * @param args An list of arbitrary corresponding to a constructor on that class 
	 * @param variables A map<String,Object> or Field Name:Object for example <"UID",55>
	 * @return A object based on class and constructed with args with the fields in variables set to the value
	 */
	@SuppressWarnings("rawtypes")
	public static final Object createClass(String name,Object[] args,HashMap<String,Object> variables)
	{
		Class<?> new_class;
		Class[] class_args = toClasses(args);
		try {
			new_class = Class.forName(name);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		Constructor consturctor;
		try {
			consturctor = new_class.getConstructor(class_args);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		Object new_obj;
		try {
			new_obj = consturctor.newInstance(args);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		for(String key : variables.keySet())
		{
			Object cur_var = variables.get(key);
			Class var_class = cur_var.getClass();
			Field var;
			try {
				var = new_class.getField(key);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			// TODO Make sure to tell someone.
			if(var.getType() != var_class)
			{
				System.err.println("Error in createClass:"+key+"class is not the same as provided");
				continue;
			}
			try {
				var.set(new_obj, cur_var);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return new_obj;
		
	}
}
