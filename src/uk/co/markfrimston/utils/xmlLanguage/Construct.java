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

package uk.co.markfrimston.utils.xmlLanguage;

import org.w3c.dom.*;
import java.lang.reflect.*;
import java.util.*;
import uk.co.markfrimston.utils.*;

public abstract class Construct implements Tree<Construct>
{
	private static enum ParamKind
	{
		SINGLE, RAW_COLLECTION, GENERIC_COLLECTION
	}
	
	private static class ParameterInfo
	{
		public String name;
		public Method method;
		public ParamKind kind;
		public Class<?> type;
		public Class<?> innerType;
		public boolean required;
		
		public ParameterInfo(String name, Method method, ParamKind kind,
				Class<?> type, Class<?> innerType, boolean required)
		{
			this.name = name;
			this.method = method;
			this.kind = kind;
			this.type = type;
			this.innerType = innerType;
			this.required = required;
		}
	}
	
	private static class Parameter
	{
		public ParameterInfo info;
		public List<Object> values;
		
		public Parameter(ParameterInfo info, List<Object> values)
		{
			this.info = info;
			this.values = values;
		}
	}
	
	public static <T extends Construct> T parseConstruct(Element element, Class<T> clazz)
		throws ParseConstructException
	{		
		T returnVal = null;
		try
		{
			returnVal = clazz.newInstance();
			String packageName = clazz.getPackage().getName();
			
			//find parameters
			Method[] methods = clazz.getMethods();
			Map<String,ParameterInfo> parameters = new HashMap<String,ParameterInfo>();
			String defaultParameterName = null;
			for(Method method : methods)
			{
				if(method.getAnnotation(ParameterSetter.class) != null
						&& method.getName().startsWith("set")
						&& method.getParameterTypes().length == 1)
				{
					String name = StringUtils.unCapitalise(method.getName().substring(3));
					boolean required = method.getAnnotation(ParameterSetter.class).required();
					ParameterInfo paramInfo = null;
					
					Type pType = method.getGenericParameterTypes()[0];
					if(pType instanceof ParameterizedType)
					{
						ParameterizedType ppType = (ParameterizedType)pType;
						if(ppType.getRawType() instanceof Class
								&& Collection.class.isAssignableFrom((Class<?>)ppType.getRawType()))								
						{							
							Type ippType = ppType.getActualTypeArguments()[0]; 
							if(ippType instanceof Class
									&& StringUtils.toType((Class<?>)ippType, "0")!=null)
							{
								//generic collection								
								Class<?> fillWith = method.getAnnotation(ParameterSetter.class).fillWith();
								Class<?> collectionType = fillWith.equals(NoClass.class) ? (Class<?>)ppType.getRawType() : fillWith;
								paramInfo = new ParameterInfo(name, method, 
										ParamKind.GENERIC_COLLECTION, collectionType,
										(Class<?>)ippType, required);
							}
						}
					}
					else if(pType instanceof Class)
					{
						Class<?> pClass = (Class<?>)pType;
						if(Collection.class.isAssignableFrom(pClass))
						{
							//raw collection
							paramInfo = new ParameterInfo(name, method,
									ParamKind.RAW_COLLECTION, pClass, String.class,
									required);
						}
						else if(StringUtils.toType(pClass, "0")!=null)
						{
							//single value
							paramInfo = new ParameterInfo(name, method,
									ParamKind.SINGLE, pClass, null, required);
						}
					}
					
					//add parameter
					if(paramInfo != null)
					{
						parameters.put(name,paramInfo);
						
						if(method.getAnnotation(DefaultParameter.class) != null
								&& defaultParameterName == null)
						{
							defaultParameterName = name;
						}
					}									
				}
			}			
			
			//check attributes
			Set<String> found = new HashSet<String>();
			for(String pName : parameters.keySet())
			{
				String attName = javaNameToTagName(pName);
				String val = element.getAttribute(attName);
				if(val != null && !val.equals(""))
				{
					found.add(pName);
					ParameterInfo paramInfo = parameters.get(pName);
					try
					{
						switch(paramInfo.kind)
						{
							case SINGLE:
							{								
								paramInfo.method.invoke(returnVal, 
										StringUtils.toType(paramInfo.type, val));
								break;
							}
							case RAW_COLLECTION:
							{
								Collection<Object> values = (Collection<Object>)paramInfo.type.newInstance();
								values.add(val.trim());
								paramInfo.method.invoke(returnVal,values);
								break;
							}
							case GENERIC_COLLECTION:
							{
								Collection<Object> values = (Collection<Object>)paramInfo.type.newInstance();
								values.add(StringUtils.toType(paramInfo.innerType, val));
								paramInfo.method.invoke(returnVal, values);
								break;
							}
						}									
					}
					catch(InvocationTargetException e)
					{ }
					catch(InstantiationException e)
					{ }
					catch(IllegalAccessException e)
					{ }
				}
			}
			
			//check for allowed nested constructs
			Map<Class<?>,Method> allowedNested = new HashMap<Class<?>,Method>();
			for(Method method : methods)
			{
				if(method.getAnnotation(NestedAdder.class)!=null)
				{
					if(method.getName().startsWith("add"))
					{
						String className = StringUtils.capitalise(method.getName().substring(3));
						try
						{
							Class<?> addClass = Class.forName(packageName+"."+className);
							if(Construct.class.isAssignableFrom(addClass))
							{
								Class[] nestedParamTypes = new Class[]{ addClass };
								if(Arrays.equals(method.getParameterTypes(),nestedParamTypes))
								{									
									allowedNested.put(addClass, method);
								}
							}
						}
						catch(ClassNotFoundException e)
						{}						
					}					
				}
			}
			
			//examine nested elements
			StringBuffer textContent = new StringBuffer(); 
			Map<String,List<Object>> parameterValues = new HashMap<String,List<Object>>();
			Node nextChild = element.getFirstChild();
			while(nextChild != null)
			{
				switch(nextChild.getNodeType())
				{
					case Node.TEXT_NODE:
					{
						textContent.append(nextChild.getNodeValue());
						break;
					}
					case Node.ELEMENT_NODE:
					{
						String className = tagNameToJavaName(nextChild.getNodeName());
						String parameterName = StringUtils.unCapitalise(className);						
						if(parameters.containsKey(parameterName) 
								&& !found.contains(parameterName))
						{						
							//element is a parameter not found in attributes.
							ParameterInfo paramInfo = parameters.get(parameterName);
							switch(paramInfo.kind)
							{
								case SINGLE:
								{									
									Object o = retrieveParameterValue(packageName, parameterName, (Element)nextChild);
									if(o!=null)
									{
										found.add(parameterName);
										List<Object> pcs = new ArrayList<Object>();
										pcs.add(o);
										returnVal.addParameter(new Parameter(paramInfo, pcs));																						
									}																											
									break;
								}
								case RAW_COLLECTION:
								case GENERIC_COLLECTION:
								{									
									if(!parameterValues.containsKey(parameterName))
									{
										List<Object> values = new ArrayList<Object>();
										parameterValues.put(parameterName, values);
									}
									Object o = retrieveParameterValue(packageName, parameterName, (Element)nextChild);
									if(o!=null)
									{
										parameterValues.get(parameterName).add(o);
									}
									break;
								}
							}
							
						}
						else
						{
							//check if element is an allowed nested construct
							boolean isNestedConstruct = false;
							try
							{
								Class<?> tagClass = Class.forName(packageName+"."+className);	
								for(Class<?> allowedClass : allowedNested.keySet())
								{
									if(allowedClass.isAssignableFrom(tagClass))
									{
										isNestedConstruct = true;
										Method adder = allowedNested.get(allowedClass);
										try
										{
											adder.invoke(returnVal, parseConstruct((Element)nextChild, 
													(Class<? extends Construct>)tagClass));
										}
										catch(InvocationTargetException e)
										{ 
											if(e.getCause() instanceof ParseConstructException)
											{
												throw (ParseConstructException)e.getCause();
											}
										}
										break;
									}
								}														
							}
							catch(ClassNotFoundException e)
							{  }
							
							if(!isNestedConstruct)
							{
								throw new ParseConstructException("Unknown construct "
										+"\""+nextChild.getNodeName()+"\" in \""+element.getNodeName()+"\"");
							}
						}
						break;
					}
				}
				nextChild = nextChild.getNextSibling();
			}
			//set collection parameters
			for(String parameterName : parameterValues.keySet())
			{
				found.add(parameterName);				
				ParameterInfo paramInfo = parameters.get(parameterName);					
				returnVal.addParameter(new Parameter(paramInfo,parameterValues.get(parameterName)));
			}
			//if default parameter not found yet, set using text content
			if(defaultParameterName!=null && !found.contains(defaultParameterName))
			{
				found.add(defaultParameterName);
				ParameterInfo paramInfo = parameters.get(defaultParameterName);
				switch(paramInfo.kind)
				{
					case SINGLE:
					case RAW_COLLECTION:
					case GENERIC_COLLECTION:
					{
						List<Object> values = new ArrayList<Object>();
						values.add(textContent.toString().trim());
						returnVal.addParameter(new Parameter(paramInfo,values));
						break;
					}					
				}
			}
			
			//check for required parameters not found
			for(String pName : parameters.keySet())
			{
				ParameterInfo paramInfo = parameters.get(pName);
				if(paramInfo.required && !found.contains(pName))
				{
					throw new ParseConstructException("Missing parameter \""+pName+"\" "
							+"for construct \""+element.getNodeName()+"\"");
				}
			}
			
			return returnVal;
		}
		catch(InstantiationException e)
		{ }
		catch(IllegalAccessException e)
		{ }
		
		return returnVal;
	}
	
