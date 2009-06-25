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

import java.util.*;

public class CommandLineUtils 
{
	public static void printUsage(CommandLineParameter[] options,
			CommandLineParameter[] compulsoryParams, CommandLineParameter[] optionalParams)
	{
		printUsage(options, compulsoryParams, optionalParams, null);
	}
	
	public static void printUsage(CommandLineParameter[] options, 
			CommandLineParameter[] compulsoryParams, CommandLineParameter[] optionalParams,
			String additionalHelpText)
	{
		String paramString = "options";
		
		for(CommandLineParameter p : compulsoryParams)
		{
			for(int i=0; i<p.getNumValues(); i++)
			{
				paramString += " "+p.getName();
			}
		}
		int brackets = 0;
		for(CommandLineParameter p : optionalParams)
		{
			paramString += " [";
			brackets++;
			for(int i=0; i<p.getNumValues(); i++)
			{
				paramString += " "+p.getName();
			}
		}
		for(int i=0; i<brackets; i++)
		{
			paramString += " ]";
		}
		
		System.out.println();
		System.out.println("PARAMETERS: "+paramString);
			
		if(compulsoryParams.length>0 || optionalParams.length>0)
		{
			System.out.println();
			for(CommandLineParameter p : compulsoryParams)
			{
				System.out.println(p.getName()+"\t"+p.getDescription());
			}
			for(CommandLineParameter p : optionalParams)
			{
				System.out.println(p.getName()+"\t"+p.getDescription());
			}
		}
		
		System.out.println();
		System.out.println("OPTIONS:");
		for(CommandLineParameter p : options)
		{
			System.out.println("-"+p.getShortName()+"\t--"+p.getName()+"\t"
					+p.getDescription());
		}
		System.out.println("-?\t--help\tShow this help info");
		
		if(additionalHelpText != null)
		{
			System.out.println();
			System.out.println(additionalHelpText);
		}
	}
	
	public static Map<CommandLineParameter,String[]> parseParameters(
			CommandLineParameter[] options, CommandLineParameter[] compulsoryParams,
			CommandLineParameter[] optionalParams, String[] args)
	{
		return parseParameters(options, compulsoryParams, optionalParams, args, null);
	}
	
