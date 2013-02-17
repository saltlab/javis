package ca.ubc.javis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;

public class TestingContentExtractions {

	@Test
	public void testContentExtraction() {
	//	final String str = "<tag>apple</tag><b>hello</b><tag>orange</tag><tag>pear</tag>";
		final String str = "135c144---> < A class='gbmt' href='http://translate.google.ca/?hl=en&amp;sugexp=les%3B&amp;gs_rn=4&amp;gs_ri=psy-ab&amp;cp=8&amp;gs_id=4j&amp;xhr=t&amp;q=zolzXhDc&amp;bav=on.2,or.r_gc.r_pw.r_qf.&amp;bvm=bv.42661473,d.cGE&amp;biw=940&amp;bih=879&amp;um=1&amp;ie=UTF-8&amp;sa=N&amp;tab=wT' id='gb_51' onclick='gbar.qs(this);gbar.logger.il(1,{t:51});'>Translate</A>" +
				"</TEXTAREA><SCRIPT>if(google.j.b)document.body.style.visibility='hidden';</SCRIPT>";
		assertNotNull(getTagValues(str));
	/*	assertEquals(4, getTagValues(str).size());
		assertEquals("hello", getTagValues(str).get(1));
		assertEquals("orange", getTagValues(str).get(2));*/
		System.out.println(getTagValues(str));
	}

	private static final Pattern TAG_REGEX = Pattern.compile("<.+?>(.+?)</.?>");	

	private static List<String> getTagValues(final String str) {
	    final List<String> tagValues = new ArrayList<String>();
	    final Matcher matcher = TAG_REGEX.matcher(str);
	    while (matcher.find()) {
	        tagValues.add(matcher.group(1));
	    }
	    return tagValues;
	}
}
