package ca.ubc.javis;

import java.io.IOException;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.custommonkey.xmlunit.Difference;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.crawljax.util.DOMComparer;
import com.crawljax.util.Helper;


public class CheckDifference {

	public String before;
	public String after;
	public Document doc1;
	
	@Test
	public void testGetDifference1_1() throws SAXException, IOException{
		before = "<HTML><body><div id=1><span>some</span></div></body></HTML>";
		after = "<HTML><body><div id=1><div></div><span>some</span></div></body></HTML>";
		System.out.println(StringUtils.getLevenshteinDistance(before, after));
	}
}

