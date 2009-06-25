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
import java.lang.reflect.*;

public class SequenceUtils
{
	public static <T> int distance(Sequence<T> strA, Sequence<T> strB, int max)
	{
		int lastRowStart = 0;
		List<Integer> lastRow = new ArrayList<Integer>();
		for(int j=0; j<=strB.size(); j++)
		{
			List<Integer> row = new ArrayList<Integer>();
			int rowStart = -1;
			for(int i=lastRowStart; i<=strA.size(); i++)
			{
				List<Integer> options = new ArrayList<Integer>();
				//down (insertion)
				if(j>0 && i<lastRowStart+lastRow.size())
				{					
					options.add(lastRow.get(i-lastRowStart)+1);
				}
				//right (deletion)
				if(i>0 && rowStart!=-1)
				{
					options.add(row.get(i-rowStart-1)+1);
				}
				//diagonal (substitution)
				if(j>0 && i>0 && i-1>=lastRowStart && i-1<lastRowStart+lastRow.size())
				{
					options.add(lastRow.get(i-1-lastRowStart) 
							+ (strA.get(i-1).equals(strB.get(j-1))?0:1));
				}
				int cost = 0;
				if(options.size()>0)
				{
					cost = ListUtils.getBest(options, ListUtils.COMPARE_MIN_INT);
				}
				else
				{
					if(i>0 && j>0){
						//no options means costs are now too high - stop row
						break;
					}
				}
				
				if(cost <= max)
				{
					row.add(cost);
					//if first, record row start
					if(rowStart==-1){
						rowStart = i;
					}
				}
				else
				{
					//into too high costs - stop calculating row
					if(rowStart!=-1){
						break;
					}
				}
			}
			lastRow = row;
			lastRowStart = rowStart;
			//check that a cost lower than threshold was actually found
			if(rowStart==-1)
			{
				//no distance found below threshold
				return -1;
			}
		}
		if(lastRow.size() > strA.size()-lastRowStart)
		{
			return lastRow.get(strA.size()-lastRowStart);
		}
		else
		{
			//the last row didn't reach all the way to result in the corner
			//possibly because stringB was empty.
			return -1;
		}
	}
	
	public static <I> List<Sequence<I>> combinations(Sequence<I> input)
	{
		List<Sequence<I>> out = new ArrayList<Sequence<I>>();
		
		if(input.size()<=1)
		{			
			out.add(input);
		}
		else
		{
			for(int i=0; i<input.size(); i++)
			{
				List<I> hat = new ArrayList<I>();
				for(int j=0; j<input.size(); j++)
				{
					hat.add(input.get(j));
				}
				hat.remove(i);
				
				List<Sequence<I>> combs = combinations(Sequence.make(hat));
				for(Sequence<I> comb : combs)
				{
					comb = input.subSequence(i,i+1).append(comb);
					if(!out.contains(comb))
					{
						out.add(comb);
					}
				}
			}
		}
		
		return out;
	}
	
	public static String toString(Sequence<Character> seq, boolean dummy)
	{
		StringBuffer sb = new StringBuffer();
		for(Character c : seq)
		{
			sb.append(c);
		}
		return sb.toString();
	}
	
	public static String toString(Sequence<Object> seq)
	{
		StringBuffer sb = new StringBuffer();
		for(Object o : seq)
		{
			sb.append(o);
		}
		return sb.toString();
	}
	
	public static <T> List<T> toList(Sequence<T> seq)
	{
		List<T> list = new ArrayList<T>();
		for(T item : seq)
		{
			list.add(item);
		}
		return list;
	}
	
	private static ListUtils.Comparer<Sequence<?>> COMPARE_LONGEST_SEQUENCE
		= new ListUtils.Comparer<Sequence<?>>()
	{
		public boolean betterThan(Sequence<?> a, Sequence<?> b)
		{
			return (a.size() > b.size());
		}		
	};

	private static ListUtils.Comparer<Sequence<?>> COMPARE_SHORTEST_SEQUENCE
		= new ListUtils.Comparer<Sequence<?>>()
	{
		public boolean betterThan(Sequence<?> a, Sequence<?> b)
		{
			return (a.size() < b.size());
		}
	};

	public static <T> Sequence<T> longest(Sequence<T>... sequences)
	{
		return (Sequence<T>)ListUtils.getBest(sequences, COMPARE_LONGEST_SEQUENCE);
	}
	
	public static <T> Sequence<T> shortest(Sequence<T>... sequences)
	{
		return (Sequence<T>)ListUtils.getBest(sequences, COMPARE_SHORTEST_SEQUENCE);
	}
}
