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

import org.w3c.dom.*;

public class ChildNodeHandler
{
	private String handledNodeName = "";
	private short handledNodeType = Node.ELEMENT_NODE;
	private ChildNodeHandler[] childHandlers;
	
	public ChildNodeHandler(String handledNodeName)
	{
		this.handledNodeName = handledNodeName;
	}
	
	public ChildNodeHandler(String handledNodeName, ChildNodeHandler[] childHandlers)
	{
		this.handledNodeName = handledNodeName;
		this.childHandlers = childHandlers;
	}
	
	public ChildNodeHandler(short handledNodeType, String handledNodeName, 
			ChildNodeHandler[] childHandlers)
	{
		this.handledNodeType = handledNodeType;
		this.handledNodeName = handledNodeName;
		this.childHandlers = childHandlers;
	}
	
	public void handleChildNode(Node node)
		throws ChildNodeException
	{
		if(childHandlers!=null)
		{
			XmlUtils.handleChildNodes(node, childHandlers);
		}
	}
	
	public short handledNodeType()
	{
		return handledNodeType;
	}
	
	public String handledNodeName()
	{
		return handledNodeName;
	}
}
