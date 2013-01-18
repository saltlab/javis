package ca.ubc.javis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import junit.framework.Assert;

import org.custommonkey.xmlunit.Difference;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.crawljax.util.DOMComparer;
import com.crawljax.util.Helper;



public class GetDifferenceTest {
	
	public static Document firstDoc;
	public static Document secondDoc;
	public static String[] finalDifferences;
	public static int differenceCounter = 0;
	
	@Test
	public void testGetDifference(){
		
		String before = "<html><body><div>some</div></body></html>";
		String after = "<html><head><meta></head><body><div><!--This is a comment--><a href=\"http\" id=\"1\"><div id=\"2\"><div id =\"3\">hello</div></div>my my</a></div><div class = \"obj\"></div><img class=\"im1\"></div></body>";
		String path= "";
		
		
		try {
			firstDoc = Helper.getDocument(before);
			secondDoc = Helper.getDocument(after);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String thisNode="";
		 
			List<Difference> differences = Helper.getDifferences(before, after);
			finalDifferences = new String[differences.size()];
			
			for(Difference d: differences){
				
				if(d.getDescription().equals("attribute value")||d.getDescription().equals("attribute name")||d.getDescription().equals("number of element attributes")||d.getDescription().equals("presence of child node")||d.getDescription().equals("sequence of attributes")||d.getDescription().equals("number of child nodes")
						||d.getDescription().equals("text value")||d.getDescription().equals("elemen tag name")||d.getTestNodeDetail().getXpathLocation().contains("text")){
					path = d.getTestNodeDetail().getXpathLocation();
					if(path.contains("@")){
						int ind1 = d.getTestNodeDetail().getXpathLocation().indexOf("@");
						path = path.substring(0, ind1);
						}
					if(path.contains("text")){
						int ind1 = d.getTestNodeDetail().getXpathLocation().indexOf("text");
						path = path.substring(0, ind1);
					}
					if(path.endsWith("/HTML[1]/")&& path.startsWith("/HTML[1]/"))
						continue;
				
				Element elem = getElement(path,after, secondDoc);
				if(elem.getNodeName().equalsIgnoreCase("style")||elem.getNodeName().equalsIgnoreCase("head")||elem.getNodeName().equalsIgnoreCase("script")||elem.getNodeName().equalsIgnoreCase("body")||elem.getNodeName().equalsIgnoreCase("html"))
					continue;
				thisNode=getDetails(d,(Node)elem, secondDoc);
				checkDifference(thisNode);
			}}
				
				
				
			
			}

	
	public String getDetails(Difference d, Node n,Document secondDoc) {
		String str = " ",attributeString = " ",children = " ";
		Boolean flag = false;
		
		if(flag){}
		if(n.hasAttributes()){
			attributeString = getElementAttributes((Element)n, new ArrayList<String>());
		}
		
		if(n.hasChildNodes()){
			children = getChildren((Element)n,new ArrayList<String>());
		}
		if(attributeString!=" " && children != " ")
			str = "<"+n.getNodeName().toLowerCase()+" "+attributeString+">"+children;
		else{
			if(attributeString!=" " && children == " ")
				str = "<"+n.getNodeName().toLowerCase()+" "+attributeString+">";
			if(attributeString ==" " && children != " ")
				str = "<"+n.getNodeName().toLowerCase()+">"+children;
			if(attributeString == " " && children == " ")
				str = "<"+n.getNodeName().toLowerCase()+">";
		}
		if(checkElement(n.getNodeName()))
			return str;
		else
			return str = str.concat("</"+n.getNodeName().toLowerCase()+">");
	}
		
	public String getElementAttributes(Element element, List<String> exclude) {
		StringBuffer buffer = new StringBuffer();
		if (element != null) {
			NamedNodeMap attributes = element.getAttributes();
			if (attributes != null) {
				for (int i = attributes.getLength()-1; i >= 0; i--) {
					Attr attr = (Attr) attributes.item(i);
					if (!exclude.contains(attr.getNodeName())) {
						buffer.append(attr.getNodeName() + "=");
						buffer.append(attr.getNodeValue() + " ");
					}
				}}}
			if(buffer.equals(null)){
				buffer.append("");
			}
			String str = buffer.toString().trim();;
			return str;
			}
		
		public String getChildren(Element element, List<String> exclude) {
			StringBuffer buffer = new StringBuffer();
			String str = " ",finalStr;
			Boolean flag = false, oneTag = false;;
			if (element != null) {
				NodeList children = element.getChildNodes();
				if (children != null) {
					for(Node childNode=element.getFirstChild();
				    childNode!=null; childNode=childNode.getNextSibling()){
						if(childNode.getNodeName().equals("#text"))
							continue;
						if(checkElement(childNode.getNodeName())){
							oneTag = true;
						}
						if(childNode.hasChildNodes()){
							str = getChildren((Element)childNode, new ArrayList<String>());
							flag = true;
						}
						if(flag){
							if(oneTag)
								buffer.append("<"+childNode.getNodeName().toLowerCase()+" "+getElementAttributes((Element)childNode, new ArrayList<String>())+">"+str);
							else								
								buffer.append("<"+childNode.getNodeName().toLowerCase()+" "+getElementAttributes((Element)childNode, new ArrayList<String>())+">"+childNode.getTextContent()+"</"+childNode.getNodeName().toLowerCase()+">"+str);
						}
						else{
							if(oneTag)
								buffer.append("<"+childNode.getNodeName().toLowerCase()+" "+getElementAttributes((Element)childNode, new ArrayList<String>())+">");
							else
								buffer.append("<"+childNode.getNodeName().toLowerCase()+" "+getElementAttributes((Element)childNode, new ArrayList<String>())+">"+childNode.getTextContent()+"</"+childNode.getNodeName().toLowerCase()+">");
						}}
				}}if(buffer.equals(null)){
					buffer.append(" ");
				}
				
			
			 finalStr = buffer.toString().trim();;
				return finalStr;
			}
			
			public Element getElement(String path, String after, Document doc){
				int lastIndex;
				//	if(path.contains("@content"))
				//		path = path;
					 if(path.contains("TEXT")||path.contains("@")){
						lastIndex = path.lastIndexOf("/");
						path = path.substring(1, lastIndex);
					}
					if(path.contains("//"))
						path = path.replaceAll("//", "/");
					
					if(path.endsWith("/"))
						path = path.substring(0, path.length()-1);
					  Element element = null;
						try {
							element = Helper.getElementByXpath(doc, path);
						} catch (XPathExpressionException e) {
							
							e.printStackTrace();
						}
					
					return element;
			}
			public void checkDifference(String str){
				Boolean flag = false;
				
				if(differenceCounter==0){
					finalDifferences[0]=str;
					System.out.println(finalDifferences[0]);
					differenceCounter++;
				}
				else {
					for(int i=0;i<differenceCounter;i++){
						if(finalDifferences[i].contains(str)){
							flag = true;
							break;
						}}
					if(!flag){
							finalDifferences[differenceCounter]=str;
							System.out.println(finalDifferences[differenceCounter]);
							differenceCounter++;
							
						}
					}
				}
					
			public Boolean checkElement(String nodeName) {
				Boolean flag = false;
				nodeName = nodeName.toLowerCase();
				if(nodeName.equalsIgnoreCase("area")||nodeName.equalsIgnoreCase("base")||
						nodeName.equalsIgnoreCase("br")||nodeName.equalsIgnoreCase("col")||
						nodeName.equalsIgnoreCase("embed")||nodeName.equalsIgnoreCase("frame")||
						nodeName.equalsIgnoreCase("hr")||nodeName.equalsIgnoreCase("img")||
						nodeName.equalsIgnoreCase("input")||nodeName.equalsIgnoreCase("keygen")||
						nodeName.equalsIgnoreCase("link")||nodeName.equalsIgnoreCase("meta")||
						nodeName.equalsIgnoreCase("param")||nodeName.equalsIgnoreCase("source")||
						nodeName.equalsIgnoreCase("track"))
					flag = true;
				return flag;
			}
			}
			
		