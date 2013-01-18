package ca.ubc.javis;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.text.html.HTML.Attribute;
import javax.xml.xpath.XPathExpressionException;

import org.custommonkey.xmlunit.Difference;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.crawljax.util.Helper;
import com.sun.xml.internal.fastinfoset.util.CharArray;

public class Difference_V3 {

		public static Document firstDoc;
		public static Document secondDoc;
		public static String[] finalDifferences;
		public static int differenceCounter = 0;
		public static Element elem;
		
		@Test
		public void getDifferences(){
			String before = "<html><body><div>some</div></body></html>";
			String after = "<html><body><div><div></div><div><a href=\"http\">my my</a></div>some text</div><div>hello</div><img id=\"1\"></body>";
			String openTag = "",Tag = "",dString,elementName,closeTag = "",result,anotherSimilarElement;
			int firstLocation = 0,secondLocation = 0,counter,j1;
			String tempResult;
			String att,path = "";
			Boolean flag = false;
			try {
				after = after.toLowerCase();
				if(!after.contains("</body>"))
					after = after.concat("</body>");
				if(!after.contains("</html>"))
					after = after.concat("</html>");
				firstDoc = Helper.getDocument(before);
				secondDoc = Helper.getDocument(after);
				List<Difference> differences = Helper.getDifferences(before, after);
				finalDifferences = new String[differences.size()];
				for(Difference d: differences){
					dString = d.toString().toLowerCase();
					if(d.getTestNodeDetail().getNode().getNodeName().equalsIgnoreCase("body"))
							continue;
					if(d.getDescription().equals("attribute value")||d.getDescription().equals("attribute name")||d.getDescription().equals("number of element attributes")||d.getDescription().equals("presence of child node")||d.getDescription().equals("sequence of attributes")||d.getDescription().equals("number of child nodes")){
						if(d.getTestNodeDetail().getXpathLocation().contains("@")){
							int ind1 = d.getTestNodeDetail().getXpathLocation().indexOf("@");
							path = d.getTestNodeDetail().getXpathLocation().substring(0, ind1);
							}
						else
							path = d.getTestNodeDetail().getXpathLocation();
						elem = getElement(path,after, secondDoc);
						att = getElementAttributes(elem, new ArrayList <String>());
						flag = checkElement(elem.getNodeName());
						if(flag){
							if(att.equals("")){
								Tag = "<"+elem.getNodeName().toLowerCase()+">";
							}
							else{
								Tag = "<"+elem.getNodeName().toLowerCase()+" "+att+">";
							}
						}
						else{
							if(att.equals("")){
								openTag = "<"+elem.getNodeName().toLowerCase()+">";	
							}
							else
								openTag = "<"+elem.getNodeName().toLowerCase()+" "+att+">";
							
							closeTag = "</"+elem.getNodeName().toLowerCase()+">";
						}}

					else if(d.getDescription().equals("text value")||d.getDescription().equals("elemen tag name")||d.getTestNodeDetail().getXpathLocation().contains("text")){
						if(d.getTestNodeDetail().getXpathLocation().contains("text")){
							int ind1 = d.getTestNodeDetail().getXpathLocation().indexOf("text");
							path = d.getTestNodeDetail().getXpathLocation().substring(0, ind1);
						}
						elem = getElement(path,after, secondDoc);
						att = getElementAttributes(elem, new ArrayList <String>());
						String text = d.getTestNodeDetail().getValue();
						if(att.equals("")){
							openTag = "<"+elem.getNodeName().toLowerCase()+">";
						}
						else{
							openTag = "<"+elem.getNodeName().toLowerCase()+" "+att+">";
						}
						closeTag = "</"+elem.getNodeName().toLowerCase()+">";
					
						}
					else
						continue;
					
					int firstElementIndex = path.lastIndexOf("[");
					int lastElementIndex = path.lastIndexOf("]");
					String elementIndexer = path.substring(firstElementIndex+1, lastElementIndex);
					
					
					
					elementName = elem.getNodeName().toLowerCase();
					anotherSimilarElement = "<"+elementName;
									
					counter = getSpecificElement(anotherSimilarElement, after);
					int[] openTagLocation = new int[counter];
					int[] closeTagLocation = new int[counter];
					int firstLocation1 = 0;
					
					
					for(int i=0;i<counter;i++){
						if(i==0){
							firstLocation1 = after.indexOf(anotherSimilarElement);
							openTagLocation[i] = firstLocation1;
						}
						else {
							
							secondLocation = after.indexOf(anotherSimilarElement, firstLocation1+closeTag.length());
							openTagLocation[i] = secondLocation;
							firstLocation1 = secondLocation;
						}
					}
					
					for(int z=0;z<counter;z++){
						if(z == Integer.parseInt(elementIndexer)-1){
							if(!flag){
								firstLocation = after.indexOf(openTag, openTagLocation[z]);	
							}
							else{
								firstLocation = after.indexOf(Tag,openTagLocation[z]);
								result = after.substring(firstLocation,Tag.length()+firstLocation);
								checkDifference(result);
								continue;
								}
						}
							
					}
					int lastIndexofElement = after.lastIndexOf(closeTag);
					firstLocation1 = 0;
					
					for(int i=0;i<counter;i++){
						if(i==0){
							if(flag){
								firstLocation1 = after.indexOf(Tag,firstLocation);
							}
							else{
								firstLocation1 = after.indexOf(closeTag,firstLocation);
							}
							closeTagLocation[i] = firstLocation1;
						}
						else {
							if(flag){
								secondLocation = after.indexOf(Tag, firstLocation1+closeTag.length());
							}
							else
							{
								secondLocation = after.indexOf(closeTag, firstLocation1+closeTag.length());	
							}
							closeTagLocation[i] = secondLocation;
							firstLocation1 = secondLocation;
						}
						if(closeTagLocation[i]==lastIndexofElement){
							for(int p = i+1;p<closeTagLocation.length;p++){
								closeTagLocation[p]=0;
							}
							break;
						}
					}	
					
				
					
					for(int n1 = 0; n1<closeTagLocation.length;n1++){
						if(n1==0){
							if(!flag)
								tempResult = after.substring(firstLocation, closeTagLocation[n1]+closeTag.length());
							else
								tempResult = after.substring(firstLocation, closeTagLocation[n1]);
							j1 = getSpecificElement(anotherSimilarElement, tempResult);
							if(j1 ==1){
								result = tempResult;
								checkDifference(result);
								break;
							}
							else
								continue;
						}
						else{
							if(!flag)
								tempResult = after.substring(closeTagLocation[n1-1], closeTagLocation[n1]+closeTag.length());
							else
								tempResult = after.substring(closeTagLocation[n1-1], closeTagLocation[n1]);
							j1 = getSpecificElement(anotherSimilarElement, tempResult);
							if(j1 ==0){
								result = after.substring(firstLocation, closeTagLocation[n1]+closeTag.length());
								checkDifference(result);
								break;
							}
							else
								continue;
						}
					}
			
				}
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

		public String getPath(String path, String after, Document doc){
			int lastIndex;
			if(path.contains("TEXT")){
				lastIndex = path.lastIndexOf("/");
				path = path.substring(1, lastIndex);
			}
			else{
				lastIndex = path.lastIndexOf("]");
				path = path.substring(1, lastIndex+1);
			}
			String elementName;
			
			  Element element = null;
				try {
					element = Helper.getElementByXpath(doc, path);
				} catch (XPathExpressionException e) {
					
					e.printStackTrace();
				}
				elementName = element.getNodeName();
			return elementName.toLowerCase();
		}
		
		public Element getElement(String path, String after, Document doc){
			int lastIndex;
			if(path.contains("TEXT")){
				lastIndex = path.lastIndexOf("/");
				path = path.substring(1, lastIndex);
			}
			else{
				lastIndex = path.lastIndexOf("]");
				path = path.substring(1, lastIndex+1);
			}
			
			
			  Element element = null;
				try {
					element = Helper.getElementByXpath(doc, path);
				} catch (XPathExpressionException e) {
					
					e.printStackTrace();
				}
			
			return element;
		}
		
		public int getSpecificElement(String openTag,String after){
			int firstLoc = 0, secondLoc;
			int sizeCounter=0,counter=0,j=0,i; 
			char[] newAfter = after.toLowerCase().toCharArray();
			for(i=0;i<after.length();i++){
				char ch = after.charAt(i);
				if(ch == '<')
					sizeCounter++;
			}
			String[] elements = new String[sizeCounter];
			for(i=0;i<newAfter.length;i++){
				if(newAfter[i]=='<'){
					firstLoc = i;
					continue;
				}
				else if(newAfter[i]=='>'){
					secondLoc = i;
					elements[j] = after.substring(firstLoc, secondLoc+1);
					j++;
				}
			}
			for(i=0; i<elements.length;i++){
				if(elements[i].contains(openTag)){
					counter++;
					continue;
				}
				else if(elements[i].equals(null))
					break;
			}	
			return counter;
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
					}
				}
			}
			
			String finalResult =  buffer.toString().trim();
			finalResult = finalResult.replaceAll("=", "=\"");
			finalResult = finalResult.replaceAll(" ", "\" ");
			finalResult = finalResult.concat("\"");
			
			if(finalResult.equals("\""))
				return finalResult="";
			return finalResult;
		}
		
		public void checkDifference(String str){
			Boolean flag = true;
			if(str.startsWith("html")){
				str = str.substring(0, str.length()-7);
			
			}
			
			if(str.startsWith("body")){
				str = str.substring(0, str.length()-7);
				if(str.equals("")||str.equals(null)){
					flag = false;
					str = "";
				}
			}
			
			int i;
			if(flag){
			if(differenceCounter==0){
				finalDifferences[0]=str;
				System.out.println(finalDifferences[0]);
				differenceCounter++;
			}
			else {
				for(i=0;i<finalDifferences.length;i++){
					if(finalDifferences[i].contains(str))
						break;
					else {
						finalDifferences[differenceCounter]=str;
						System.out.println(finalDifferences[differenceCounter]);
						differenceCounter++;
						break;
					}
				}
			}
		}
		}
	}