	private static String tagNameToJavaName(String input)
	{
		String[] bits = input.split("-");
		for(int i=0; i<bits.length; i++)
		{
			bits[i] = StringUtils.capitalise(bits[i].toLowerCase());
		}
		return StringUtils.implode(bits, "");
	}
	
	private static String javaNameToTagName(String input)
	{
		String[] bits = input.split("(?<=[a-z])(?=[A-Z])");
		for(int i=0; i<bits.length; i++)
		{
			bits[i] = bits[i].toLowerCase();
		}
		return StringUtils.implode(bits, "-");
	}
	
	private static Object retrieveParameterValue(String packageName, String paramName, 
			Element paramElement)
		throws ParseConstructException
	{
		Node nextChild = paramElement.getFirstChild();
		Element nestedElement = null;
		StringBuffer textContent = new StringBuffer();
		while(nextChild != null)
		{
			switch(nextChild.getNodeType())
			{
				case Node.TEXT_NODE:
					textContent.append(nextChild.getNodeValue());
					break;
				case Node.ELEMENT_NODE:
					if(nestedElement==null){
						nestedElement = (Element)nextChild;
					}else{
						throw new ParseConstructException("More than one element found in parameter \""+paramName+"\"");
					}
					break;
			}
			nextChild = nextChild.getNextSibling();
		}
		if(nestedElement!=null)
		{
			try
			{
				String className = tagNameToJavaName(nestedElement.getNodeName());
				Class<?> tagClass = Class.forName(packageName+"."+className);
				if(ParameterConstruct.class.isAssignableFrom(tagClass))
				{
					ParameterConstruct construct = (ParameterConstruct)Construct.parseConstruct(
							nestedElement, (Class<? extends ParameterConstruct>)tagClass);
					return construct;
				}
				else
				{
					throw new ParseConstructException("Construct \""+nestedElement.getNodeName()+"\" "
							+"in parameter \""+paramName+"\" not allowed");
				}
			}
			catch(ClassNotFoundException e)
			{
				throw new ParseConstructException("Unknown construct \""+nestedElement.getNodeName()+"\" "
						+"in parameter \""+paramName+"\"");
			}
		}
		else
		{
			return textContent.toString().trim();
		}
	}

