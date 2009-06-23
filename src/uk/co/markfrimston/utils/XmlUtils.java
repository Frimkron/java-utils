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
import java.util.*;
import java.io.*;
import javax.xml.parsers.*;
import org.xml.sax.*;

public class XmlUtils
{
	private static DocumentBuilderFactory builderFact = DocumentBuilderFactory.newInstance();
	
	public static void handleChildNodes(Node node, ChildNodeHandler[] handlers)
		throws ChildNodeException
	{
		if(node.hasChildNodes())
		{
			HashMap<Pair<Short,String>,ChildNodeHandler> handleHash = 
				new HashMap<Pair<Short,String>,ChildNodeHandler>();
			for(ChildNodeHandler handler : handlers)
			{
				handleHash.put(new Pair<Short,String>(handler.handledNodeType(),
						handler.handledNodeName()),handler);
			}
			Node child = node.getFirstChild();
			while(child!=null)
			{
				Pair<Short,String> type = new Pair<Short,String>(
						child.getNodeType(), child.getNodeName());
				ChildNodeHandler handler = handleHash.get(type);
				if(handler!=null)
				{
					handler.handleChildNode(child);
				}
				child = child.getNextSibling();
			}
		}
	}
	
	public static String textContent(Node node)
	{
		switch(node.getNodeType())
		{
			case Node.ELEMENT_NODE:
				StringBuffer sb = new StringBuffer();
				Node nextChild = node.getFirstChild();
				while(nextChild!=null)
				{
					sb.append(textContent(nextChild));
					nextChild = nextChild.getNextSibling();
				}
				return sb.toString();
			case Node.TEXT_NODE:
			case Node.CDATA_SECTION_NODE:
				return node.getNodeValue();
			default:
				return "";
		}
	}
	
	public static class DOMTreeAdapter implements Tree
	{
		private class DOMTreeAdapterIterator implements Iterator<Tree>
		{		
			private int currentAttribute = 0;
			private Node currentChild = node.getFirstChild();
			
			public boolean hasNext()
			{
				if(attributes != null && currentAttribute < attributes.getLength())
				{
					return true;
				}
				if(currentChild != null)
				{
					return true;
				}
				return false;
			}

			public Tree next()
			{		
				if(attributes != null && currentAttribute < attributes.getLength())
				{
					Tree returnThis = new DOMTreeAdapter(attributes.item(currentAttribute));
					currentAttribute++;	
					
					return returnThis;
				}
				
				if(currentChild == null){
					throw new NoSuchElementException();
				}
				
				Tree returnThis = new DOMTreeAdapter(currentChild);
				currentChild = currentChild.getNextSibling();
				
				return returnThis;
			}

			public void remove()
			{	
				throw new UnsupportedOperationException();
			}			
		}
		
		private NamedNodeMap attributes;
		private Node node;
		
		public DOMTreeAdapter(Node node)
		{
			this.node = node;
			if(node instanceof Element){
				attributes = ((Element)node).getAttributes();
			}
		}

		public Object getNodeData()
		{
			StringBuffer sb = new StringBuffer();
			
			switch(node.getNodeType())
			{
				case Node.ELEMENT_NODE:					sb.append("ELEMENT");		break;
				case Node.TEXT_NODE:					sb.append("TEXT");			break;
				case Node.CDATA_SECTION_NODE:			sb.append("CDATA");			break;
				case Node.ATTRIBUTE_NODE:				sb.append("ATTRIBUTE");		break;
				case Node.COMMENT_NODE:					sb.append("COMMENT");		break;
				case Node.DOCUMENT_FRAGMENT_NODE:		sb.append("DOC-FRAGMENT");	break;
				case Node.DOCUMENT_NODE:				sb.append("DOCUMENT");		break;
				case Node.ENTITY_NODE:					sb.append("ENTITY");		break;
				case Node.ENTITY_REFERENCE_NODE:		sb.append("ENTITY-REF");	break;
				case Node.NOTATION_NODE:				sb.append("NOTATION");		break;
				case Node.PROCESSING_INSTRUCTION_NODE:	sb.append("PROC-INST");		break;
				case Node.DOCUMENT_TYPE_NODE:			sb.append("DOC-TYPE");		break;
				default: 								sb.append("?");				break;
			}
			String value = null;
			if(node==null){
				value = "";
			}else if(node.getNodeValue()==null){
				value = "";
			}else{
				value = node.getNodeValue()
					.replaceAll("\\n", "\\\\n")
					.replaceAll("\\r", "\\\\r")
					.replaceAll("\\t", "\\\\t");
			}
			sb.append(" "+node.getNodeName()+": "+value);
			
			return sb.toString();
		}

		public Iterator<Tree> iterator()
		{
			return new DOMTreeAdapterIterator();
		}
	}
	
	public static String writeTreeToString(Node node)
	{
		StringWriter sWriter = new StringWriter();
		try
		{
			TreeUtils.writeTree(new DOMTreeAdapter(node), sWriter);
		}
		catch(IOException e)
		{}
		
		return sWriter.toString();
	}
	
	public static void printTree(Node node)
	{
		System.out.println(writeTreeToString(node));
	}
	
	public static String escape(String input)
	{
		return input
			.replaceAll("&","&amp;")
			.replaceAll("<", "&lt;")
			.replaceAll(">", "&gt;")
			.replaceAll("\"", "&quot");
	}
	
	public static Document parse(String filename)
		throws IOException, ParserConfigurationException, SAXException
	{
		File file = new File(filename);
		return parse(file);
	}
	
	public static Document parse(File file)
		throws IOException, ParserConfigurationException, SAXException
	{
		FileInputStream inputStream = new FileInputStream(file);
		try
		{
			return parse(inputStream);
		}
		finally
		{
			if(inputStream != null){
				try{
					inputStream.close();
				}catch(Exception e){}
			}
		}
	}
	
	public static Document parse(InputStream inputStream)	
		throws IOException, ParserConfigurationException, SAXException
	{
		DocumentBuilder builder = builderFact.newDocumentBuilder();
		return builder.parse(inputStream);
	}
}
