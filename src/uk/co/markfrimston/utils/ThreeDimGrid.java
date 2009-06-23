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

public class ThreeDimGrid<X,Y,Z,V> implements Cloneable
{
	private List<X> xLegend = new ArrayList<X>();
	private List<Y> yLegend = new ArrayList<Y>();
	private List<Z> zLegend = new ArrayList<Z>();
	
	private HashMap<X,HashMap<Y,HashMap<Z,V>>> data = new HashMap<X,HashMap<Y,HashMap<Z,V>>>();
	
	public void addXType(X xType)
	{
		if(!xLegend.contains(xType))
		{
			xLegend.add(xType);
			//x hash must have new y hash added to it, under new x key
			HashMap<Y,HashMap<Z,V>> newHash = new HashMap<Y,HashMap<Z,V>>();
			for(Y yType : yLegend)
			{
				newHash.put(yType, new HashMap<Z,V>());
			}
			data.put(xType,newHash);
		}
	}
	
	public void addYType(Y yType)
	{
		if(!yLegend.contains(yType))
		{
			yLegend.add(yType);
			//each y hash must have new z hash added to it, under new y key
			for(X xType : xLegend)
			{
				data.get(xType).put(yType, new HashMap<Z,V>());
			}
		}
	}
	
	public void addZType(Z zType)
	{
		if(!zLegend.contains(zType))
		{
			zLegend.add(zType);			
		}
	}
	
	public List<X> getXLegend()
	{
		return Collections.unmodifiableList(xLegend);
	}
	
	public List<Y> getYLegend()
	{
		return Collections.unmodifiableList(yLegend);
	}
	
	public List<Z> getZLegend()
	{
		return Collections.unmodifiableList(zLegend);
	}
	
	public void add(X xType, Y yType, Z zType, V value)
	{
		addXType(xType);
		addYType(yType);
		addZType(zType);
		
		data.get(xType).get(yType).put(zType, value);
	}
	
	public V get(X xType, Y yType, Z zType)
	{
		if(xLegend.contains(xType))
		{
			if(yLegend.contains(yType))
			{
				return data.get(xType).get(yType).get(zType);
			}
			else
			{
				return null;
			}
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
		zLegend.clear();
		data.clear();
	}
	
	public Object clone()
	{
		ThreeDimGrid<X,Y,Z,V> obj = new ThreeDimGrid<X,Y,Z,V>();
		for(X xType : xLegend)
		{
			for(Y yType : yLegend)
			{
				for(Z zType : zLegend)
				{
					obj.add(xType, yType, zType, get(xType,yType,zType));
				}
			}
		}
		return obj;
	}
	
	public boolean isEmpty()
	{
		return data.isEmpty();
	}
}
