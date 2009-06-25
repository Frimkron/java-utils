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
import java.net.*;

public class FileUtils 
{
	public static String getFilenameWithoutExtension(String filePath)
	{
		if(filePath==null){
			return null;
		}
		String[] parts = filePath.split(RegexUtils.escape(File.separator));
		String filename = parts[parts.length-1];
		int pos = filename.lastIndexOf('.');
		if(pos == -1)
		{
			return filename;
		}
		else
		{
			return filename.substring(0,pos);
		}
	}
	
	public static String getFilenameExtension(String filePath)
	{
		if(filePath==null){
			return null;
		}
		String[] parts = filePath.split(RegexUtils.escape(File.separator));
		String filename = parts[parts.length-1];
		int pos = filename.lastIndexOf('.');
		if(pos == -1)
		{
			return "";
		}
		else
		{
			return filename.substring(pos);
		}
	}
	
	public static String getPathWithoutFilename(String filePath)
	{
		if(filePath==null){
			return null;
		}
		String[] parts = filePath.split(RegexUtils.escape(File.separator));
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<parts.length-1; i++)
		{
			sb.append(parts[i]).append(File.separator);
		}
		return sb.toString();
	}
	
	public static String getFilenameWithoutPath(String filePath)
	{
		if(filePath==null){
			return null;
		}
		String[] parts = filePath.split(RegexUtils.escape(File.separator));
		if(parts.length > 1)
		{
			return parts[parts.length-1];
		}
		else
		{
			return "";
		}
	}
	
	public static String slurpFile(String fileName)
		throws IOException
	{
		return slurpFile(new File(fileName));
	}
	
	public static String slurpFile(File file)
		throws IOException
	{
		FileInputStream stream = new FileInputStream(file);
		return slurpFile(stream);
	}
	
	public static String slurpResource(Class<?> classInPackage, String name)
		throws IOException
	{
		return slurpFile(classInPackage.getResourceAsStream(name));
	}
	
	public static String slurpResourceRuntime(Class<?> classInPackage, String name)
	{
		try
		{
			return slurpFile(classInPackage.getResourceAsStream(name));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static String slurpFile(InputStream stream)
		throws IOException
	{
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(stream));
			String line = null;
			while((line = reader.readLine()) != null)
			{
				sb.append(line+"\n");
			}
		}
		finally
		{
			try{
				reader.close();
			}catch(Exception e){}
		}
		return sb.toString();
	}
	
	public static URL fileToUrl(File file)
		throws MalformedURLException, IOException
	{
		return new URL("file:///"+file.getCanonicalPath().replaceAll("\\\\","/"));
	}
	
	public static String fileToUrl(String file)
		throws MalformedURLException, IOException
	{
		return fileToUrl(new File(file)).toString();
	}
}
