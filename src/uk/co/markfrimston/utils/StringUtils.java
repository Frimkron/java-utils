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
import java.io.*;
import java.util.regex.*;

public class StringUtils
{
	public static String implode(Object[] array, String glue)
	{
		return implode(Sequence.make(array), glue);
	}
	
	public static String implode(Object[] array)
	{
		return implode(array,", ");
	}
	
	public static String implode(Iterable<?> collection, String glue)
	{
		StringBuffer str = new StringBuffer();
		boolean first = true;
		for(Object obj : collection)
		{		
			if(first){
				first = false;
			}else{
				str.append(glue);
			}
			str.append(obj.toString());
		}
		return str.toString();
	}
	
	public static String implode(Iterable<?> collection, String glue, String template)
	{
		StringBuffer str = new StringBuffer();
		boolean first = true;
		for(Object obj : collection)
		{		
			if(first){
				first = false;
			}else{
				str.append(glue);
			}
			str.append(StringUtils.template(template, obj.toString()));
		}
		return str.toString();
	}
	
	public static String implode(Iterable<?> collection)
	{
		return implode(collection, ", ");
	}
	
	public static String implode(char[] charArray, String glue, String template)
	{
		return implode(Sequence.make(charArray), glue, template);
	}
	
	public static String implode(char[] charArray, String glue)
	{
		return implode(Sequence.make(charArray), glue);
	}
	
	public static String implode(char[] charArray)
	{
		return implode(charArray,", ");
	}
	
	public static int occurences(String string, char find)
	{
		int count = 0;
		int pos = -1;
		while((pos = string.indexOf(find, pos+1)) != -1)
		{
			count++;
		}
		return count;
	}
	
	public static int occurences(String string, String find)
	{
		int count = 0;
		int pos = -1;
		while((pos = string.indexOf(find, pos+1)) != -1)
		{			
			count++;
		}
		return count;
	}
	
	public static String leftPad(String input, int characters, char padding)
	{
		StringBuffer sb = new StringBuffer();
		while(sb.length()+input.length() < characters)
		{
			sb.append(padding);
		}
		sb.append(input);
		return sb.toString();
	}
	
