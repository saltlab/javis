package ca.ubc.javis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import javax.swing.text.html.HTML.Attribute;
import javax.xml.xpath.XPathExpressionException;

import org.custommonkey.xmlunit.Difference;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.crawljax.util.Helper;
import com.sun.xml.internal.fastinfoset.util.CharArray;


public class NewDifferences {

	public static Document firstDoc;
	public static Document secondDoc;
	public static String[] finalDifferences;
	public static int differenceCounter=0;
	
	@Test
	public void getDifferences(){
		String before = "<html><body><div id=\"1\">some</div></body></html>";
		String after = "<html><head></head><body><div id=\"2\" class=\"obj\"><div></div><div></div>some text</div></body>";
		String openTag = "",dString,elementName,closeTag,result,anotherSimilarElement;
		int firstIndicator,secondIndicator,firstLocation = 0,secondLocation = 0,counter,temp,openTagTemp,closeTagIndex;
		String atLocation,comparingTags;
		try {
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
	/*			if(d.getDescription().equals("presence of child node")){
					elementName = getPath(d.getTestNodeDetail().getXpathLocation(),after,firstDoc);
					if(d.getTestNodeDetail().getNode().hasAttributes()){
						String str="";
						for(int p=d.getTestNodeDetail().getNode().getAttributes().getLength()-1;p>=0;p--){
						str = str.concat(d.getTestNodeDetail().getNode().getAttributes().item(p).toString());
						str = str.concat(" ");
						}
						str = str.substring(0,str.length()-1);
						elementName = getPath(d.getTestNodeDetail().getXpathLocation(),after,firstDoc);
						openTag = "<"+elementName+" "+str;
					}
					else 
						openTag = "<"+elementName;
				}*/
				Element elem;
	
					elem = getElement(d.getTestNodeDetail().getXpathLocation(),after, secondDoc);
					String att = Helper.getAllElementAttributes(elem);
					
				//	System.out.println(att);
				
				
				
				if(d.getDescription().equals("attribute value")){
				/*	elementName = getPath(d.getTestNodeDetail().getXpathLocation(),after,firstDoc);
					openTag = "<"+elementName+" "+d.getTestNodeDetail().getNode();*/
					elem = getElement(d.getTestNodeDetail().getXpathLocation(),after, secondDoc);
					String att1 = Helper.getAllElementAttributes(elem);
					elementName = "<"+elem.getNodeName().toLowerCase()+" "+att1+">";
				}
				else if(d.getDescription().equals("number of element attributes")){
					String str="";
					for(int p=d.getTestNodeDetail().getNode().getAttributes().getLength()-1;p>=0;p--){
					str = str.concat(d.getTestNodeDetail().getNode().getAttributes().item(p).toString());
					str = str.concat(" ");
					}
					str = str.substring(0,str.length()-1);
					elementName = getPath(d.getTestNodeDetail().getXpathLocation(),after,firstDoc);
					openTag = "<"+elementName+" "+str;
				}
				
				else{
				if(d.getTestNodeDetail().getXpathLocation().contains("@id")||d.toString().contains("null to")){
					firstIndicator = dString.indexOf(" to ");
					temp = firstIndicator+4;
					openTagTemp = 4;
				}
				else{
					firstIndicator = dString.indexOf("] to ");
					temp = firstIndicator+5;
					openTagTemp = 4;
				}
				secondIndicator = dString.indexOf(" at ", firstIndicator);
				comparingTags = dString.substring(temp, secondIndicator-openTagTemp);
				if(comparingTags.contains(">")){
					if(comparingTags.contains(" ")){
					closeTagIndex = comparingTags.indexOf(" ...>");
					comparingTags = comparingTags.substring(0, closeTagIndex);
					}
					else{
						closeTagIndex = comparingTags.indexOf("...>");
						comparingTags = comparingTags.substring(0, closeTagIndex);
					}
						
				}
				atLocation = dString.substring(secondIndicator+4, dString.length());
				elementName = getPath(atLocation.toUpperCase(), after, secondDoc);
				if(comparingTags.length()==(elementName.length()+1))
					openTag = "<"+elementName;
		
				
				if(atLocation.contains("text")){
					secondIndicator = dString.indexOf(">", firstIndicator);
					openTag = dString.substring(temp, secondIndicator-openTagTemp);
				} }
				
				anotherSimilarElement = "<"+elementName;
				closeTag = "</"+elementName+">";
				counter = getSpecificElement(anotherSimilarElement, after);
				int[] locations = new int[counter];
				for(int i=0;i<counter;i++){
					if(i==0){
						firstLocation = after.indexOf(closeTag);
						locations[i] = firstLocation;
					}
					else {
						
						secondLocation = after.indexOf(closeTag, firstLocation+closeTag.length());
						locations[i] = secondLocation;
						firstLocation = secondLocation;
					}
				}	
				firstLocation = after.indexOf(openTag);
				if(locations.length==1)
					result = after.substring(firstLocation, locations[0]+closeTag.length());
				else{
				result = after.substring(after.indexOf(openTag), locations[0]+closeTag.length());
				int resultCounter = getSpecificElement(anotherSimilarElement, result);
				result = after.substring(firstLocation, (locations[resultCounter]+closeTag.length()));
				}
				checkDifference(result);
				
			/*	result = after.substring(firstLocation, secondLocation+closeTag.length());
				checkDifference(result);*/	
			}
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	public void checkDifference(String str){
		if(str.contains("body")){
			str = str.substring(6, str.length()-7);
		}
	/*	if(str.contains("head")){
			str = str.substring(6, str.length()-7);
		}*/
		int i;
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
				}
			}
		}
	}
}
