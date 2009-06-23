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

public class ListUtils
{	
	public static interface Comparer<T>
	{
		public boolean betterThan(T a, T b);
	}
	
	public static final Comparer<Integer> COMPARE_MIN_INT 
		= new Comparer<Integer>(){
		public boolean betterThan(Integer a, Integer b)
		{
			return a < b;
		}
	};
	
	public static final Comparer<Integer> COMPARE_MAX_INT
		=new Comparer<Integer>(){
		public boolean betterThan(Integer a, Integer b)
		{
			return a > b;
		}
	};
	
	public static final Comparer<Double> COMPARE_MIN_DOUBLE
		= new Comparer<Double>(){
		public boolean betterThan(Double a, Double b)
		{
			return a < b;
		}
	};
	
	public static final Comparer<Double> COMPARE_MAX_DOUBLE
		= new Comparer<Double>(){
		public boolean betterThan(Double a, Double b)
		{
			return a > b;
		}
	};
	
	public static <T> T getBest(T[] coll, Comparer<T> comp)
	{
		T best = null;
		for(T item : coll)
		{
			if(best==null || comp.betterThan(item,best))
			{
				best = item;
			}
		}
		return best;
	}
	
	public static <T> T getBest(Collection<T> coll, Comparer<T> comp)
	{
		T best = null;
		for(T item : coll)
		{
			if(best==null || comp.betterThan(item,best))
			{
				best = item;
			}
		}
		return best;
	}
	
	public static interface ItemProcessor<T>
	{
		public T process(T item);
	}
	
	public static <I, C extends Collection<I>> void process(C input, C output,
			ItemProcessor<I>... processors)
	{
		process(input, output, Arrays.asList(processors));
	}
	
	public static <I,C extends Collection<I>> void process(C input, C output,
			Collection<ItemProcessor<I>> processors)
	{
		output.clear();
		Iterator<I> it = input.iterator();
		while(it.hasNext())
		{
			I item = it.next();
			for(ItemProcessor<I> processor : processors)
			{				
				if(item != null)
				{
					item = processor.process(item);
				}
				if(item == null)
				{
					it.remove();
					break;
				}
			}
			if(item != null)
			{
				output.add(item);
			}
		}
	}
}
