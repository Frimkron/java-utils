/*
Copyright (c) 2008 Mark Frimston

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
*/

package uk.co.markfrimston.utils;

import java.lang.reflect.*;
import java.util.*;
import java.lang.*;

public class Bean
{
	private static abstract class Property
	{
		protected Class<?> type;
		
		public Property(Class<?> type)
		{
			this.type = type;;
		}
		
		public Class<?> getType()
		{
			return type;
		}
		
		public abstract void set(Object bean, Object value);		
		public abstract Object get(Object bean);
		public abstract boolean isReadable();
		public abstract boolean isWritable();
	}
	
	private static class MethodProperty extends Property
	{
		private Method getter;
		private Method setter;
		
		public MethodProperty(Class<?> type, Method getter, Method setter)
		{
			super(type);
			this.getter = getter;
			this.setter = setter;
		}
		
		public Object get(Object bean)
		{
			if(isReadable())
			{
				try
				{
					return getter.invoke(bean);
				}
				catch(InvocationTargetException e){}
				catch(IllegalAccessException e){}
			}
			return null;
		}
		
		public void set(Object bean, Object value)
		{
			if(isWritable())
			{
				try
				{
					setter.invoke(bean, value);
				}
				catch(InvocationTargetException e){}
				catch(IllegalAccessException e){}	
			}
		}
		
		public boolean isWritable()
		{
			return setter != null;
		}
		
		public boolean isReadable()
		{
			return getter != null;
		}
	}
	
	private static class FieldProperty extends Property
	{
		private Field field;
		
		public FieldProperty(Class<?> type, Field field)
		{
			super(type);
			this.field = field;
		}
		
		public Object get(Object bean)
		{
			try
			{
				return field.get(bean);
			}
			catch(IllegalAccessException e){}
			return null;
		}
		
		public void set(Object bean, Object value)
		{
			try
			{
				field.set(bean, value);
			}
			catch(IllegalAccessException e){}
		}
		
		public boolean isReadable()
		{
			return true;
		}
		
		public boolean isWritable()
		{
			return true;
		}
	}
	
	private Class<?> type;
	private Object object;
	private Map<String,Property> properties = new HashMap<String,Property>();
	private Map<Class<?>,Property> adders = new HashMap<Class<?>,Property>();
	
	public Bean(Class<?> type)
		throws IllegalArgumentException
	{
		this.type = type;
		
		// find parameterless constructor
		try
		{
			Constructor<?> c = type.getConstructor();
			object = c.newInstance();
		}
		catch(NoSuchMethodException e){}
		catch(InstantiationException e){}
		catch(IllegalAccessException e){}
		catch(InvocationTargetException e){}
		
		if(object == null)
		{
			throw new IllegalArgumentException("Instance of "+type.getName()+" could not be created");
		}		
	}
	
	public Bean(Object object)
	{
		this.type = object.getClass();
		this.object = object;
	}
	
	public Object get(String property)
	{
		Property prop = getOrMakeProperty(property);
		if(prop != null && prop.isReadable())
		{
			return prop.get(object);
		}
		return null;
	}
	
	public void setFromString(String property, String string)
	{
		Property prop = getOrMakeProperty(property);		
		if(prop != null && prop.isWritable())
		{
			Object value = StringUtils.toType(prop.getType(), string);
			if(value != null)
			{
				prop.set(object, value);
			}
		}
	}
	
	public void set(String property, Object value)
	{
		Property prop = getOrMakeProperty(property);		
		if(prop != null && prop.isWritable() && prop.getType().isAssignableFrom(value.getClass()))
		{
			prop.set(object, value);
		}
	}
	
	public void add(Object value)
	{
		Property adder = getOrMakeAdder(value.getClass());
		if(adder != null)
		{
			adder.set(object, value);
		}
	}
	
	public void add(Class<?> type, Object value)
	{
		Property adder = getOrMakeAdder(type);
		if(adder != null && adder.getType().isAssignableFrom(value.getClass()))
		{
			adder.set(object, value);
		}
	}
	
	private Property getOrMakeAdder(Class<?> type)
	{
		Property adder = adders.get(type);
		if(adder == null)
		{
			adder = makeAdder(type);
			adders.put(type, adder);
		}
		return adder;
	}
	
	private Property getOrMakeProperty(String property)
	{
		Property prop = properties.get(property);
		if(prop == null)
		{
			prop = makeProperty(property);
			properties.put(property, prop);
		}
		return prop;
	}
	
	public boolean isWritable(String property)
	{
		Property prop = getOrMakeProperty(property);
		if(prop != null)
		{
			return prop.isWritable();
		}
		else
		{
			return false;
		}
	}
	
	public boolean isReadable(String property)
	{
		Property prop = getOrMakeProperty(property);
		if(prop != null)
		{
			return prop.isReadable();
		}
		else
		{
			return false;
		}
	}
	
	public Object getObject()
	{
		return object;
	}
	
	public Class<?> getType()
	{
		return type;
	}
	
	private Property makeAdder(Class<?> type)
	{
		// look for add method
		try
		{
			String adderName = "add"+type.getSimpleName();			
			Method adder = this.type.getMethod(adderName, type);
			if(Modifier.isPublic(adder.getModifiers()))
			{
				Property prop = new MethodProperty(type, null, adder);
				return prop;
			}
		}
		catch(NoSuchMethodException e){}
		
		return null;
	}
	
	private Property makeProperty(String name)
	{	
		// look for public field
		try
		{
			Field field = type.getField(name);			
			if(Modifier.isPublic(field.getModifiers()))
			{
				Property prop = new FieldProperty(field.getType(), field);
				return prop;
			}			
		}
		catch(NoSuchFieldException e){}
		
		// look for getter & setter
		Method getter = null;
		Class<?> methodPropertyType = null;
		try
		{
			String getterName = "get"+StringUtils.capitalise(name);
			Method g = type.getMethod(getterName);
			if(Modifier.isPublic(g.getModifiers()) && g.getReturnType()!=null)
			{
				getter = g;
				methodPropertyType = g.getReturnType();
			}
		}
		catch(NoSuchMethodException e){}
		Method setter = null;
		try
		{
			String setterName = "set"+StringUtils.capitalise(name);
			if(methodPropertyType!=null)
			{
				Method s = type.getMethod(setterName, methodPropertyType);
				if(Modifier.isPublic(s.getModifiers()))
				{
					setter = s;
				}
			}
			else
			{
				Method[] methods = type.getMethods();
				for(Method method : methods)
				{
					if(method.getName().equals(setterName) && Modifier.isPublic(method.getModifiers())
							&& method.getParameterTypes().length==1)
					{
						setter = method;
						methodPropertyType = method.getParameterTypes()[0];
						break;
					}
				}
			}
		}
		catch(NoSuchMethodException e){}
		if(getter!=null || setter!=null)
		{
			Property prop = new MethodProperty(methodPropertyType,getter,setter);
			return prop;
		}
		
		return null;
	}
	
	public static class Foo
	{
		public List<Object> foos = new ArrayList<Object>();
			
		public void addObject(Object foo)
		{
			foos.add(foo);
		}
	}
	
	public static void main(String[] args)
	{	
		System.out.println("Creating bean");
		Bean fooBean = new Bean(Foo.class);
		
		fooBean.add(Object.class, "This is a test");
		System.out.println("foos now:"+((Foo)fooBean.getObject()).foos);
	}
}