	public static String rightPad(String input, int characters, char padding)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(input);
		while(sb.length() < characters)
		{
			sb.append(padding);
		}
		return sb.toString();
	}
	
	
	
	public static int distance(String strA, String strB, int max)
	{
		return SequenceUtils.distance(Sequence.make(strA), Sequence.make(strB), max);
	}
	
	public static int wordDistance(String strA, String strB, int max)
	{
		return SequenceUtils.distance(Sequence.make(strA.split("\\W+")),
				Sequence.make(strB.split("\\W+")), max);
	}
	
	public static int lineDistance(String strA, String strB, int max)
	{
		return SequenceUtils.distance(Sequence.make(strA.split("[\n\r]+")),
				Sequence.make(strB.split("[\n\r]+")), max);
	}
	
	public static String preview(String input, int maxChars)
	{
		if(input.length() <= maxChars)
		{
			return input;
		}
		else
		{
			return input.substring(0, maxChars)+"...";
		}
	}
	
	public static String fixedLength(String input, int length, boolean rightAlign,
			char filler)
	{
		if(input.length() > length)
		{
			return input.substring(0,length);
		}
		else
		{
			if(rightAlign)
			{
				return leftPad(input, length, filler);
			}
			else
			{
				return rightPad(input, length, filler);
			}
		}
	}
	
	public static String fixedLength(String input, int length, boolean rightAlign)
	{
		return fixedLength(input, length, rightAlign, ' ');
	}
	
	public static String fixedLength(String input, int length)
	{
		return fixedLength(input, length, false, ' ');
	}
	
	public static int instancesOf(String subString, String subject)
	{
		if(subject == null || subString == null){
			return 0;
		}
		int count = 0;
		int pos = -1;
		while((pos = subject.indexOf(subString, pos+1)) != -1)
		{
			count++;
		}
		return count;
	}
	
	public static List<String> combinations(String input)
	{
		List<Sequence<Character>> combs = SequenceUtils.combinations(Sequence.make(input));
		List<String> out = new ArrayList<String>();
		for(Sequence<Character> chars : combs)
		{
			out.add(SequenceUtils.toString(chars,true));
		}
		return out;
	}
	
	public static String repeat(String input, int times)
	{
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<times; i++)
		{
			sb.append(input);
		}
		return sb.toString();
	}
	
	public static String capitalise(String input)
	{
		if(input==null || input.length()==0)
		{
			return input;
		}
		return input.substring(0,1).toUpperCase()+input.substring(1);
	}
	
	public static String unCapitalise(String input)
	{
		if(input==null || input.length()==0)
		{
			return input;
		}
		return input.substring(0,1).toLowerCase()+input.substring(1);
	}
	
	public static <T> T toType(Class<T> type, String value)
	{
		if(type.equals(String.class))
		{
			return (T)value;
		}
		else if(type.equals(Boolean.class) || type.equals(Boolean.TYPE))
		{
			return (T)new Boolean(value);
		}
		else if(type.equals(Byte.class) || type.equals(Byte.TYPE))
		{
			try{
				Byte b = Byte.parseByte(value);
				return (T)b;
			}
			catch(NumberFormatException e){
				return (T)Byte.valueOf((byte)0);
			}
		}
		else if(type.equals(Short.class) || type.equals(Short.TYPE))
		{
			try{
				Short s = Short.parseShort(value);
				return (T)s;
			}
			catch(NumberFormatException e){
				return (T)Short.valueOf((short)0);
			}
		}		
		else if(type.equals(Integer.class) || type.equals(Integer.TYPE))
		{
			try{
				Integer i = Integer.parseInt(value);
				return (T)i;
			}
			catch(NumberFormatException e){
				return (T)Integer.valueOf(0);
			}
		}
		else if(type.equals(Long.class) || type.equals(Long.TYPE))
		{
			try{
				Long l = Long.parseLong(value);
				return (T)l;				
			}
			catch(NumberFormatException e){
				return (T)Long.valueOf(0L);
			}
		}
		else if(type.equals(Float.class) || type.equals(Float.TYPE))
		{
			try{
				Float f = Float.parseFloat(value);
				return (T)f;
			}
			catch(NumberFormatException e){
				return (T)Float.valueOf(0f);
			}
		}
		else if(type.equals(Double.class) || type.equals(Double.TYPE))
		{
			try{
				Double d = Double.parseDouble(value);
				return (T)d;
			}
			catch(NumberFormatException e){
				return (T)Double.valueOf(0.0);
			}
		}
		else if(type.equals(Character.class) || type.equals(Character.TYPE))
		{
			return (T)(value.length()>0 ? Character.valueOf(value.charAt(0)) : Character.valueOf((char)0));
		}
		else
		{
			return null;
		}
	}
	
	private static Pattern templatePattern = Pattern.compile("(?<!\\\\)\\{(\\d{1,3})\\}");
	
	public static String template(String template, Object... params)
	{
		StringBuffer sb = new StringBuffer();
		Matcher m = templatePattern.matcher(template);
		while(m.find())
		{
			int n = Integer.parseInt(m.group(1));
			String replacement = "";
			if(n>=0 && n<params.length)
			{
				replacement = String.valueOf(params[n]);
			}
			m.appendReplacement(sb,replacement);
		}
		m.appendTail(sb);
		return sb.toString().replaceAll("\\\\\\{", "{");
	}
	
	public static String sqEscape(String input)
	{
		input = input
		.replaceAll("\\\\", "\\\\\\\\")
		.replaceAll("'", "\\\\'")
		.replaceAll("\n", "\\\\n")
		.replaceAll("\r", "\\\\r")
		.replaceAll("\t", "\\\\t");
		return input;
	}
	
	public static String dqEscape(String input)
	{
		input = input
		.replaceAll("\\\\", "\\\\\\\\")
		.replaceAll("\"", "\\\\\"")
		.replaceAll("\n", "\\\\n")
		.replaceAll("\r", "\\\\r")
		.replaceAll("\t", "\\\\t");
		return input;
	}
}
