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
import java.util.regex.*;

public class RegexUtils 
{
	public static String escape(String input)
	{
		return input.replaceAll("[^\\w ]","\\\\$0");
	}
	
	public static String allOrders(String[] sequence)
	{
		List<Sequence<Object>> result = SequenceUtils.combinations(Sequence.make(sequence));
		List<String> result2 = new ArrayList<String>();
		for(Sequence<Object> seq : result)
		{
			result2.add(SequenceUtils.toString(seq));
		}
		return "("+StringUtils.implode(result2, "|")+")";
	}
	
	/*
	 * Return group 1 of the regex match against subject, or null if no match
	 */
	public static String group(String regex, String subject)
	{
		Matcher m = Pattern.compile(regex).matcher(subject);
		if(m.matches()){
			return m.group(1);
		}
		else{
			return null;
		}
	}
	
	/*
	 * Return groups 1+ of the regex match against subject, or null if no match
	 */
	public static String[] groups(String regex, String subject)
	{
		Matcher m = Pattern.compile(regex).matcher(subject);
		if(m.matches())
		{
			String[] out = new String[m.groupCount()];
			for(int i=0; i<out.length; i++)
			{
				out[i] = m.group(i+1);
			}
			return out;
		}
		else
		{
			return null;
		}
	}
	
	public static void main(String[] args)
	{
		System.out.println(StringUtils.implode("ThisIsSomeCamelCase".split("(?<=[a-z])(?=[A-Z])")));
	}
}
