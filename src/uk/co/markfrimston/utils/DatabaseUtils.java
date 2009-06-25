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

import java.sql.*;
import java.util.*;
import java.lang.reflect.*;
import java.util.regex.*;

public class DatabaseUtils
{
	private static int[] NUMERIC_TYPES = {
		Types.BIGINT, Types.DECIMAL, Types.DOUBLE, Types.FLOAT, Types.INTEGER,
		Types.NUMERIC, Types.REAL, Types.SMALLINT, Types.TINYINT
	};
	private static int[] STRING_TYPES = {
		Types.CHAR, Types.CLOB, Types.LONGVARCHAR, Types.VARCHAR
	};
	
	static
	{
		Arrays.sort(NUMERIC_TYPES);
		Arrays.sort(STRING_TYPES);
	}
	
	public static boolean isNumericType(int sqlType)
	{
		return Arrays.binarySearch(NUMERIC_TYPES, sqlType) >=0;
	}
	
	public static boolean isStringType(int sqlType)
	{
		return Arrays.binarySearch(STRING_TYPES, sqlType) >=0;
	}
	
	public static <T> List<T> mapResults(ResultSet results, Class<T> clazz)
		throws ColumnMappingException, SQLException
	{
		ResultSetMetaData rsmd = results.getMetaData();
		return mapResults(results, new ColumnMapping<T>(clazz,rsmd.getColumnCount()));
	}
	
	public static <T> List<T> mapResults(ResultSet results, ColumnMapping<T> root, ColumnMapping<?>... children)
		throws ColumnMappingException, SQLException
	{
		ColumnMapping<?>[] mappings = new ColumnMapping[children.length+1];
		mappings[0] = root;
		int mappingCols = root.cols;		
		for(int i=0; i<children.length; i++)
		{
			mappings[i+1] = children[i];
			mappingCols += children[i].cols;
		}
		ResultSetMetaData rsmd = results.getMetaData();
		int numCols = rsmd.getColumnCount();
		if(numCols != mappingCols)
		{
			throw new ColumnMappingException("Number of columns in result set does not match number in mapping");
		}
		
		List<T> returnVal = new ArrayList<T>();
		String[][] lastRow = null;
		Bean[] currentObjects = new Bean[mappings.length];
		Bean[] newObjects = new Bean[mappings.length];
		boolean hadResults = false;
	
		while(results.next())
		{
			if(!hadResults){
				hadResults = true;
			}
			String[][] row = new String[mappings.length][];
			
			//retrieve the result row
			int colIndex = 0;
			for(int i=0; i<mappings.length; i++)
			{
				row[i] = new String[numCols];
				for(int j=0; j<mappings[i].cols; j++)
				{
					row[i][j] = results.getString(colIndex+1);
					colIndex++;
				}
			}
			
			/*System.out.print("Row: ");
			for(int i=0; i<mappings.length; i++)
			{
				System.out.print("<");
				for(int j=0; j<mappings[i].cols; j++)
				{
					System.out.print("["+row[i][j]+"]");
				}
				System.out.print(">");
			}
			System.out.println();*/
			
			int colIndexStart = 0;
			boolean changed = false;
			//compare the row with the last row for changes
			for(int i=0; i<mappings.length; i++)
			{		
				if(!changed)
				{
					if(lastRow == null)
					{
						changed = true;
					}
					else
					{
						//check each field for changes in this mapping
						for(int j=0; j<mappings[i].cols; j++)
						{
							//has changed value or null-ness
							if((row[i][j]==null) != (lastRow[i][j]==null)
									|| (row[i][j]!=null && !row[i][j].equals(lastRow[i][j])))
							{
								changed = true;
							}
						}
					}
				}
				//System.out.println("Mapping "+i+" changed? "+changed);
				// column values have changed for this mapping - create and store object
				if(changed)
				{
					//store previous object
					if(currentObjects[i]!=null)
					{					
						storeObject(currentObjects, i, returnVal);
					}
					
					//check if the columns for the new object are null (left joined)
					boolean allNull = true;
					for(int j=0; j<mappings[i].cols; j++)
					{
						if(row[i][j] != null)
						{
							allNull = false;
							break;
						}
					}					
					if(!allNull)
					{				
						// prepare field value map
						Map<String,String> fields = new HashMap<String,String>();
						for(int j=0; j<mappings[i].cols; j++)
						{					
							int index = colIndexStart+j;
							fields.put(rsmd.getColumnLabel(index+1), row[i][j]);
						}
						
						// make new object
						newObjects[i] = makeBeanAndSetFields(mappings[i].clazz, fields);
					}
					else
					{
						//set new object to null
						newObjects[i] = null;
					}
				}	
				
				colIndexStart += mappings[i].cols;
			}		
			
			// set last row to current row and current objects to new objects
			lastRow = new String[mappings.length][];
			for(int i=0; i<mappings.length; i++)
			{
				lastRow[i] = new String[mappings[i].cols];
				for(int j=0; j<mappings[i].cols; j++)
				{
					lastRow[i][j] = row[i][j];
				}					
				currentObjects[i] = newObjects[i];
			}
		}
		
		//finished iterating over results, now store last objects
		if(hadResults)
		{
			for(int i=0; i<mappings.length; i++)
			{
				if(currentObjects[i]!=null)
				{
					storeObject(currentObjects, i, returnVal);
				}
			}
		}
		
		return returnVal;
	}
	
