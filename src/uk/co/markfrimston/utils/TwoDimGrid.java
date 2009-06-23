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

public class TwoDimGrid<X,Y,V> implements Cloneable
{
	private List<X> xLegend = new ArrayList<X>();
	private List<Y> yLegend = new ArrayList<Y>();
	private HashMap<X,HashMap<Y,V>> data = new HashMap<X,HashMap<Y,V>>();
	
	public void addXType(X xType)
	{
		if(!xLegend.contains(xType))
		{
			xLegend.add(xType);
			data.put(xType, new HashMap<Y,V>());
		}
	}
	
	public void addYType(Y yType)
	{
		if(!yLegend.contains(yType))
		{
			yLegend.add(yType);
		}
	}
	
	public void add(X xType,Y yType,V value)
	{
		addXType(xType);
		addYType(yType);
		data.get(xType).put(yType, value);
	}
	
	public List<X> getXLegend()
	{
		return Collections.unmodifiableList(xLegend);
	}
	
	public List<Y> getYLegend()
	{
		return Collections.unmodifiableList(yLegend);
	}
	
	public V get(X xType, Y yType)
	{
		if(xLegend.contains(xType))
		{
			return data.get(xType).get(yType);
		}
		else
		{
			return null;
		}
	}
	
	public void clear()
	{
		xLegend.clear();
		yLegend.clear();
		data.clear();
	}
	
	public Object clone()
	{
		TwoDimGrid<X,Y,V> obj = new TwoDimGrid<X,Y,V>();
		for(X xType : xLegend)
		{
			for(Y yType : yLegend)
			{
				obj.add(xType, yType, get(xType,yType));
			}
		}		
		return obj;
	}
	
	public boolean isEmpty()
	{
		return data.isEmpty();
	}
}
