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

public class ObjectUtils 
{
	public static boolean standardEquals(StandardHashable lhs, Object obj)
	{
		if(obj==null){ 
			return false;
		}
		if(!lhs.getClass().equals(obj.getClass())){
			return false;
		}
		
		StandardHashable rhs = (StandardHashable)obj;
		Object[] lhsValues = lhs.getHashableValues();
		Object[] rhsValues = rhs.getHashableValues();
		
		if(lhsValues.length != rhsValues.length){
			return false;
		}
		for(int i=0; i<lhsValues.length; i++)
		{
			if((lhsValues[i]==null) != (rhsValues[i]==null)){
				return false;
			}
			if(lhsValues[i]!=null && !lhsValues[i].equals(rhsValues[i])){
				return false;
			}
		}
		
		return true;
	}
	
	public static int standardHashCode(StandardHashable obj)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(obj.getClass().getSimpleName());
		
		for(Object value : obj.getHashableValues())
		{
			sb.append(value.toString());
		}
		return sb.toString().hashCode();
	}
	
	public static <T extends Comparable<? super T>> T max(T... values)
	{
		List<T> list = Arrays.asList(values);
		return Collections.max(list);
	}
	
	public static <T extends Comparable<? super T>> T min(T... values)
	{
		List<T> list = Arrays.asList(values);
		return Collections.min(list);
	}
}