	protected List<Construct> nestedConstructs = new ArrayList<Construct>();
	protected List<Tree<Construct>> constructBranches = new ArrayList<Tree<Construct>>();
	
	protected List<Parameter> parameters = new ArrayList<Parameter>();
	
	protected void addNestedConstruct(Construct construct)
	{
		nestedConstructs.add(construct);
		constructBranches.add(construct);
	}
	
	protected void addParameter(Parameter parameter)
	{
		parameters.add(parameter);
	}
	
	public List<Construct> getNestedConstructs()
	{
		return nestedConstructs;
	}
	
	public List<Tree<Construct>> getNestedConstructsAsBranches()
	{
		return constructBranches;
	}
	
	public Construct getNodeData()
	{
		return this;
	}

	public Iterator<Tree<Construct>> iterator()
	{
		return getNestedConstructsAsBranches().iterator();
	}
	
	protected void evaluateParameters()
		throws EvaluationException
	{
		for(Parameter p : parameters)
		{
			switch(p.info.kind)
			{
				case SINGLE:
				{
					String val = null;
					Object paramVal = p.values.get(0);
					if(paramVal instanceof String)
					{
						val = (String)paramVal;
					}
					else if(paramVal instanceof ParameterConstruct)
					{
						val = ((ParameterConstruct)paramVal).evaluate();
					}
					try
					{
						p.info.method.invoke(this, StringUtils.toType(p.info.type, val));
					}
					catch(InvocationTargetException e)
					{
						if(e.getCause() instanceof EvaluationException)
						{
							throw (EvaluationException)e.getCause();
						}
					}
					catch(IllegalAccessException e){}
					break;
				}
				case RAW_COLLECTION:
				case GENERIC_COLLECTION:
				{
					try
					{
						Collection<Object> vals = (Collection<Object>)p.info.type.newInstance();
						for(Object paramVal : p.values)
						{
							String val = null;
							if(paramVal instanceof String)
							{
								val = (String)paramVal;
							}
							else if(paramVal instanceof ParameterConstruct)
							{
								val = ((ParameterConstruct)paramVal).evaluate();
							}
							vals.add(StringUtils.toType(p.info.innerType, val));
						}
						p.info.method.invoke(this, vals);
					}
					catch(InstantiationException e){}
					catch(IllegalAccessException e){}
					catch(InvocationTargetException e)
					{
						if(e.getCause() instanceof EvaluationException)
						{
							throw (EvaluationException)e.getCause();
						}
					}
					break;
				}
			}
		}
	}
	
	public String evaluate()
		throws EvaluationException
	{
		evaluateParameters();
		return null;
	}
}