	public static Map<CommandLineParameter,String[]> parseParameters(
			CommandLineParameter[] options, CommandLineParameter[] compulsoryParams,
			CommandLineParameter[] optionalParams, String[] args, String additionalHelpText)
	{
		if(args.length>0 && (args[0].equals("-?") || args[0].equals("--help")))
		{
			printUsage(options, compulsoryParams, optionalParams, additionalHelpText);
			System.exit(0);
			return null;
		}
		
		HashMap<String,CommandLineParameter> shortNameHash = new HashMap<String,CommandLineParameter>();
		HashMap<String,CommandLineParameter> longNameHash = new HashMap<String,CommandLineParameter>();
		for(CommandLineParameter p : options)
		{
			shortNameHash.put(p.getShortName(), p);
			longNameHash.put(p.getName(), p);
		}
		
		Set<CommandLineParameter> checkList = new HashSet<CommandLineParameter>();
		for(CommandLineParameter option : options){
			checkList.add(option);
		}
		for(CommandLineParameter compParam : compulsoryParams){
			checkList.add(compParam);
		}
		for(CommandLineParameter optParam : optionalParams){
			checkList.add(optParam);
		}
		
		Map<CommandLineParameter,String[]> returnList = 
			new HashMap<CommandLineParameter,String[]>();
		
		try
		{
		
			final int OPTIONS = 0;
			final int COMPULSORY_PARAMS = 1;
			final int OPTIONAL_PARAMS = 2;
			final int END = 3;
			
			int state = OPTIONS;
			CommandLineParameter currentParameter=null;
			int expectedValues = 0;
			List<String> collectedValues = new ArrayList<String>();
			int parameterNumber = 0;
			for(String arg : args)
			{
				if(state == OPTIONS)
				{
					if(arg.startsWith("--"))
					{
						if(expectedValues==0)
						{
							CommandLineParameter p = longNameHash.get(arg.substring(2));
							if(p == null)
							{
								throw new Exception("Invalid option "+arg);
							}
							if(p.getNumValues()!=0)
							{
								currentParameter = p;
								expectedValues = p.getNumValues();
								collectedValues.clear();
							}
							else
							{
								returnList.put(p, new String[]{"true"});
								if(!checkList.contains(p))
								{
									throw new Exception("duplicate option "+p.getName());
								}
								checkList.remove(p);
							}
						}
						else
						{
							throw new Exception(currentParameter.getName()+" option expects "
									+(currentParameter.getNumValues()!=CommandLineParameter.MANY
											?currentParameter.getNumValues()+" ":"")+"values");
						}					
					}
					else if(arg.startsWith("-"))
					{
						if(expectedValues==0)
						{
							CommandLineParameter p = shortNameHash.get(arg.substring(1));
							if(p == null)
							{
								throw new Exception("Invalid option "+arg);
							}
							if(p.getNumValues()!=0)
							{
								currentParameter = p;
								expectedValues = p.getNumValues();
								collectedValues.clear();
							}
							else
							{
								returnList.put(p, new String[]{"true"});
								if(!checkList.contains(p))
								{
									throw new Exception("Duplicate option "+p.getName());
								}
								checkList.remove(p);
							}
						}
						else
						{
							throw new Exception(currentParameter.getName()+" option expects "
									+(currentParameter.getNumValues()!=CommandLineParameter.MANY
											?currentParameter.getNumValues()+" ":"")+"values");
						}
					}
					else
					{
						if(expectedValues != 0)
						{
							collectedValues.add(arg);
							expectedValues--;
							
							if(expectedValues==0)
							{
								returnList.put(currentParameter, collectedValues.toArray(new String[]{}));
																
								if(!checkList.contains(currentParameter))
								{
									throw new Exception("Duplicate option "+currentParameter.getName());
								}
								checkList.remove(currentParameter);
								currentParameter = null;
							}
						}
						else
						{
							// first non-option param
							if(compulsoryParams.length==0)
							{
								state = OPTIONAL_PARAMS;
								if(optionalParams.length==0)
								{
									throw new Exception("Unexpected value "+arg);
								}
								if(optionalParams[0].getNumValues()!=0
										&& optionalParams[0].getNumValues()!=1)
								{
									currentParameter = optionalParams[0];
									collectedValues.clear();
									collectedValues.add(arg);
									expectedValues = optionalParams[0].getNumValues()-1;									
								}
								else
								{
									returnList.put(optionalParams[0], new String[]{arg});
									parameterNumber++;
									
									if(!checkList.contains(optionalParams[0]))
									{
										throw new Exception("Duplicate parameter "+optionalParams[0].getName());
									}
									checkList.remove(optionalParams[0]);
								}
							}
							else
							{
								state = COMPULSORY_PARAMS;
								if(compulsoryParams[0].getNumValues()!=0
										&& compulsoryParams[0].getNumValues()!=1)
								{
									currentParameter = compulsoryParams[0];
									collectedValues.clear();
									collectedValues.add(arg);
									expectedValues = compulsoryParams[0].getNumValues()-1;									
								}
								else
								{
									returnList.put(compulsoryParams[0], new String[]{arg});
									parameterNumber++;
									
									if(!checkList.contains(compulsoryParams[0]))
									{
										throw new Exception("Duplicate parameter "+compulsoryParams[0].getName());
									}
									checkList.remove(compulsoryParams[0]);
								}
							}							
						}
					}
				}
				else if(state == COMPULSORY_PARAMS)
				{
					if(expectedValues == 0)
					{
						CommandLineParameter p = compulsoryParams[parameterNumber];
						if(p.getNumValues()!=0 && p.getNumValues()!=1)
						{
							currentParameter = p;
							collectedValues.clear();
							collectedValues.add(arg);
							expectedValues = p.getNumValues()-1;							
						}
						else
						{
							returnList.put(p, new String[]{arg});
							parameterNumber++;
							
							if(!checkList.contains(p))
							{
								throw new Exception("Duplicate parameter "+p.getName());
							}
							checkList.remove(p);
							
							if(parameterNumber >= compulsoryParams.length)
							{
								if(optionalParams.length>0){
									state = OPTIONAL_PARAMS;
								}else{
									state = END;
								}
							}
						}
					}
					else
					{
						collectedValues.add(arg);
						expectedValues--;
						if(expectedValues == 0)
						{
							returnList.put(currentParameter, collectedValues.toArray(new String[]{}));
							parameterNumber++;
							
							if(!checkList.contains(currentParameter))
							{
								throw new Exception("Duplicate parameter "+currentParameter.getName());
							}
							checkList.remove(currentParameter);
							
							currentParameter = null;
							
							if(parameterNumber >= compulsoryParams.length)
							{
								if(optionalParams.length>0){
									state = OPTIONAL_PARAMS;									
								}else{
									state = END;
								}
							}
						}						
					}
				}
				else if(state == OPTIONAL_PARAMS)
				{
					if(expectedValues == 0)
					{
						CommandLineParameter p = optionalParams[parameterNumber-compulsoryParams.length];
						if(p.getNumValues()!=0 && p.getNumValues()!=1)
						{
							currentParameter = p;
							collectedValues.clear();
							collectedValues.add(arg);
							expectedValues = p.getNumValues()-1;							
						}
						else
						{
							returnList.put(p, new String[]{arg});
							parameterNumber++;
							
							if(!checkList.contains(p))
							{
								throw new Exception("Duplicate parameter "+p.getName());
							}
							checkList.remove(p);
							
							if(parameterNumber-compulsoryParams.length >= optionalParams.length)
							{
								state = END;
							}
						}
					}
					else
					{
						collectedValues.add(arg);
						expectedValues--;
						if(expectedValues == 0)
						{
							returnList.put(currentParameter, collectedValues.toArray(new String[]{}));
							parameterNumber++;
							
							if(!checkList.contains(currentParameter))
							{
								throw new Exception("Duplicate parameter "+currentParameter.getName());
							}
							checkList.remove(currentParameter);
							
							currentParameter = null;
							
							if(parameterNumber-compulsoryParams.length >= optionalParams.length)
							{								
								state = END;								
							}
						}						
					}
				}
				else if(state == END)
				{
					throw new Exception("Unexpected value "+arg);
				}					
			}
			
			if(expectedValues > 0)
			{
				throw new Exception("Parameter "+currentParameter.getName()+" expects "
						+(currentParameter.getNumValues()!=CommandLineParameter.MANY
								?currentParameter.getNumValues()+" ":"")+"values");
			}
			if(expectedValues < 0)
			{
				if(collectedValues.size()==0)
				{
					throw new Exception("Parameter "+currentParameter.getName()
						+" expects values");
				}
				else
				{
					returnList.put(currentParameter, collectedValues.toArray(new String[]{}));
					if(state==COMPULSORY_PARAMS || state==OPTIONAL_PARAMS)
					{
						parameterNumber ++;
					}
					
					if(!checkList.contains(currentParameter))
					{
						throw new Exception("Duplicate parameter "+currentParameter.getName());
					}
					checkList.remove(currentParameter);
					
					currentParameter = null;
										
					if(state == COMPULSORY_PARAMS)
					{
						if(parameterNumber >= compulsoryParams.length)
						{
							if(optionalParams.length>0){
								state = OPTIONAL_PARAMS;									
							}else{
								state = END;
							}
						}
					}
					else if(state == OPTIONAL_PARAMS)
					{
						if(parameterNumber >= optionalParams.length)
						{
							state = END;
						}
					}
				}
			}
			if(state == OPTIONS)
			{
				if(compulsoryParams.length > 0) 
				{
					throw new Exception("Missing parameter "+compulsoryParams[0].getName());
				}
			}
			if(state == COMPULSORY_PARAMS)
			{
				if(parameterNumber < compulsoryParams.length)
				{
					throw new Exception("Missing parameter "+compulsoryParams[parameterNumber].getName());
				}
			}
			
			//add remaining params as defaults
			for(CommandLineParameter p : checkList)
			{
				returnList.put(p, p.getDefaultValues());
			}
			
			//return result
			return returnList;
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			System.out.println();
			System.out.println(e.getMessage());
			System.out.println();
			System.out.println("Use -? option for help");
			System.exit(0);
			return null;
		}
	}
}
