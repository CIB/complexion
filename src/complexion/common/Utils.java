package complexion.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Utils {
	@SuppressWarnings("rawtypes")
	public static  final Class[] toClass(Object[] args)
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
	@SuppressWarnings("rawtypes")
	public static final Object createClass(String name,Object[] args,HashMap<String,Object> variables)
	{
		Class<?> new_class;
		Class[] class_args = toClass(args);
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
