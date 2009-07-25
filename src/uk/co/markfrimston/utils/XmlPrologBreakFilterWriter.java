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

/*
	FilterWriter which inserts a linebreak after the first '>' written. Can be 
	used to insert a break in the prolog of an xml document (non-standard) after 
	the <?xml ?> processing instruction, to place the root element on the next 
	line.
*/
public class XmlPrologBreakFilterWriter extends FilterWriter 
{
	protected boolean found = false;
	
	public XmlPrologBreakFilterWriter(Writer arg0) 
	{
		super(arg0);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException 
	{
		if(!found)
		{
			for(int i=off; i<off+len; i++)
			{								
				write(cbuf[i]);				
			}
		}
		else
		{
			super.write(cbuf,off,len);
		}
	}

	@Override
	public void write(String str, int off, int len) throws IOException 
	{
		if(!found)
		{
			for(int i=off; i<off+len; i++)
			{
				write(str.charAt(i));
			}
		}
		else
		{
			super.write(str,off,len);
		}
	}

	@Override
	public void write(int c) throws IOException 
	{
		super.write(c);
		if(!found && c=='>')
		{
			write('\n');
			found = true;
		}
	}
}