	private static <T> void storeObject(Bean[] currentObjects, int mappingIndex, List<T> rootList)
		throws ColumnMappingException
	{
		if(mappingIndex==0)
		{
			//special case - the root object is stored in the result list
			rootList.add((T)currentObjects[mappingIndex].getObject());
		}
		else
		{
			//object is stored inside its parent - mapping-1.
			if(currentObjects[mappingIndex-1]==null)
			{
				throw new ColumnMappingException("Cannot store child of null parent object");
			}
			currentObjects[mappingIndex-1].add(currentObjects[mappingIndex].getObject());
		}
	}
	
	private static Bean makeBeanAndSetFields(Class<?> clazz, Map<String,String> fields)
		throws ColumnMappingException
	{
		Bean bean = null;
		try
		{
			bean = new Bean(clazz);
		}
		catch(IllegalArgumentException e) 
		{
			throw new ColumnMappingException("Failed to create new "+clazz.getName());
		}						
		
		//set fields of new object
		for(Map.Entry<String,String> field : fields.entrySet())
		{							
			bean.setFromString(field.getKey(), field.getValue());
		}
		
		return bean;
	} 
	
	/*
	 * Same as prepareStatement, but query params may be optionally numbered 
	 * e.g. ?0, ?1 etc. Parameters correspond to the numbered parameters 
	 * i.e. the same parameter can be repeated multiple times in the query
	 */
	public static PreparedStatement prepareNumberedStatement(Connection con, String query, Object... parameters)
		throws SQLException
	{
		List<Object> paramList = new ArrayList<Object>();
		Pattern p = Pattern.compile("\\?([0-9]+)?");
		Matcher m = p.matcher(query);
		StringBuffer newQuery = new StringBuffer();
		int nextNonNumbered = 0;
		while(m.find())
		{
			if(m.group(1)!=null)
			{
				int pNum = Integer.parseInt(m.group(1));
				paramList.add(parameters[pNum]);
				m.appendReplacement(newQuery, "?");
			}
			else
			{
				int pNum = nextNonNumbered;
				nextNonNumbered++;
				paramList.add(parameters[pNum]);
			}
		}
		m.appendTail(newQuery);

		return prepareStatement(con, newQuery.toString(), paramList.toArray(new Object[]{}));
	}
	
	/*	Takes sql string with ?s as parameters and parameter objects and will
	 * 	prepare a statement for it. If a parameter is a list, it will expand
	 *  the corresponding ? into a comma separated list of parameters which can
	 *  be used, for example, for an "in" statement
	 *  
	 *  Returns a the prepared statement with parameter values set
	 */
	public static PreparedStatement prepareStatement(Connection con, String query, Object... parameters)
		throws SQLException
	{
		List<Object> paramList = new ArrayList<Object>();
		Pattern p = Pattern.compile("\\?");
		Matcher m = p.matcher(query);
		StringBuffer newQuery = new StringBuffer();
		for(int i=0; i<parameters.length; i++)
		{
			if((parameters[i].getClass().isArray() || parameters[i] instanceof Collection) 
					&& Sequence.isSequenceable(parameters[i]))
			{
				Sequence<?> s = Sequence.make(parameters[i]);
				String[] qMarks = new String[s.size()];
				Arrays.fill(qMarks, "?");
				m.find();
				m.appendReplacement(newQuery, StringUtils.implode(qMarks));				
				paramList.addAll(s);
			}
			else
			{		
				m.find();
				paramList.add(parameters[i]);				
			}
		}
		m.appendTail(newQuery);
		
		PreparedStatement stmt = con.prepareStatement(newQuery.toString());
		for(int i=0; i<paramList.size(); i++)
		{
			stmt.setObject(i+1, paramList.get(i));
		}
		
		return stmt;
	}
}
