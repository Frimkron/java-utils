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

import java.io.*;
import java.util.*;

public class TreeUtils
{
	public static <T> String writeTreeToString(Tree<T> tree)
	{
		StringWriter sw = new StringWriter();
		try
		{
			writeTree(tree, sw);
		}
		catch(IOException e)
		{}
		return sw.toString();
	}
	
	public static <T> void writeTree(Tree<T> tree, Writer writer)
		throws IOException
	{
		writeTreeBranch(tree, writer, new ArrayList<Boolean>(), true);
	}
	
	private static <T> void writeTreeBranch(Tree<T> tree, Writer writer, 
			List<Boolean> lines, boolean lastBranch)
		throws IOException
	{
		for(boolean line : lines)
		{
			if(line){
				writer.write("  |  ");
			}else{
				writer.write("     ");
			}
		}
		if(lastBranch){
			writer.write("  '-- ");
		}else{
			writer.write("  |-- ");
		}
		writer.write(String.valueOf(tree.getNodeData()));
		writer.write("\n");
		Iterator<Tree<T>> branchIt = tree.iterator();
		while(branchIt.hasNext())
		{
			Tree<T> branch = branchIt.next();
			List<Boolean> newLines = new ArrayList<Boolean>();
			newLines.addAll(lines);
			newLines.add(!lastBranch);
			writeTreeBranch(branch, writer, newLines, !branchIt.hasNext());
		}
	}
	
	public static <T> List<List<T>> getAllPaths(Tree<T> tree)
	{
		return getAllPaths(tree, new ArrayList<T>());
	}
	
	private static <T> List<List<T>> getAllPaths(Tree<T> tree, List<T> startingWith)
	{
		List<List<T>> result = new ArrayList<List<T>>();
		
		List<T> newStartingWith = new ArrayList<T>();
		newStartingWith.addAll(startingWith);
		newStartingWith.add(tree.getNodeData());
		
		Iterator<Tree<T>> it = tree.iterator();
		if(it.hasNext())
		{
			while(it.hasNext())
			{
				Tree<T> branch = it.next();				
				result.addAll(getAllPaths(branch, newStartingWith));
			}
		}
		else
		{
			result.add(newStartingWith);
		}
		
		return result;
	}
}
